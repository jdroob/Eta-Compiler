package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;

/** An intermediate representation for statements */
public abstract class IRStmt extends IRNode_c {

	private boolean dead = false;

	public boolean isDead() {
		return dead;
	};

	public void markDead(boolean isDead) {
		dead = isDead;
	}

	public boolean isNestedSeq() {
		return false;
	}

	public IRStmt lower(IRNodeFactory factory) {
		return this;
	}

	public abstract String label();

	public abstract String toString();

	public abstract String targetString();

}
