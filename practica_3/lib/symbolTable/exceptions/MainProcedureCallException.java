package lib.symbolTable.exceptions;

public class MainProcedureCallException extends Exception {
    
    String main_name;

	public MainProcedureCallException(String main_name) {
        this.main_name = main_name;
    }

	@Override
    public String toString() {
        return "main procedure '" + main_name + "' can not be called.";
    }
}