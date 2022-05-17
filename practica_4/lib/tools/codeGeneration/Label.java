//*****************************************************************
// File:   Label.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   abril 2022
// Coms:   Elemento de código que representa una etiqueta. 
//			Hereda de CodeElement
//*****************************************************************

package lib.tools.codeGeneration;

public class Label extends CodeElement {
	
	String label;
	
	public Label (String label) {
		this.label = label; 
	}
	
	public String toString() {
		return label+":"; 
	}
}
