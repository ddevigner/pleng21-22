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
	public static enum Procedure { GET, PUT, PUTLINE, CUSTOM };

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
				s = new SymbolArray(t.image, n, var.baseType, var.parClass, t.beginLine, t.beginColumn);
			} else if (var.baseType == Types.INT) {
				s = new SymbolInt(t.image, var.parClass, t.beginLine,  t.beginColumn);
			} else if (var.baseType == Types.CHAR) {
				s = new SymbolChar(t.image, var.parClass, t.beginLine, t.beginColumn);
			} else {
				s = new SymbolBool(t.image, var.parClass, t.beginLine, t.beginColumn);
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
			if (!at.main) at.params = new ArrayList<>();
			if (at.type == Types.PROCEDURE) 
				st.insertSymbol(new SymbolProcedure(t.image, at.params, at.main, 
					t.beginLine, t.beginColumn));
			else 
				st.insertSymbol(new SymbolFunction(t.image, at.params, at.baseType,
					t.beginLine, t.beginColumn));
			at.name = t.image;
			at.line = t.beginLine;
			at.column = t.beginColumn;
		} catch (AlreadyDefinedSymbolException e) {
			se.detection(e, t);
			at.params = null;
		}
		st.insertBlock();
	}
	/* --------------------------------------------------------------------- */
	/* --------------------------------------------------------------------- */
	/* Procedimientos y funciones.                                           */
	/* --------------------------------------------------------------------- */
	/* Verifica el tipo de retorno de procedimiento o funcion.               */
	/* --------------------------------------------------------------------- */
	private void evaluateReturnHeader(Types def, Types type) throws ReturnHeaderDeclarationException {
		if (def == Types.PROCEDURE && def != type)
			throw new ReturnHeaderDeclarationException(true);
		if (def == Types.FUNCTION  && def != type)
			throw new ReturnHeaderDeclarationException(false);
	}

	public void EvaluateReturnHeader(Attributes at, Types type) {
		try {
			evaluateReturnHeader(at.type, type);
		} catch (ReturnHeaderDeclarationException e) {
			se.detection(e, at.line, at.column, at.name);
			at.baseType = Types.UNDEFINED;
		}
	}

	private void evaluatePutline(Types type) throws PutlineException {
		
	}

	public void EvaluatePutline(Attributes at) {
		try {
			evaluatePutline(at.baseType);
		} catch (PutlineException e) {
			System.err.println("Mensaje de error de que putline esta mal.");
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

	private boolean evaluateParClass(ParameterClass a, ParameterClass b) {
		if (a == ParameterClass.REF && b != ParameterClass.VAL) return true;
		if (a == ParameterClass.VAL && b != ParameterClass.REF) return true;
		return false;
	}

	private void evaluateProcedure(Types type) throws ProcedureNotFoundException {
		if (p == Procedure.GET && type != Types.INT && type != Types.CHAR)
			throw new ProcedureNotFoundException(p, type); 
		if((p == Procedure.PUT || p == Procedure.PUTLINE) && type == Types.UNDEFINED)
			throw new ProcedureNotFoundException(p, type); 
	}

	/******************************** GET ************************************/
	public void evaluateGet(Types type) throws GetException {
		if (type != Types.INT && type != Types.CHAR) throw new GetException(type);
	}

	public void EvaluateGet(Token t, Types type) {
		try {
			evaluateGet(type);
		} catch (GetException e) {
			se.detection(e, t);
		}
	}

	/******************************** PUT ************************************/
	private void evaluatePut(Types type) throws PutException {
		if (type == Types.UNDEFINED) throw new PutException(); 
	}

	public void EvaluatePut(Token t, Types type){
		try{
			evaluateProcedure(Procedure.PUT, at.baseType);
		}catch (PutException e){
			se.detection(e, t);
		}
	}

	/****************************** PUTLINE **********************************/
	private void evaluatePutline(Types type) throws 
		PutlineException
	{
		if (type == Types.UNDEFINED) throw new PutlineException(); 
	}

	public void EvaluatePutline(Token t, Types type) {
		try {
			evaluateGet(Procedure.PUTLINE, type);
		} catch (PutlineException e) {
			se.detection(e, t);
		}
	}

	/****************************** PROCEDURE ********************************/
	private void evaluateProcedure(Symbol s, Attributes at) throws 
		MismatchedSymbolTypeException, 
		MainProcedureCallException, 
		ProcedureNotFoundException
	{
		if (s.type != Types.PROCEDURE) 
			throw new MismatchedSymbolTypeException(s.type, Types.PROCEDURE);
		
		SymbolProcedure p = (SymbolProcedure) s;
		if (p.main) throw new MainProcedureCallException();
		if (p.parList.size() != at.given.size()) throw new ProcedureNotFoundException();
		for (int i = 0; i < p.parList.size(); i++) {
			//if (p.parList.get(i).type != at.given.get(0).baseType || !evaluateParClass(p.parList.get(i).parClass, at.given.get(i).parClass))
			if ((p.parList.get(i).type != at.given.get(i).baseType && p.parList.get(i).type!=Types.ARRAY) 
				|| (!evaluateParClass(p.parList.get(i).parClass, at.given.get(i).parClass))
				|| (p.parList.get(i).type==Types.ARRAY && ((SymbolArray)p.parList.get(i)).baseType != at.given.get(i).baseType)){
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


	private void evaluateFunction(Symbol s, Attributes at, Attributes fst) throws 
		MismatchedSymbolTypeException, 
		FunctionNotFoundException
	{
		if (s.type != Types.FUNCTION) {
			
			throw new MismatchedSymbolTypeException();
		}
		SymbolFunction f = (SymbolFunction) s;
		at.baseType = f.returnType;
		if (f.parList.size() != fst.given.size()) {
			throw new FunctionNotFoundException();
		}
		
		for (int i = 0; i < f.parList.size(); i++) {
				if ((f.parList.get(i).type != fst.given.get(i).baseType && f.parList.get(i).type!=Types.ARRAY) 
				|| (!evaluateParClass(f.parList.get(i).parClass, fst.given.get(i).parClass))
				|| (f.parList.get(i).type==Types.ARRAY && ((SymbolArray)f.parList.get(i)).baseType != fst.given.get(i).baseType)){
					throw new FunctionNotFoundException();
			}
		}
	}
	
	public void EvaluateFunction(SymbolTable st, Attributes at, Attributes fst) {
		try {
			Symbol s = st.getSymbol(fst.name); 
			evaluateFunction(s, at, fst);
		} catch (SymbolNotFoundException e) {
			System.err.println("Error -- symbol not declared.");
		} catch (FunctionNotFoundException e) {
			System.err.println("Funcion no encontrada.");
			at.baseType = Types.UNDEFINED;
		} catch (MismatchedSymbolTypeException e) {
			System.err.println("Inutil, utilizas un simbolo que no es procedimiento como procedimiento.");
		}
	}


	
	/* --------------------------------------------------------------------- */
	/* Verifica si una variable es asignable.                                */
	/* --------------------------------------------------------------------- */
	public Types evaluateAssignable(Symbol s, Types index_type, Types match_type) throws MismatchedSymbolTypeException, IndexNotIntegerException {
		if (match_type == Types.ARRAY) {
			if (index_type != Types.INT) throw new IndexNotIntegerException(index_type);
			if (match_type != s.type) throw new MismatchedSymbolTypeException(match_type, s.type);
			else return ((SymbolArray) s).baseType;
		} else {
			if (s.type != Types.INT &&  s.type != Types.CHAR && s.type != Types.BOOL)
				throw new MismatchedSymbolTypeException(match_type, s.type);
			else
				return s.type;
		}
	}

	public void EvaluateAssignable(Attributes at, Token t, Types match_type) {
		try {
			Symbol s = st.getSymbol(t.image);
			at.baseType = evaluateAssignable(s, at.baseType, match_type);
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		} catch (IndexNotIntegerException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		} catch (MismatchedSymbolTypeException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		}
	}
	/* --------------------------------------------------------------------- */

	
	/* --------------------------------------------------------------------- */
	/* Evalua una expresion.                                                 */
	/* --------------------------------------------------------------------- */
	private void evaluateExpression(Types fst) throws MismatchedTypesException {
		if (fst != Types.BOOL) throw new MismatchedTypesException();
	}
	
	private void evaluateExpression(Types fst, Types snd) throws MismatchedTypesException {
		
		if (fst != snd) {
			throw new MismatchedTypesException();} 
	}

	private void evaluateExpression(Attributes at, Types fst, Operator op, Types snd) throws MismatchedTypesException {
		if (op == Operator.CMP_OP) {
			if (fst == snd) at.baseType = Types.BOOL;
			else throw new MismatchedTypesException();
		} else if (op == Operator.INT_OP || op == Operator.BOOL_OP) {
			if ((fst == Types.INT && snd == Types.INT) || (fst == Types.BOOL && snd == Types.BOOL) ) 
				at.baseType = fst;
			else throw new MismatchedTypesException();
		}
	}

	public void EvaluateExpression(Attributes at) {
		try {
			evaluateExpression(at.baseType);
		} catch (MismatchedTypesException e) {
			System.err.println("ERROR DE TIPOS DISTINTOS (EXPRESSION). LUEGO PONEMOS ALGO MAS BONITO.");
			at.baseType = Types.UNDEFINED;
		}
	}

	public void EvaluateExpression(Attributes fst, Attributes snd) {
		try {
			evaluateExpression(fst.baseType, snd.baseType);
		} catch(MismatchedTypesException e){
			System.err.println("ERROR DE TIPOS DISTINTOS (EXPRESSION). LUEGO PONEMOS ALGO MAS BONITO.");
		}
	}

	public void EvaluateExpression(Attributes at, Attributes fst, Attributes snd) {
		try {
			evaluateExpression(at, fst.baseType, fst.op, snd.baseType);
		} catch(MismatchedTypesException e){
			System.err.println("ERROR DE TIPOS DISTINTOS (EXPRESSION). LUEGO PONEMOS ALGO MAS BONITO.");
			fst.baseType = at.baseType = Types.UNDEFINED;
		}
	}

	public void EvaluateExpression(Attributes at, Types type, int kind) {
		try {
			evaluateExpression(at.baseType, type);
		} catch (MismatchedTypesException e) {
			System.err.println("ERROR SE ESPERABA BOOL EN WHILE OR IF");
		}
	}

	/** EVALUATE IF */
	public void evaluateIf(Attributes at) throws MismatchedIfConditionType {
		if (type != Types.BOOL) throw new MismatchedIfConditionType(type);
	}

	public void EvaluateIf(Attributes at) {
		try {
			evaluateIf(at.baseType);
		} catch (MismatchedIfConditionType e) {
			se.detection(e, at.line, at.column);
		}
	}

	/** EVALUATE WHILE */
	private void evaluateWhile(Types type) throws MismatchedWhileConditionType {
		if (type != Types.BOOL) throw new MismatchedWhileConditionType(type);
	}

	public void EvaluateWhile(Attributes at) {
		try {
			evaluateWhile(at.baseType);
		} catch (MimstachedWhileConditionType e) {
			se.detection(e, at.line, at.column);
		}
	}

	
	
	/* --------------------------------------------------------------------- */
	/* Verifica si el simbolo es una expresion variable.                     */
	/* --------------------------------------------------------------------- */
	private void checkExpression(Attributes at, Symbol s, Types t) throws MismatchedSymbolTypeException {
		//Habra que comprobar el tipo del vector
		if (t == Types.UNDEFINED) {
			if (s.type != Types.INT && s.type != Types.CHAR && s.type != Types.BOOL && s.type != Types.ARRAY){ 
				throw new MismatchedSymbolTypeException();
			}
			else 
				at.baseType = s.type;
				if(s.type == Types.ARRAY){	//Es de tipo array asi que hay que guardar que es array y el tipo de array
					at.type = s.type;
					at.baseType = ((SymbolArray)s).baseType;	
				}else{
					at.baseType = s.type;
				}
		} else if (t == Types.ARRAY) {
			if (s.type != t) {
				throw new MismatchedSymbolTypeException();
			}
			else 
				at.baseType = ((SymbolArray) s).baseType;
		} else if (t == Types.FUNCTION) {
			if (s.type != t){
				throw new MismatchedSymbolTypeException();
			}
			else 
				at.baseType = ((SymbolFunction) s).returnType;
		}
	}

	public void CheckExpression(SymbolTable st, Attributes at, Token t, Types type) {
		Symbol s;
		try {
			at.baseType = Types.UNDEFINED;
			s = st.getSymbol(t.image);	//Se obtiene la variable
			checkExpression(at, s, type);
			at.name = s.name;
		} catch (SymbolNotFoundException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn + ") ERROR -- \'" + t.image + "\' not defined.");
		} catch (MismatchedSymbolTypeException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn + ") ERROR -- Expected " + type + " got " + e.toString());
		}
	}
	/* --------------------------------------------------------------------- */
	private void checkInt2Char(Attributes at, Attributes fst) throws MismatchedTypesException {
		if(fst.baseType != Types.INT) throw new MismatchedTypesException();
		else at.baseType = Types.CHAR;
	}

	public void CheckInt2Char(Attributes at, Attributes fst) {
		try {
			checkInt2Char(at, fst);
		} catch (MismatchedTypesException e) {
			System.err.println("Error -- int2char debe recibir como parametro un integer");
		}
	}

	private void checkChar2Int(Attributes at, Attributes fst) throws MismatchedTypesException {
		if(fst.baseType != Types.CHAR) throw new MismatchedTypesException();
		else at.baseType = Types.INT;
	}

	public void CheckChar2Int(Attributes at, Attributes fst) {
		try {
			checkChar2Int(at, fst);
		} catch (MismatchedTypesException e) {
			System.err.println("Error -- char2int debe recibir como parametro un character");
		}
	}
	
	/* --------------------------------------------------------------------- */
	/* MEMORABLE                                                             */
	/* --------------------------------------------------------------------- */

	public static void AlgoID(SymbolTable st, Attributes at, Token t){
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

	/* --------------------------------------------------------------------- */
	/* --------------------------------------------------------------------- */
	private void comprobarReturnIfEX(Attributes at,Attributes fst)throws ReturnIfException{
		at.haveReturn = true;
				if(at.baseType != fst.baseType && at.baseType != Types.UNDEFINED)
					System.err.println("Error -- Expected " + at.baseType + " value, got " + fst.baseType);
					throw new ReturnIfException();
	}

	public void comprobarReturnIf(Attributes at,Attributes fst){
		try{
			comprobarReturnIfEX( at, fst);
		}catch(ReturnIfException e){
			
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
