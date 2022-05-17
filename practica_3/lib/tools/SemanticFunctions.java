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
	private SemanticError se; //clase común de errores semánticos

	private SymbolTable st;
	
	public static enum Operator { NOP, INT_OP, BOOL_OP, CMP_OP };

	public SemanticFunctions(SymbolTable st) {
		this.st = st;
		se = new SemanticError();
	}

	public SemanticError getErrorSemantico(){
		return se;
	}

	/* --------------------------------------------------------------------- */
	/* Insertar nuevo procedimiento o funcion, o variable.                   */
	/* --------------------------------------------------------------------- */
	private void checkArrayIndexDefinition(int n) throws ZeroSizeArrayException {
		if (n == 0) throw new ZeroSizeArrayException();
	}

	public void AddVar(Attributes var, Token t, Token i, Types type) {
		try {
			Symbol s;
			if (type == Types.ARRAY) {
				int n = Integer.parseInt(i.image);
				checkArrayIndexDefinition(n);
				s = new SymbolArray(t.image, n, var.baseType, var.parClass);
			} else if (var.baseType == Types.INT) {
				s = new SymbolInt(t.image, var.parClass);
			} else if (var.baseType == Types.CHAR) {
				s = new SymbolChar(t.image, var.parClass);
			} else {
				s = new SymbolBool(t.image, var.parClass);
			}
			st.insertSymbol(s);
			if (var.params != null) st.insertSymbol(var.params, s);
		} catch (ZeroSizeArrayException e) {
			se.detection(e, t, i);
		} catch (AlreadyDefinedSymbolException e) {
			se.detection(e, t);
		}
	}

	public void AddMethod(Attributes at, Token t) {
		try {
			at.name = t.image;
			if (!at.main) at.params = new ArrayList<>();
			if (at.type == Types.PROCEDURE) 
				st.insertSymbol(new SymbolProcedure(t.image, at.params, at.main));
			else 
				st.insertSymbol(new SymbolFunction(t.image, at.params, at.baseType));
		} catch (AlreadyDefinedSymbolException e) {
			se.detection(e, t);
			at.params = null;
		}
		st.insertBlock();
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}
	/* --------------------------------------------------------------------- */
	/* --------------------------------------------------------------------- */
	/* Procedimientos y funciones.                                           */
	/* --------------------------------------------------------------------- */
	/* Verifica el tipo de retorno de procedimiento o funcion.               */
	/* --------------------------------------------------------------------- */
	private void evaluateReturnTypeDef(Types type, Types baseType) throws 
		ProcedureReturnTypeException,
		FunctionReturnTypeException
	{
		if (type == Types.PROCEDURE && baseType != Types.UNDEFINED)
			throw new ProcedureReturnTypeException();
		if (type == Types.FUNCTION  && baseType == Types.UNDEFINED)
			throw new FunctionReturnTypeException();
	}

	public void EvaluateReturnTypeDef(Attributes at, Token t) {
		try {
			evaluateReturnTypeDef(at.type, at.baseType);
		} catch (ProcedureReturnTypeException e) {
			se.detection(e, at.line, at.column);
			at.baseType = Types.UNDEFINED;
		} catch (FunctionReturnTypeException e) {
			se.detection(e, t);
		}
	}

	//-----------------------------------------------------------------------
	// Evaluar get.
	//-----------------------------------------------------------------------
	private void evaluateGet(Types type) throws GetException {
		if (type != Types.INT && type != Types.CHAR) throw new GetException(type);
	}

	public void EvaluateGet(Attributes at) {
		try {
			evaluateGet(at.baseType);
		} catch (GetException e) {
			se.detection(e, at.line, at.column);
		}
	}

	//-----------------------------------------------------------------------
	// Evaluar put.
	//-----------------------------------------------------------------------
	private void evaluatePut(Types type) throws PutException{
		if (type == Types.UNDEFINED) throw new PutException();
	}

	public void EvaluatePut(Attributes at) {
		try{
			evaluatePut(at.baseType);
		}catch (PutException e){
			se.detection(e, at.line, at.column);
		}
	}

	//-----------------------------------------------------------------------
	// Evaluar putline.
	//-----------------------------------------------------------------------
	private void evaluatePutline(Types type) throws PutlineException {
		if (type == Types.UNDEFINED) throw new PutlineException();
	}

	public void EvaluatePutline(Attributes at) {
		try {
			evaluatePutline(at.baseType);
		} catch (PutlineException e) {
			se.detection(e, at.line, at.column);
		}
	}

	//-----------------------------------------------------------------------
	// Evaluar procedimiento.
	//-----------------------------------------------------------------------
	private void evaluateProcedure(Symbol s, Attributes at) throws
		MismatchedSymbolTypeException, 
		MainProcedureCallException, 
		ProcedureNotFoundException
	{
		if (s.type != Types.PROCEDURE) 
			throw new MismatchedSymbolTypeException(s.type, Types.PROCEDURE);

		SymbolProcedure p = (SymbolProcedure) s;
		if (p.main) throw new MainProcedureCallException(p.name);
		if (p.parList.size() != at.given.size()) throw new ProcedureNotFoundException();
		for (int i = 0; i < p.parList.size(); i++) {
			if (p.parList.get(i).type == Types.ARRAY) {
				if (((SymbolArray) p.parList.get(i)).baseType != at.given.get(i).baseType) 
					throw new ProcedureNotFoundException();
				if (((SymbolArray) p.parList.get(i)).parClass != at.given.get(i).parClass)
					throw new ProcedureNotFoundException();
				if (((SymbolArray) p.parList.get(i)).maxInd != at.given.get(i).maxInd)
					throw new ProcedureNotFoundException();
			} else {
				if (p.parList.get(i).type != at.given.get(i).baseType)
					throw new ProcedureNotFoundException();
				if (p.parList.get(i).parClass != at.given.get(i).parClass) 
					throw new ProcedureNotFoundException();
			}
		}
		
	}
	public void EvaluateProcedure(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image); 
			evaluateProcedure(s, at);
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
		} catch (ProcedureNotFoundException e) {
			se.detection(e, t);
		} catch (MainProcedureCallException e) {
			se.detection(e);
		} catch (MismatchedSymbolTypeException e) {
			se.detection(e, t);
		}
	}

	//-----------------------------------------------------------------------
	// Evaluar asignacion.
	//-----------------------------------------------------------------------
	private void evaluateAssignation(Types a, Types exp) throws MismatchedTypesException {
		if (a != exp) throw new MismatchedTypesException(a, exp);
	}

	public void EvaluateAssignation(Attributes a, Attributes exp) {
		try {
			evaluateAssignation(a.baseType, exp.baseType);
		} catch (MismatchedTypesException e) {
			se.detection(e, exp.line, exp.column);
		}
	}

	//-----------------------------------------------------------------------
	// Evaluar declaracion del return.
	//-----------------------------------------------------------------------
	private void evaluateReturn(Types type, Types base, Types got) throws 
		ProcedureReturnException,
		FunctionReturnException
	{
		if(type == Types.PROCEDURE) throw new ProcedureReturnException();
		else if (base != got) throw new FunctionReturnException(base, got);
	}

	public void hasReturn(boolean hasReturn) throws FunctionReturnException {
		if (!hasReturn) throw new FunctionReturnException();
	}

	public void EvaluateReturn(Attributes at, Attributes exp, Token t){
		try{
			evaluateReturn(at.type, at.baseType, exp.baseType);
			at.hasReturn = true;
		} catch(ProcedureReturnException e) {
			se.detection(e, t);
		} catch (FunctionReturnException e) {
			se.detection(e, exp.line, exp.column);
		}
	}

	public void EvaluateReturn(Attributes at, Token t) {
		try {
			if (at.type == Types.FUNCTION) hasReturn(at.hasReturn);
		} catch (FunctionReturnException e) {
			se.detection(e, t.beginLine, t.beginColumn);
		}
	} 

	//-----------------------------------------------------------------------
	// Evaluar operacion.
	//-----------------------------------------------------------------------
	private Types evaluateOperation(Types fst, Operator op, Types snd) throws 
		MismtachedCompareTypesException,
		MismtachedAddTypesException,
		MismtachedProductTypesException 
	{
		if (op == Operator.CMP_OP) {
			if (fst != snd) throw new MismtachedCompareTypesException(fst, snd);
			else return Types.BOOL;
		} else if (op == Operator.INT_OP) {
			if (fst != Types.INT || snd != Types.INT)
				throw new MismtachedAddTypesException(fst, snd);
		} else if (op == Operator.BOOL_OP) {
			if (fst != Types.BOOL || snd != Types.BOOL)
				throw new MismtachedProductTypesException(fst, snd);
		}
		return fst;
	}

	public void EvaluateOperation(Attributes fst, Attributes snd) {
		try {
			fst.baseType = evaluateOperation(fst.baseType, snd.op, snd.baseType);
			fst.parClass = ParameterClass.VAL;
		} catch (MismtachedCompareTypesException e) {
			se.detection(e, snd.line, snd.column);
			fst.baseType = Types.UNDEFINED;
		} catch (MismtachedAddTypesException e) {
			se.detection(e, snd.line, snd.column);
			fst.baseType = Types.UNDEFINED;
		} catch (MismtachedProductTypesException e) {
			se.detection(e, snd.line, snd.column);
			fst.baseType = Types.UNDEFINED;
		}
	}

	//-----------------------------------------------------------------------
	// Evaluar condicion.
	//-----------------------------------------------------------------------
	private void evaluateCondition(Types type) throws MismatchedTypesException {
		if(type != Types.BOOL) throw new MismatchedTypesException(Types.BOOL, type);
	}

	public void EvaluateCondition(Attributes at){
		try {
			evaluateCondition(at.baseType);
		} catch (MismatchedTypesException e) {
			se.detection(e, at.line, at.column);
			at.baseType = Types.UNDEFINED;
		}
	}

	public void EvaluateCondition(Attributes at, Token t){
		try {
			evaluateCondition(at.baseType);
		} catch (MismatchedTypesException e) {
			se.detection(e, at.line, at.column);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	//-----------------------------------------------------------------------
	// Evaluar Int2Char.
	//-----------------------------------------------------------------------
	private Types evaluateInt2Char(Types type) throws MismatchedTypesException {
		if(type != Types.INT) throw new MismatchedTypesException(Types.INT, type);
		else return Types.CHAR;
	}

	public void EvaluateInt2Char(Attributes at, Attributes exp, Token t) {
		try {
			at.baseType = evaluateInt2Char(exp.baseType);
		} catch (MismatchedTypesException e) {
			se.detection(e, exp.line, exp.column);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	//-----------------------------------------------------------------------
	// Evaluar Char2Int.
	//-----------------------------------------------------------------------
	private Types evaluateChar2Int(Types type) throws MismatchedTypesException{
		if(type != Types.CHAR) throw new MismatchedTypesException(Types.CHAR, type);
		else return Types.INT;
	}

	public void EvaluateChar2Int(Attributes at, Attributes exp, Token t) {
		try {
			at.baseType = evaluateChar2Int(exp.baseType);
		} catch (MismatchedTypesException e) {
			se.detection(e, exp.line, exp.column);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	//-----------------------------------------------------------------------
	// Evaluar Funcion.
	//-----------------------------------------------------------------------
	private void evaluateFunction(Symbol s, Attributes at) throws MismatchedSymbolTypeException, FunctionNotFoundException {
		if (s.type != Types.FUNCTION) throw new MismatchedSymbolTypeException(Types.FUNCTION, s.type);
		SymbolFunction f = (SymbolFunction) s;
		at.name = f.name;
		at.baseType = f.returnType;
		if (f.parList.size() != at.given.size()) throw new FunctionNotFoundException(f.toString(), at.toFunction());
		for (int i = 0; i < f.parList.size(); i++) {
			if (f.parList.get(i).type == Types.ARRAY) {
				if (((SymbolArray) f.parList.get(i)).baseType != at.given.get(i).baseType) 
					throw new FunctionNotFoundException(f.toString(), at.toFunction());
				if (((SymbolArray) f.parList.get(i)).parClass != at.given.get(i).parClass)
					throw new FunctionNotFoundException(f.toString(), at.toFunction());
				if (((SymbolArray) f.parList.get(i)).maxInd != at.given.get(i).maxInd)
					throw new FunctionNotFoundException(f.toString(), at.toFunction());
			} else {
				if (f.parList.get(i).type != at.given.get(i).baseType)
					throw new FunctionNotFoundException(f.toString(), at.toFunction());
				if (f.parList.get(i).parClass != at.given.get(i).parClass)
					throw new FunctionNotFoundException(f.toString(), at.toFunction());
			}
		}
	}

	public void EvaluateFunction(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image); 
			evaluateFunction(s, at);
		} catch (SymbolNotFoundException e) {
			se.detection(e,t);
			at.baseType = Types.UNDEFINED;
		} catch (FunctionNotFoundException e) {
			se.detection(e,t);
			at.baseType = Types.UNDEFINED;
		} catch (MismatchedSymbolTypeException e) {
			se.detection(e,t);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}


	//-----------------------------------------------------------------------
	// Evaluar Array.
	//-----------------------------------------------------------------------
	private Types evaluateArray(Symbol s) throws MismatchedSymbolTypeException {
		if (s.type != Types.ARRAY) throw new MismatchedSymbolTypeException(Types.ARRAY, s.type);
		return ((SymbolArray) s).baseType;
	}

	private Types evaluateArray(Symbol s, Types index) throws MismatchedSymbolTypeException, IndexNotIntegerException {
		if (s.type != Types.ARRAY) throw new MismatchedSymbolTypeException(Types.ARRAY, s.type);
		if (index != Types.INT) throw new IndexNotIntegerException(index);
		return ((SymbolArray) s).baseType;
	}

	public void EvaluateArray(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image);
			at.baseType = evaluateArray(s);
			at.maxInd = ((SymbolArray) s).maxInd;
			at.name = t.image;
			at.parClass = ParameterClass.REF;
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		} catch (MismatchedSymbolTypeException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	public void EvaluateArray(Attributes at, Attributes index, Token t) {
		try {
			Symbol s = st.getSymbol(t.image);
			at.baseType = evaluateArray(s, index.baseType);
			at.name = t.image;
			at.parClass = ParameterClass.REF;
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		} catch (MismatchedSymbolTypeException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		} catch (IndexNotIntegerException e) {
			se.detection(e, index.line, index.column);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	//-----------------------------------------------------------------------
	// Evaluar variable.
	//-----------------------------------------------------------------------
	private Types evaluateVar(Symbol s) throws MismatchedSymbolTypeException {
		if (s.type != Types.CHAR && s.type != Types.INT && s.type != Types.BOOL)
			throw new MismatchedSymbolTypeException(Types.UNDEFINED, s.type);
		return s.type;
	}

	public void EvaluateVar(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image);
			at.baseType = evaluateVar(s);
			at.name = t.image;
			at.parClass = ParameterClass.REF;
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		} catch (MismatchedSymbolTypeException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	public void EvaluateParam(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image);
			at.name = t.image;
			at.parClass = ParameterClass.REF;
			if (s.type == Types.ARRAY) {
				at.type = Types.ARRAY;
				at.baseType = ((SymbolArray) s).baseType;
				at.maxInd = ((SymbolArray) s).maxInd;
			} else {
				at.baseType = s.type;
			}
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		}
	}

	//-----------------------------------------------------------------------
	// Evaluacion de constantes.
	//-----------------------------------------------------------------------
	// -- Constante entera.
	public void EvaluateInt(Attributes at, Token t) {
		at.baseType = Types.INT;
		at.parClass = ParameterClass.VAL;
		at.intVal   = Integer.parseInt(t.image);
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	// -- Constante caracter.
	public void EvaluateChar(Attributes at, Token t) {
		at.baseType = Types.CHAR;
		at.parClass = ParameterClass.VAL;
		at.charVal  = t.image.charAt(0);
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	// -- Constante booleana.
	public void EvaluateBool(Attributes at, Token t) {
        at.baseType = Types.BOOL;
        at.parClass = ParameterClass.VAL;
        at.boolVal  = t.image.equals("true") ? true : false;
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	// -- Constante string.
	public void EvaluateString(Attributes at, Token t) {
		at.baseType = Types.STRING;
		at.parClass = ParameterClass.VAL;
		at.stringVal   = t.image;
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}

	//-----------------------------------------------------------------------
	// Evaluar tipo de operador.
	//-----------------------------------------------------------------------
	public void EvaluateOperator(Attributes at, Token t, Operator op) {
		at.op = op;
		at.name = t.image;
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}
	

	/* --------------------------------------------------------------------- */
	/* MEMORABLE                                                             */
	/* --------------------------------------------------------------------- */

	public void AlgoID(SymbolTable st, Attributes at, Token t){
		try {
			Symbol aux = st.getSymbol(t.image);
			at.type = aux.type;
			at.parClass = aux.parClass;
		} catch (SymbolNotFoundException e) {
			se.detection(e,t);
			at.type = Types.UNDEFINED;
			at.parClass = ParameterClass.NONE;
		}
	}

}





// Cositas. NO BORRAR.
/*
{
	if (s != null) {
		try {
			Symbol aux = s.parList.get(i);
			if (!SemanticFunctions.CheckParClass(aux.parClass, snd.parClass)) {
				System.err.println("Error -- In function \'" + s.name + "\', expecting: ");
				System.err.println("\t" + aux.parClass + ", " + "got " + snd.parClass);
			}
			if (snd.type != aux.type) {
				System.err.println("Error -- In function \'" + s.name + "\', expecting: ");
				System.err.println("\t" + aux.type + ", got " + snd.type);
			} 
			i++;
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Error -- Expected \'" + s.name + "()\'");
			s = null;
		}
	}
}
{
	if (s != null) {
		try {
			Symbol aux = s.parList.get(0);
			if (!SemanticFunctions.CheckParClass(aux.parClass, fst.parClass)) {
				System.err.println("Error -- In function \'" + s.name +
					"\', expecting: ");
				System.err.println("\t" + aux.parClass + ", " + 
					"got " + fst.parClass);
			}
			if (fst.type != aux.type) {
				System.err.println("Error -- In function \'" + s.name +
					"\', expecting: ");
				System.err.println("\t" + aux.type + ", got " + 
					fst.type);
			}
		i++;
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Error -- Expected \'" + s.name + "()\'");
		}
	}
} 
*/
