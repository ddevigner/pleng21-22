package lib.symbolTable.exceptions;

public class ProcedureNotFoundException extends Error {

    int procedure, kind;

    public ProcedureNotFoundException() {}

	public ProcedureNotFoundException(int procedure, int kind) {
        this.procedure = procedure;
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "ProcedureNotFoundException message not defined yet.";
    }
}