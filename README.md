# PROCESADORES DEL LENGUAJE 2021-22
## 1. Practicas
- [Practica 1](https://github.com/ddevigner/pleng21-22/tree/main/practica_1): implementacion del analizador lexico del lenguaje ADAC.
- [Practica 2](https://github.com/ddevigner/pleng21-22/tree/main/practica_2): implementacion del analizador sintactico del lenguaje ADAC.
- [Practica 3](https://github.com/ddevigner/pleng21-22/tree/main/practica_3): implementacion del analizador semantico del lenguaje ADAC.
- [Practica 4](https://github.com/ddevigner/pleng21-22/tree/main/practica_4): implementacion de la generacion de codigo del lenguaje ADAC, diseño completo del compilador.

## 2. Material adicional de soporte
### Programas. Compilados para su uso en Hendrix.
- [adac_cpp](https://github.com/ddevigner/pleng21-22/tree/main/testbench/adac_cpp/): compilador que traduce de adac a C++.
    ```bash
    # Entrada estandar.
    java -jar adac_cpp.jar

    # Lee del fichero Adac. 
    java -jar adac_cpp.jar <fichero>.adac [> <fichero_output>]
    ```

- [adaccomp](https://github.com/ddevigner/pleng21-22/blob/main/testbench/adaccomp): compilador que traduce de adac (<file>.adac) a código P (<file>.pcode).
    ```bash
    adaccomp <fichero> [-v] [-r] [-o]
    # -v: verbose. En el fichero .pcode se incluye un comentario indicando el numero de cada p-instruccion.
    ```

- [ensamblador](https://github.com/ddevigner/pleng21-22/blob/main/doc/ensamblador): ensamblador de Maquina P a codigo binario. Genera un binario.
    ```bash
    ensamblador <fichero>.pcode
    ```

- [maquinap](https://github.com/ddevigner/pleng21-22/blob/main/doc/maquinap): ejecuta un binario de Maquina P.
    ```bash
    maquinap <binario>
    ```

### Banco de tests:
- [text_stats.adac](https://github.com/ddevigner/pleng21-22/blob/main/doc/text_stats.adac): estima numero de caracteres, palabras y lineas de un fichero de texto.
    ```bash
    adaccomp text_stats.adac
    ensamblador text_stats.pcode
    maquinap text_stats < <fichero>
    ```

- [invertir_pgm.adac](https://github.com/ddevigner/pleng21-22/blob/main/doc/invertir_pgm.adac): carga de stdin una imagen en blanco y negro, formato PGM sin comentarios, y guarda en stdout la imagen invertida, en formato PGM sin comentarios
    ```bash
    adaccomp invertir_pgm.adac
    ensamblador invertir_pgm.pcode
    maquinap invertir_pgm < <imagen_entrada>.pgm > <imagen_salida>.pgm
    ```

- [einstein.pgm](https://github.com/ddevigner/pleng21-22/blob/main/doc/einstein.pgm): imagen binaria en formato PGM sin comentarios.

### Proyectos de ejemplo:
- [Calculadora de enteros](https://github.com/ddevigner/pleng21-22/tree/main/material/calc_enteros): diferentes fases de una calculadora de enteros implementada con JavaCC.
- [Comando wc](https://github.com/ddevigner/pleng21-22/tree/main/material/wc): comando wc completamente implementado en JavaCC.
- [JavaCC first proyect](https://github.com/ddevigner/pleng21-22/tree/main/material/javacc_first_contact): un primer proyecto de contacto implementado y compilado en JavaCC.
- [JavaCC syntax files](https://github.com/ddevigner/pleng21-22/tree/main/material/javacc_syntax_examples): archivos de ejemplo que muestran la implementacion de un fichero JavaCC y su sintaxis.
