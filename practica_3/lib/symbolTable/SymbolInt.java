//*****************************************************************
// File:   SymbolInt.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   julio 2021
// Coms:   Atributos públicos para evitar el uso de getters y setters
//*****************************************************************

package lib.symbolTable;

public class SymbolInt extends Symbol implements Cloneable {
    public int value;
    
    public SymbolInt(String _name) {
    	super(_name, Types.INT, ParameterClass.NONE); 
    	value = 0;
    }

    public SymbolInt(String _name, ParameterClass _class) {
    	super(_name, Types.INT, _class); 
    	value = 0;
    }

    public SymbolInt(String _name, int _value, ParameterClass _class) {
    	super(_name, Types.INT, _class); 
        value = _value;
    }

    public String toString() {
        return "(" + name + "," + type + "," + value + "," + parClass + "," + nivel + ")";
    }
    
    public SymbolInt clone () {
    	return (SymbolInt) super.clone(); 
    }
    
}
