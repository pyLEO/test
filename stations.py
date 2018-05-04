#!/usr/bin/python3 env
# -*- coding: UTF-8 -*-

"""
store stations index in mongodb

"""


from selenium import webdriver
from lxml import html
import re
import redis

browser = webdriver.Chrome()
# 首页
browser.get("https://kyfw.12306.cn/otn/leftTicket/init")
browser.find_element_by_xpath("//input[contains(@id,'fromStationText')]").click()


r = redis.StrictRedis(host='192.168.0.127', port=6379)

for i in range(1, 7):
    # 点击标签
    browser.find_element_by_xpath("//li[contains(@id,'nav_list{}')]".format(i)).click()
    if i == 1:
        doc = html.document_fromstring(browser.page_source)
        for d in doc.xpath("//ul[contains(@id,'ul_list')]//li"):
            r.hmset("name", {d.get('title'): d.get("data")+",1,0,0"})
    else:
        # 获取分页
        tab = browser.find_element_by_xpath("//a[contains(@class,'cityflip')]")
        onclick = tab.get_attribute("onclick")
        page_num = re.search(r'\d+,', onclick, re.M | re.I)
        if page_num:
            pnum = int(page_num.group().strip(','))
            for j in range(pnum):
                # 点击分页
                if j == 0:
                    doc = html.document_fromstring(browser.page_source)
                    row_num = doc.xpath("count(//*[contains(@id,'ul_list{}')]//ul)".format(i))
                    # 获取行数
                    for k in range(1, int(row_num)+1):
                        for d in doc.xpath("//*[contains(@id,'ul_list{}')]//ul[contains(@class,"
                                           "'popcitylist')][{}]//li".format(i, k)):
                            r.hmset("name", {d.get('title'): str(d.get("data")) + ",{},{},{}".format(i, j + 1, k)})
                else:
                    browser.find_element_by_xpath("//a[contains(@class,'cityflip') and contains(.,'下一页')]").click()

                    doc = html.document_fromstring(browser.page_source)
                    row_num = doc.xpath("count(//*[contains(@id,'ul_list{}')]//ul)".format(i))
                    for k in range(1, int(row_num)+1):
                        for d in doc.xpath("//*[contains(@id,'ul_list{}')]//ul[contains(@class,"
                                           "'popcitylist')][{}]//li".format(i, k)):
                            r.hmset("name", {d.get('title'): str(d.get("data")) + ",{},{},{}".format(i, j+1, k)})
        else:
            print('wrong page number')







