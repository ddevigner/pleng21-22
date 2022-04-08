Práctica 2: Batería de tests para el analizador sintactico para "adac"
Sergio Gabete César (774631) & Devid Dokash (780131).

Test 1:
En el primer test se han creado variables con distintos nombre y algunas con palabras reservadas pero en mayúsculas para ver que
no da error esos nombres. Se han creado dos funciones: una recibe por referencia una variable character y modifica su valor dentro de la funcion,
la otra función incrementa un contador hasta que sea igual al valor de n pasado por valor, el contador incrementa sumandole el segundo parámetro de 
la función.
En el programa principal se mustran varias lineas con put_line, se modifica el valor de la variable mensaje y se obtiene el valor
de la función sum para las variables i,j y se muestran por pantalla.

Test 2: 
Hay una función llamada muestreo_informacion que recibe un entero por por valor y muestra ese valor convirtiendolo a character
con int2char() directamente dentro de la función put_line.
La función pedir_dato solicita un número al usuario hasta que se introduzca un valor entre 1 y 100.
La función dame_informacion2 hace los mismo pero el dato no puede ser menor o igual que 0 y en vez de llamar a la función get() llama a la 
función pedir_dato().
En el programa principal se llama a la función dame_información() y se muestra con muestreo_informacion().

Test 3: 
En el test 3 la función es_palabra() comprueba si el character pasado en los argumentos es igual a 'c' y mostrará un mensaje por pantalla
dependiendo del valor. En el programa principal se pide al usuario que introduzca un caracter, llama a la función es_palabra() para 
mostrar si el usuario ha acertado o no y acaba si ha acertado.

Test 4:
La función p2() toma dos parametros, uno por referencia y oto por valor, en caso de que sea iguales muestra uno en forma de entero y de caracter
con put_line. Si r1 es menor que r2 se ejecuta un while y se va sumando 1 a r1 hasta que tenga el valor de r1 y se hace return del valor.
En la funcion p3() se van rellenando posiciones del vector pasado por referencia hasta que el contador llegue a 100. Se va incrementando cada
vez multiplicando por 2 y se introduce un entero pasado a caracter.
En la función p4() se devuelve un booleano dependiendo de los valores de sus parametros a y b. 
En el programa principal se comprueba si pasando (100,100) como parámetros se cumple la condición de p4() y sino se ejcutaan p3() y p2().

Test 5: 
La función ponerLadrillo() toma un entero por valor y un booleano por referencia. Ejecuta un bucle mostrando por pantalla que ha puesto un ladrillo
hasta alcanzar el valor del parametro y pone el booleano pasado por referencia a true.
En el programa principal se pide al usuario que introduzca un numero de ladrillos y se llama a ponerLadrillos().


