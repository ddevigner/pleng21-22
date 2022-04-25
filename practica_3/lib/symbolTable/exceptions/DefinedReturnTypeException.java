/*********************************************************************************
 * Excepción utilizada al intentar utilizar un símbolo no definido en
 * la tabla de símbolos
 *
 * Fichero:    ReturnTypeDefinedException.java
 * Fecha:      02/03/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022, basado en código del 19-20
 **********************************************************************************/

package lib.symbolTable.exceptions;

public class DefinedReturnTypeException extends Error {

    boolean isProc;

	public DefinedReturnTypeException(boolean isProc) {
        this.isProc = isProc;
    }

    @Override
    public String toString() {
        return (isProc) ? "Error -- Defining a return type in a procedure?" :
           "Error -- Forgetting something maybe? What about a return type?";
    }
}
