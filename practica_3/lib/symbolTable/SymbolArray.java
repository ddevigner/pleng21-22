//*****************************************************************
// File:   SymbolArray.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   julio 2021
// Coms:   Atributos públicos para evitar el uso de getters y setters
//*****************************************************************

package lib.symbolTable;

public class SymbolArray extends Symbol implements Cloneable {
    public int minInd;
    public int maxInd;
    public Types baseType;

    public SymbolArray(String _name, int _line, int _column) {
        super(_name, Types.ARRAY, ParameterClass.NONE, _line, _column); 
    	minInd = -1;
        maxInd = -1;
        baseType = Types.UNDEFINED;
    }

    public SymbolArray(String _name, int _minInd, int _maxInd, Types _baseType, int _line, int _column) {
        super(_name, Types.ARRAY, ParameterClass.NONE, _line, _column);
        minInd = _minInd;
        maxInd = _maxInd;
        baseType = _baseType; 
    }

    public SymbolArray(String _name, int _minInd, int _maxInd, Types _baseType, ParameterClass _class, int _line, int _column) {
        super(_name, Types.ARRAY, _class, _line, _column); 
        minInd = _minInd;
        maxInd = _maxInd;
        baseType = _baseType;
    }

    public SymbolArray(String _name, int _numComp, Types _baseType, int _line, int _column) {
        super(_name, Types.ARRAY, ParameterClass.NONE, _line, _column);
        minInd = 0;
        maxInd = _numComp - 1;
        baseType = _baseType; 
    }

    public SymbolArray(String _name, int _numComp, Types _baseType, ParameterClass _class, int _line, int _column) {
        super(_name, Types.ARRAY, _class, _line, _column); 
        minInd = 0;
        maxInd = _numComp - 1;
        baseType = _baseType;
    }

    public String toString() {
        return "(" + name + "," + type +  "," +  minInd + "," + maxInd + "," + 
                baseType + "," + parClass + "," + nivel + "," + line + "," + 
                column + ")";
    }
    
    public SymbolArray clone () {
    	return (SymbolArray) super.clone(); 
    }

}