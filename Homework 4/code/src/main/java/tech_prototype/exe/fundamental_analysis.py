import asyncio
import aiohttp
import re
import json
from datetime import datetime
from bs4 import BeautifulSoup
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import numpy as np
import sys


class NewsScraper:
    def __init__(self, session, max_concurrent_requests=10):
        self.base_url = "https://www.mse.mk/en/symbol/{issuer}"
        self.text_url = "https://api.seinet.com.mk/public/documents/single/{news_id}"
        self.session = session
        self.semaphore = asyncio.Semaphore(max_concurrent_requests)

    async def fetch_news_links(self, issuer):
        url = self.base_url.format(issuer=issuer)
        async with self.semaphore:
            try:
                async with self.session.get(url, timeout=15) as response:
                    response.raise_for_status()
                    page_content = await response.text()
                    soup = BeautifulSoup(page_content, "html.parser")
                    news_links = soup.select('#seiNetIssuerLatestNews a')
                    return [
                        {
                            "news_id": self.extract_news_id(link["href"]),
                            "date": self.extract_date(link.select_one("ul li:nth-child(2) h4").text) if link.select_one("ul li:nth-child(2) h4") else None,
                        }
                        for link in news_links if "href" in link.attrs
                    ]
            except Exception as e:
                return []

    def extract_news_id(self, url):
        return url.split("/")[-1]

    def extract_date(self, date_str):
        try:
            match = re.search(r"\d{1,2}/\d{1,2}/\d{4}", date_str)
            if match:
                return datetime.strptime(match.group(), "%m/%d/%Y").date()
        except Exception:
            pass
        return None

    async def fetch_news_content(self, news_id):
        url = self.text_url.format(news_id=news_id)
        async with self.semaphore:
            try:
                async with self.session.get(url, timeout=15) as response:
                    if response.status == 200:
                        data = await response.json()
                        content = data.get("data", {}).get("content")
                        clean_content = re.sub(r"<[^>]*>", "", content) if content else None
                        return clean_content
            except Exception:
                return None


class SentimentAnalyzer:
    def __init__(self):
        self.tokenizer = AutoTokenizer.from_pretrained("ProsusAI/finbert")
        self.model = AutoModelForSequenceClassification.from_pretrained("ProsusAI/finbert")

    def analyze(self, text):
        inputs = self.tokenizer(text, return_tensors="pt", truncation=True, padding=True)
        outputs = self.model(**inputs)
        label = outputs.logits.argmax().item()
        sentiment = ["negative", "neutral", "positive"][label]
        return sentiment


class Pipeline:
    def __init__(self):
        self.analyzer = SentimentAnalyzer()
        # Initialize lists to accumulate results
        self.recommendations = []
        self.scraped_dates = []
        self.sentiments = []

    async def process_issuer(self, issuer, scraper):
        news_links = await scraper.fetch_news_links(issuer)
        for news in news_links:
            content = await scraper.fetch_news_content(news["news_id"])
            if content:
                sentiment = self.analyzer.analyze(content)
                recommendation = "buy" if sentiment == "positive" else "sell" if sentiment == "negative" else "hold"
                self.recommendations.append(recommendation)
                self.scraped_dates.append(news["date"].strftime('%Y-%m-%d') if news["date"] else "N/A")
                self.sentiments.append(sentiment)

    async def finalize(self):
        # After processing all the data, compile the final JSON structure
        output = {
            "recommendations": self.recommendations,
            "scraped_dates": self.scraped_dates,
            "sentiments": self.sentiments
        }
        print("EXCHANGE")
        print(json.dumps(output, ensure_ascii=False, indent=4))


if __name__ == "__main__":
    issuer = sys.argv[1]


    async def main():
        async with aiohttp.ClientSession() as session:
            scraper = NewsScraper(session)
            pipeline = Pipeline()
            await pipeline.process_issuer(issuer, scraper)
            await pipeline.finalize()

    asyncio.run(main())
