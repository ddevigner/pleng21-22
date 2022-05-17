package lib.symbolTable.exceptions;

public class MainProcedureCallException extends Exception {
    
    public String error;

	public MainProcedureCallException(String main_name) {
        this.error = "main procedure '" + main_name + "' can not be called.";
    }
}