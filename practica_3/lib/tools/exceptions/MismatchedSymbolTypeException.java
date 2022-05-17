/******************************************************************************
 * Descripcion: excepción utilizada cuando se utiliza un simbolo de un tipo
 *  como otro tipo (por ejemplo: se utiliza un simbolo funcion como array).
 * Fichero:    MismatchedSymbolTypeException.java
 * Fecha:      17/05/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022.
 *****************************************************************************/
package lib.tools.exceptions;
import lib.symbolTable.Symbol.Types;

public class MismatchedSymbolTypeException extends Exception {

    public String error;

	public MismatchedSymbolTypeException(String name, Types expected, Types got) {
        if (expected == Types.UNDEFINED) {
			this.error = "symbol '" + name + "' expected to be a simple " 
                + "variable but got a(n) " + got;
        } else if(got == Types.UNDEFINED) {
            this.error = "symbol '" + name + "' expected to be a(n) " + expected
                + "but got a simple variable";
        } else {
            this.error = "symbol '" + name + "' expected to be a(n) " + expected 
                + ", but got a(n) " + got;
		}
    }
}