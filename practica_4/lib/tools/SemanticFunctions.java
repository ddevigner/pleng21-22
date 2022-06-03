//*****************************************************************
// Descripcion: funciones de analisis semantico.
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
import lib.tools.exceptions.*;
import lib.errores.*;
import lib.tools.codeGeneration.*;
import lib.tools.codeGeneration.PCodeInstruction.OpCode;

public class SemanticFunctions {
	private ErrorSemantico se; //clase común de errores semánticos

	private SymbolTable st;
	
	public static enum Operator { NOP, INT_OP, BOOL_OP, CMP_OP };

	public SemanticFunctions(SymbolTable st) {
		this.st = st;
		se = new ErrorSemantico();
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
				//var.code.addComment("Se ha anyadido una variable de tipo array");
				int n = Integer.parseInt(i.image);
				checkArrayIndexDefinition(n);
				s = new SymbolArray(t.image, n, var.baseType, var.parClass);
			} else {
				if (var.baseType == Types.INT) {
					s = new SymbolInt(t.image, var.parClass);
				} else if (var.baseType == Types.CHAR) {
					s = new SymbolChar(t.image, var.parClass);
				} else {
					s = new SymbolBool(t.image, var.parClass);
				}
			}
			
			st.insertSymbol(s);
			if (var.params != null) st.insertSymbol(var.params, s);

