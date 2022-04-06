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
import lib.symbolTable.exceptions.*;
import lib.errores.*;

public class SemanticFunctions {
	private ErrorSemantico errSem; //clase común de errores semánticos

	public SemanticFunctions() {
		errSem = new ErrorSemantico();
	}

	//COMPLETAR

	public static void CreateClassVar(SymbolTable st, Token t, Symbol.Types var_type, Symbol.ParameterClass _class){
		if (_class != Symbol.ParameterClass.NONE) {
			if (var_type == Symbol.Types.INT) {
				st.insertSymbol(new SymbolInt(t.image, _class));
				System.err.println(new SymbolInt(t.image));
			} 
			else if (var_type == Symbol.Types.BOOL) {
				st.insertSymbol(new SymbolBool(t.image, _class));
				System.err.println(new SymbolBool(t.image));
			}
			else if (var_type == Symbol.Types.CHAR) {
				st.insertSymbol(new SymbolChar(t.image, _class));
				System.err.println(new SymbolChar(t.image));
			}
		} else {
			if (var_type == Symbol.Types.INT) {
				st.insertSymbol(new SymbolInt(t.image));
				System.err.println(new SymbolInt(t.image));
			} 
			else if (var_type == Symbol.Types.BOOL) {
				st.insertSymbol(new SymbolBool(t.image));
				System.err.println(new SymbolBool(t.image));
			}
			else if (var_type == Symbol.Types.CHAR) {
				st.insertSymbol(new SymbolChar(t.image));
				System.err.println(new SymbolChar(t.image));
			}
		}
	}

	public static void CreateClassVar(SymbolTable _st, String _name, int _nElem, Symbol.Types _baseType, Symbol.ParameterClass _class) {
		if (_class != Symbol.ParameterClass.NONE) {
			try {
				_st.insertSymbol(new SymbolArray(_name, _nElem, _baseType, _class));
			}
		} else {
			_st.insertSymbol(new SymbolArray(_name, _nElem, _baseType));
		}
	}
}
