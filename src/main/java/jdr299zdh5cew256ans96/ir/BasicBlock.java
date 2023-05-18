package main.java.jdr299zdh5cew256ans96.ir;

import java.util.ArrayList;

import org.apache.tools.ant.types.selectors.ExtendFileSelector;

public class BasicBlock {
    private IRStmt entryStmt;
    private IRStmt exitStmt;
    private ArrayList<IRStmt> body = new ArrayList<>();
    private static int count;
    private String name;
    private String startLabel;
    private String nextBlockLabel;
    private ArrayList<BasicBlock> predecessors;
    private boolean isMarked;
    private boolean isExitStmtCJump;
    private IRStmt CJumpFTStmt;

    // can get rid of this constructor
    public BasicBlock(IRStmt start, ArrayList<IRStmt> irStmts, IRStmt end) {
        entryStmt = start;
        body = irStmts;
        exitStmt = end;
        name = "Block_" + count;
        count += 1;
        predecessors = new ArrayList<>();
        isMarked = false;

        startLabel = start.label();
        CJumpFTStmt = null;
    }

    public BasicBlock(IRStmt start) {
        entryStmt = start;
        exitStmt = null;
        name = "Block_" + count;
        count += 1;
        predecessors = new ArrayList();
        isMarked = false;
        isExitStmtCJump = false;
        CJumpFTStmt = null;
        startLabel = start.label();
        nextBlockLabel = "NULL";
    }

    public BasicBlock() {
        entryStmt = null;
        exitStmt = null;
        name = "Block_" + count;
        count += 1;
        predecessors = new ArrayList();
        isMarked = false;
        isExitStmtCJump = false;
        CJumpFTStmt = null;
        startLabel = "NULL";
        nextBlockLabel = "NULL";
    }

    // overriding equals() method
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return this.getName() == ((BasicBlock) obj).getName();
    }

    public void setCJumpFTStmt(IRStmt ftLabel) {
        CJumpFTStmt = ftLabel;
    }

    public IRStmt getCJumpFTStmt() {
        return CJumpFTStmt;
    }

    public String getCJumpFTLabel() {
        IRLabel label = (IRLabel) CJumpFTStmt;
        return removeParens(label.label());
    }

    public void printCJumpFTStmt() {
        if (CJumpFTStmt != null) {
            IRLabel label = (IRLabel) CJumpFTStmt;
            // System.out.println(label.label());
        } else {
            // System.out.println("CJumpFTStmt is null");
        }
    }

    public void addStartOfBlock(IRStmt start) {
        entryStmt = start;
        // startLabel = start.label();
        if (start instanceof IRLabel) {
            startLabel = start.label();
        } else {
            startLabel = "not a label";
        }
    }

    public void addEndOfBlock(IRStmt end) {
        exitStmt = end;
        if (end instanceof IRCJump) {
            isExitStmtCJump = true;
        }

    }

    public void addToBody(IRStmt ordinaryStmt) {
        body.add(ordinaryStmt);
    }

    public IRStmt getEntryStmt() {
        return entryStmt;
    }

    public ArrayList<IRStmt> getBody() {
        return body;
    }

    public IRStmt getExitStmt() {
        return exitStmt;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return Integer.parseInt(name.substring(6));
    }

    public void addPredecessor(BasicBlock p) {
        predecessors.add(p);
    }

    public boolean isPredecessor(BasicBlock b) {
        if (b.getPredecessors().size() > 0) {
            for (BasicBlock testBlock : b.getPredecessors()) {
                if (this.equals(testBlock)) {
                    return true;
                }
            }
        }
        return false;
    }

    public BasicBlock getPredecessor(int index) {
        return predecessors.get(index);
    }

    public ArrayList<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public boolean isExitStmtCJump() {
        return isExitStmtCJump;
    }

    public void mark() {
        isMarked = true;
    }

    public void setNextBlockLabel(String next) {
        nextBlockLabel = next;
    }

    public String getNextBlockLabel() {
        if (nextBlockLabel.contains("(")) {
            return removeParens(nextBlockLabel);
        }
        return nextBlockLabel;
    }

    public String getStartLabel() {
        if (startLabel.contains("(")) {
            return removeParens(startLabel);
        }
        return startLabel;
    }

    public String removeParens(String str) {
        String newStr = str.substring(
                str.indexOf("(") + 1, str.indexOf(")"));
        return newStr;
    }

    public String toString() {
        String s = "";
        if (entryStmt == null) {
            // System.out.println("block " + name + " has null entry statement");
        } else {
            s = s + entryStmt.toString() + "\n";
        }
        for (IRStmt stmt : body) {
            s = s + stmt.toString() + "\n";
        }
        if (exitStmt == null) {
            // System.out.println("block " + name + " has null exit statement");
        } else {
            s = s + exitStmt.toString() + "\n";
        }
        return s;

    }

    // toString() but make it on one line with \n characters
    public String toStringInline() {
        String s = "";
        s = s + entryStmt.toString() + "\\n";
        for (IRStmt stmt : body) {
            s = s + stmt.toString() + "\\n";
        }
        s = s + exitStmt.toString() + "\\n";
        return s;

    }
}