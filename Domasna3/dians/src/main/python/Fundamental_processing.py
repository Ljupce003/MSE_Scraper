import pandas as pd
import numpy as np
from transformers import pipeline
import torch
import time
from datetime import datetime, timedelta
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime
from dateutil.relativedelta import relativedelta
from playwright.sync_api import sync_playwright
from collections import Counter
import logging
import io
import requests
from bs4 import BeautifulSoup
import re
import json
import os
import sys
import xml.etree.ElementTree as ET


class ChannelItem:
    def __init__(self, item_title, item_link, item_pub_date):
        self.title = item_title
        self.link = item_link
        self.pub_date = item_pub_date

    def __str__(self):
        to_string = (' Item : ' + self.title + '\nLink : ' + self.link
                     + '\n Publication Date : ' + self.pub_date)

        return to_string

    def to_dict(self):
        return {
            'title': self.title,
            'link': self.link,
            'pub_date': self.pub_date
        }


class Channel:
    def __init__(self, title, link, code, items: list[ChannelItem]):
        self.title = title
        self.link = link
        self.code = code
        self.rss_items = items
        self.model_processed_texts = list()
        self.result = "NEUTRAL"
        self.score = 0.00
        self.last_date = datetime.today()

    def setProcessed(self, processed_list: list):
        self.model_processed_texts = processed_list

        labels = [entry['label'] for entry in processed_list]

        if labels:
            most_common_label, _ = Counter(labels).most_common(1)[0]

            self.result = most_common_label

            scores = [entry["score"] for entry in processed_list if entry['label'] == most_common_label]

            most_common_score = sum(scores) / len(scores) if scores else 0.00
            self.score = most_common_score

            self.last_date = datetime.today()

    def __str__(self):
        to_string = ('Channel : ' + self.title + '\n Channel link : ' + self.link
                     + '\n Channel description : ' + self.code + '\n Items:\n')

        for rss_item_object in self.rss_items:
            to_string += rss_item_object.__str__()

        to_string += "\n"
        for text in self.model_processed_texts:
            to_string += "\n RSS link is :" + str(text['rss_link'])
            to_string += ("\nOriginal text is :\n" + str(text['original_text']) +
                          "\n Translated text is: \n" + str(text['text']) + '\n And the Analysis result is:\n'
                          + str(text['label']) + " with score " + str(text['score']) + "\n" + "-" * 50)

        to_string += ("\nAnd the final score of the news is :" + str(self.result)
                      + " with score : " + str(self.score) + "\n")

        return to_string

    def to_dict(self):
        return {
            'title': self.title,
            'link': self.link,
            'code': self.code,
            'rss_items': [item.to_dict() for item in self.rss_items],
            'model_processed_texts': self.model_processed_texts,
            'result': self.result,
            'score': self.score,
            'last_date': self.last_date.isoformat()
        }


def getIssuerSiteLinksFromLocal(json_path, processed_json_path):
    """
    Gi vraka listata od recnici od JSON fajl
    :param processed_json_path:
    :param json_path:
    :return:
    """
    with open(json_path, 'r', encoding='utf-8') as file_og:
        # Load the contents of the JSON file into a Python list of dictionaries
        data = json.load(file_og)

    # Initialize an empty list for processed_data
    processed_data = []

    # Check if the processed file exists
    if os.path.exists(processed_json_path):
        # If the file exists, load the processed issuers data
        with open(processed_json_path, 'r', encoding='utf-8') as file_processed:
            processed_data = json.load(file_processed)

    # Get today's date in the same format as the 'last_date' (ISO 8601)
    today_date = datetime.today().date().isoformat()

    # Filter out issuers that have been processed today
    unprocessed_issuers = []

    for issuer in data:
        issuer_code = issuer['Issuer code']
        # Check if the issuer has been processed today
        processed_issuer = next((item for item in processed_data if item['code'] == issuer_code), None)

        if processed_issuer:
            last_date_str = processed_issuer['last_date']
            # Convert last_date string to a datetime object
            last_date = datetime.fromisoformat(last_date_str).date().isoformat()

            # If it was processed today, skip this issuer
            if last_date == today_date:
                continue

        # If the issuer hasn't been processed or was processed on a different day, add to list
        unprocessed_issuers.append(issuer)

    print(unprocessed_issuers)
    print(len(unprocessed_issuers))
    return unprocessed_issuers


