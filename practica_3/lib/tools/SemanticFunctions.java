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
import lib.tools.exceptions.*;

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



	public static void comprobarGet(Symbol var)throws GetException{
		if(var != null) {
			if (var.type == Types.ARRAY) {
				if(((SymbolArray) var).baseType != Types.CHAR && ((SymbolArray) var).baseType != Types.INT){ 
					System.err.println("Error -- Get(). Expected char or int, got " + ((SymbolArray) var).baseType);
					throw new GetException();
					}
			}
			else if (var.type == Types.CHAR && var.type != Types.INT)
				System.err.println("Error -- Get(). Expected char or int, got " + var.type);
				throw new GetException();
		}
	}



	public static void comprobarAssignableGet(Symbol var)throws AGetException{
		if (var.type == Types.ARRAY) {
			if(((SymbolArray) var).baseType != Types.CHAR && ((SymbolArray) var).baseType != Types.INT) 
				System.err.println("Error -- Get(). Expected char or int, got " + ((SymbolArray) var).baseType);
				throw new AGetException();
		}
		else if (var.type == Types.CHAR && var.type != Types.INT)
			System.err.println("Error -- Get(). Expected char or int, got " + var.type);
			throw new AGetException();
	}
	


	public static void comprobarPut(Attributes fst)throws PutException{
		if(fst.type == Types.UNDEFINED){	//Se le pasa una expresion indefinida
				System.err.println("put necesita expresiones no nulas");
				throw new PutException();
		}
	}



	public static void comprobarPutLine(Attributes fst)throws PutLineException{
		if(fst.type == Types.UNDEFINED){	//Se le pasa una expresion indefinida
				System.err.println("put_line necesita expresiones no nulas");
				throw new PutLineException();
		}
	}



	public static void comprobarProcedimiento(Token t,SymbolTable st,SymbolProcedure s){
		try{
			Symbol aux = st.getSymbol(t.image);
			if(aux.type != Types.PROCEDURE) {
				if(aux.type == Types.FUNCTION) System.err.println("Warn -- Se esta ignorando el valor devuelto");
				else {System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- Se debe invocar un procedimiento");}
			} else {
				s = (SymbolProcedure) aux;
				if (s.main)
					System.err.println("(" + t.beginLine + "," + t.beginColumn + ") Error -- You can not call the main procedure");
			}
		} catch (SymbolNotFoundException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- symbol \'" + t.image + "\' not declared.");
		}
	}



	public static void comprobarNumArgumentos(Token t,SymbolProcedure s,int i)throws NArgsException{
		if (s != null && i != s.parList.size()) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- Bad number of parameters");
			throw new NArgsException();
		}
	}



	public static void comprobarAssignableInst(Attributes fst,Symbol var,Attributes at)throws AInstException{
		if (var != null) {
			if (var.type == Types.ARRAY) {
				if (((SymbolArray) var).baseType != fst.type)
					System.err.println("(" + var.line + "," + var.column + ") Error -- Assign. Mismatched types. Expected " + ((SymbolArray) var).baseType + ", got " + at.type);
					throw new AInstException();
			}
			else if (var.type != fst.type)
				System.err.println("(" + var.line + "," + var.column + ") Error -- Assign. Mismatched types. Expected " + var.type + ", got " + at.type);
				throw new AInstException();
		}
	}
	


	public static void comprobarWhile(Attributes fst)throws WhileBooleanException{
		// if(fst.type != Types.BOOL)
		// 	System.err.println("Error -- No poner guardas no booleanas en if");
		if(fst.type != Types.BOOL){
			System.err.println("Error -- No poner guardas no booleanas en while");
			throw new WhileBooleanException();
		}
		
	}



	public static void comprobarIf(Attributes fst)throws IfException{
		if(fst.type != Types.BOOL)
			System.err.println("Error -- No poner guardas no booleanas en if");
			throw new IfException();
	}

	public static void comprobarReturnIf(Attributes at,Attributes fst)throws ReturnIfException{
		at.haveReturn = true;
				if(at.returnType != fst.type && at.returnType != Types.UNDEFINED)
					System.err.println("Error -- Expected " + at.returnType + " value, got " + fst.type);
					throw new ReturnIfException();
	}



	public static Symbol comprobarAssignableVector(SymbolTable st,Token t)throws AVectorException{
		Symbol s = st.getSymbol(t.image);
				if(s.type != Types.ARRAY) {
					System.err.println("Error -- Assignable. Trying to use a(n) " + s.type + " as an array?");
					
					return null;
					//throw new AVectorException();
				}
				return st.getSymbol(t.image);

	}



	public static Symbol comprobarAssignableNormal(SymbolTable st,Token t)throws ANormalException{
		Symbol s = st.getSymbol(t.image);
		if(s.type != Types.CHAR && s.type != Types.INT && s.type != Types.BOOL) {
			System.err.println("Error -- Assignable. Trying to use a(n) " + s.type + " as a simple var?");
			return null;
			//throw new ANormalException();
		}
		return st.getSymbol(t.image);
	}



	public static void comprobarExpression(Attributes fst,Attributes snd,Attributes at)throws ExpressionException{
		if (fst.type == snd.type) {
			at.type = Types.BOOL;
			at.parClass = ParameterClass.VAL;
		}
		else {
			System.err.println("Error -- Mismatched types");
			at.type = Types.UNDEFINED;
			at.parClass = ParameterClass.NONE;
			throw new ExpressionException();
		}
	}



	public static void comprobarExpSimple(Attributes fst,Attributes snd,Attributes at,Attributes op)throws ExpSimpleException{
		if (op.opType == Operator.ADD || op.opType == Operator.SUB) {
			if (fst.type == Types.INT && snd.type == fst.type) {
				at.type = fst.type;
				at.parClass = ParameterClass.VAL;
			}
			else {
				System.err.println("Error -- Mismatched types.");
				at.type = Types.UNDEFINED;
				at.parClass = ParameterClass.NONE;
				throw new ExpSimpleException();
			}
		} else {
			if (fst.type == Types.BOOL && snd.type == fst.type) {
				at.type = fst.type;
				at.parClass = ParameterClass.VAL;
			}
			else {
				System.err.println("Error -- Mismatched types.");
				at.type = Types.UNDEFINED;
				at.parClass = ParameterClass.NONE;
				throw new ExpSimpleException();
			}
		}
	}



	public static void comprobarFactor(Attributes fst,Attributes snd,Attributes at,Attributes op)throws FactorException{
		if (op.opType == Operator.MUL || op.opType == Operator.MOD || op.opType == Operator.DIV) {
			if (fst.type == Types.INT && snd.type == fst.type) {
				at.type = fst.type;
				at.parClass = ParameterClass.VAL;
			}
			else {
				System.err.println("Error -- Mismatched types");
				at.type = Types.UNDEFINED;
				at.parClass = ParameterClass.NONE;
				throw new FactorException();
			}
		} else {
			if (fst.type == Types.BOOL && snd.type == fst.type) {
				at.type = fst.type;
				at.parClass = ParameterClass.VAL;
			}
			else {
				System.err.println("Error -- Mismatched types");
				at.type = Types.UNDEFINED;
				at.parClass = ParameterClass.NONE;
				throw new FactorException();
			}
		}
	}



	public static void comprobarFactorID(Token t,SymbolTable st,SymbolFunction s,Attributes at){
		try {
			Symbol aux = st.getSymbol(t.image);
			if (aux.type == Symbol.Types.FUNCTION) {
				s = (SymbolFunction) aux;
				at.type = ((SymbolFunction) s).returnType;
				at.parClass = ParameterClass.VAL;
			}
			else {
				System.err.println("Error -- symbol \'" + t.image + "\' bad usage. Expected function, got " + aux.type.name());
				at.type = Types.UNDEFINED;
				at.parClass = ParameterClass.NONE;
			}
		} catch (SymbolNotFoundException e) {
			System.err.println("Error -- symbol \'" + t.image + "\' not found");
			at.type = Types.UNDEFINED;
			at.parClass = ParameterClass.NONE;
		}
	}



	public static void comprobarVector(Token t, Attributes at,SymbolTable st,Attributes fst){
		try {
			Symbol aux = st.getSymbol(t.image);
			if (aux.type == Types.ARRAY) {
				at.type = ((SymbolArray) aux).baseType;
				at.parClass = aux.parClass;
			}
			else {
				System.err.println("Error -- symbol \'" + t.image + 
					"\' bad usage. Expected array, got " + aux.type.name());
				at.type = Types.UNDEFINED;
				at.parClass = ParameterClass.NONE;
			}
			if (fst.type != Types.INT) {
				System.err.println("Error -- are you trying to index " +
					"into an array with a " + fst.type.name() + "?");
				at.type = Types.UNDEFINED;
				at.parClass = ParameterClass.NONE;
			}
		} catch (SymbolNotFoundException e) {
			System.err.println("Error -- symbol \'" + t.image + "\' not found");
			at.type = Types.UNDEFINED;
			at.parClass = ParameterClass.NONE;
		}
	}



	public static void AlgoID(Token t,Attributes at,SymbolTable st){
		try {
			Symbol aux = st.getSymbol(t.image);
			at.type = aux.type;
			at.parClass = aux.parClass;
		} catch (SymbolNotFoundException e) {
			System.err.println("Error -- symbol \'" + t.image + "\' not found");
			at.type = Types.UNDEFINED;
			at.parClass = ParameterClass.NONE;
		}
	}

}
