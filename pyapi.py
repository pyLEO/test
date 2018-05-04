#! /usr/bin/python3 env
# -*- coding: utf-8 -*-

import urllib3
import requests
import base64
import json
url = 'http://api.pub.train.qunar.com/captcha/api/captcha.jsp?agentCode=hangt'
headers = {
    'Accept': 'text/xml;text/html',
    'Content-Type': 'text/xml;charset=utf-8'
}
http = urllib3.PoolManager()
with open(r'E:\PycharmProjects\test\b.jpg', 'rb') as f:
    ef = base64.b64encode(f.read())
#    ef = f.read()
#fs = {"data": str(ef, encoding='utf-8')}
res = http.request("POST", url, body=ef, headers={'Content-Type':'image/jpeg'})
print(res.data.decode("utf-8"))