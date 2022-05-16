package lib.symbolTable.exceptions;
import lib.symbolTable.Symbol.Types;
 

public class MismatchedSymbolTypeException extends Exception {

    public Types symbol_type;
    public Types match_type;

	public MismatchedSymbolTypeException(Types symbol_type, Types match_type) {
        this.symbol_type = symbol_type;
        this.match_type = match_type;
    }
}