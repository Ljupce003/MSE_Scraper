import requests
from bs4 import BeautifulSoup
import pandas as pd
from datetime import datetime, timedelta
from concurrent.futures import ThreadPoolExecutor, as_completed

url = "https://www.mse.mk/mk/stats/symbolhistory/MPT"

def format_price(price):
    """Format prices to the specified format: 1.000.000,00."""
    formatted_price = f"{price:,.2f}".replace(",", "X").replace(".", ",").replace("X", ".")
    return formatted_price

def fetch_data_for_period(firm_code, start_date, end_date):
    """Fetch data for a specific firm and date range."""
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
    """Fetch data across a large date range by splitting into sub-intervals."""
    all_data = []
    max_days = 365
    current_start = datetime.strptime(start_date, "%d.%m.%Y")

    # Define a list to hold each start and end date for each interval
    date_intervals = []
    while current_start <= datetime.strptime(end_date, "%d.%m.%Y"):
        next_end = current_start + timedelta(days=max_days - 1)
        if next_end > datetime.strptime(end_date, "%d.%m.%Y"):
            next_end = datetime.strptime(end_date, "%d.%m.%Y")
        date_intervals.append((current_start.strftime("%d.%m.%Y"), next_end.strftime("%d.%m.%Y")))
        current_start = next_end + timedelta(days=1)

    # Use ThreadPoolExecutor to fetch each interval concurrently
    with ThreadPoolExecutor(max_workers=10) as executor:  # Adjust number of workers as needed
        futures = {executor.submit(fetch_data_for_period, firm_code, start, end): (start, end) for start, end in date_intervals}
        for future in as_completed(futures):
            result = future.result()
            if result is not None:
                all_data.append(result)

    # Combine all data into a single DataFrame
    if all_data:
        combined_data = pd.concat(all_data, ignore_index=True)
        return combined_data
    return None

def Call_save_data_from_to(firm_code, start_date, end_date):
    """Fetch and save data for a firm across a date range."""
    all_issuers_data = []

    # Fetch data
    data = fetch_data_for_large_date_range(firm_code, start_date, end_date)
    if data is not None:
        for column in ["open", "high", "low", "close"]:
            if column in data.columns:
                data[column] = data[column].apply(lambda x: format_price(float(x.replace(',', '.'))))
        all_issuers_data.append(data)
        print(f"Data fetched for issuer: {firm_code}")

    # Write data to CSV
    if all_issuers_data:
        combined_data = pd.concat(all_issuers_data, ignore_index=True)
        combined_data.to_csv('../Baza/mega-data.csv', mode='a', header=False, index=False)
        print("Data appended to 'mega-data.csv'")
    else:
        print("No data to append.")
