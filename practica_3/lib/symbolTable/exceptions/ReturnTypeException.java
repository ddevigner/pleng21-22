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

public class ReturnTypeException extends Exception {

	public boolean proc_or_func;
	public String error;

	public ReturnTypeException() {
		this.proc_or_func = false;
		this.error = "return type statement in procedure declaration.";
	}

	public ReturnTypeException(boolean proc_or_func) {
		this.proc_or_func = proc_or_func;
		this.error = "missing return type in function declaration";
	}
}
