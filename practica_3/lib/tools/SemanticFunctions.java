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
	private ErrorSemantico se; //clase común de errores semánticos
	
	public static enum Operator { NOP, INT_OP, BOOL_OP, CMP_OP };

	public SemanticFunctions() {
		se = new ErrorSemantico();
		
	}

	public ErrorSemantico getErrorSemantico(){
		return se;
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
	/* Anyadir nuevo procedimiento o funcion, o variable.                     */
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
		//System.out.println(st.toString());
	}

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
		//System.out.println(st.toString());
		st.insertBlock();
	}
	/* --------------------------------------------------------------------- */
	/* --------------------------------------------------------------------- */
	/* Procedimientos y funciones.                                           */
	/* --------------------------------------------------------------------- */
	/* Verifica el tipo de retorno de procedimiento o funcion.               */
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

	private void evaluateGet(Types type) throws ProcedureNotFoundException {
		if (type != Types.INT && type != Types.CHAR) throw new ProcedureNotFoundException(1,1);
	}

	public void EvaluateGet(Attributes at) {
		try {
			evaluateGet(at.baseType);
		} catch (ProcedureNotFoundException e) {
			System.err.println("Error -- Get solo recibe variables INT o CHAR");
		}
	}

	private void evaluatePut(Types type) throws PutException {
		if(type == Types.UNDEFINED) throw new PutException();
	}

	public void EvaluatePut(Attributes at){
		try{
			evaluatePut(at.baseType);
		}catch (PutException e){
			System.err.println("put necesita expresiones no nulas");
		}
	}

	private void evaluatePutline(Types type) throws PutlineException {
		if (type == Types.UNDEFINED) throw new PutlineException();
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

	private void evaluateProcedure(Symbol s, Attributes at) throws MismatchedSymbolTypeException, MainProcedureCallException, ProcedureNotFoundException {
		if (s.type != Types.PROCEDURE) throw new MismatchedSymbolTypeException();
		SymbolProcedure p = (SymbolProcedure s);
		if (p.main) throw new MainProcedureCallException();
		if (p.parList.size() != at.given.size()) throw new ProcedureNotFoundException();
		for (int i = 0; i < p.parList.size(); i++) {
			if (p.parList[i].baseType != at.given[0].baseType || !evaluateParClass(p.parList[i].parClass, at.given[i].parClass))
				throw new ProcedureNotFoundException();
		}
	}
	
	public void EvaluateProcedure(SymbolTable st, Attributes at) {
		try {
			Symbol s = st.getSmybol(at.name); 
			evaluateProcedure(s, at);
		} catch (SymbolNotFoundException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- symbol \'" + t.image + "\' not declared.");
		} catch (ProcedureNotFoundException e) {
			System.err.println("Procedimiento no encontrao.");
		} catch (MainProcedureCallException e) {
			System.err.println("Procedimiento main? Que coño haces?");
		} catch (MismatchedSymbolTypeException e) {
			System.err.println("Inutil, utilizas un simbolo que no es procedimiento como procedimiento.");
		}
	}

	private void evaluateFunction(Symbol s, Attributes at) throws MismatchedSymbolTypeException, FunctionNotFoundException {
		if (s.type != Types.FUNCTION) throw new MismatchedSymbolTypeException();
		SymbolFunction f = (SymbolFunction s);
		if (p.parList.size() != at.given.size()) throw new FunctionNotFoundException();
		for (int i = 0; i < p.parList.size(); i++) {
			if (p.parList[i].baseType != at.given[0].baseType || !evaluateParClass(p.parList[i].parClass, at.given[i].parClass))
				throw new FunctionNotFoundException();
		}
	}
	
	public void EvaluateFunction(SymbolTable st, Attributes at) {
		try {
			Symbol s = st.getSmybol(at.name); 
			evaluateFunction(s, at);
		} catch (SymbolNotFoundException e) {
			System.err.println("(" + t.beginLine + "," + t.beginColumn+ ") Error -- symbol \'" + t.image + "\' not declared.");
		} catch (FunctionNotFoundException e) {
			System.err.println("Funcion no encontraa.");
		} catch (MismatchedSymbolTypeException e) {
			System.err.println("Inutil, utilizas un simbolo que no es procedimiento como procedimiento.");
		}
	}


	
	/* --------------------------------------------------------------------- */
	/* Verifica si una variable es asignable.                                */
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
	private void evaluateExpression(Types fst) throws MismatchedTypesException {
		if (fst != Types.BOOL) throw new MismatchedTypesException();
	}
	
	private void evaluateExpression(Types fst, Types snd) throws MismatchedTypesException {
		if (fst != snd) throw new MismatchedTypesException(); 
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
	/* --------------------------------------------------------------------- */
	
	
	/* --------------------------------------------------------------------- */
	/* Verifica si el simbolo es una expresion variable.                     */
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