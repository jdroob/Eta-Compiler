package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.ir.IRCJump;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;

import java.util.ArrayList;

public class CJumpTile extends Tile {
    @Override
    public boolean isMatch(IRNode node) {
        return node instanceof IRCJump;
    }

    @Override
    public ArrayList<IRNode> getRecursiveChild(IRNode node) {
        ArrayList<IRNode> children = new ArrayList<>();
        IRCJump cjmpNode = (IRCJump) node;
        children.add(cjmpNode.cond());
        return children;
    }
}
