/******************************************************************************
 * Descripcion: excepción utilizada a la hora de declarar un return dentro del
 * 	cuerpo de una funcion, tanto si se declará en un procedimiento o falta
 *  de declararse en una funcion.
 * Fichero:    ReturnStatementException.java
 * Fecha:      17/05/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022.
 *****************************************************************************/
package lib.tools.exceptions;
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
