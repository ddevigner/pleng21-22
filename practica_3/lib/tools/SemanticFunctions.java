//*****************************************************************
// Tratamiento de errores sintácticos
//
// Fichero:    SemanticFunctions.java
// Fecha:      03/03/2022
// Versión:    v1.0
// Asignatura: Procesadores de Lenguajes, curso 2021-2022
//*****************************************************************

package lib.tools;

import java.util.*;
import traductor.Token;
import lib.attributes.*;
import lib.symbolTable.*;
import lib.symbolTable.Symbol.ParameterClass;
import lib.symbolTable.Symbol.Types;
import lib.symbolTable.exceptions.*;
import lib.errores.*;

public class SemanticFunctions {
	private ErrorSemantico errSem; //clase común de errores semánticos

	public static enum Operator { ADD, SUB, OR, MUL, MOD, DIV, AND };

	public SemanticFunctions() {
		errSem = new ErrorSemantico();
	}


	// --
	public static boolean CheckParClass(Symbol.ParameterClass fst, Symbol.ParameterClass snd) {
		return (fst == ParameterClass.VAL || (fst == ParameterClass.REF && snd != ParameterClass.VAL));
	}


	//-- Procedimientos.
	public static ArrayList<Symbol> CreateProcedure(SymbolTable st, Token t, 
			Symbol.Types baseType, Symbol.Types returnType, boolean main) {

		ArrayList<Symbol> parList = new ArrayList<>();
		try {
			if (baseType == Symbol.Types.FUNCTION)
				st.insertSymbol(new SymbolFunction(
					t.image, parList, returnType, t.beginLine, t.beginColumn));
			else 
				st.insertSymbol(new SymbolProcedure(
					t.image, parList, t.beginLine, t.beginColumn, main));
			st.insertBlock();
			//System.err.println(st.toString());
			return parList;
		} catch (AlreadyDefinedSymbolException e) {
			// ErrorSemantico.deteccion(e, t);
			System.err.println("Error - did you already define " + t.image 
				+ " symbol?");
			return null;
		}
	}

	//-- Parametros.
	public static void CreateVar(SymbolTable st, ArrayList<Symbol> parList, 
			Token t, int nElem, Symbol.Types baseType, 
			Symbol.ParameterClass parClass) {
		
		Symbol sym;
		try {
			if (nElem <= 0) {
				if (baseType == Symbol.Types.INT) sym = new SymbolInt(
					t.image, parClass, t.beginLine, t.beginColumn);
				else if (baseType == Symbol.Types.BOOL) sym = new SymbolBool(
					t.image, parClass, t.beginColumn, t.beginColumn);
				else sym = new SymbolChar(
					t.image, parClass, t.beginColumn, t.beginColumn);
			} else 
				sym = new SymbolArray(t.image, nElem, baseType, parClass, 
					t.beginLine, t.beginColumn);

			if (parList != null) SymbolTable.insertSymbol(parList, sym);
			st.insertSymbol(sym);
			//System.err.println(st.toString());
		} catch (AlreadyDefinedSymbolException e) {
			System.err.println("Error -- Simbol \'" + 
				t.image + "\' already defined...");
		}
	}

	public static void comprobarGet(Symbol var){
		if(var != null) {
			if (var.type == Types.ARRAY) {
				if(((SymbolArray) var).baseType != Types.CHAR && ((SymbolArray) var).baseType != Types.INT) 
					System.err.println("Error -- Get(). Expected char or int, got " + ((SymbolArray) var).baseType);
			}
			else if (var.type == Types.CHAR && var.type != Types.INT)
				System.err.println("Error -- Get(). Expected char or int, got " + var.type);
		}
	}

	public static void comprobarAssignableGet(Symbol var){
		if (var.type == Types.ARRAY) {
			if(((SymbolArray) var).baseType != Types.CHAR && ((SymbolArray) var).baseType != Types.INT) 
				System.err.println("Error -- Get(). Expected char or int, got " + ((SymbolArray) var).baseType);
		}
		else if (var.type == Types.CHAR && var.type != Types.INT)
			System.err.println("Error -- Get(). Expected char or int, got " + var.type);
	}
	
	public static void comprobarPut(Attributes fst){
		if(fst.type == Types.UNDEFINED){	//Se le pasa una expresion indefinida
				System.err.println("put necesita expresiones no nulas");
		}
	}

	public static void comprobarPutLine(Attributes fst){
		if(fst.type == Types.UNDEFINED){	//Se le pasa una expresion indefinida
				System.err.println("put_line necesita expresiones no nulas");
		}
	}

	public static void comprobarProcedimiento(Token t,SymbolTable st,SymbolProcedure s){
		try{
			Symbol aux = st.getSymbol(t.image);
			if(aux.type != Types.PROCEDURE) {
				if(aux.type == Types.FUNCTION) System.err.println("Warn -- Se esta ignorando el valor devuelto");
				else System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- Se debe invocar un procedimiento");
			} else {
				s = (SymbolProcedure) aux;
				if (s.main)
					System.err.println("(" + t.beginLine + "," + t.beginColumn + ") Error -- You can not call the main procedure");
			}
		} catch (SymbolNotFoundException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- symbol \'" + t.image + "\' not declared.");
		}
	}

	public static void comprobarNumArgumentos(Token t,SymbolProcedure s,int i){
		if (s != null && i != s.parList.size()) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- Bad number of parameters");
		}
	}

	public static void comprobarAssignableInst(Attributes fst,Symbol var,Attributes at){
		if (var != null) {
			if (var.type == Types.ARRAY) {
				if (((SymbolArray) var).baseType != fst.type)
					System.err.println("(" + var.line + "," + var.column + ") Error -- Assign. Mismatched types. Expected " + ((SymbolArray) var).baseType + ", got " + at.type);
			}
			else if (var.type != fst.type)
				System.err.println("(" + var.line + "," + var.column + ") Error -- Assign. Mismatched types. Expected " + var.type + ", got " + at.type);
		}
	}
	
	public static void comprobarWhile(Attributes fst){
		if(fst.type != Types.BOOL)
			System.err.println("Error -- No poner guardas no booleanas en if");
	}

	public static void comprobarIf(Attributes fst){
		if(fst.type != Types.BOOL)
			System.err.println("Error -- No poner guardas no booleanas en if");
	}

	public static void comprobarReturnIf(Attributes at,Attributes fst){
		at.haveReturn = true;
				if(at.returnType != fst.type && at.returnType != Types.UNDEFINED)
					System.err.println("Error -- Expected " + at.returnType + " value, got " + fst.type);
	}

}
