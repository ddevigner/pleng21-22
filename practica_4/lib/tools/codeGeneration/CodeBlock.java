//*****************************************************************
// File:   CodeBlock.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   abril 2022
// Coms:   Clase para almacenar y gestionar los distintos bloques
// 			de código que se van a generar. Aparte de instrucciones, 
// 			permite añadir etiquetas, comentarios y marcar con XML
// 			nuestro código. Hay un método adhoc para OSF con 
// 			etiqueta en vez de con @ de memoria. 
//*****************************************************************

package lib.tools.codeGeneration;

import java.util.ArrayList;

import lib.tools.codeGeneration.PCodeInstruction.OpCode;

public class CodeBlock {
	
	public enum BlockMode {PLAIN, XML};
	
	public static BlockMode generationMode = BlockMode.PLAIN; 
	
	ArrayList<CodeElement> code;
	
	public CodeBlock() {
		code = new ArrayList<CodeElement>(); 
	}

	public void addInst (OpCode opCode, int... args) {
		code.add(new PCodeInstruction(opCode, args)); 
	}
	
	public void addInst (OpCode opCode, String label) {
		code.add(new PCodeInstruction(opCode, label));
	}
	
	public void addOSFInst (int s, int l, String label) {
		code.add(new PCodeInstruction(OpCode.OSF, s, l, label)); 
	}

	public void encloseXMLTags(String XMLLabel) {
		code.add(0, new XMLTag("<"+XMLLabel+">"));
		code.add(new XMLTag("</"+XMLLabel+">")); 
	}
	
	public void addComment (String comment) {
		code.add(new Comment(comment)); 
	}
	
	public void addLabel (String label) {
		code.add(new Label(label)); 
	}
	
	public void addBlock (CodeBlock block) {
		code.addAll(block.code); 
	}
	
	public void clearBlock() {
		code.clear();
	}
	
	public String toString() {
		StringBuilder strBldr = new StringBuilder(); 
		for (CodeElement element: code) {
			if (element instanceof XMLTag) {
				if (generationMode == BlockMode.XML) {
					strBldr.append(element.toString());
					strBldr.append("\n"); 
				}
			}
			else {
				strBldr.append(element.toString());
				strBldr.append("\n"); 
			}
		}
		return strBldr.toString(); 
	}
	
	public boolean isEmpty() {
		return code.size() == 0; 
	}
	
}
