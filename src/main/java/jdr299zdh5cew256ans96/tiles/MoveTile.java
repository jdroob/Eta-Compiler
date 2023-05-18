package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;

import java.util.ArrayList;

public class MoveTile extends Tile {

    @Override
    public boolean isMatch(IRNode node) {
        return node instanceof IRMove;
    }

    @Override
    public ArrayList<IRNode> getRecursiveChild(IRNode node) {
        ArrayList<IRNode> children = new ArrayList<>();
        IRMove move = (IRMove) node;
        children.add(move.target());
        children.add(move.source());
        return children;
    }
}