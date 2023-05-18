package main.java.jdr299zdh5cew256ans96.assembly;

public class Global extends Instruction {

	private String val;

	public Global(String val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return ".quad "+val;
	}

}
