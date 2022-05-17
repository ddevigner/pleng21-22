/*********************************************************************************
 * Excepción utilizada al intentar utilizar un símbolo no definido en
 * la tabla de símbolos
 *
 * Fichero:    FunctionNotFoundException.java
 * Fecha:      02/03/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022, basado en código del 19-20
 **********************************************************************************/
package lib.symbolTable.exceptions;
import lib.symbolTable.Symbol.Types;
import lib.symbolTable.Symbol.ParameterClass;

public class FunctionNotFoundException extends Exception {

    public String expected;
    public String got;
    public String info;

	public FunctionNotFoundException(String expected, String got) {
        this.expected = expected;
        this.got = got;
    }

    public FunctionNotFoundException(String expected, String got, int expected_l, 
        int got_l) 
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched parameters length, found symbol needs " 
            + expected_l + " but got " + got_l + ".";
    }

    public FunctionNotFoundException(String expected, String got, Types expected_c, 
        Types got_c, int i)
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched base type in parameter " + i 
            + ", expected " + expected_c + " but got " + got_c + ".";
    }

    public FunctionNotFoundException(String expected, String got, int i, 
        boolean symbol_type) 
    { 
       this.expected = expected;
       this.got = got;
       this.info = "mismatched type in parameter " + i + ", expected ";
       if (!symbol_type) this.info += "array but got simple variable.";
       else this.info += "simple variable but got array.";
    }

    public FunctionNotFoundException(String expected, String got, 
        ParameterClass expected_c, ParameterClass got_c, int i) 
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched parameters class in parameter " + i 
            + ", expected " + expected_c + " but got " + got_c + ".";
    }

    public FunctionNotFoundException(String expected, String got, 
        int expected_ind, int got_ind, int i) 
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched array size in parameter " + i + ", expected " 
            + expected_ind + " but got " + got_ind + ".";
    }

    public FunctionNotFoundException(String expected, String got, String info) {
        this.expected = expected;
        this.got = got;
        this.info = info;
    }
    
}