			//Meter una variable como en la funcion
			if(var.params != null){
				// if(s.parClass == ParameterClass.REF){
				// 	var.code.addComment("Se anyade el parametro en el for este y es ref " + s.name);
				// 	//CGUtils.memorySpaces[st.level] += 1;
				// 	s.dir = CGUtils.memorySpaces[st.level]++;
				// 	long aux = s.dir;
				// 	//var.code.addComment("S.dir del simbolo es " + s.dir + " pero deberia ser " + CGUtils.memorySpaces[st.level]);
				// 	var.code.addInst(PCodeInstruction.OpCode.SRF,st.level-s.nivel,(int)aux);	//Aqui da error
				// 	var.code.addInst(PCodeInstruction.OpCode.ASGI);
				// }else if(s.parClass == ParameterClass.VAL && s.type != Types.ARRAY){
				// 	//CGUtils.memorySpaces[st.level] += 1;
				// 	var.code.addComment("Se anyade el parametro en el for este y es valor y no array " + s.name);
				// 	s.dir = CGUtils.memorySpaces[st.level]++;
				// 	long aux = s.dir;
				// 	var.code.addInst(PCodeInstruction.OpCode.SRF,st.level-s.nivel,(int)aux);	//Aqui da error
				// 	var.code.addInst(PCodeInstruction.OpCode.ASGI);
				// }else if(s.parClass == ParameterClass.VAL && s.type == Types.ARRAY){
				// 	s.dir = CGUtils.memorySpaces[st.level]++;
				// 	SymbolArray vec = (SymbolArray) s;
				// 	CGUtils.memorySpaces[st.level] += vec.maxInd;
				// 	var.code.addComment("Se suma al nivel " + st.level + "el tamanyo " + vec.maxInd);
				// 	//Se recorre el vector y se recupera cada Posicion 
				// 	for(int j=0;j<vec.maxInd;j++){
				// 		var.code.addComment("Se anyade el parametro en el for este y es valor y array " + s.name);
				// 		long aux = s.dir + (long) j;	//Revisarlo
				// 		var.code.addInst(PCodeInstruction.OpCode.SRF,st.level-s.nivel,(int)aux + j);	//Aqui da error
				// 		var.code.addInst(PCodeInstruction.OpCode.ASGI);
				// 	}
				// }
				
			}else{ //Se esta definiendo una funcion
				if (type == Types.ARRAY) {
					SymbolArray vec = (SymbolArray) s;
					s.dir = CGUtils.memorySpaces[st.level]++;
					CGUtils.memorySpaces[st.level] += vec.maxInd;
				} else {
					s.dir = CGUtils.memorySpaces[st.level]++;
					var.code.addComment("Se ha anyadido una variable de tipo normal");
				}
			}

		} catch (ZeroSizeArrayException e) {
			se.detection(e, t, i);
		} catch (AlreadyDefinedSymbolException e) {
			se.detection(e, t);
		}
	}

	public void AddMethod(Attributes at, Token t,String Label) {
		try {
			//Aqui al añadir un procedimiento le guardas su label de las funciones para poder llamarla en OSF
			at.name = t.image;
			if (!at.main) at.params = new ArrayList<>();
			if (at.type == Types.PROCEDURE){
				SymbolProcedure new_func = new SymbolProcedure(t.image, at.params, at.main);
				new_func.label = Label;
				st.insertSymbol(new_func);
			}
			else{ 
				SymbolFunction new_func = new SymbolFunction(t.image, at.params, at.baseType);
				new_func.label = Label;
				st.insertSymbol(new_func);
			}
		} catch (AlreadyDefinedSymbolException e) {
			se.detection(e, t);
			at.params = null;
		}
		st.insertBlock();
		at.line = t.beginLine;
		at.column = t.beginColumn;
	}
	
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
			if(at.baseType==Types.INT){	//Si es INT a RD se le pasa un 1
				at.code.addInst(OpCode.RD,1);
			}else if(at.baseType==Types.CHAR){	//Si es CHAR a RD se le pasa un 0
				at.code.addInst(OpCode.RD,0);
			}
			
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
			
			 SymbolProcedure p = (SymbolProcedure) s;
			
			//at.code.addOSFInst (st.level, st.level-s.nivel, p.label);	//La label sera la que se ha anyadido al simbol desdel su creacion
			at.code.addOSFInst (CGUtils.memorySpaces[st.level], st.level-s.nivel, p.label);
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
	//Aqui fst es el at que se devuelve asi que su code es el primero (bien)
	//Luego hay que anyadir el codigo de snd
	//Por utlimo anyades la operacion
	//Se hace asi porque hace por ejemplo en la suma push(pop + pop) por eso se hace asi
	private void evaluateOperation(Attributes fst, Attributes snd) throws MismatchedTypesException {
		if (snd.op == Operator.CMP_OP) {
			//System.out.println("Es una operacion de comparacion " + snd.op_name);
			//Mirar el numbre a ver que operacion es 
			if (fst.baseType != snd.baseType) { 
				throw new MismatchedTypesException(snd.op_name, Types.UNDEFINED, 
					fst.baseType, snd.baseType);
			}
			else fst.baseType = Types.BOOL;
		} else if (snd.op == Operator.INT_OP) {
			//System.out.println("Es una operacion de enteros " + snd.op_name);
			if (fst.baseType != Types.INT || snd.baseType != Types.INT) {
				throw new MismatchedTypesException(snd.op_name, Types.INT, 
					fst.baseType, snd.baseType);
			}
		} else if (snd.op == Operator.BOOL_OP) {
			//System.out.println("Es una operacion de booleanos " + snd.op_name);
			if (fst.baseType != Types.BOOL || snd.baseType != Types.BOOL) {
				throw new MismatchedTypesException(snd.op_name, Types.BOOL, 
					fst.baseType, snd.baseType);
			}
		}

		//Se anyade a at (en esta funcion se llama fst) el codigo de snd y luego el codigo del operando
		fst.code.addBlock(snd.code);
		if(snd.op_name=="+"){
			fst.code.addInst(PCodeInstruction.OpCode.PLUS);
		}else if(snd.op_name=="-"){
			fst.code.addInst(PCodeInstruction.OpCode.SBT);
		}else if(snd.op_name=="*"){
			fst.code.addInst(PCodeInstruction.OpCode.TMS);
		}else if(snd.op_name=="div"){
			fst.code.addInst(PCodeInstruction.OpCode.DIV);
		}else if(snd.op_name=="mod"){
			fst.code.addInst(PCodeInstruction.OpCode.MOD);
		}else if(snd.op_name=="and"){
			fst.code.addInst(PCodeInstruction.OpCode.AND);
		}else if(snd.op_name=="or"){
			fst.code.addInst(PCodeInstruction.OpCode.OR);
		}else if(snd.op_name=="="){
			fst.code.addInst(PCodeInstruction.OpCode.EQ);
		}else if(snd.op_name=="<>"){
			fst.code.addInst(PCodeInstruction.OpCode.NEQ);
		}else if(snd.op_name=="<"){
			fst.code.addInst(PCodeInstruction.OpCode.LT);
		}else if(snd.op_name=="<="){
			fst.code.addInst(PCodeInstruction.OpCode.LTE);
		}else if(snd.op_name==">"){
			fst.code.addInst(PCodeInstruction.OpCode.GT);
		}else if(snd.op_name==">="){
			fst.code.addInst(PCodeInstruction.OpCode.GTE);
		}else if(snd.op_name=="not"){
			fst.code.addInst(PCodeInstruction.OpCode.NGB);
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
	private Types evaluateInt2Char(Types type) throws FunctionNotFoundException {
		if(type != Types.INT) {
			if (type != Types.UNDEFINED) {
				throw new FunctionNotFoundException("int2char(INT) -> CHAR",
					"int2char(" + type + ") -> CHAR", Types.INT, type, 1);
			} else {
				throw new FunctionNotFoundException("int2char(INT) -> CHAR",
				"int2char(<undefined>) -> CHAR", Types.INT, type, 1);
			}
		}
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
		if(type != Types.CHAR) {
			if (type != Types.UNDEFINED) {
				throw new FunctionNotFoundException("char2int(CHAR) -> INT",
					"char2int(" + type + ") -> INT", Types.CHAR, type, 1);
			} else {
				throw new FunctionNotFoundException("char2int(CHAR) -> INT",
					"char2int(<undefined>) -> INT", Types.CHAR, type, 1);
			}
		}
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

		//Comprobar la lista de parametros pasados con los que deberia ser
		/////////////////////////////////////////////////////
		//********************************************* */
		////////////////////////////////////////////////////

		// for (int i = 0; i < f.parList.size(); i++) {
		// 	try {
		// 		Symbol e = f.parList.get(i);	//El symbol de cada hueco del parametro de la funcion
		// 		Attributes g = at.given.get(i);	//Los attributes de los parametros pasados
		// 		Symbol sym = st.getSymbol(g.name);
		// 		long aux = sym.dir;
		// 		at.code.addComment("Se anyade un parametro desde evaluateFunction");
		// 		if(e.parClass==ParameterClass.VAL){
		// 			if(g.parClass==ParameterClass.NONE){
		// 				at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-sym.nivel,(int)aux);
		// 				at.code.addInst(PCodeInstruction.OpCode.DRF);
		// 			}else if(g.parClass==ParameterClass.VAL){
		// 				at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-sym.nivel,(int)aux);
		// 				at.code.addInst(PCodeInstruction.OpCode.DRF);
		// 			}else if(g.parClass==ParameterClass.REF){
		// 				at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-sym.nivel,(int)aux);
		// 				at.code.addInst(PCodeInstruction.OpCode.DRF);
		// 				at.code.addInst(PCodeInstruction.OpCode.DRF);
		// 			}
		// 		}else if(e.parClass==ParameterClass.REF){
		// 			if(g.parClass==ParameterClass.NONE){
		// 				at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-sym.nivel,(int)aux);
		// 			}else if(g.parClass==ParameterClass.VAL){
		// 				at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-sym.nivel,(int)aux);
		// 				at.code.addInst(PCodeInstruction.OpCode.DRF);
		// 			}else if(g.parClass==ParameterClass.REF){
		// 				at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-sym.nivel,(int)aux);
		// 				at.code.addInst(PCodeInstruction.OpCode.DRF);
		// 			}
		// 		}
		// 	}catch (SymbolNotFoundException e) {
				
		// 	}
		// }

		/////////////////////////////////////////////////////
		//********************************************* */
		////////////////////////////////////////////////////
	}

	public void EvaluateFunction(Attributes at, Token t) {		//Cuidado que a lo mejor aqui no hay que comprobar parametros, a lo mejor hay que hcaerlo al anydadir la variable
		try {
			System.out.println("Se llama a EvaluateFunction para " + t.image);
			Symbol s = st.getSymbol(t.image); 
			evaluateFunction(s, at, t);
			
			//Se anyaden las cosas 
			SymbolFunction p = (SymbolFunction) s;
			long aux;
			//at.code.addOSFInst (st.level, st.level-s.nivel, p.label);	//La label sera la que se ha anyadido al simbol desdel su creacion
			at.code.addComment("Se llama a la funcion " + t.image);
			at.code.addOSFInst (CGUtils.memorySpaces[st.level], st.level-s.nivel, p.label);
			//cgu.memorySpaces[st.level]
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
			long aux= s.dir;
			
			if(s.type != Types.ARRAY){	//El tipo array funciona distinto
				if(s.parClass == ParameterClass.VAL){	//Pusheo el valor en la pila
					//at.code.addInst(PCodeInstruction.OpCode.SRF)
					at.code.addComment("Se anyade el parametro y es VAL");
					at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-s.nivel,(int)aux);	//DRF mete en la pila la direccion de la variable
					at.code.addInst(PCodeInstruction.OpCode.DRF);	//Se mete el valor de la variable en la pila, DRF mete en la pila el valor de la direccion que hay en la pila
				}else if(s.parClass == ParameterClass.REF){
					at.code.addComment("Se anyade el parametro y es REF");
					at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-s.nivel,(int)aux);
					at.code.addInst(PCodeInstruction.OpCode.DRF);
					at.code.addInst(PCodeInstruction.OpCode.DRF);
					//at.code.addInst(PCodeInstruction.OpCode.DRF);
				}else if(s.parClass == ParameterClass.NONE){	//El none es si es una variable local, entonces deberia ser tratada igual que REF porque puede cambiar su valor???
					at.code.addComment("Se anyade el parametro y es NONE");
					at.code.addInst(PCodeInstruction.OpCode.SRF,st.level-s.nivel,(int)aux);
					at.code.addInst(PCodeInstruction.OpCode.DRF);
				}else{
					at.code.addComment("Se anyade el parametro y no se mete en nada");
				}
			}else{	//Es de tipo array. Si es valor meto cada una de sus componentes
				// if(s.parClass == ParameterClass.VAL){

				// }
			}

			at.code.addComment("Se anyade el parametro !!!!!"+s.name);
		} catch (SymbolNotFoundException e) {
			se.detection(e, t);
			at.baseType = Types.UNDEFINED;
		}
	}
	//Cuando anyades una variable local que es un vector entonces sumas a memorySpaces el tamanyo del vector
	//La cosa es que al pasar un parametro este se convierte en local a la funcion por
	//tanto hay que anyadirlo tambien a memorySpaces
	//Cuando defines la funcion tienes que modificar memorySpaces (anyades 1 por cada variable)
	//Para int y char da igual ya que tanto por valor como por referencia ocupan 1
	//Pero en el caso del vector si es por referencia anyades 1 ya que solo es la direccion del vector
	//Pero si es por valor tienes que sumar 1 por cada componente del vector
	//Ademas al recuperar los argumentos si el vector es por referencia lo recuperas a el solo
	//Pero si el vector es por valor entonces recuperas cada una de las componentes

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

		// Se mete el valor en la pila
		at.code.addInst(PCodeInstruction.OpCode.STC,at.intVal);
	}

	// -- Constante caracter.
	public void EvaluateChar(Attributes at, Token t) {
		at.baseType = Types.CHAR;
		at.parClass = ParameterClass.VAL;
		at.charVal  = t.image.charAt(0);
		at.line = t.beginLine;
		at.column = t.beginColumn;

		// Se mete el valor en la pila
		at.code.addInst(PCodeInstruction.OpCode.STC,at.charVal);
	}

	// -- Constante booleana.
	public void EvaluateBool(Attributes at, Token t) {
        at.baseType = Types.BOOL;
        at.parClass = ParameterClass.VAL;
		at.line = t.beginLine;
		at.column = t.beginColumn;

		// Se mete el valor en la pila: 1 si true o 0 si false.
		if (t.image == "false") {
			at.boolVal = false;
			at.code.addInst(OpCode.STC, 1);
		} else {
			at.boolVal = true;
			at.code.addInst(OpCode.STC, 0);
		}
	}

	// -- Constante string.
	public void EvaluateString(Attributes at, Token t) {
		at.baseType = Types.STRING;
		at.parClass = ParameterClass.VAL;
		at.stringVal   = t.image;
		at.line = t.beginLine;
		at.column = t.beginColumn;
		
		for (int i = t.image.length() - 2; i > 0; i--) 
			at.code.addInst(OpCode.STC, t.image.charAt(i));
		at.code.encloseXMLTags("String_" + t.image);
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