def getRSS_url(url_input: str):
    """
    Convertira link na issuer od json fajlot vo rss url kade se naogaat vestite za toj issuer
    :param url_input: original issuer url link
    :return: Converted url link to rss url
    """
    # url_input = url_input.replace("/mk/", "/en/")
    response_local = requests.get(url_input)

    if response_local.status_code != 200:
        print(f'Failed to retrieve the page. Status code: {response_local.status_code}')
        return

    rss_url_local = response_local.url.replace("/issuer/", "/rss/seinet/")

    return rss_url_local


def processIssuerDictToChannel(issuer):
    rss_url = getRSS_url(issuer['Issuer link'])

    try:
        # Fetch the RSS feed content
        response = requests.get(rss_url)
        response.raise_for_status()  # Raise an error for HTTP issues
        rss_content = response.text

        # Parse the RSS feed
        root = ET.fromstring(rss_content)

        # Extract channel information
        channel = root.find("channel")

        rss_objects_list = []

        # Extract items
        for item in channel.findall("item"):
            title = item.find("title").text
            link = item.find("link").text
            pub_date = item.find("pubDate").text

            rss_item_object = ChannelItem(title, link, pub_date)
            rss_objects_list.append(rss_item_object)

        new_channel = Channel(issuer['Issuer name'], issuer['Issuer link'], issuer['Issuer code'], rss_objects_list)
        return new_channel

    except requests.exceptions.RequestException as e:
        print(f"Error fetching RSS feed: {e}")
    except ET.ParseError as e:
        print(f"Error parsing RSS feed: {e} . No information found for : {issuer['Issuer name']} and code "
              f"{issuer['Issuer code']}")


def getRSSlinksForEachIssuer(dictionary_list: list):
    """
    Vraka lista od objekti Channel kade toj objekt sodrzi issuer name,link i rss linkovi za novostite so toj issuer
    :param dictionary_list: Primi lista od racnici kade se naogat iminjata i linkovite na sekoj issuer
    :return: Vraka lista od objekti Channel kade podatocite se struktuirani za podobra manipulacija so niv
    """
    channel_list = list()

    with ThreadPoolExecutor(max_workers=10) as executor:
        # Submit all issuers to the thread pool
        future_to_issuer = {
            executor.submit(processIssuerDictToChannel, issuer): issuer
            for issuer in dictionary_list
        }

        # Collect results as they complete
        for future in as_completed(future_to_issuer):
            try:
                channel = future.result()  # This will return the processed channel
                if channel:  # Only append valid channels
                    channel_list.append(channel)
            except Exception as e:
                print(f"Error processing issuer: {e}")

    return channel_list


def fetch_rss_page_with_playwright(url):
    """
    Metodata se koristi namesto soup zatoa sto soup ne raboti so dynamic pages(pages wih javascript)

    :param url: Url za da bide dinamicki processirano
    :return: Vraka soup objekt od url koe e vneseno
    """
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)  # You can also use .launch(headless=False) to see the browser
        page = browser.new_page()
        page.goto(url)
        page.wait_for_load_state('load')  # Wait for page to load
        page_source = page.content()  # Get the full HTML
        browser.close()

    soup = BeautifulSoup(page_source, "html.parser")
    return soup


