package lib.symbolTable.exceptions;
import lib.tools.SemanticFunctions.Procedure;
import lib.Symbol.Types;

public class ProcedureNotFoundException extends Error {

    Types type;

    public ProcedureNotFoundException() {}

	public ProcedureNotFoundException(Types type) {
        this.type = type;
    }
}