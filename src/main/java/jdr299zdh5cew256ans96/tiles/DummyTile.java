package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Dummy;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;

import java.util.ArrayList;

// TODO: delete
public class DummyTile extends Tile {
	@Override
	public boolean isMatch(IRNode node) {
		return true;
	}

	@Override
	public Assembly generateAssembly(IRNode node) {
		return new Assembly(new Dummy());
	}

	@Override
	public ArrayList<IRNode> getRecursiveChild(IRNode node) {
		return new ArrayList<>();
	}
}
