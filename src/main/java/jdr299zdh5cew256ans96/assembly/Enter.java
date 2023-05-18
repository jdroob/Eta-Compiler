package main.java.jdr299zdh5cew256ans96.assembly;

public class Enter extends Instruction {
	private int alloc;

	public Enter(int alloc) {
		this.alloc = alloc;
	}

	@Override
	public String toString() {
		return "enter "+alloc+", 0";
	}

	public boolean hasAbstractTemp() {
		return false;
	}
}
