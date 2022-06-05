Sergio Gabete César (774631) & Devid Dokash (780131).
Práctica 4: Construcción completa de un compilador de lenguaje "adac".
Blah, blah, blah

Para la recuperacion de errores se ha optado por el metodo en modo panico en dos 
niveles, un primer nivel por instruccion (;) y otro superior a nivel de bloque (end).

El nivel optado del analizador semantico es de nivel 4, el lenguaje permite uso
de parametros esclares y de vectores, sea por valor como por referencia en
procedimientos y funciones. El nivel mas completo.

Organizacion de clases:
lib
|--- attributes/Attributes.java: clase que guarda datos generales para traspasarlos entre diferentes niveles del analisis semantico.
|--- errores/ErrorSemantico.java: guarda registro de errores y warnings y detecta los diferentes errores mostrandolos por pantalla.
|--- symbolTable:
     |--- exceptions: excepciones de la tabla de simbolos.
     |--- Clases que implementan los diferentes tipos de simbolos.
|--- tools:
     |--- exceptions: excepciones del analisis semantico, los nombres de los archivos son autoexplicativos.
     |--- SemanticFunctions.java: implementa funciones de analisis semantico y detecta errores.
|--- traductor/adac_4.jj: analizador semantico.

ERRORES QUE NO HAN PODIDO AVERIGUARSE:
-> Extrañamente, si un String contenía el caracter '>' no permitía compilar el 
pcode.