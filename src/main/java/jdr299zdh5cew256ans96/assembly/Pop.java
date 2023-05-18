package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class Pop extends Instruction {
	private Register val;

	public Pop(String val) {
		this.val = new Register(val);
	}

	@Override
	public String toString() {
		return "pop "+val;
	}

	public String getVal() {
		return val.getReg();
	}

	@Override
	public ArrayList<Register> getAbstractTemps() {
		return new ArrayList<>(val.getAbstractTemps());
	}

	@Override
	public ArrayList<Register> getPrecoloredTemps() {
		return new ArrayList<>(val.getPrecoloredTemps());
	}

	@Override
	public void calculateDef() {
		setDef(val.getRegs());
	}

	@Override
	public void allocateRegisters() {
		val.allocateRegisters();
		Instruction.regCounter = 10;
	}

}
