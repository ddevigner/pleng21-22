//*****************************************************************
// File:   SymbolChar.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   julio 2021
// Coms:   Atributos públicos para evitar el uso de getters y setters
//*****************************************************************

package lib.symbolTable;

public class SymbolChar extends Symbol implements Cloneable {
    public char value;

    public SymbolChar(String _name) {
    	super(_name, Types.CHAR, ParameterClass.NONE);
        value = '\0';
         
    }

     public SymbolChar(String _name, ParameterClass _class) {
    	super(_name, Types.CHAR, _class);
        value = '\0';
        
    }

    public SymbolChar(String _name, char _value, ParameterClass _class) {
    	super(_name, Types.CHAR, _class);
        value = _value;
    }

    public String toString() {
        return "(" + name + "," + type + "," + value + "," + parClass + "," + nivel + ")";
    }
    
    public SymbolChar clone () {
    	return (SymbolChar) super.clone(); 
    }
}
