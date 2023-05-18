package main.java.jdr299zdh5cew256ans96.lexertokens;
import java.math.BigInteger;

public class EtaIntLiteral extends EtaToken {
	private String val;

	public EtaIntLiteral(String val) {
		BigInteger temp = new BigInteger(val);
		this.val = temp.toString();
	}

	public String getVal() {
		return val;
	}

	public String toString() {
		return val;
	}
	public String getLexedString() {
		return getPos()+" integer "+ val+"\n";
	}
}
