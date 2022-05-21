if [ $# -eq 0 ]; then exit 1; fi
if [ $1 -eq "a780131" ]; then dir="ingi/pleng"
else dir =""
fi
scp ${1}@central.cps.unizar.es:/home/${1}/${dir}