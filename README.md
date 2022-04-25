# PROCESADORES DEL LENGUAJE 2021-22
## 1. Material adicional de soporte
### Documentacion
- [xamples](https://github.com/ddevigner/pleng21-22/tree/main/doc/xamples): ejemplos en adac.
- [javacc_intro](https://github.com/ddevigner/pleng21-22/blob/main/doc/javacc_intro.pdf): introduccion a javacc.
- [javacc_intro_xample](https://github.com/ddevigner/pleng21-22/blob/main/doc/javacc_intro_xample.zip): un primer programa compilado de JavaCC de contacto.
- [javacc_xamples](https://github.com/ddevigner/pleng21-22/blob/main/doc/javacc_xamples.zip): archivos javacc de ejemplo.
- [integer calculator](https://github.com/ddevigner/pleng21-22/blob/main/doc/calc_enteros_sint.zip): calculadora de enteros implementada con JavaCC (analizador lexico + sintactico).
- [wc](https://github.com/ddevigner/pleng21-22/tree/main/doc/wc): comando wc implementado con JavaCC.

### Ejecutables. Compilados para Hendrix.
- [adac_cpp](https://github.com/ddevigner/pleng21-22/tree/main/doc/adac_cpp): compilador de adac a C++.
    ```bash
    java -jar adac_cpp.jar <fichero>.adac
    ```

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

- [adaccomp](https://github.com/ddevigner/pleng21-22/blob/main/doc/adaccomp): compilador de adac a Maquina P. Genera un archivo .pcode.
    ```bash
    adaccomp <fichero>.adac
    ```

- [ensamblador](https://github.com/ddevigner/pleng21-22/blob/main/doc/ensamblador): ensamblador de Maquina P a codigo binario. Genera un binario.
    ```bash
    ensamblador <fichero>.pcode
    ```

- [maquinap](https://github.com/ddevigner/pleng21-22/blob/main/doc/maquinap): ejecuta un binario de Maquina P.
    ```bash
    maquinap <binario>
    ```

## 2. Practicas
- [Practica 1](https://github.com/ddevigner/pleng21-22/tree/main/practica_1): implementacion del analizador lexico del lenguaje ADAC.
- [Practica 2](https://github.com/ddevigner/pleng21-22/tree/main/practica_2): implementacion del analizador sintactico del lenguaje ADAC.
- [Practica 3](https://github.com/ddevigner/pleng21-22/tree/main/practica_3): implementacion del analizador semantico del lenguaje ADAC.
