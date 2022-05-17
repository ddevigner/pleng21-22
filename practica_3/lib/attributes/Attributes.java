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
    public Types vectorBaseType;

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
    public boolean method;
    public ArrayList<Symbol> params;
    public ArrayList<Attributes> given;
    public boolean hasReturn;

    // Variables Attributes.
    public int maxInd;
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
        type = baseType = vectorBaseType = Types.UNDEFINED;
        main = false;
        params = null;
        given = new ArrayList<>();
        hasReturn = false;
        parClass = ParameterClass.NONE;
    }

    // Method attributes.
    public Attributes(boolean method) {
        this.method = method;
    }

    public Attributes(ArrayList<Symbol> params) {
        this.params = params;
    }

    public Attributes(Types methodType, Types returnType, ArrayList<Symbol> params) {
        this.type = methodType;
        this.baseType = returnType;
        this.main = false;
        this.params = params;
        hasReturn = false;
    }

    public Attributes(Types methodType, Types returnType, ArrayList<Symbol> params, boolean main) {
        this.type = methodType;
        this.baseType = returnType;
        this.main = main;
        this.params = params;
        hasReturn = false;
    }

    // Variable attributes.
    public Attributes(ParameterClass parClass) {
        this.parClass = parClass;
    }

    /** METODOS **************************************************************/
    // Clone Attributes.
    public Attributes clone() {
    	try { return (Attributes) super.clone(); }
    	catch (CloneNotSupportedException e) { return null; }
    }

    // Attributes to string.
    public String toString(String n) {
        return "ATTRIBUTES " + n + ":\n" +
            "\tNAME: " + name + "\n" +
            "\tTYPE: " + type + "\n" +
            "\tBASETYPE: " + baseType + "\n" +
            "\tPARAMS: " + params + "\n" +
            "\tHASRETURN: " + hasReturn + "\n" +
            "\tintVal: " + intVal + "\n" +
            "\tboolVal: " + boolVal + "\n" +
            "\tcharVal: " + charVal + "\n" +
            "\tstringVal: " + stringVal + "\n";
    }

    public String toParam() {
        if (type == Types.UNDEFINED && baseType == Types.UNDEFINED) return "<undefined>, ";
        else {
            String par_str = parClass + " ";
            if (type == Types.ARRAY) par_str += toArray();
            else {
                par_str += baseType;
                if (name != null) par_str += name; 
            }
            return par_str + ", ";
        }
    }

    public String toArray() {
        return baseType + " " + name + "[0.." + maxInd + "]"; 
    }

    public String toFunction() {
        String f_str = name + "(";
        for (Attributes i : given) f_str += i.toParam(); 
        return (f_str.substring(0, f_str.length()-2) + ") -> " + baseType);
    }

    public String toProcedure() {
        String p_str = name + "(";
        for (Attributes i : given) p_str += i.toParam();
        return (p_str.substring(0, p_str.length()-2) + ")");
    }
}
