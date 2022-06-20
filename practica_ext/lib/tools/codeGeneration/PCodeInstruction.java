//*****************************************************************
// File:   PCodeInstruction.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   abril 2022
// Coms:   Elemento de código que representa una instrucción. 
//			Hereda de CodeElement
//*****************************************************************

package lib.tools.codeGeneration;

public class PCodeInstruction extends CodeElement{
	
	public enum OpCode {
		AND (0), 
		ASG (1), 
		ASGI (2), 
		CSF (3), 
		DIV (4), 
		DRF (5), 
		DUP (6), 
		ENP (7), 
		EQ (8), 
		GT (9), 
		GTE (10),
		JMF (11),
		JMP (12),
		JMT (13),
		LT (14),
		LTE (15),
		LVP (16),
		MOD (17),
		NEQ (18),
		NGB (19),
		NGI (20),
		NOP (21),
		OR (22),
		OSF (23),
		PLUS (24),
		POP (25),
		RD (26),
		SBT (27),
		SRF (28),
		STC (29),
		SWP (30),
		TMS (31),
		WRT (32); 
		
		int code; 
		private OpCode(int code) {
			this.code = code; 
		}
	}
	
	OpCode code; 
	int args[] = null; 
	String argetiq = ""; 
	
	public PCodeInstruction (OpCode code, int... args) {
		this.code = code; 
		this.args = new int[args.length]; 
		for (int i=0; i<args.length; i++) {
			this.args[i] = args[i]; 
		}
	}
	
	public PCodeInstruction (OpCode code, int size, int level, String label) {
		// only for OSF s l label
		this.code = code; 
		this.args = new int[2]; 
		this.args[0] = size; 
		this.args[1] = level; 
		this.argetiq = label; 
	}
	
	public PCodeInstruction (OpCode code, String label) {
		this.code = code; 
		this.argetiq = label; 
	}
	
	public String toString () {
		StringBuilder strBldr = new StringBuilder();
		strBldr.append("  "); 
		strBldr.append(code);
		if (code != OpCode.OSF) {
			if (args != null) {
				for (int i=0; i<args.length; i++) {
					strBldr.append (String.format(" %2d", args[i])); 
				}
			}
			else if (!"".equals(argetiq)) {
				strBldr.append(" "); 
				strBldr.append(argetiq); 
			}
		}
		else {
			for (int i=0; i<args.length; i++) {
				strBldr.append (String.format(" %2d", args[i])); 
			}
			if (!"".equals(argetiq)) {
				strBldr.append(" "); 
				strBldr.append(argetiq); 
			}
		}
		return strBldr.toString(); 
	}
	
}

