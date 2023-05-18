package main.java.jdr299zdh5cew256ans96.assembly;

public class Jmp extends Instruction {

	private String jmpType;
	private String label;

	public Jmp(String jmpType, String label) {
		this.jmpType = jmpType;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean alwaysJump() {
		return jmpType.equals("jmp");
	}

	@Override
	public String toString() {
		return jmpType+" "+label;
	}

}
