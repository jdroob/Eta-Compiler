package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;

import java.util.ArrayList;

public abstract class Tile {

	public abstract boolean isMatch(IRNode node);

	public Assembly generateAssembly(IRNode node) {
		return new Assembly();
	}

	public ArrayList<IRNode> getRecursiveChild(IRNode node) {
		return new ArrayList<>();
	}
}