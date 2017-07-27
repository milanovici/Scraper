import urllib.request
from bs4 import BeautifulSoup
import csv

pages = range(0, 20, 10)

link = "https://www.yelp.com/search?find_desc=tacos&find_loc=San+Francisco,+CA&start={0}"

css_selector = "span > a[data-analytics-label=biz-name]"

data = []
for p in pages:
  response = urllib.request.urlopen(link.format(p))
  soup = BeautifulSoup(response.read(), "html.parser")
  data += soup.select(css_selector)

print(len(data))

fieldnames = ['name', 'link']
with open('yelp.csv', 'w') as csvfile:
  writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
  writer.writeheader()
  for d in data:
    name = str(d.contents[0].text)
    link = d['href']
    writer.writerow({'name': name, 'link': 'https://www.yelp.com' + link})


//*[@id="thing_t3_62dant"]/div[2]/p[1]/a
#thing_t3_62dant > div.entry.unvoted > p.title > a
#//*[@id="list-items"]/ul/li[17]/div
##//*[@id="list-items"]/ul/li[18]/div

#list-items > ul > li:nth-child(19) > div
#list-items > ul > li:nth-child(20) > div
#main > ul > li:nth-child(2) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > div > p:nth-child(1) > span
#main > ul > li:nth-child(6) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > div > p:nth-child(1) > span


data = []
for el in a:
  if el != -1:
    data.append('x')
  else:
    data.append(-1)