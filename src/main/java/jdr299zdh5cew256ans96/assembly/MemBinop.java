package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class MemBinop extends Operand {

	private Register left;
	private String operator;
	private Operand right;

	public MemBinop(Register left, String operator, Operand right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public String toString() {
		return "qword ptr ["+left+" "+operator+" "+right+"]";
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
	public void allocateRegisters() {
		left.allocateRegisters();
		right.allocateRegisters();
	}

	@Override
	public boolean isMem() {
		return true;
	}
}
