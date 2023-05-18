package main.java.jdr299zdh5cew256ans96.lexertokens;

public class EtaStrLiteral extends EtaToken {
	private String val;

	public EtaStrLiteral(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}

	public String toString() {
		return val;
	}

	public String getLexedString() {
		return getPos()+" string "+getVal()+"\n";
	}
}
