package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class Register extends Operand {

	private String reg;

	public Register(String reg) {
		this.reg = reg;
	}

	public String toString() {
		return reg;
	}

	public boolean isAbstractTemp() {
		if (reg.contains("string_const") || isGlobal(reg)) {
			return false;
		}
		return !Assembly.x86regs.contains(reg);
	}
	public boolean isMachineReg() {
		return Assembly.x86regs.contains(reg);
	}

	public void setReg(String reg) {
		this.reg = reg;
	}

	public String getReg() {
		return reg;
	}

	private boolean isAbstractAssemblyTemp(String t) {
		if (t.length() < 3) {
			return false;
		}
		return t.contains("_r") && Assembly.isNumeric(t.substring(2));
	}

	private boolean isGlobal(String g) {
		if (g.length() == 0) {
			return false;
		}
		return !isAbstractAssemblyTemp(g) && g.charAt(0) == '_';
	}

	@Override
	public ArrayList<Register> getAbstractTemps() {
		ArrayList<Register> abstractTemps = new ArrayList<>();
		if (isAbstractTemp()) {
			abstractTemps.add(this);
		}
		return abstractTemps;
	}

	@Override
	public ArrayList<Register> getPrecoloredTemps() {
		ArrayList<Register> precoloredTemps = new ArrayList<>();
		if (isMachineReg()) {
//			System.out.println("isMachineReg() "+reg);
			precoloredTemps.add(this);
		}
		return precoloredTemps;
	}

	@Override
	public boolean isMem() {
		return reg.equals("rbp");
	}

	@Override
	public void allocateRegisters() {
		if (isAbstractTemp()) {
			reg = "r"+Instruction.regCounter;
			Instruction.regCounter ++;
		}
	}

}
