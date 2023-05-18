package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.CallStack;
import main.java.jdr299zdh5cew256ans96.assembly.Const;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Pop;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;

public class MoveRegToRegTile extends Tile {

    @Override
    public boolean isMatch(IRNode node) {
        if (node instanceof IRMove) {
            IRMove move = (IRMove) node;
            return move.source() instanceof IRTemp &&
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
        String srcName = ((IRTemp) src).name();

        if (targetName.contains("_ARG")) {
            CallStack.push(srcName);
            return new Assembly();
        }

        // TODO: clean up logic
        int numRegTarget = -1;
        if (targetName.contains("_ARG") || targetName.contains("_RV")) {

            //TODO: FIX SO THIS SUPPORTS DOUBLE-DIGIT _RVs and _ARGs
            numRegTarget = Integer.parseInt(
                    String.valueOf(
                            targetName.charAt(targetName.length() - 1)));
        }

        int numRegSrc = -1;
        if (srcName.contains("_ARG") || srcName.contains("_RV")) {
            numRegSrc = Integer.parseInt(
                    String.valueOf(
                            srcName.charAt(srcName.length() - 1)));
        }

        int maxArgs = 6;
        if (CallStack.isMoreThanTwoReturns()) {
            maxArgs = 5;
        }

        // case for if the target is an argument
        if (targetName.contains("_ARG")) {
            if (numRegTarget > maxArgs) {
                 CallStack.push("push " + srcName);
                return new Assembly();
            } else {
                int regNum = numRegTarget;
                if (CallStack.isMoreThanTwoReturns()) {
                    regNum++;
                }

                targetName = getArgReg(regNum);
            }

        }
        // case for if the source is an argument
        else if (srcName.contains("_ARG")) {
            if (numRegSrc > maxArgs) {
                int addressOffset = (numRegSrc - maxArgs) * 8 + 8;
                if (CallStack.isOddStackSpilled()) {
                    addressOffset += 8;
                }
                return new Assembly(new Mov(new Register(targetName),
                        new MemBinop(new Register("rbp"), "+", new Const(addressOffset))));
            } else {
                int regNum = numRegSrc;
                if (CallStack.isMoreThanTwoReturns()) {
                    regNum++;
                }
                srcName = getArgReg(regNum);
            }

        }
        // case for if the target is a return value
        else if (targetName.contains("_RV")) {
            if (numRegTarget > 2) {
                int offset = (numRegTarget - 3) * 8;
                return new Assembly(new Mov(new MemBinop(new Register("rdi"),
                        "+", new Const(offset)),
                        new Register(srcName)));
            } else {
                targetName = getRetReg(numRegTarget);
            }

        }
        // case for if the source is a return value
        else if (srcName.contains("_RV")) {

            if (numRegSrc > 2) {
                return new Assembly(new Pop(targetName));
            } else {
                srcName = getRetReg(numRegSrc);
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