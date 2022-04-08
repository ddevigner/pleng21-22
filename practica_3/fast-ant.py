import getpass
import os

user = getpass.getuser()
target = ""
if user == "Dede":
    target = "C:\tools\javacc\target"
elif user == "gabete":
    target = "/home/gabete/javacc/target"
elif user == "a780131" or user == "a774631":
    target = f"/home/{user}/apps/javacc/target"

os.system(f"ant -Djavacchome=\"{target}\"")
os.system("java -jar dist\\adac.jar dist\\test.adac")
