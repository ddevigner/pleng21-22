import getpass
import os
import sys

code = 0
if len(sys.argv) > 1 and sys.argv[1] == "-b":
    user = getpass.getuser()
    target = ""
    if user == "Dede":
        target = "C:\tools\javacc\target"
    elif user == "a780131" or user == "a774631":
        target = "/home/" + user + "/apps/javacc/target"
    else:
        target = "/home/gabete/javacc/target"
    code = os.system("ant -Djavacchome=\"" + target + "\"")

if code == 0:
    os.system("java -jar dist/adac.jar dist/test.adac")
