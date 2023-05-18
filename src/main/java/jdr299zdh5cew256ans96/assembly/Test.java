package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class Test extends Instruction{

	private Operand left;
	private Operand right;

	public Test(String left, String right) {
		this.left = new Register(left);
		this.right = new Register(right);
	}

	@Override
	public String toString() {
		return "test "+left + ", " + right;
	}

	@Override
	public ArrayList<Register> getAbstractTemps() {
		ArrayList<Register> abstractTemps = new ArrayList<>();
		abstractTemps.addAll(left.getAbstractTemps());
		abstractTemps.addAll(right.getAbstractTemps());
		return abstractTemps;
	}

	@Override
	public ArrayList<Register> getPrecoloredTemps() {
		ArrayList<Register> precoloredTemps = new ArrayList<>();
		precoloredTemps.addAll(left.getPrecoloredTemps());
		precoloredTemps.addAll(right.getPrecoloredTemps());
		return precoloredTemps;
	}

	@Override
	public void calculateUse() {
		ArrayList<Register> use = left.getRegs();
		use.addAll(right.getRegs());
		setUse(use);
	}

	@Override
	public void allocateRegisters() {
		left.allocateRegisters();
		right.allocateRegisters();
		Instruction.regCounter = 10;
	}
}
