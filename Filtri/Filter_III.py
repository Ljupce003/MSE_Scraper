
import requests
from bs4 import BeautifulSoup
import pandas as pd
url = "https://www.mse.mk/mk/stats/symbolhistory/MPT"

def fetch_data_for_period(firm_code, start_date, end_date):
    session = requests.Session()

    # Prepare payload with date and firm_code parameters
    payload = {
        "FromDate": start_date,
        "ToDate": end_date,
        "Code": firm_code
    }

    # Post request to retrieve the data page
    response = session.post(url, data=payload)

    # Check if the request was successful
    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')

        # Assuming the table data can be parsed directly
        table = soup.find('table')  # Find the table with data (update selector as needed)

        if table:
            rows = []
            headers = [th.text for th in table.find_all('th')]

            for tr in table.find_all('tr')[1:]:
                cells = [td.text for td in tr.find_all('td')]
                if cells:
                    rows.append(cells)
            print("\t".join(headers))
            for row in rows:
                print("\t".join(row))
        else:
            print("No table found on the page.")
    else:
        print("Failed to retrieve data:", response.status_code)


from datetime import datetime, timedelta

# Главната функција за поделба на интервали и повикување на getDataitem
def fetch_data_for_large_date_range(start_date, end_date):
    max_days = 365
    current_start = start_date

    while current_start <= end_date:  # Променето: <= за да го вклучиме и последниот ден
        # Одредување на крајната дата за тековниот интервал (максимум 365 дена)
        next_end = current_start + timedelta(days=max_days - 1)

        # Ако next_end надмине end_date, го поставуваме на end_date
        if next_end > end_date:
            next_end = end_date

        # Повик на getDataitem за тековниот под-интервал
        #fetch_data_for_period("KMB", "01.11.2024", "07.11.2024")
        #getDataitem(current_start, next_end)

        # Поместување на стартната дата за следниот интервал
        current_start = next_end + timedelta(days=1)




fetch_data_for_period("KMB", "05.11.2023", "04.11.2024")