def extract_text_concatenated_recursively(elements):
    """
    Metodata prima lista tag elementi (e.g. div,p,i,...) i go vraka tesktot sto se naoga vo nego i vo site negovi deca
    :param elements: List of soup TagElements
    :return: Celosniot tekst od extracted od elementot
    """
    text_concatenated = ''
    for element in elements:
        # Extract text from the current element
        text = element.get_text(strip=True)
        if text:  # Add non-empty text
            text_concatenated += text + ' '  # Add space for readability

    return text_concatenated.strip()  # Remove extra spaces from the final result


def extractTextForChannels(channel_list: list[Channel], container_found_counter=0):
    model = pipeline('sentiment-analysis', model='distilbert-base-uncased')
    translator = pipeline("translation", model="Helsinki-NLP/opus-mt-mk-en")

    for channel in channel_list:

        channel_texts_list = []
        rss_links_of_texts = []

        for rss_item in channel.rss_items:
            rss_link = rss_item.link

            soup = fetch_rss_page_with_playwright(rss_link)
            container = soup.select('.container')

            if container:
                container = container[2]
                container_found_counter += 1
            else:
                continue

            concatenated_t = str(container.get_text(strip=True)).replace("Листај по издавачЛистај по урнек", "")

            concatenated_t = concatenated_t.replace(
                "VW_DOCUMENT_PREVIEW_BYISSUERVW_DOCUMENT_PREVIEW_BYLAYOUTVW_DOCUMENT_PREVIEW_PUBLISHEDON:VW_DOCUMENT_PREVIEW_PUBLICID:VW_DOCUMENT_PREVIEW_LAYOUT:",
                "")
            concatenated_t = concatenated_t.replace("\xa0", "")
            if concatenated_t.strip():
                channel_texts_list.append(concatenated_t)

                rss_links_of_texts.append(rss_link)

        print("Channel text size", len(channel_texts_list))

        translated = translator(channel_texts_list, batch_size=4, max_length=512, truncation=True)

        translated_texts = []
        for res in translated:
            translated_texts.append(res['translation_text'].strip())

        sentiments = model(translated_texts, batch_size=4)

        rss_model_processed = []

        label_mapping = {
            "LABEL_0": "NEGATIVE",
            "LABEL_1": "POSITIVE"
        }

        for i in range(len(translated)):
            sentiment_label = label_mapping[sentiments[i]['label']]
            rss_model_processed.append({
                "rss_link": rss_links_of_texts[i],
                "original_text": channel_texts_list[i],
                "text": translated_texts[i],
                "label": sentiment_label,
                "score": sentiments[i]['score']
            })

        channel.setProcessed(rss_model_processed)

    return channel_list


def process_rss_item(rss_item, channel_title):
    try:
        rss_link = rss_item.link
        soup = fetch_rss_page_with_playwright(rss_link)
        container = soup.select('.container')

        if not container or len(container) < 3:
            return None  # Skip if the expected container isn't found

        container = container[2]
        concatenated_t = str(container.get_text(strip=True)).replace("Листај по издавачЛистај по урнек", "")
        concatenated_t = concatenated_t.replace(
            "VW_DOCUMENT_PREVIEW_BYISSUERVW_DOCUMENT_PREVIEW_BYLAYOUTVW_DOCUMENT_PREVIEW_PUBLISHEDON:VW_DOCUMENT_PREVIEW_PUBLICID:VW_DOCUMENT_PREVIEW_LAYOUT:",
            "")
        concatenated_t = concatenated_t.replace("\xa0", "").strip()

        if not concatenated_t:
            return None  # Skip if text is empty

        return {"channel_title": channel_title, "rss_link": rss_link, "text": concatenated_t}
    except Exception as e:
        logging.error(f"Error processing RSS item {rss_item.link}: {str(e)}")
        return None


