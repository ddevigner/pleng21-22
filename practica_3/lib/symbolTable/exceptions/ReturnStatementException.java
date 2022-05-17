/*********************************************************************************
 * Excepción utilizada al intentar declarar un símbolo con el mismo nombre
 * que alguno de los existentes en el nivel actual
 *
 * Fichero:    AlreadyDefinedSymbolException.java
 * Fecha:      02/03/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022, basado en código del 19-20
 **********************************************************************************/

package lib.symbolTable.exceptions;

import lib.symbolTable.Symbol.Types;

public class ReturnStatementException extends Exception {

	public boolean proc_or_func;
	public boolean heavy;
	public String error;

	public ReturnStatementException() {
		proc_or_func = false;
		this.error = "return statement in procedure.";
	}

	public ReturnStatementException(boolean hasReturn) {
		proc_or_func = true;
		heavy = false;
		this.error = "missing return statement in function.";
	}

	public ReturnStatementException(Types expected, Types got) {
		proc_or_func = true;
		heavy = true;
		this.error = "return " + expected + " function with " + got + " return"
			+ " statement."; 
	}
}
