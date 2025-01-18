import time
from datetime import datetime, timedelta
from concurrent.futures import ThreadPoolExecutor, as_completed
import pandas as pd
from dateutil.relativedelta import relativedelta

import io
import requests
from bs4 import BeautifulSoup
import re
import json
import os
import sys

csv_file_path = "./Smestuvanje/mega-data.csv"
json_file_path = "./Smestuvanje/issuer_names.json"
output_json = "./Smestuvanje/last_dates.json"
patjson = "./Smestuvanje/issuer_names.json"
url = "https://www.mse.mk/mk/stats/symbolhistory/ALK"
pat = "./Smestuvanje/mega-data.csv"

start_time = time.time()

directory = './Smestuvanje'
if not os.path.exists(directory):
    os.makedirs(directory)


# URL на страницата
url_base45 = "https://www.mse.mk/mk/stats/current-schedule"


def Call_Filter_1():
    # print("Filter 1 started")
    url_local_in_method = 'https://www.mse.mk/mk/stats/current-schedule'
    codes_by_tab = fetch_codes_from_tabs(url_local_in_method)
    if codes_by_tab:
        # Flatten all codes and assign unique IDs
        all_codes = [{"id": idx + 1, "name": code} for idx, code in enumerate(sum(codes_by_tab.values(), []))]
        with open(patjson, "w", encoding="utf-8") as file_local:
            json.dump(all_codes, file_local, indent=4, ensure_ascii=False)
        # print("Issuer codes saved to 'issuer_names.json'.")
    Call_Filter_II()


def Call_Filter_II():
    get_last_dates_for_firms(csv_file_path, json_file_path, output_json)
    Filter_III(output_json)


# Функција за вчитување и извлекување на податоци од една категорија
def fetch_data_from_category(url_input):
    """
    Gi prevzema iminjata i kogovite zaedno so nivnite linkovi koi se naoogaat na dadenata URL
    :param url_input: URL na kategorijata kade se locirani del od Izdavacite
    :return: list od recnici za Izdavacite
    """
    response = requests.get(url_input)

    if response.status_code != 200:
        # print(f'Error with reading page: {response.status_code}')
        return []

    soup1 = BeautifulSoup(response.content, 'html.parser')

    # Пребарување на сите табели на страницата
    tables = soup1.find_all('table')

    category_data = []

    # Пребарување низ секоја табела
    for table in tables:
        rows = table.find_all('tr')

        for row in rows[1:]:  # Првиот ред е хедер, па го прескокнуваме
            columns = row.find_all('td')

            if len(columns) >= 3:
                # Извлекување на 'Issuer code' и Issuer name
                code = columns[0].get_text(strip=True)
                name = columns[1].get_text(strip=True)
                link = 'https://www.mse.mk' + columns[0].find('a').get('href') if columns[0].find('a') else None

                # Проверка дали "Issuer code" не содржи бројки
                if not re.search(r'\d', code):  # Проверка дали има бројки
                    category_data.append({
                        'Issuer code': code,
                        'Issuer name': name,
                        'Issuer link': link
                    })

    return category_data


def FetchNames():
    """
    Gi prevzema iminjata i kogovite zaedno so nivnite linkovi i potoa gi zacuvuva vo JSON fajl
    """
    # Список за чување на податоци
    data12 = []

    # Параметри за различните категории
    categories = [
        {"name": "Континуирано (+/- 10%)", "url": url_base45 + "?category=10"},
        {"name": "Аукциско со ценовни ограничувања (+/- 20%)", "url": url_base45 + "?category=20"},
        {"name": "Аукциско без ценовни ограничувања", "url": url_base45 + "?category=no-limit"}
    ]

    for category in categories:
        # print(f"Reading data for category: {category['name']}")
        category_data = fetch_data_from_category(category['url'])
        data12.extend(category_data)
    # Отстранување на дупликатите користејќи сет
    unique_data = []
    seen = set()

    for item in data12:
        code = item['Issuer code']
        name = item['Issuer name']
        if (code, name) not in seen:
            unique_data.append(item)
            seen.add((code, name))

    # Запишување на податоците во JSON фајл
    output_dir = './Smestuvanje'
    os.makedirs(output_dir, exist_ok=True)  # Ако не постои папката, ја создава
    with open(os.path.join(output_dir, 'names.json'), 'w', encoding='utf-8') as f:
        json.dump(unique_data, f, ensure_ascii=False, indent=4)

    print("Податоците успешно се зачувани во JSON.")


