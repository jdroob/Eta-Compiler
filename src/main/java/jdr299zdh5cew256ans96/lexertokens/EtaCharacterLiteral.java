package main.java.jdr299zdh5cew256ans96.lexertokens;

public class EtaCharacterLiteral extends EtaToken {			// val needs to be stored as a String here to account for \x unicode cases
	private String val;

	public EtaCharacterLiteral(String val) {
		this.val = val;
	}

	public String toString() {
		return val;
	}

	public String getVal() {
		return val;
	}

	public String getLexedString() {
		return getPos()+" character "+getVal()+"\n";
	}
}
