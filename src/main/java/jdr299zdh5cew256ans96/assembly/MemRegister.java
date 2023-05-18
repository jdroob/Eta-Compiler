package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class MemRegister extends Register {

	public MemRegister(String reg) {
		super(reg);
	}

	@Override
	public String toString() {
		return "qword ptr ["+super.toString()+"]";
	}

	@Override
	public boolean isMem() {
		return true;
	}

}
