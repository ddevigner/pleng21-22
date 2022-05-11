//*****************************************************************
// File:   Attributes.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   enero 2022
//         Clase única para almacenar los atributos que pueden aparecer en el traductor de adac
//         Se hace como clase plana, por simplicidad. Los atributos que se pueden almacenar
//         se autoexplican con el nombre
//*****************************************************************

package lib.attributes;
import lib.symbolTable.*;
import lib.symbolTable.Symbol.Types;
import lib.symbolTable.Symbol.ParameterClass;
import lib.tools.SemanticFunctions;
import lib.tools.SemanticFunctions.Operator;
import java.util.ArrayList;
// import java.util.AbstractMap.SimpleEntry;

public class Attributes implements Cloneable {
    /** ATRIBUTOS ************************************************************/
    // Common Attributes.
    public String name;
    public Types type;
    public Types baseType;

    // Constants.
    public int valInt;
    public boolean valBool;
    public char valChar;
    public String valString;

    // Symbol information.
    public int line;
    public int column;

    // Methods Attributes.
    public boolean main;
    public ArrayList<Symbol> params;
    public ArrayList<Attributes> given;
    public boolean haveReturn;

    // Variables Attributes.
    public ParameterClass parClass;
    public int intVal;
    public boolean boolVal;
    public char charVal;
    public String stringVal;

    // Expressions Attributes.
    public Operator op;


    /** CONSTRUCTORES ********************************************************/
    // Empty Attributes.
    public Attributes() {
        name = "";
        type = baseType = Types.UNDEFINED;
        main = false;
        params = null;
        given = new ArrayList<>();
        haveReturn = false;
        parClass = ParameterClass.NONE;
    }

       // Method attributes.
       public Attributes(ArrayList<Symbol> params) {
        this.params = params;
    }

    // Method attributes.
    public Attributes(Types methodType, Types returnType, ArrayList<Symbol> params) {
        this.type = methodType;
        this.baseType = returnType;
        this.main = false;
        this.params = params;
        haveReturn = false;
    }

    public Attributes(Types methodType, Types returnType, ArrayList<Symbol> params, boolean main) {
        this.type = methodType;
        this.baseType = returnType;
        this.main = main;
        this.params = params;
        haveReturn = false;
    }

    /** METODOS **************************************************************/
    // Clone Attributes.
    public Attributes clone() {
    	try { return (Attributes) super.clone(); }
    	catch (CloneNotSupportedException e) { return null; }
    }

    public void initInt(String value) {
        baseType = Types.INT;
        parClass = ParameterClass.VAL;
        intVal = Integer.parseInt(value);
    }

    public void initChar(String value) {
        baseType = Types.CHAR;
        parClass = ParameterClass.VAL;
        charVal = value.charAt(0);
    }

    public void initBool(String value) {
        baseType = Types.BOOL;
        parClass = ParameterClass.VAL;
        boolVal = value.equals("true") ? true : false;
    }

    public void initString(String value) {
        baseType = Types.STRING;
        parClass = ParameterClass.VAL;
        stringVal = value;
    }

    // Attributes to string.
    public String toString(String n) {
        return "ATTRIBUTES " + n + ":\n" +
            "\tNAME: " + name + "\n" +
            "\tTYPE: " + type + "\n" +
            "\tBASETYPE: " + baseType + "\n" +
            "\tPARAMS: " + params + "\n" +
            "\tHAVERETURN: " + haveReturn + "\n" +
            "\tintVal: " + intVal + "\n" +
            "\tboolVal: " + boolVal + "\n" +
            "\tcharVal: " + charVal + "\n" +
            "\tstringVal: " + stringVal + "\n";
    }
}
