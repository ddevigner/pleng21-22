//*****************************************************************
// File:   SymbolBool.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   julio 2021
// Coms:   Atributos públicos para evitar el uso de getters y setters
//*****************************************************************

package lib.symbolTable;

public class SymbolBool extends Symbol implements Cloneable {
    public boolean value;

    public SymbolBool(String _name, int _line, int _column) {
        super(_name, Types.BOOL, ParameterClass.NONE, _line, _column ); 
        value = false;
    }

    public SymbolBool(String _name, ParameterClass _class, int _line, int _column) {
        super(_name, Types.BOOL, _class, _line, _column); 
        value = false;
    }

    public SymbolBool(String _name, boolean _value, ParameterClass _class, int _line, int _column) {
        super(_name, Types.BOOL, _class, _line, _column); 
    	value = _value;
    }

    public String toString() {
        return "(" + name + "," + type + "," + value + "," + parClass + "," 
                + nivel + "," + line + "," + column + ")";
    }
    
    public SymbolBool clone () {
    	return (SymbolBool) super.clone(); 
    }
}
