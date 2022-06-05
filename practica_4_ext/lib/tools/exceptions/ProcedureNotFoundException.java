/******************************************************************************
 * Descripcion: excepción utilizada cuando un procedimiento existente es 
 *  utilizado de manera incorrecta.
 * Fichero:    ProcedureNotFoundException.java
 * Fecha:      17/05/2022
 * Versión:    v1.1
 * Asignatura: Procesadores de Lenguajes, curso 2021-2022.
 *****************************************************************************/
package lib.tools.exceptions;
import lib.symbolTable.Symbol.Types;
import lib.symbolTable.Symbol.ParameterClass;

public class ProcedureNotFoundException extends Exception {

    public String expected;
    public String got;
    public String info;

    public ProcedureNotFoundException() {}


    public ProcedureNotFoundException(String expected, String got) {
        this.expected = expected;
        this.got = got;
    }

    public ProcedureNotFoundException(String expected, String got, int expected_l,
        int got_l)
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched parameters length, in found symbol needs " 
            + expected_l + " but got " + got_l + ".";
    }

    public ProcedureNotFoundException(String expected, String got, Types expected_b, 
        Types got_b, int i)
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched base type in parameter " + i 
            + ", expected " + expected_b + " but got " + got_b + ".";
    }

    public ProcedureNotFoundException(String expected, String got, int i, 
        boolean symbol_type) 
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched type in parameter " + i + ", expected ";
        if (!symbol_type) this.info += "array but got simple variable.";
        else this.info += "simple variable but got array.";
    }

    public ProcedureNotFoundException(String expected, String got,
        ParameterClass expected_c, ParameterClass got_c, int i)
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched parameters class in parameter " + i 
            + ", expected " + expected_c + " but got " + got_c + ".";
    }

    public ProcedureNotFoundException(String expected, String got,
        int expected_ind, int got_ind, int i)
    {
        this.expected = expected;
        this.got = got;
        this.info = "mismatched array size in parameter " + i + ", expected " 
            + expected_ind + " but got " + got_ind + ".";
    }

    public ProcedureNotFoundException(String expected, String got, String info) {
        this.expected = expected;
        this.got = got;
        this.info = info;
    }

    
}