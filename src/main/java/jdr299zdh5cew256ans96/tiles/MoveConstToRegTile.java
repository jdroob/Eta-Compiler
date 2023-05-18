package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.CallStack;
import main.java.jdr299zdh5cew256ans96.assembly.Const;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Push;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.assembly.TempStack;
import main.java.jdr299zdh5cew256ans96.ir.IRConst;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;

/**
 * maybe have all tile classes extend from Tile super class
 * all tile classes will implement isTile() function
 * each munch will go through data structure of tiles and find first
 * where isTile() is true (ordered by priority)
 */

public class MoveConstToRegTile extends Tile {

    @Override
    public boolean isMatch(IRNode node) {
        if (node instanceof IRMove) {
            IRMove move = (IRMove) node;
            return move.source() instanceof IRConst &&
                    move.target() instanceof IRTemp;
        }
        return false;
    }

    @Override
    public Assembly generateAssembly(IRNode node) {
        IRMove move = (IRMove) node;
        IRExpr target = move.target();
        IRExpr src = move.source();
        String targetName = ((IRTemp) target).name();
        int srcName = (int)((IRConst) src).value();

        // TODO: clean up logic
        int numRegTarget = -1;
        if (targetName.contains("_ARG") || targetName.contains("_RV")) {
            numRegTarget = Integer.parseInt(
                    String.valueOf(
                            targetName.charAt(targetName.length() - 1)));
        }

        int maxArgs = 6;
        if (CallStack.isMoreThanTwoReturns()) {
            maxArgs = 5;
        }

        // case for if the target is an argument
        if (targetName.contains("_ARG")) {
            if (numRegTarget > maxArgs) {
                return new Assembly();
            } else {
                int regNum = numRegTarget;
                if (CallStack.isMoreThanTwoReturns()) {
                    regNum++;
                }

                targetName = getArgReg(regNum);
            }
        }
        // case for if the target is a return value
        else if (targetName.contains("_RV")) {
            if (numRegTarget > 2) {
                int offset = (numRegTarget - 3) * 8;
                return new Assembly(new Mov(new MemBinop(new Register("rdi"),
                        "+", new Const(offset)),
                        new Const(srcName)));
            } else {
                targetName = getRetReg(numRegTarget);
            }

        }
        return new Assembly(new Mov(targetName, srcName));
    }

    private String getArgReg(int num) {
        return switch (num) {
            case 1 -> "rdi";
            case 2 -> "rsi";
            case 3 -> "rdx";
            case 4 -> "rcx";
            case 5 -> "r8";
            case 6 -> "r9";
            default -> "";
        };
    }
    private String getRetReg(int num) {
        if (num == 1) {
            return "rax";
        }
        return "rdx";
    }
}