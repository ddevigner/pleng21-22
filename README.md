# Material adicional de soporte
## Documentacion
- [xamples](https://github.com/ddevigner/pleng21-22/tree/main/doc/xamples): ejemplos en adac.
- [javacc_intro](): introduccion a javacc.
- [javacc_intro_xample](): un primer programa compilado de JavaCC de contacto.
- [javacc_xamples](): archivos javacc de ejemplo.
- [int calculator](): calculadora de enteros implementada con JavaCC (analizador lexico + sintactico).
- [wc](): comando wc implementado con JavaCC.

## Ejecutables: compilados para Hendrix.
- adac_cpp: compilador de adac a C++.
    ```bash
    java -jar adac_cpp.jar <fichero>.adac
    ```

- text_stats.adac: estima numero de caracteres, palabras y lineas de un fichero de texto.
    ```bash
    adaccomp text_stats.adac
    ensamblador text_stats.pcode
    maquinap text_stats < <fichero>
    ```

- invertir_pgm.adac: carga de stdin una imagen en blanco y negro, formato PGM sin comentarios, y guarda en stdout la imagen invertida, en formato PGM sin comentarios
    ```bash
    adaccomp invertir_pgm.adac
    ensamblador invertir_pgm.pcode
    maquinap invertir_pgm < <imagen_entrada>.pgm > <imagen_salida>.pgm
    ```

- einstein.pgm: imagen binaria en formato PGM sin comentarios.

- adaccomp: compilador de adac a Maquina P. Genera un archivo .pcode.
    ```bash
    adaccomp <fichero>.adac
    ```

- ensamblador: ensamblador de Maquina P a codigo binario. Genera un binario.
    ```bash
    ensamblador <fichero>.pcode
    ```

- maquinap: ejecuta un binario de Maquina P.
    ```bash
    maquinap <binario>
    ```

## Practicas:
- Practica 1: implementacion del analizador lexico de lenguaje ADAC.
- Practica 2: implementacion del analizador sintactico de lenguaje ADAC.

