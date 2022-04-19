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
import lib.tools.SemanticFunctions;
// import java.util.ArrayList;
// import java.util.AbstractMap.SimpleEntry;

public class Attributes implements Cloneable {
    public Symbol.Types type;
    public Symbol.ParameterClass parClass;
    public SemanticFunctions.Operator opType;
    public String name;

    // Constants.
    public int valInt;
    public boolean valBool;
    public char valChar;
    public String valString;

    // Symbol information.
    public int line;
    public int column;

    public Attributes() {
        //COMPLETAR
    }

    public Attributes(SemanticFunctions.Operator opType, int line, int column) {
        this.opType = opType;
        this.line = line;
        this.column = column;
    }

    public Attributes clone() {
    	try {
    		return (Attributes) super.clone();
    	}
    	catch (CloneNotSupportedException e) {
    		return null; 
    	}
    }

    public String toString() {
        return
            "type = " + type + "\n" +
            "parClass = " + parClass + "\n" ;
            //COMPLETAR
        
    }
}
