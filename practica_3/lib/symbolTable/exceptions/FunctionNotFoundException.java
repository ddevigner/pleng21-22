/*********************************************************************************
 * Excepción utilizada al intentar utilizar un símbolo no definido en
 * la tabla de símbolos
 *
 * Fichero:    FunctionNotFoundException.java
 * Fecha:      02/03/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022, basado en código del 19-20
 **********************************************************************************/
package lib.symbolTable.exceptions;

public class FunctionNotFoundException extends Exception {

    public String expected;
    public String got;

	public FunctionNotFoundException(String expected, String got) {
        this.expected = expected;
        this.got = got;
    }
    
}