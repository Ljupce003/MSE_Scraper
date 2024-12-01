import requests
from bs4 import BeautifulSoup
import pandas as pd
from datetime import datetime, timedelta
from concurrent.futures import ThreadPoolExecutor, as_completed

url = "https://www.mse.mk/mk/stats/symbolhistory/MPT"

def format_price(price):
    return f"{price:,.2f}".replace(",", "X").replace(".", ",").replace("X", ".")

def fetch_data_for_period(firm_code, start_date, end_date):
    session = requests.Session()
    payload = {"FromDate": start_date, "ToDate": end_date, "Code": firm_code}
    response = session.post(url, data=payload)
    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')
        table = soup.find('table')
        if table:
            rows = []
            headers = [th.text.strip() for th in table.find_all('th')]
            for tr in table.find_all('tr')[1:]:
                cells = [td.text.strip() for td in tr.find_all('td')]
                if cells:
                    rows.append(cells)
            data = pd.DataFrame(rows, columns=headers)
            data.insert(0, "Issuer", firm_code)
            return data
    return None

def fetch_data_for_large_date_range(firm_code, start_date, end_date):
    all_data = []
    max_days = 365
    current_start = datetime.strptime(start_date, "%d.%m.%Y")

    date_intervals = []
    while current_start <= datetime.strptime(end_date, "%d.%m.%Y"):
        next_end = min(current_start + timedelta(days=max_days - 1), datetime.strptime(end_date, "%d.%m.%Y"))
        date_intervals.append((current_start.strftime("%d.%m.%Y"), next_end.strftime("%d.%m.%Y")))
        current_start = next_end + timedelta(days=1)

    with ThreadPoolExecutor(max_workers=10) as executor:
        futures = {executor.submit(fetch_data_for_period, firm_code, start, end): (start, end) for start, end in date_intervals}
        for future in as_completed(futures):
            result = future.result()
            if result is not None:
                all_data.append(result)

    if all_data:
        return pd.concat(all_data, ignore_index=True)
    return None

def Call_save_data_from_to(firm_code, start_date, end_date):
    data = fetch_data_for_large_date_range(firm_code, start_date, end_date)
    if data is not None:
        for column in ["open", "high", "low", "close"]:
            if column in data.columns:
                data[column] = data[column].apply(lambda x: format_price(float(x.replace(',', '.'))))
        data.to_csv('../MSE_Scraper/shared/mega-data.csv', mode='a', header=False, index=False)
        print(f"Data for {firm_code} saved.")
