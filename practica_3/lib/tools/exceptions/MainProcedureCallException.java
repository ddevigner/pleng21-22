/******************************************************************************
 * Descripcion: excepción utilizada cuando se realiza una llamada al procedi-
 *  miento principal.
 * Fichero:    MainProcedureCallException.java
 * Fecha:      17/05/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022.
 *****************************************************************************/
package lib.tools.exceptions;

public class MainProcedureCallException extends Exception {
    
    public String error;

	public MainProcedureCallException(String main_name) {
        this.error = "main procedure '" + main_name + "' can not be called.";
    }
}