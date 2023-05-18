package main.java.jdr299zdh5cew256ans96.lexertokens;

public class EtaToken {
	private int line;
	private int col;

	public EtaToken() {
		
	}

	public String getPos() {
		return Integer.toString(line)+":"+Integer.toString(col);
	}

	public void setLine(int line) {
		this.line = line;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String toString() {
		return "";
	}

	public String getLexedString() {
		return getPos()+" "+toString()+ "\n";
	}
}