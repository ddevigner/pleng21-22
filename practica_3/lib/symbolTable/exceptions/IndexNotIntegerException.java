package lib.symbolTable.exceptions;
import lib.symbolTable.Symbol.Types;

public class IndexNotIntegerException extends Exception {

    public Types index_type;

	public IndexNotIntegerException(Types index_type) {
        this.index_type = index_type;
    }

}
