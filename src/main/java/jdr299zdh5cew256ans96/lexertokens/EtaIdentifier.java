package main.java.jdr299zdh5cew256ans96.lexertokens;

public class EtaIdentifier extends EtaToken {

	private String id;

	public EtaIdentifier(String id) {
		
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return id;
	}

	public String getLexedString() {
		if (id.equals("_")) {
			return super.getLexedString();
		}
		else if (id.equals("length")) {
			return getPos()+" "+toString()+ "\n";
		} else {
			return getPos()+" id "+toString()+ "\n";
		}
	}
}