def fetch_codes_from_tabs(url_input):
    """Fetch the codes (Issuer code) from all three tabs on the page."""
    response = requests.get(url_input)
    if response.status_code != 200:
        # print(f'Failed to retrieve the page. Status code: {response.status_code}')
        return {}

    soup = BeautifulSoup(response.text, 'html.parser')
    tabs = soup.find_all('table', class_='table')
    if not tabs:
        # print("No tabs found.")
        return {}

    codes_by_tab = {}
    tab_names = ["Континуирано (+/- 10%)", "Аукциско со ценовни ограничувања (+/- 20%)",
                 "Аукциско без ценовни ограничувања"]

    for i, tab in enumerate(tabs):
        codes = []
        rows = tab.find_all('tr')
        for row in rows:
            columns = row.find_all('td')
            if columns:
                code = columns[0].text.strip()  # Assuming 'Issuer code' is in the first column
                if code and not re.search(r'\d', code):  # Filter out codes containing numbers
                    codes.append(code)
        codes_by_tab[tab_names[i]] = codes

    return codes_by_tab


def load_or_create_csv(csv_file):
    """Checks and loads or creates a 'csv' file if it is missing"""
    folder = os.path.dirname(csv_file)
    if folder and not os.path.exists(folder):
        os.makedirs(folder)
        # print(f'Created folder: {folder}')

    if not os.path.isfile(csv_file):
        headers = ["code", "date", "close", "max", "low", "avg", "volume", "turnover in BEST", "total turnover"]
        pd.DataFrame(columns=headers).to_csv(csv_file, index=False)
        # print(f'Created CSV file: {csv_file}')

    return pd.read_csv(csv_file, header=0,
                       names=["code", "date", "close", "max", "low", "avg", "volume", "turnover in BEST",
                              "total turnover"])


def get_last_dates_for_firms(csv_file, json_file, output_json_in):
    csv_data = load_or_create_csv(csv_file)
    csv_data["date"] = pd.to_datetime(csv_data["date"], errors='coerce', format="%d.%m.%Y")
    csv_data = csv_data.sort_values(by=["code", "date"])

    with open(json_file, "r", encoding="utf-8") as file_local:
        json_data = json.load(file_local)

    last_dates = []
    for firm in json_data:
        code = firm["name"]
        firm_data = csv_data[csv_data["code"] == code]

        if not firm_data.empty:
            last_date = firm_data["date"].max().strftime("%d.%m.%Y")

        else:
            date_10_years_ago = (datetime.today() - relativedelta(years=10)).strftime("%d.%m.%Y")
            last_date = date_10_years_ago

        last_dates.append({"code": code, "last_date": last_date})

    with open(output_json_in, "w", encoding="utf-8") as file_local:
        json.dump(last_dates, file_local, ensure_ascii=False, indent=4)


def Filter_III(last_dates_json):
    # print("Filter 3 started")
    with open(last_dates_json, "r", encoding="utf-8") as file_local:
        last_dates_data = json.load(file_local)

    today = datetime.today().strftime("%d.%m.%Y")
    for entry in last_dates_data:
        code = entry["code"]
        last_date = entry["last_date"]

        if last_date and last_date != today:
            print(f"Code: {code}, Last Date: {last_date}, Today's Date: {today}")
            Call_save_data_from_to(code, last_date, today)


def format_price(price):
    return f"{price:,.2f}".replace(",", "X").replace(".", ",").replace("X", ".")


