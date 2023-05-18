package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.ir.IRMem;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;

import java.util.ArrayList;

public class MemTile extends Tile {

	@Override
	public boolean isMatch(IRNode node) {
		return node instanceof IRMem;
	}

	@Override
	public ArrayList<IRNode> getRecursiveChild(IRNode node) {
		ArrayList<IRNode> children = new ArrayList<>();
		IRMem memNode = (IRMem)node;
		children.add(memNode.expr());
		return children;
	}
}
