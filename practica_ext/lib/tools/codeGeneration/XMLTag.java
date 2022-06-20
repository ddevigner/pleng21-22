//*****************************************************************
// File:   XMLTag.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   abril 2022
// Coms:   Elemento de código que representa una etiqueta XML. 
//			Hereda de CodeElement
//*****************************************************************

package lib.tools.codeGeneration;

public class XMLTag extends CodeElement{
	String label; 
	public XMLTag (String label) {
		this.label= label; 
	}
	public String toString () {
		return label; 
	}
}
