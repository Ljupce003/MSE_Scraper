import json
import requests
from bs4 import BeautifulSoup
import re  # For regex to filter out codes with numbers

import Filter_II


def fetch_codes_from_tabs(url):
    """Fetch the codes (Шифра на ХВ) from all three tabs on the page."""
    response = requests.get(url)
    if response.status_code != 200:
        print(f"Failed to retrieve the page. Status code: {response.status_code}")
        return {}

    soup = BeautifulSoup(response.text, 'html.parser')
    tabs = soup.find_all('table', class_='table')
    if not tabs:
        print("No tabs found.")
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
                code = columns[0].text.strip()  # Assuming Шифра на ХВ is in the first column
                if code and not re.search(r'\d', code):  # Filter out codes containing numbers
                    codes.append(code)
        codes_by_tab[tab_names[i]] = codes

    return codes_by_tab


def Call_Filter_1():
    url = 'https://www.mse.mk/mk/stats/current-schedule'
    codes_by_tab = fetch_codes_from_tabs(url)
    if codes_by_tab:
        # Flatten all codes and assign unique IDs
        all_codes = [{"id": idx + 1, "name": code} for idx, code in enumerate(sum(codes_by_tab.values(), []))]
        with open("../MSE_Scraper/Domasna 2/tech prototype/DiAnS-Project/src/main/resources/static/csv/issuer_names.json", "w", encoding="utf-8") as file:
            json.dump(all_codes, file, indent=4, ensure_ascii=False)
        print("Issuer codes saved to 'issuer_names.json'.")
    Filter_II.Call_Filter_II()