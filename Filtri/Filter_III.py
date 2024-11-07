
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


fetch_data_for_period("KMB", "01.11.2024", "07.11.2024")
