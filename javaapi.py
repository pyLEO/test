#! /usr/bin/env/python3
# -*- coding: utf-8 -*-


from jpype import *


startJVM(r"E:\Program Files\Java\jdk1.7.0_80\jre\bin\server\jvm.dll", "-ea")

java.lang.System.out.println("hello world")

shutdownJVM()

