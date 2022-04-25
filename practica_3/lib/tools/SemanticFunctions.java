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

	public static enum Operator { NOP, INT_OP, BOOL_OP, CMP_OP };

	public SemanticFunctions() {
		errSem = new ErrorSemantico();
	}

	/* --------------------------------------------------------------------- */
	/* Verifica si el acceso al array es mediante un indice entero.          */
	/* --------------------------------------------------------------------- */
	private void checkIntegerIndexing(Types type) throws IndexNotIntegerException {
		if (type != Types.INT) throw new IndexNotIntegerException();
	}

	public void CheckIntegerIndexing(Types type) {
		try {
			checkIntegerIndexing(type);
		} catch (IndexNotIntegerException e) {
			System.err.println("ERROR -- Index expression is not integer");
		}
	}
	/* --------------------------------------------------------------------- */


	/* --------------------------------------------------------------------- */
	/* Añadir nuevo procedimiento o funcion, o variable.                     */
	/* --------------------------------------------------------------------- */
	private void evaluateDefinedReturnType(Attributes at, Types type) throws DefinedReturnTypeException {
		if (at.type == Types.PROCEDURE && at.type != type)
			throw new DefinedReturnTypeException(true);
		if (at.type == Types.FUNCTION  && at.type != type)
			throw new DefinedReturnTypeException(false);
	}

	public void EvaluateDefinedReturnType(Attributes at, Types type) {
		try {
			evaluateDefinedReturnType(at, type);
		} catch (DefinedReturnTypeException e) {
			System.err.println(e.toString());
			at.baseType = Types.UNDEFINED;
		}
	}
	/* --------------------------------------------------------------------- */


	/* --------------------------------------------------------------------- */
	/* Añadir nuevo procedimiento o funcion, o variable.                     */
	/* --------------------------------------------------------------------- */
	private void checkArrayIndexDefinition(int n) throws ZeroSizeArrayException {
		if (n == 0) throw new ZeroSizeArrayException();
	}

	public void AddVar(SymbolTable st, Attributes at, Token t, int n) {
		Symbol s;
		try {
			if (at.type == Types.ARRAY) {
				checkArrayIndexDefinition(n);
				s = new SymbolArray(t.image, n, at.baseType, at.parClass, t.beginLine, t.beginColumn);
			} else if (at.baseType == Types.INT) 
				s = new SymbolInt(t.image, at.parClass, t.beginLine, t.beginColumn);
			else if (at.baseType == Types.CHAR)
				s = new SymbolChar(t.image, at.parClass, t.beginLine, t.beginColumn);
			else
				s = new SymbolBool(t.image, at.parClass, t.beginLine, t.beginColumn);
			if (at.params != null) st.insertSymbol(at.params, s);
			st.insertSymbol(s);
		} catch (ZeroSizeArrayException e) {
			System.err.println("ERROR AL USAR INDICE MENOR O IGUAL QUE 0 PARA ARRAY.");
		} catch (AlreadyDefinedSymbolException e) {
			System.err.println("ERROR VARIABLE YA DEFINIDA AL AGREGAR UNA NUEVA.");
		}
		System.out.println(st.toString());
	}

	/* --------------------------------------------------------------------- */
	
	public void AddMethod(SymbolTable st, Attributes at, Token t) {
		Symbol s;
		if (!at.main) at.params = new ArrayList<>();
		try {
			if (at.type == Types.PROCEDURE)
				st.insertSymbol(new SymbolProcedure(t.image, at.params, at.main, t.beginLine, t.beginColumn));
			else
				st.insertSymbol(new SymbolFunction(t.image, at.params, at.baseType,  t.beginLine, t.beginColumn));
		} catch (AlreadyDefinedSymbolException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn + 
				") Error -- Simbolo \'" + t.image + "\' ya existente");
			at.params = null;
		}
		System.out.println(st.toString());
		st.insertBlock();
	}
	/* --------------------------------------------------------------------- */

	
	/* --------------------------------------------------------------------- */
	/* Verifica si las variables recuperadas son asignables.                 */
	/* --------------------------------------------------------------------- */
	public void checkAssignable(Attributes at, Symbol s, Types type) throws SymbolNotAssignableException {
		if (type == Types.ARRAY) {
			if (s.type != type)
				throw new SymbolNotAssignableException();
			else 
				at.baseType = ((SymbolArray) s).baseType;
		} else if(type == Types.UNDEFINED) {
			if (s.type != Types.INT &&  s.type != Types.CHAR && s.type != Types.BOOL)
				throw new SymbolNotAssignableException();
			else
				at.baseType = s.type;
		}
	}

	public void CheckAssignable(SymbolTable st, Attributes at, Token t, Types type) {
		Symbol s;
		try {
			at.baseType = Types.UNDEFINED;
			s = st.getSymbol(t.image);
			checkAssignable(at, s, type);
		} catch (SymbolNotFoundException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn + ") ERROR -- \'" + t.image + "\' not defined.");
		} catch (SymbolNotAssignableException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn + ") ERROR -- Expected " + type + " got " + e.toString());
		}
	}
	/* --------------------------------------------------------------------- */


	/* --------------------------------------------------------------------- */
	/* Evalua una expresion.                                                 */
	/* --------------------------------------------------------------------- */
	private void evaluateExpression(Types fst, Operator op, Types snd) throws MismatchedTypesException {
		if (op == Operator.CMP_OP && fst != snd)
			throw new MismatchedTypesException();
		if (op == Operator.INT_OP && (fst != Types.INT || snd != Types.INT))
			throw new MismatchedTypesException();
		if (op == Operator.BOOL_OP && (fst != Types.BOOL || snd != Types.BOOL))
			throw new MismatchedTypesException();
		if (fst != snd) 
			throw new MismatchedTypesException();
	}

	public void EvaluateExpression(Attributes at, Attributes fst, Attributes snd) {
		try {
			evaluateExpression(fst.baseType, fst.op, snd.baseType);
			at.baseType = fst.baseType;
			at.parClass = ParameterClass.VAL;
		}catch(MismatchedTypesException e){
			System.err.println("ERROR DE TIPOS DISTINTOS (EXPRESSION). LUEGO PONEMOS ALGO MAS BONITO.");
		}
	}
	/* --------------------------------------------------------------------- */
	
	
	/* --------------------------------------------------------------------- */
	/* Verifica si el simbolo es una expresion con tipo asignable.           */
	/* --------------------------------------------------------------------- */
	private void checkExpression(Attributes at, Symbol s, Types t) throws MismatchedSymbolTypeException {
		if (t == Types.UNDEFINED) {
			if (s.type != Types.INT && s.type != Types.CHAR && s.type != Types.BOOL) 
				throw new MismatchedSymbolTypeException();
			else 
				at.baseType = s.type;
		} else if (t == Types.ARRAY) {
			if (s.type != t) 
				throw new MismatchedSymbolTypeException();
			else 
				at.baseType = ((SymbolArray) s).baseType;
		} else if (t == Types.FUNCTION) {
			if (s.type != t)
				throw new MismatchedSymbolTypeException();
			else 
				at.baseType = ((SymbolFunction) s).returnType;
		}
	}

	public void CheckExpression(SymbolTable st, Attributes at, Token t, Types type) {
		Symbol s;
		try {
			at.baseType = Types.UNDEFINED;
			s = st.getSymbol(t.image);
			checkExpression(at, s, type);
		} catch (SymbolNotFoundException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn + ") ERROR -- \'" + t.image + "\' not defined.");
		} catch (MismatchedSymbolTypeException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn + ") ERROR -- Expected " + type + " got " + e.toString());
		}
	}
	/* --------------------------------------------------------------------- */

	
	/* --------------------------------------------------------------------- */
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





















	// --
	public static boolean CheckParClass(Symbol.ParameterClass fst, Symbol.ParameterClass snd) {
		return (fst == ParameterClass.VAL || (fst == ParameterClass.REF && snd != ParameterClass.VAL));
	}

	public static void comprobarGetEX(Symbol var)throws GetException{
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

	public static void comprobarGet(Symbol var){
		try{
			comprobarGetEX( var);
		}catch(GetException e){
			
		}
	}

	public static void comprobarAssignableGetEX(Symbol var)throws AGetException{
		if (var.type == Types.ARRAY) {
			if(((SymbolArray) var).baseType != Types.CHAR && ((SymbolArray) var).baseType != Types.INT) 
				System.err.println("Error -- Get(). Expected char or int, got " + ((SymbolArray) var).baseType);
				throw new AGetException();
		}
		else if (var.type == Types.CHAR && var.type != Types.INT)
			System.err.println("Error -- Get(). Expected char or int, got " + var.type);
			throw new AGetException();
	}
	
	public static void comprobarAssignableGet(Symbol var){
		try{
			comprobarAssignableGetEX(var);
		}catch(AGetException e){

		}
	}

	public static void comprobarPutEX(Attributes fst)throws PutException{
		if(fst.type == Types.UNDEFINED){	//Se le pasa una expresion indefinida
				System.err.println("put necesita expresiones no nulas");
				throw new PutException();
		}
	}

	public static void comprobarPut(Attributes fst){
		try{
			comprobarPutEX( fst);
		}catch(PutException e){

		}
	}

	public static void comprobarPutLineEX(Attributes fst)throws PutLineException{
		if(fst.type == Types.UNDEFINED){	//Se le pasa una expresion indefinida
				System.err.println("put_line necesita expresiones no nulas");
				throw new PutLineException();
		}
	}

	public static void comprobarPutLine(Attributes fst){
		try{
			comprobarPutLineEX( fst);
		}catch(PutLineException e){

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

	public static void comprobarNumArgumentosEX(Token t,SymbolProcedure s,int i)throws NArgsException{
		if (s != null && i != s.parList.size()) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- Bad number of parameters");
			throw new NArgsException();
		}
	}

	public static void comprobarNumArgumentos(Token t,SymbolProcedure s,int i){
		try{
			comprobarNumArgumentosEX( t, s, i);
		}catch(NArgsException e){
			
		}
	}

	public static void comprobarWhileEX(Attributes fst)throws WhileBooleanException{
		// if(fst.type != Types.BOOL)
		// 	System.err.println("Error -- No poner guardas no booleanas en if");
		if(fst.type != Types.BOOL){
			System.err.println("Error -- No poner guardas no booleanas en while");
			throw new WhileBooleanException();
		}
		
	}

	public static void comprobarWhile(Attributes fst){
		try{
			comprobarWhileEX( fst);
		}catch(WhileBooleanException e){
			
		}
	}

	public static void comprobarIfEX(Attributes fst)throws IfException{
		if(fst.type != Types.BOOL)
			System.err.println("Error -- No poner guardas no booleanas en if");
			throw new IfException();
	}

	public static void comprobarIf(Attributes fst){
		try{
			comprobarIfEX( fst);
		}catch(IfException e){
			
		}
	}

	public static void comprobarReturnIfEX(Attributes at,Attributes fst)throws ReturnIfException{
		at.haveReturn = true;
				if(at.returnType != fst.type && at.returnType != Types.UNDEFINED)
					System.err.println("Error -- Expected " + at.returnType + " value, got " + fst.type);
					throw new ReturnIfException();
	}

	public static void comprobarReturnIf(Attributes at,Attributes fst){
		try{
			comprobarReturnIfEX( at, fst);
		}catch(ReturnIfException e){
			
		}
	}

	public static void comprobarFactorEX(Attributes fst,Attributes snd,Attributes at,Attributes op)throws FactorException{
		if (op.op == Operator.INT_OP) {
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

	public static void comprobarFactor(Attributes fst,Attributes snd,Attributes at,Attributes op){
		try{
			comprobarFactorEX(fst,snd,at,op);
		}catch(FactorException e){
			
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

}
