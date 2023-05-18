package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class Push extends Instruction {
	private Operand val;

	public Push(String val) {
		this.val = new Register(val);
	}

	public Push(int val) {
		this.val = new Const(val);
	}

	@Override
	public String toString() {
		return "push "+val;
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
	public void calculateUse() {
		setUse(val.getRegs());
	}

	@Override
	public void allocateRegisters() {
		val.allocateRegisters();
		Instruction.regCounter = 10;
	}

}
