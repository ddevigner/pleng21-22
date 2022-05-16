//*****************************************************************
// Tratamiento de errores sintácticos
//
// Fichero:    ErrorSintactico.java
// Fecha:      03/03/2022
// Versión:    v1.0
// Asignatura: Procesadores de Lenguajes, curso 2021-2022
//*****************************************************************

package lib.errores;

import traductor.Token;

import lib.symbolTable.exceptions.*; 

public class SemanticError {
	final static String sep = "*************************************************************************";

	private static int errors = 0;
	private static int warnings = 0;

	private static String error_header(Token t) {
		return "semantic error: (" + t.beginLine + "," + t.beginColumn + "): ";
	}


	// -- ALREADY DEFINED SYMBOL DETECTION.
	public static void detection(AlreadyDefinedSymbolException e, Token t) {
		errors++;
		System.err.println(sep);
		System.err.println("ERROR SEMANTICO (" + t.beginLine + "," + t.beginColumn + "): " +
				"Símbolo: '" + t.image + "'. No se puede redefinir el símbolo");
		System.err.println(sep);
	}

	// -- SYMBOL NOT FOUND DETECTION.
	public static void detection(SymbolNotFoundException e, Token t) {
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "symbol '" + t.image + "' has "
			+ "not been defined.");
		System.err.println(sep);
	}

	// -- ZERO SIZE ARRAY DETECTION.
	public static void detection(ZeroSizeArrayException e, Token t, Token i) {
		errors++;
		System.err.println(sep);
		System.err.println("SEMANTIC ERROR (" + i.beginLine + "," + i.beginColumn + "): " + 
				"Array '" + t.image + "' has been declared with index zero.");
		System.err.println(sep);
	}

	// -- INVALID RETURN HEADER DECLARATION.
	public static void detection(ReturnHeaderDeclarationException e, int line, 
		int column, String name)
	{
		System.err.println(sep);
		if (!e.is_procedure) {
			errors++;
			System.err.println("warning: (" + line + "," + column + "): " + "'" 
				+ name + "' function declaration needs a return type.");
		} else {
			warnings++;
			System.err.println("semantic error: (" + line + "," + column + "): " 
			+ "'" + name + "' procedure has a return type when is not needed.");
		}
		System.err.println(sep);
	}

	// -- INDEX NOT INTEGER TYPE.
	public static void detection(IndexNotIntegerException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "arrays can only be accessed " 
			+ "with integer expressions.");
		System.err.println(sep);
	}

	// -- MISMATCHED SYMBOL TYPE DETECTION.
	public static void detection(MismatchedSymbolTypeException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "symbol '" + t.image + "' of type " 
			+ e.symbol_type + " is used as " + e.match_type);
		System.err.println(sep);
	}

	// -- MAIN PROCEDURE CALLED DETECTION.
	public static void detection(MainProcedureCallException e) 
	{
		errors++;
		System.err.println(sep);
		System.err.println(e.toString());
		System.err.println(sep);
	}

	// CAMBIAR.
	public static void detection(ProcedureNotFoundException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + e.toString());
		System.err.println(sep);
	}

	public static void detection(GetException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "expected character or integer,"
			+ " got " + e.type);
		System.err.println(sep);
	}

	public static void detection(PutException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "incorrect usage of put.");
		System.err.println(sep);
	}

	public static void detection(PutlineException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "incorrect usage of putline.");
		System.err.println(sep);
	}



	// public static void deteccion(ActionInvocationException e, String mensaje, Token t) {
	// 	errors++;
	// 	System.err.println(sep);
	// 	System.err.println("ERROR SEMANTICO (" + t.beginLine + "," + t.beginColumn + "): " +
	// 			"Error al invocar a: '" + t.image + "'. " + mensaje);
	// 	System.err.println(sep);
	// }

	public static void deteccion(String mensaje, Token t) {
		errors++;
		System.err.println(sep);
		System.err.println("ERROR SEMANTICO (" + t.beginLine + "," + t.beginColumn + "): " +
				"Símbolo: '" + t.image + "'. " + mensaje);
		System.err.println(sep);
	}

	public static void warning(String mensaje, Token t) {
		warnings++;
		System.err.println(sep);
		System.err.println("WARNING: (" + t.beginLine + "," + t.beginColumn + "): " +
				"Símbolo: '" + t.image + "'. " + mensaje);
		System.err.println(sep);
	}

	public static int getErrors() {
		return errors;
	}
}
