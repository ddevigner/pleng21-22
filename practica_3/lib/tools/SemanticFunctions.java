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

	public int getErrors(){
		return se.getErrors();
	}

	public int getWarnings() {
		return se.getWarnings();
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
	private void evaluateReturnTypeDef(Types type, Types baseType) throws ReturnTypeException
	{
		if (type == Types.PROCEDURE && baseType != Types.UNDEFINED)
			throw new ReturnTypeException();
		if (type == Types.FUNCTION  && baseType == Types.UNDEFINED)
			throw new ReturnTypeException(true);
	}

	public void EvaluateReturnTypeDef(Attributes at, Token t) {
		try {
			evaluateReturnTypeDef(at.type, at.baseType);
		} catch (ReturnTypeException e) {
			if (!e.proc_or_func) se.detection(e, t.beginLine, t.beginColumn);
			else se.detection(e, at.line, at.column);
			at.baseType = Types.UNDEFINED;
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
	private boolean evaluateParameterClass(ParameterClass a, ParameterClass b) {
		if (a == ParameterClass.REF && b == ParameterClass.VAL) return false;
		else return true;
	}


	private void evaluateProcedure(Symbol s, Attributes at, Token t) throws
		MismatchedSymbolTypeException, 
		MainProcedureCallException, 
		ProcedureNotFoundException
	{
		
		if (s.type != Types.PROCEDURE) 
			throw new MismatchedSymbolTypeException(s.name, s.type, Types.PROCEDURE);
		SymbolProcedure p = (SymbolProcedure) s;
		if (p.main) throw new MainProcedureCallException(p.name);
		at.name = t.image;
		if (p.parList.size() != at.given.size()) {
			throw new ProcedureNotFoundException(p.toString(), at.toProcedure(), 
				p.parList.size(), at.given.size());
		}
		for (int i = 0; i < p.parList.size(); i++) {
			try {
				Attributes  g = at.given.get(i);
				if (p.parList.get(i).type == Types.ARRAY) {
					SymbolArray e = ((SymbolArray) p.parList.get(i));
					if (g.type != Types.ARRAY) {
						throw new ProcedureNotFoundException(p.toString(),
							at.toProcedure(), i+1, false);
					}
					if (e.baseType != g.baseType) {
						throw new ProcedureNotFoundException(p.toString(), 
							at.toProcedure(), e.baseType, g.baseType, i+1);
					}
					if (!evaluateParameterClass(e.parClass, g.parClass)) {
						throw new ProcedureNotFoundException(p.toString(), 
							at.toProcedure(), e.parClass, g.parClass, i+1);
					}
					if (e.maxInd != g.maxInd) {
						throw new ProcedureNotFoundException(p.toString(), 
							at.toProcedure(), e.maxInd, g.maxInd, i+1);
					}
				} else {
					Symbol e = p.parList.get(i);
					if (g.type == Types.ARRAY) {
						throw new ProcedureNotFoundException(p.toString(),
							at.toProcedure(), i+1, true);
					}
					if (e.type != g.baseType) {
						throw new ProcedureNotFoundException(p.toString(), 
							at.toProcedure(), e.type, g.baseType, i+1);
					}
					if (!evaluateParameterClass(e.parClass, g.parClass)) {
						throw new ProcedureNotFoundException(p.toString(), 
							at.toProcedure(), e.parClass, g.parClass, i+1);
					}
				}
			} catch (ProcedureNotFoundException e) {
				se.detection(e, t);
			}
		}
	}
	public void EvaluateProcedure(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image); 
			evaluateProcedure(s, at, t);
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
		} catch (ProcedureNotFoundException e) {
			se.detection(e, t);
		} catch (MainProcedureCallException e) {
			se.detection(e, t);
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
	private void evaluateReturn(Types type, Types base, Types got) throws ReturnStatementException
	{
		if(type == Types.PROCEDURE) throw new ReturnStatementException();
		else if (base != got) throw new ReturnStatementException(base, got);
	}

	public void hasReturn(boolean hasReturn) throws ReturnStatementException {
		if (!hasReturn) throw new ReturnStatementException(false);
	}

	public void EvaluateReturn(Attributes at, Attributes exp, Token t){
		try{
			evaluateReturn(at.type, at.baseType, exp.baseType);
			at.hasReturn = true;
		} catch(ReturnStatementException e) {
			if (!e.proc_or_func || !e.heavy) 
				se.detection(e, t.beginLine, t.beginColumn);
			else
				se.detection(e, at.line, at.column);
		}
	}

	public void EvaluateReturn(Attributes at, Token t) {
		try {
			if (at.type == Types.FUNCTION) hasReturn(at.hasReturn);
		} catch (ReturnStatementException e) {
			se.detection(e, t.beginLine, t.beginColumn);
		}
	} 

	//-----------------------------------------------------------------------
	// Evaluar operacion.
	//-----------------------------------------------------------------------
	private void evaluateOperation(Attributes fst, Attributes snd) throws MismatchedTypesException {
		if (snd.op == Operator.CMP_OP) {
			if (fst.baseType != snd.baseType) { 
				throw new MismatchedTypesException(snd.op_name, Types.UNDEFINED, 
					fst.baseType, snd.baseType);
			}
			else fst.baseType = Types.BOOL;
		} else if (snd.op == Operator.INT_OP) {
			if (fst.baseType != Types.INT || snd.baseType != Types.INT) {
				throw new MismatchedTypesException(snd.op_name, Types.INT, 
					fst.baseType, snd.baseType);
			}
		} else if (snd.op == Operator.BOOL_OP) {
			if (fst.baseType != Types.BOOL || snd.baseType != Types.BOOL) {
				throw new MismatchedTypesException(snd.op_name, Types.BOOL, 
					fst.baseType, snd.baseType);
			}
		}
	}

	private void evaluateOperation(Operator op, Types fst) throws MismatchedTypesException {
		if (fst != Types.INT) throw new MismatchedTypesException(fst);
	}

	public void EvaluateOperation(Attributes fst, Attributes snd) {
		try {
			evaluateOperation(fst, snd);
			fst.parClass = ParameterClass.VAL;
		} catch (MismatchedTypesException e) {
			se.detection(e, snd.line, snd.column);
			fst.baseType = Types.UNDEFINED;
			fst.parClass = ParameterClass.NONE;
		} 
	}

	public void EvaluateOperation(Attributes fst, Token t) {
		try {
			if (t != null) {
				evaluateOperation(fst.op, fst.baseType);
				fst.parClass = ParameterClass.VAL;
				fst.line = t.beginLine;
				fst.column = t.beginColumn;
			}
		} catch (MismatchedTypesException e) {
			se.detection(e, fst.line, fst.column);
			fst.baseType = Types.UNDEFINED;
			fst.parClass = ParameterClass.NONE;
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
	// private Types evaluateInt2Char(Types type) throws MismatchedTypesException {
	// 	if(type != Types.INT) throw new MismatchedTypesException(Types.INT, type);
	// 	else return Types.CHAR;
	// }

	// public void EvaluateInt2Char(Attributes at, Attributes exp, Token t) {
	// 	try {
	// 		at.baseType = evaluateInt2Char(exp.baseType);
	// 	} catch (MismatchedTypesException e) {
	// 		se.detection(e, exp.line, exp.column);
	// 		at.baseType = Types.UNDEFINED;
	// 	}
	// 	at.line = t.beginLine;
	// 	at.column = t.beginColumn;
	// }
	private Types evaluateInt2Char(Types type) throws FunctionNotFoundException {
		if(type != Types.INT) throw new FunctionNotFoundException("int2char", "int2char", Types.INT, type, 1);
		else return Types.CHAR;
	}

	public void EvaluateInt2Char(Attributes at, Attributes exp, Token t) {
		try {
			at.baseType = evaluateInt2Char(exp.baseType);
		} catch (FunctionNotFoundException e) {
			//se.detection(e, exp.line, exp.column);
			se.detection(e,t);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}



	//-----------------------------------------------------------------------
	// Evaluar Char2Int.
	//-----------------------------------------------------------------------
	private Types evaluateChar2Int(Types type) throws FunctionNotFoundException{
		if(type != Types.CHAR) throw new FunctionNotFoundException("char2int", "char2int", Types.CHAR, type, 1);
		else return Types.INT;
	}

	public void EvaluateChar2Int(Attributes at, Attributes exp, Token t) {
		try {
			at.baseType = evaluateChar2Int(exp.baseType);
		} catch (FunctionNotFoundException e) {
			//se.detection(e, exp.line, exp.column);
			se.detection(e,t);
			at.baseType = Types.UNDEFINED;
		}
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}
	

	//-----------------------------------------------------------------------
	// Evaluar Funcion.
	//-----------------------------------------------------------------------
	private void evaluateFunction(Symbol s, Attributes at, Token t) throws 
		MismatchedSymbolTypeException, 
		FunctionNotFoundException
	{
		if (s.type != Types.FUNCTION) throw new MismatchedSymbolTypeException(s.name, Types.FUNCTION, s.type);
		SymbolFunction f = (SymbolFunction) s;
		at.name = f.name;
		at.baseType = f.returnType;
		if (f.parList.size() != at.given.size()) {
			throw new FunctionNotFoundException(f.toString(), at.toFunction(),
				f.parList.size(), at.given.size());
		}
		for (int i = 0; i < f.parList.size(); i++) {
			try {
				Attributes g = at.given.get(i);
				if (f.parList.get(i).type == Types.ARRAY) {
					SymbolArray e = (SymbolArray) f.parList.get(i);
					if (g.type != Types.ARRAY) {
						throw new FunctionNotFoundException(f.toString(),
							at.toFunction(), i+1, false);
					}
					if (e.baseType != g.baseType) {
						throw new FunctionNotFoundException(f.toString(), 
							at.toFunction(), e.baseType, g.baseType, i+1);
					}
					if (!evaluateParameterClass(e.parClass, g.parClass)) {
						throw new FunctionNotFoundException(f.toString(), 
							at.toFunction(), e.parClass, g.parClass, i+1);
					}
					if (e.maxInd != g.maxInd) {
						throw new FunctionNotFoundException(f.toString(), 
							at.toFunction(), e.maxInd, g.maxInd, i+1);
					}
				} else {
					Symbol e = f.parList.get(i);
					if (g.type == Types.ARRAY) {
						throw new FunctionNotFoundException(f.toString(),
							at.toFunction(), i+1, true);
					}
					if (e.type != g.baseType) {
						throw new FunctionNotFoundException(f.toString(), 
							at.toFunction(), e.type, g.baseType, i+1);
					}
					if (!evaluateParameterClass(e.parClass, g.parClass)) {
						throw new FunctionNotFoundException(f.toString(), 
							at.toFunction(), e.parClass, g.parClass, i+1);
					}
				}
			} catch (FunctionNotFoundException e) {
				se.detection(e,t);
				at.baseType = Types.UNDEFINED;
			}
		}
	}

	public void EvaluateFunction(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image); 
			evaluateFunction(s, at, t);
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
		if (s.type != Types.ARRAY) throw new MismatchedSymbolTypeException(s.name, Types.ARRAY, s.type);
		return ((SymbolArray) s).baseType;
	}

	private Types evaluateArray(Symbol s, Types index) throws MismatchedSymbolTypeException, IndexNotIntegerException {
		if (s.type != Types.ARRAY) throw new MismatchedSymbolTypeException(s.name, Types.ARRAY, s.type);
		if (index != Types.INT) throw new IndexNotIntegerException(index);
		return ((SymbolArray) s).baseType;
	}

	public void EvaluateArray(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image);
			at.baseType = evaluateArray(s);
			at.maxInd = ((SymbolArray) s).maxInd;
			at.name = t.image;
			at.parClass = ((SymbolArray) s).parClass;
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
			at.parClass = ((SymbolArray) s).parClass;
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
			throw new MismatchedSymbolTypeException(s.name, Types.UNDEFINED, s.type);
		return s.type;
	}

	public void EvaluateVar(Attributes at, Token t) {
		try {
			Symbol s = st.getSymbol(t.image);
			at.baseType = evaluateVar(s);
			at.name = t.image;
			at.parClass = s.parClass;
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
			at.parClass = s.parClass;
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
		at.op_name = t.image;
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}
	
	/* --------------------------------------------------------------------- */
	/* PORQUE MOLA                                                           */
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
