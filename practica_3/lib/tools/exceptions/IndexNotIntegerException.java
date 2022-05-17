/******************************************************************************
 * Descripcion: excepción utilizada cuando se intenta acceder a un elemento de
 *  un vector con una expresion no entera.
 * Fichero:    IndexNotIntegerException.java
 * Fecha:      17/05/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022.
 *****************************************************************************/
package lib.tools.exceptions;
import lib.symbolTable.Symbol.Types;

public class IndexNotIntegerException extends Exception {

    public Types index_type;

	public IndexNotIntegerException(Types index_type) {
        this.index_type = index_type;
    }

}
