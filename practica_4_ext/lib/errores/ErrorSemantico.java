//*****************************************************************
// Descripcion: Tratamiento de errores sintácticos
// Fichero:    ErrorSemantico.java
// Fecha:      03/03/2022
// Versión:    v1.0
// Asignatura: Procesadores de Lenguajes, curso 2021-2022
//*****************************************************************
package lib.errores;

import traductor.Token;

import lib.symbolTable.exceptions.*; 
import lib.tools.exceptions.*;
import lib.symbolTable.Symbol.Types;

public class ErrorSemantico {
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

	public static int getErrors() {
		return errors;
	}

	public static int getWarnings() {
		return warnings;
	}

	// -- Se ha intentado añadir un simbolo ya existente.
	public static void detection(AlreadyDefinedSymbolException e, Token t) {
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "symbol '" + t.image + "' already"
			+ "exists.");
		System.err.println(sep);
	}

	// -- Se ha intentado usar un simbolo no definido.
	public static void detection(SymbolNotFoundException e, Token t) {
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "symbol '" + t.image + "' has "
			+ "not been defined.");
		System.err.println(sep);
	}

	// -- Se ha inicializado un array con tamaño 0.
	public static void detection(ZeroSizeArrayException e, Token t, Token i) {
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + "array '" + t.image + "' has " 
			+ "been declared with index zero.");
		System.err.println(sep);
	}

	// -- Acceso a array con indice no entero.
	public static void detection(IndexNotIntegerException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "arrays can only be " 
		    + "accessed with integer expressions.");
		System.err.println(sep);
	}

	// -- Se ha llamado al procedimiento principal.
	public static void detection(MainProcedureCallException e, Token t) 
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + e.error);
		System.err.println(sep);
	}

	// -- Uso incorrecto de un procedimiento existente.
	public static void detection(ProcedureNotFoundException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + e.info);
		System.err.println("--> procedure used '" + e.got + "'.");
		System.err.println("--> did you mean   '" + e.expected + "' ?");
		System.err.println(sep);
	}


	public static void detection(MismatchedParameterException e)
	{
		errors++;
	}

	// -- Uso incorrecto del procedimiento get.
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
		System.err.println("--> get usage: get(INT or CHAR [, ... ]);");
		System.err.println(sep);
	}

	// -- Uso incorrecto del procedimiento put.
	public static void detection(PutException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "invalid expression," 
			+ " can not evaluate put.");
		System.err.println(sep);
	}

	// -- Uso incorrecto del procedimiento putline.
	public static void detection(PutlineException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + "invalid expression," 
			+ " can not evaluate putline.");
		System.err.println(sep);
	}

	// -- Dos tipos en una expresion o una asignacion difieren.
	public static void detection(MismatchedTypesException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + e.error);
		System.err.println(sep);
	}

	// -- Dos tipos en una expresion o una asignacion difieren.
	public static void detection(MismatchedTypesException e, int line, int column)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(line, column) + e.error);
		System.err.println(sep);
	}


	// -- Se utiliza un simbolo de un tipo como otro tipo.
	public static void detection(MismatchedSymbolTypeException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + e.error);
		System.err.println(sep);
	}

	// -- Se utiliza una funcion existente de manera incorrecta.
	public static void detection(FunctionNotFoundException e, Token t)
	{
		errors++;
		System.err.println(sep);
		System.err.println(error_header(t) + e.info);
		System.err.println("--> function used '" + e.got + "'");
		System.err.println("--> did you mean  '" + e.expected + "' ?");
		System.err.println(sep);
	}

	// -- Un procedimiento tiene el tipo que devuelve o una funcion no la 
	// tiene declarada.
	public static void detection(ReturnTypeException e, int line, int column)
	{
		warnings++;
		System.err.println(sep);
		if (!e.proc_or_func) System.err.println(warning_header(line, column) + e.error);
		else System.err.println(error_header(line, column) + e.error);
		System.err.println(sep);
	}

	// -- El cuerpo de un procedimiento tiene declaracion de un return, una
	// funcion no tiene ningun return o el tipo en el return es diferente al 
	// tipo de la funcion.
	public static void detection(ReturnStatementException e, int line, int column)
	{
		System.err.println(sep);
		if (!e.proc_or_func || !e.heavy ) {
			warnings++;
			System.err.println(warning_header(line, column) + e.error);
		} else {
			errors++;
			System.err.println(error_header(line, column) + e.error);
		}
		System.err.println(sep);
	}

/*
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
	}*/
}
