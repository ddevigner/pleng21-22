/*********************************************************************************
 * Excepción utilizada al intentar utilizar un símbolo no definido en
 * la tabla de símbolos
 *
 * Fichero:    SymbolNotFoundException.java
 * Fecha:      02/03/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022, basado en código del 19-20
 **********************************************************************************/

package lib.symbolTable.exceptions;
import lib.symbolTable.Symbol.Types;
import lib.tools.SemanticFunctions.Operator;


public class MismatchedTypesException extends Exception {

	public String error;

	public MismatchedTypesException(Types expected, Types got) {
		if (expected == Types.UNDEFINED || got == Types.UNDEFINED)
			this.error = "could not evaluate expression."; 
		else
			this.error = "expected " + expected + " got " + got;
	}

	public MismatchedTypesException(Types fst) {
		if (fst == Types.UNDEFINED) this.error = "could not evaluate sign expression.";
		else this.error = "expression of type " + fst + " with sign.";
	}

	public MismatchedTypesException(String op_name, Types expected, Types f_got, 
		Types s_got)
	{
		if (f_got == Types.UNDEFINED || s_got == Types.UNDEFINED) {
			this.error = "could not evaluate binary operator '" + op_name + "''.";
		}
		else {
			this.error = "bad operand types for binary operator '" + op_name 
				+ "', expected ";
			if (expected == Types.UNDEFINED ) {
				this.error += "same type operators but got " + f_got + " and " 
					+ s_got + ".";
			} else if (expected == Types.INT) {
				this.error += "integer operators but got " + f_got 
					+ " and " + s_got + ".";
			} else if (expected == Types.BOOL) {
				this.error += "boolean operators but got " + f_got 
					+ " and " + s_got;
			}
		}
	}
}
