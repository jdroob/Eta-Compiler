package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRData;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;

import java.util.ArrayList;

public abstract class Global extends Definition {

	public Global(String pos) {
		super(pos);
	}

	public abstract ArrayList<IRData> translate(IRNodeFactory factory);
}
