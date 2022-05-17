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
import lib.symbolTable.Symbol.Types;

public class SemanticError {
	final static String sep = "*************************************************************************";

	private static int errors = 0;
	private static int warnings = 0;

	private static String error_header(Token t) {
		return "semantic error: (" + t.beginLine + "," + t.beginColumn + "): ";
	}

	private static String error_header(int beginLine, int beginColumn) {
		return "semantic error: (" + beginLine + "," + beginColumn + "): ";
	}

	private static String warning_header(Token t) {
		return "warning: (" + t.beginLine + "," + t.beginColumn + "): ";
	}

	private static String warning_header(int beginLine, int beginColumn) {
		return "warning: (" + beginLine + "," + beginColumn + "): ";
	}

	private static int padding(String a, String b) {
		return a.length() - b.length();
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
	public static void detection(IndexNotIntegerException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "arrays can only be " 
		    + "accessed with integer expressions.");
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
		System.err.println(error_header(t) + "error de procedure.");
		System.err.println(sep);
	}

	// Get exception.
	public static void detection(GetException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		if (e.type != Types.UNDEFINED) {
			System.err.println(error_header(line, column) + "expected character or integer,"
			+ " got " + e.type + ".");
		} else {
			System.err.println(error_header(line, column) + "invalid expression" 
				+ ", can not evaluate get.");
		}

		System.err.println(sep);
	}

	// PutException.
	public static void detection(PutException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "invalid expression," 
			+ " can not evaluate put.");
		System.err.println(sep);
	}

	// PutlineException.
	public static void detection(PutlineException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "invalid expression," 
			+ " can not evaluate putline.");
		System.err.println(sep);
	}

	// MismatchedTypesException 
	public static void detection(MismatchedTypesException e, Token t)
	{
		errors++;
		System.err.println(sep);
		if (e.expected != Types.UNDEFINED && e.got != Types.UNDEFINED) {
			System.err.println(error_header(t) + "expected " + e.expected 
				+ ", got " + e.got + "."); 
		} else {
			System.err.println(error_header(t) + "invalid expression, could " + 
				"not be evaluated.");
		}
		System.err.println(sep);
	}

	public static void detection(MismatchedTypesException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		if (e.expected != Types.UNDEFINED && e.got != Types.UNDEFINED) {
			System.err.println(error_header(line, column) + "expected " + e.expected 
				+ ", got " + e.got + "."); 
		} else {
			System.err.println(error_header(line, column) + "invalid expression"
				+ ", could not be evaluated.");
		}
		System.err.println(sep);
	}

	public static void detection(MismtachedCompareTypesException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "expected same type " 
			+ "operators, got" + e.fst + " and " + e.snd);
		System.err.println(sep);
	}

	public static void detection(MismtachedAddTypesException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "expected integer "
			+ "operators, got " + e.fst + " and " + e.snd);
		System.err.println(sep);
	}

	public static void detection(MismtachedProductTypesException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "expected boolean "
			+ "operators, got " + e.fst + " and " + e.snd);
		System.err.println(sep);
	}

	// -- MISMATCHED SYMBOL TYPE DETECTION.
	public static void detection(MismatchedSymbolTypeException e, Token t)
	{
		errors++;
		System.err.println(sep);
		if (e.expected == Types.UNDEFINED) {
			System.err.println(error_header(t) + "symbol '" + t.image + "' expected"
				+ " to be character, boolean or integer but got a(n) " + e.got);

		} else {
			System.err.println(error_header(t) + "symbol '" + t.image + "' expected"
				+ " to be a(n) " + e.expected + ", but got a(n) " + e.got);
		}
		System.err.println(sep);
	}

	public static void detection(FunctionNotFoundException e, Token t)
	{
		int pad = padding(error_header(t) + "function", "--> did you mean");
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "function '" + e.got + "' not found.");
		System.err.printf("%"+pad+"s%s\n", "", "--> did you mean '" + e.expected + "' ?");
		System.err.println(sep);
	}

	public static void detection(ProcedureReturnException e, Token t)
	{
		warnings++;
		System.err.println(sep);
		System.err.println(warning_header(t) + "return statement in procedure");
		System.err.println(sep);
	}

	public static void detection(ProcedureReturnTypeException e, int line, int column)
	{
		warnings++;
		System.err.println(sep);
		System.err.println(warning_header(line, column) + "return type in procedure");
		System.err.println(sep);
	}

	public static void detection(FunctionReturnException e, int line, int column)
	{
		System.err.println(sep);
		if (e.hasReturn) {
			errors++;
			System.err.println(error_header(line, column) + "return " + e.expected 
				+ " function with " + e.got + " return statement.");
		} else {
			warnings++;
			System.err.println(warning_header(line, column) + "missing return" 
				+ " statement in function");
		}
		System.err.println(sep);
	}

	public static void detection(FunctionReturnTypeException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "missing return type in function");
		System.err.println(sep);
	}


	// public static void deteccion(ActionInvocationException e, String mensaje, Token t) {
	// 	errors++;
	// 	System.err.println(sep);
	// 	System.err.println("ERROR SEMANTICO (" + t.beginLine + "," + t.beginColumn + "): " +
	// 			"Error al invocar a: '" + t.image + "'. " + mensaje);
	// 	System.err.println(sep);
	// }
/*
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
	}*/

	public static int getErrors() {
		return errors;
	}

	/*
	public static void detection(MismatchedTypesException e) {
		errors++;
		System.err.println("Error --  Mismatched types in expression");
	}*/
}
