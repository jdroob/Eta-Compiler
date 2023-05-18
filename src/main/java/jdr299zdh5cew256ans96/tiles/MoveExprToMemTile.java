package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.ir.IRMem;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;

import java.util.ArrayList;

public class MoveExprToMemTile extends Tile {
	@Override
	public boolean isMatch(IRNode node) {
		if (node instanceof IRMove) {
			IRMove move = (IRMove) node;
			return move.target() instanceof IRMem;
		}

		return false;
	}

	@Override
	public ArrayList<IRNode> getRecursiveChild(IRNode node) {
		ArrayList<IRNode> children = new ArrayList<>();
		IRMove move = (IRMove) node;
		children.add(move.source());
		return children;
	}
}
