/******************************************************************************
 * Descripcion: excepción utilizada a la hora de declarar el tipo devuelto de
 * 	la funcion, sea si se define en un procedimiento o falta en una funcion.
 * Fichero:    ReturnTypeException.java
 * Fecha:      17/05/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022.
 *****************************************************************************/
package lib.tools.exceptions;

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
