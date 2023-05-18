package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class Binop extends Instruction {

	private Operand left;
	private Operand right;
	private String op;

	public Binop(String op, Operand left, Operand right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}

	public Binop(String op, String left, String right) {
		this.op = op;
		this.left = new Register(left);
		this.right = new Register(right);
	}

	public Binop(String op, String left, int right) {
		this.op = op;
		this.left = new Register(left);
		this.right = new Const(right);
	}

	public Binop(String op, String operand) {
		this.op = op;
		this.left = new Register(operand);
	}

	@Override
	public String toString() {
		if (right == null) {
			return op+" "+left;
		}
		return op + " " + left + ", " + right;
	}

	@Override
	public void calculateUse() {
		ArrayList<Register> use = right.getRegs();
		if (!op.equals("lea")) {
			use.addAll(left.getRegs());
		}
		setUse(use);
	}

	@Override
	public void calculateDef() {
		setDef(left.getRegs());
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
		Instruction.regCounter = 10;
	}
}