def process_channel(channel):
    try:
        # Collect all texts for this channel
        texts_to_translate = []
        rss_links = []

        # Use multithreading for RSS item processing
        with ThreadPoolExecutor(max_workers=4) as executor:
            futures = [executor.submit(process_rss_item, rss_item, channel.title) for rss_item in channel.rss_items]
            for future in as_completed(futures):
                result = future.result()
                if result:
                    texts_to_translate.append(result["text"])
                    rss_links.append(result["rss_link"])

        model = pipeline('sentiment-analysis', model='distilbert-base-uncased')
        translator = pipeline("translation", model="Helsinki-NLP/opus-mt-mk-en")

        # Translate texts in batches
        translated_texts = []
        for i in range(0, len(texts_to_translate), 4):  # Batch size = 4
            batch = texts_to_translate[i:i + 4]
            try:
                translated_batch = translator(batch, max_length=512, truncation=True)
                translated_texts.extend([res['translation_text'].strip() for res in translated_batch])
            except Exception as e:
                logging.error(f"Translation error: {str(e)}")

        # Perform sentiment analysis
        sentiments = []
        for i in range(0, len(translated_texts), 4):  # Batch size = 4
            batch = translated_texts[i:i + 4]
            try:
                sentiments.extend(model(batch))
            except Exception as e:
                logging.error(f"Sentiment analysis error: {str(e)}")

        # Prepare processed data
        rss_model_processed = []
        label_mapping = {"LABEL_0": "NEGATIVE", "LABEL_1": "POSITIVE"}
        for i in range(len(translated_texts)):
            sentiment_label = label_mapping.get(sentiments[i]['label'], "UNKNOWN")
            rss_model_processed.append({
                "rss_link": rss_links[i],
                "original_text": texts_to_translate[i],
                "text": translated_texts[i],
                "label": sentiment_label,
                "score": sentiments[i]['score']
            })

        # Update the channel with processed data
        channel.setProcessed(rss_model_processed)
    except Exception as e:
        logging.error(f"Error processing channel {channel.title}: {str(e)}")


def extractTextForChannels_threaded(channel_list):
    with ThreadPoolExecutor(max_workers=4) as executor:
        futures = [executor.submit(process_channel, channel) for channel in channel_list]
        for future in as_completed(futures):
            future.result()  # Wait for all channels to be processed
    return channel_list


def save_channels_to_file(channel_list: list[Channel], filename='channels.json'):
    # Convert the list of channels to a list of dictionaries (if necessary)
    # but if each channel is an object, make sure it can be serialized
    channels_dict = [channel.to_dict() for channel in channel_list]  # If Channel is a class

    # Alternatively, if channel is a simple dictionary-like object:
    # channels_dict = channel_list

    if os.path.exists(filename):
        # Load the processed issuers data from the file
        with open(filename, 'r', encoding='utf-8') as file:
            processed_data = json.load(file)
    else:
        # If the file doesn't exist, it's the first time the method is being executed
        processed_data = []

    processed_issuers_codes = {item['code']: item for item in processed_data}

    # Update processed channels in the existing data (if they exist in the new data)
    for channel in channels_dict:
        # If the channel is already in the processed file, update it
        if channel['code'] in processed_issuers_codes:
            # Find the index of the processed issuer and update it
            processed_issuers_codes[channel['code']] = channel

    # Convert the processed_issuers_codes back to a list
    updated_processed_data = list(processed_issuers_codes.values())

    with open(filename, 'w', encoding='utf-8') as f:
        json.dump(updated_processed_data, f, ensure_ascii=False, indent=4)
        print(f"Channels saved to {filename}")


def main():
    start_time = time.time()
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

    json_file_path = './Smestuvanje/names.json'
    json_channels_path = './Smestuvanje/channels.json'

    dictio_list = getIssuerSiteLinksFromLocal(json_file_path, json_channels_path)
    channels = getRSSlinksForEachIssuer(dictio_list)
    channels = extractTextForChannels_threaded(channels)

    for channel_object in channels:
        print(channel_object)

    save_channels_to_file(channels, json_channels_path)

    end_time = time.time()
    duration = end_time - start_time
    print(f'\nThere are {len(channels)} channels found')
    print(f"Program completed in {duration:.2f} seconds")
    sys.exit(0)


if __name__ == "__main__":
    main()
