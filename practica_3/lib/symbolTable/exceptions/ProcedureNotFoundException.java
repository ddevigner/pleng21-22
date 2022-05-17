package lib.symbolTable.exceptions;

public class ProcedureNotFoundException extends Exception {

    public String expected;
    public String got;
    public String info;

    public ProcedureNotFoundException(String expected, String got) {
        this.expected = expected;
        this.got = got;
    }

    public ProcedureNotFoundException(String expected, String got, String info) {
        this.expected = expected;
        this.got = got;
        this.info = info;
    }
    
}