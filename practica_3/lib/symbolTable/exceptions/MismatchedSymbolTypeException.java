package lib.symbolTable.exceptions;
import lib.symbolTable.Symbol.Types;
 

public class MismatchedSymbolTypeException extends Exception {

    public String error;

	public MismatchedSymbolTypeException(String name, Types expected, Types got) {
        if (expected == Types.UNDEFINED) {
			this.error = "symbol '" + name + "' expected to be a simple " 
                + "variable but got a(n) " + got;
        } else if(got == Types.UNDEFINED) {
            this.error = "symbol '" + name + "' expected to be a(n) " + expected
                + "but got a simple variable";
        } else {
            this.error = "symbol '" + name + "' expected to be a(n) " + expected 
                + ", but got a(n) " + got;
		}
    }
}