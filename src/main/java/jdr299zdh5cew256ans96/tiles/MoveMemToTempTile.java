package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.ir.IRMem;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;

import java.util.ArrayList;

public class MoveMemToTempTile extends Tile {

	@Override
	public boolean isMatch(IRNode node) {
		if (node instanceof IRMove) {
			IRMove move = (IRMove) node;
			return move.source() instanceof IRMem && move.target() instanceof IRTemp;
		}
		return false;
	}

	@Override
	public ArrayList<IRNode> getRecursiveChild(IRNode node) {
		ArrayList<IRNode> children = new ArrayList<>();
		IRMove move = (IRMove) node;
		IRMem mem = (IRMem) move.source();
		children.add(mem.expr());
		return children;
	}
}
