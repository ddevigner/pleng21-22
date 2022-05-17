package lib.symbolTable.exceptions;
import lib.symbolTable.Symbol.Types;
 

public class MismatchedSymbolTypeException extends Exception {

    public Types expected;
    public Types got;

	public MismatchedSymbolTypeException(Types expected, Types got) {
        this.expected = expected;
        this.got = got;
    }
}