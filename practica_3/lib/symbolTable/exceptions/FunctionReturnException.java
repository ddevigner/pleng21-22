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

public class FunctionReturnException extends Exception {

	public boolean hasReturn;
	public Types expected;
	public Types got;

	public FunctionReturnException() {
		hasReturn = false;
	}

	public FunctionReturnException(Types expected, Types got) {
		hasReturn = true;
		this.expected = expected;
		this.got = got;
	}
}
