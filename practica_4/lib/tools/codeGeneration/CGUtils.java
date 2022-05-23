//*****************************************************************
// File:   CGUtils.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   abril 2022
// Coms:   Librería con el método para crear etiquetas frescas y 
// 			una variable estática para llevar la traza de las 
// 			direcciones de memoria 
//*****************************************************************

package lib.tools.codeGeneration;

import lib.tools.codeGeneration.PCodeInstruction.OpCode;

public class CGUtils {
	
	private static int l=0; 
	
	public static String newLabel () {
		return "L"+(l++); 
	}
	
	// campo requeridos para la generación de código
	public static int memorySpaces[] = new int[100];
	//gestionar los bloques de memoria llevando cuenta del tamaño de cada bloque de activacion
	//Aqui se mete el tamanyo del bloque del nivl en el que estas
	//El main pondras tres
	//Luego cuando anyadas variables o funciones(creo) haras memorySpaces[st.level]++;

	//Por ejemplo quiero hacer un SRF
	//Pues mi nivel actual es SymbolTable.level
	//El nivel de la variable es Symbol.nivel
	//El ofsset sera direccion=memorySpaces[nivel]++ y se guarda eso en la variable dir de Symbol


	//Si se pasa un parametro por referencia entonces se pasa su direccion de memoria (SRF)
	//Si es por valor se pasa solo su valor (SRF + DRF)
	
}
