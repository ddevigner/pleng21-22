//*****************************************************************
// File:   SymbolBool.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   julio 2021
// Coms:   Atributos públicos para evitar el uso de getters y setters
//*****************************************************************

package lib.symbolTable;

public class SymbolBool extends Symbol implements Cloneable {
    public boolean value;

    public SymbolBool(String _name) {
        super(_name, Types.BOOL, ParameterClass.NONE ); 
        value = false;
    }

    public SymbolBool(String _name, ParameterClass _class) {
        super(_name, Types.BOOL, _class); 
        value = false;
    }

    public SymbolBool(String _name, boolean _value, ParameterClass _class) {
        super(_name, Types.BOOL, _class); 
    	value = _value;
    }

    public String toString() {
        return "(" + name + "," + type + "," + value + "," + parClass + "," + nivel + ")";
    }
    
    public SymbolBool clone () {
    	return (SymbolBool) super.clone(); 
    }
}
