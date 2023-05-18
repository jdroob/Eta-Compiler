package main.java.jdr299zdh5cew256ans96.assembly;

public class Call extends Instruction {

	private String func;
	private int numSpilledArgs;
	private int numSpilledRVs;

	public Call(String func, int sa, int sr) {
		this.func = func;
		numSpilledArgs = sa;
		numSpilledRVs = sr;
	}

	public int getNumSpilledArgs() {
		return numSpilledArgs;
	}

	public int getNumSpilledRVs() {
		return numSpilledRVs;
	}

	public String getFunc() {
		return func;
	}

	@Override
	public String toString() {
		return "call "+func;
	}

}
