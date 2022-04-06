#!/usr/bin/python3
# -*- coding: utf-8 -*-
"""
*****************************************************************
File:   test_adac.py
Author: Procesadores de lenguajes. Univ. de Zaragoza
Date:   marzo 2022
Coms:   
    Estructura para la prueba: en un directorio
    |--- adac_comp  (contiene las herramientas: adaccomp, ensamblador, interprete)
    |--- test_adac.py    (este script)

        Ejecutar como
            python3 test_adac.py <path_adac.jar> <path_fuentes_adac>
        Alternativamente, si se le han dado permisos de ejecución
            ./test_adac.py <path_adac.jar> <path_fuentes_adac>
*****************************************************************
"""
import os
import sys
import shutil

#----------------------------------------------------------------
#Comprueba que se invoque con dos parámetros
if len(sys.argv) != 3:
    print('------------------------------------------')
    sys.exit('Invocar como: ' + sys.argv[0] + ' <path_adac.jar> <path_fuentes_adac>') 
else:
    traductor = sys.argv[1]
    path_fuentes = sys.argv[2]

fuentes = os.listdir(path_fuentes)
# solo nos interesan los que tengan extensión '.adac'
fuentes_adac = [f for f in fuentes if os.path.splitext(f)[1] == '.adac']

for f in fuentes_adac:
    try:
        print('\n====================================')
        res = os.system('java -jar ' + traductor + ' ' + os.path.join(path_fuentes,f))
        print('\n========== ' + f + ' ==========')
        #res = input('\nReturn para continuar')
    except Exception as e:
        print(e)
    