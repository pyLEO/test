#!/usr/bin/python3 env
# -*- coding: UTF-8 -*-

"""
check stations
"""


from selenium import webdriver
import redis
import time


class Check:

    def __init__(self, frst, tost):
        self.frst = frst
        self.tost = tost

    def run(self):
        browser = webdriver.Chrome()
        r = redis.StrictRedis(host='192.168.0.127', port=6379)
        browser.get("https://kyfw.12306.cn/otn/leftTicket/init")  # 首页

        browser.find_element_by_id("fromStationText").click()   # 点击获取始发
        val1 = r.hmget("name", self.frst)
        lf = str(val1[0], encoding='utf-8').split(',')
        print(lf)
        browser.find_element_by_id("nav_list{}".format(lf[1])).click()
        for i in range(int(lf[2])):
            if i == 0:
                continue
            else:
                browser.find_element_by_xpath("//a[contains(@class,'cityflip') and contains(.,'下一页')]").click()
        browser.find_element_by_xpath("//*[contains(@id,'ul_list{}')]//li[@data='{}']".format(lf[1], lf[0])).click()

        time.sleep(1)
        browser.find_element_by_id("toStationText").click()   # 点击获取终点
        val2 = r.hmget("name", self.tost)
        lt = str(val2[0], encoding='utf-8').split(',')
        print(lt)
        browser.find_element_by_id("nav_list{}".format(lt[1])).click()
        for i in range(int(lt[2])):
            if i == 0:
                continue
            else:
                browser.find_element_by_xpath("//a[contains(@class,'cityflip') and contains(.,'下一页')]").click()
        browser.find_element_by_xpath("//*[contains(@id,'ul_list{}')]//li[@data='{}']".format(lt[1], lt[0])).click()

        time.sleep(1)
        js = "document.getElementById('train_date').removeAttribute('readonly')"
        browser.execute_script(js)


        time.sleep(1)
        browser.find_element_by_id("train_date").clear()
        browser.find_element_by_id("train_date").send_keys('2018-05-05')

        time.sleep(2)
        browser.find_element_by_id("query_ticket").click()


        time.sleep(30)
        browser.quit()


c = Check("北京", "上海")
c.run()




