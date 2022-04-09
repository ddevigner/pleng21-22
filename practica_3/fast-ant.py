import getpass
import os

user = getpass.getuser()
target = ""
if user == "Dede":
    target = "C:\tools\javacc\target"
elif user == "a780131" or user == "a774631":
    target = "/home/" + user + "/apps/javacc/target"
else:
    target = "/home/gabete/javacc/target"

os.system("ant -Djavacchome=\"" + target + "\"")
os.system("java -jar dist/adac.jar dist/test.adac")
