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

public class MismatchedTypesException extends Exception {

	public Types expected;
	public Types got;


	public MismatchedTypesException(Types expected, Types got) {
		this.expected = expected;
		this.got = got;
	}
}