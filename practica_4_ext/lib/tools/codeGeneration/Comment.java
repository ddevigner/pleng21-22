//*****************************************************************
// File:   Comment.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   abril 2022
// Coms:   Elemento de código que representa un comentario. 
//			Hereda de CodeElement
//*****************************************************************

package lib.tools.codeGeneration;

public class Comment extends CodeElement{
	String comment; 
	
	public Comment (String comment) {
		this.comment = comment; 
	}
	
	public String toString () {
		return "; --"+comment; 
	}
}
