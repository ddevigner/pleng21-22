# PROCESADORES DEL LENGUAJE 2021-22
## 1. Practicas
- [Practica 1](https://github.com/ddevigner/pleng21-22/tree/main/practica_1): implementacion del analizador lexico del lenguaje ADAC.
- [Practica 2](https://github.com/ddevigner/pleng21-22/tree/main/practica_2): implementacion del analizador sintactico del lenguaje ADAC.
- [Practica 3](https://github.com/ddevigner/pleng21-22/tree/main/practica_3): implementacion del analizador semantico del lenguaje ADAC.
- [Practica 4](https://github.com/ddevigner/pleng21-22/tree/main/practica_4): implementacion de la generacion de codigo del lenguaje ADAC, diseño completo del compilador.

## 2. Material adicional de soporte
### Entorno de ejecución. Compilados para su uso en Hendrix.
- [adac_cpp](https://github.com/ddevigner/pleng21-22/tree/main/testbench/adac_cpp/): compilador que traduce de adac a C++.
    ```bash
    # Entrada estandar.
    java -jar adac_cpp.jar

    # Lee del fichero Adac. 
    java -jar adac_cpp.jar <fichero>.adac [> <fichero_output>]
    ```

- [adaccomp](https://github.com/ddevigner/pleng21-22/blob/main/testbench/adaccomp): compilador que traduce de adac (<file>.adac) a código P (<file>.pcode).
    ```bash
    adaccomp <fichero> [-v] [-r] [-o] [-x] [-c]
    # -v: verbose. En el fichero .pcode se incluye un comentario indicando el numero de cada p-instruccion.
    # -r: run time checks. Se genera código para comprobar durante la ejecución los valores de los indices de acceso a vectores.
    # -o: optimize for speed. Se genera código p más eficiente, incluyendo evaluación de expresiones constantes.
    # -x: xml. Se genera el fichero .pcode con formato xml.
    # -c: comments. Se incluyen comentarios en el fichero .pcode.
    ```

- [ensamblador](https://github.com/ddevigner/pleng21-22/blob/main/doc/ensamblador): ensamblador de Maquina P a codigo binario. Genera un binario.
    ```bash
    ensamblador <fichero>.pcode
    ```

- [maquinap](https://github.com/ddevigner/pleng21-22/blob/main/doc/maquinap): ejecuta un binario de Maquina P.
    ```bash
    maquinap <binario>
    ```
