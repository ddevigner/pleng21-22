/******************************************************************************
 * Descripcion: excepción utilizada cuando el uso del Get es incorrecto.
 * Fichero:    GetException.java
 * Fecha:      17/05/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022.
 *****************************************************************************/
package lib.tools.exceptions;
import lib.symbolTable.Symbol.Types;

public class GetException extends Exception {

    public Types type;
    
    public GetException (Types type) {
        this.type = type;
    }
}