def fetch_data_for_period(firm_code, start_date, end_date):
    """
    Fetches data for a issuer for a max period of 1 year
    :param firm_code: The issuer that we fetch data for
    :param start_date: Start date of the period to fetch for
    :param end_date: End date of the period to fetch for
    :return: pd.DataFrame that contains the data
    """
    session = requests.Session()
    payload = {"FromDate": start_date, "ToDate": end_date, "Code": firm_code}
    response = session.post(url, data=payload)
    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')
        table = soup.find('table')
        if table:
            rows = []
            headers = ['date', "close", "max", "low", "avg", "volume", "turnover in BEST", "total turnover"]

            for tr in table.find_all('tr')[1:]:
                cells = [td.text.strip() for td in tr.find_all('td')]
                if cells:
                    if cells[2] and cells[3]:
                        cells.pop(5)
                        rows.append(cells)
            data_out = pd.DataFrame(rows, columns=headers)
            data_out.insert(0, "Issuer", firm_code)
            return data_out
    return None


def fetch_data_for_large_date_range(firm_code, start_date, end_date):
    """
    Collectively fetches the whole data for a issuer based on the given start and end dates
    :param firm_code: The issuer that we fetch data for
    :param start_date: Start date that we start fetching
    :param end_date: End date that we start fetching
    :return: pd.DataFrame that contains the whole data for that issuer
    """
    all_data = []
    max_days = 365
    current_start = datetime.strptime(start_date, "%d.%m.%Y")

    date_intervals = []
    while current_start <= datetime.strptime(end_date, "%d.%m.%Y"):
        next_end = min(current_start + timedelta(days=max_days - 1), datetime.strptime(end_date, "%d.%m.%Y"))
        date_intervals.append((current_start.strftime("%d.%m.%Y"), next_end.strftime("%d.%m.%Y")))
        current_start = next_end + timedelta(days=1)

    with ThreadPoolExecutor(max_workers=10) as executor:
        futures = {executor.submit(fetch_data_for_period, firm_code, start, end): (start, end) for start, end in
                   date_intervals}
        for future in as_completed(futures):
            result = future.result()
            if result is not None:
                all_data.append(result)

    if all_data:
        return pd.concat(all_data, ignore_index=True)
    return None


def Call_save_data_from_to(firm_code, start_date, end_date):
    """
    Collectively fetches the whole data for a issuer based on the given start and end dates and then save it to a
    local file
    :param firm_code: The issuer that we fetch data for
    :param start_date: Start date that we start fetching
    :param end_date: End date that we start fetching
    """
    try:
        memory_data = fetch_data_for_large_date_range(firm_code, start_date, end_date)
        if memory_data is not None:
            for column in ["close", "max", "low", "avg", "turnover in BEST", "total turnover"]:
                if column in memory_data.columns:
                    memory_data[column] = memory_data[column].apply(lambda x: ChangeNumberFormat(x))
                    memory_data[column] = memory_data[column].astype(str)

            memory_data.drop_duplicates(subset=['Issuer', 'date'], inplace=True)
            memory_data['date'] = pd.to_datetime(memory_data['date'], format='%d.%m.%Y')

            memory_data.sort_values(by=["Issuer", "date"], inplace=True)

            memory_data['date'] = memory_data['date'].dt.strftime('%d.%m.%Y')
            memory_data.to_csv(pat, mode='a', header=False, index=False)
            # print(f"Data for {firm_code} saved.")

    except BrokenPipeError as e:
        print(f"BrokenPipeError while processing {firm_code}: {e}")
    except Exception as e:
        print(f"Unexpected error while processing {firm_code}: {e}")


def ChangeNumberFormat(price_string):
    price_string = price_string.replace(".", "")
    price_string = price_string.replace(",", ".")
    return price_string


def ReplaceDots(price_string):
    price_string = price_string.replace(".", "'")
    price_string = price_string.replace(",", ".")
    price_string = price_string.replace("'", ",")
    return price_string


def StartScraping():
    Call_Filter_1()
    FetchNames()
