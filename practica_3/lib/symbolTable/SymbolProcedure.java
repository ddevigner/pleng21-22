//*****************************************************************
// File:   SymbolProcedure.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   julio 2021
// Coms:   Atributos públicos para evitar el uso de getters y setters
//*****************************************************************

package lib.symbolTable;
import java.util.*;
//import java.util.AbstractMap.SimpleEntry;

public class SymbolProcedure extends Symbol {

    //cada parámetro es un par <id,símbolo> donde "símbolo" es una copia del valor
    //introducido en la tabla de símbolos. Esta duplicación de la información es necesaria
    //ya que cuando se cierra un bloque, tanto los parámetros como las variables locales
    //se eliminan de la tabla, pero la propia función/proc sigue en la tabla, haciendo
    //necesario mantener la información de los parámetros.
    public ArrayList<Symbol> parList;
    public boolean main;

    public SymbolProcedure(String name, ArrayList<Symbol> parList, boolean main) {
    	super(name, Types.PROCEDURE, ParameterClass.NONE);
        this.parList = parList;
        this.main    = main;
    }

    public String toString() {
        String p_str = name + "(";
        for (Symbol i : parList) {
            p_str += i.parClass + " ";
            p_str += (i.type == Types.ARRAY ? ((SymbolArray) i).toString() : i.type + " " + i.name);
            p_str += ", "; 
        }
        return (p_str.substring(0, p_str.length()-2) + ")");
    }

    public String Debug() {
        return "(" + name + "," + type + "," + parList + "," + nivel + ")";
    }

    public SymbolProcedure clone () {
    	SymbolProcedure clone = (SymbolProcedure) super.clone();
    	clone.parList = new ArrayList<Symbol> (parList); 
    	return clone;
    }
}