from datetime import *
from selenium.webdriver.support.ui import Select
import requests
from bs4 import BeautifulSoup
from selenium import webdriver
import pandas as pd
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


def contains_number(string):
    return any(char.isdigit() for char in string)


def first_Filter(url):
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')

    browser = webdriver.Chrome(options=options)
    browser.get(url)

    firms = browser.find_elements(By.CSS_SELECTOR, ".form-control option")
    firm_codes = []

    for firm in firms:
        code = firm.text
        if not contains_number(code):
            firm_codes.append(firm.text)

    return firm_codes


URL = "https://www.mse.mk/mk/stats/symbolhistory/ALK"

codes = first_Filter(URL)

codes_df = pd.DataFrame(codes)

codes_df.to_csv("codes.csv")

print(codes)


def collectforDecade(firm_code):
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')

    browser = webdriver.Chrome(options=options)
    browser.get('https://www.mse.mk/mk/stats/symbolhistory/ALK')

    #voa e za begin/end date
    t_bound = datetime.now()
    l_bound = t_bound - timedelta(days=365)

    #tuka e for ciklusot za sekoja goidna da vrte
    firm_table = []


    #Multithreaded za sekoja goidna na edna sifra
    # togas sledniot for neka bide poveke threads

    for i in range(0, 10):
        t_bound_str = t_bound.strftime("%d.%m.%Y")
        l_bound_str = l_bound.strftime("%d.%m.%Y")

        #Ova gi ciste i gi vnesuva od(Datum) i do(Datum)
        date_pickers = browser.find_elements(By.CSS_SELECTOR, ".datepicker ")
        date_pickers[1].clear()
        date_pickers[0].clear()
        date_pickers[1].send_keys(t_bound_str)
        date_pickers[0].send_keys(l_bound_str)
        code_select = Select(browser.find_element(By.CSS_SELECTOR, '#Code'))

        #Tuka se selektira sifrata
        code_select.select_by_visible_text(firm_code)

        #Ova e dugmeto za prikazi
        dugme = browser.find_element(By.CSS_SELECTOR, ".container-end input")
        dugme.click()

        #Ova ceka da se loadira cela tabela
        WebDriverWait(browser, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "#resultsTable tbody tr")))

        rows = browser.find_elements(By.CSS_SELECTOR, "#resultsTable tbody tr")

        for row in rows:
            cl = row.find_elements(By.CSS_SELECTOR, "td")
            #date - 0
            #price - 1
            #max - 2
            #min - 3
            #volume - 6
            #BEST - 7
            data = cl[0].text
            price = cl[1].text
            max = cl[2].text
            min = cl[3].text
            volume = cl[6].text
            BEST = cl[7].text

            parsed_row = {"Date": data,
                          "Price": price,
                          "Max": max,
                          "Min": min,
                          "Volume": volume,
                          "BEST": BEST}

            firm_table.append(parsed_row)

        t_bound = l_bound
        l_bound = t_bound - timedelta(days=365)

    return firm_table
