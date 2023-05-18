package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public abstract class Operand {
	// TODO: do we still need this?
	public ArrayList<Register> getAbstractTemps() {
		return new ArrayList<>();
	}

	public ArrayList<Register> getPrecoloredTemps() {
		return new ArrayList<>();
	}

	public ArrayList<Register> getRegs() {
		ArrayList<Register> regs = new ArrayList<>(getAbstractTemps());
		regs.addAll(getPrecoloredTemps());
		return regs;
	}

	public boolean isMem() {
		return false;
	}

	public void allocateRegisters() { }
}
