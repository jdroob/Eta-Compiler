package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.ir.IRBinOp.OpType;
import main.java.jdr299zdh5cew256ans96.ir.BlockGraph;

import java.util.List;
import java.util.Map;

public class IRNodeFactory_c implements IRNodeFactory {

    private int numHeaders;
    private int numTrueLabels;
    private int numFalseLabels;
    private int numTrueIntermediateLabels;
    private int numFalseIntermediateLabels;
    private int numLabels;
    private int numEndLabels;
    private int numTemps;

    @Override
    public IRBinOp IRBinOp(OpType type, IRExpr left, IRExpr right) {
        return new IRBinOp(type, left, right);
    }

    @Override
    public IRCall IRCall(IRExpr target, IRExpr... args) {
        return new IRCall(target, args);
    }

    @Override
    public IRCall IRCall(IRExpr target, List<IRExpr> args) {
        return new IRCall(target, args);
    }

    @Override
    public IRCJump IRCJump(IRExpr expr, String trueLabel) {
        return new IRCJump(expr, trueLabel);
    }

    @Override
    public IRCJump IRCJump(IRExpr expr, String trueLabel, String falseLabel) {
        return new IRCJump(expr, trueLabel, falseLabel);
    }

    @Override
    public IRCompUnit IRCompUnit(String name) {
        return new IRCompUnit(name);
    }

    @Override
    public IRCompUnit IRCompUnit(String name, Map<String, IRFuncDecl> functions) {
        return new IRCompUnit(name, functions);
    }

    // public IRFuncDecl IRCompUnit(String name, Map<String, IRFuncDecl> functions,
    // BlockGraph cfg) {
    // return new IRCompUnit(name, functions, cfg);
    // }

    @Override
    public IRConst IRConst(long value) {
        return new IRConst(value);
    }

    @Override
    public IRESeq IRESeq(IRStmt stmt, IRExpr expr) {
        return new IRESeq(stmt, expr);
    }

    @Override
    public IRExp IRExp(IRExpr expr) {
        return new IRExp(expr);
    }

    @Override
    public IRFuncDecl IRFuncDecl(String name, IRStmt stmt) {
        return new IRFuncDecl(name, stmt);
    }

    @Override
    public IRFuncDecl IRFuncDecl(String name, IRStmt stmt, BlockGraph cfg) {
        return new IRFuncDecl(name, stmt, cfg);
    }

    @Override
    public IRJump IRJump(IRExpr expr) {
        return new IRJump(expr);
    }

    @Override
    public IRLabel IRLabel(String name) {
        return new IRLabel(name);
    }

    @Override
    public IRMem IRMem(IRExpr expr) {
        return new IRMem(expr);
    }

    @Override
    public IRCallStmt IRCallStmt(IRExpr target, Long n_returns, List<IRExpr> args) {
        return new IRCallStmt(target, n_returns, args);
    }

    @Override
    public IRMove IRMove(IRExpr target, IRExpr expr) {
        return new IRMove(target, expr);
    }

    @Override
    public IRName IRName(String name) {
        return new IRName(name);
    }

    @Override
    public IRReturn IRReturn(List<IRExpr> rets) {
        return new IRReturn(rets);
    }

    @Override
    public IRReturn IRReturn(IRExpr... rets) {
        return new IRReturn(rets);
    }

    @Override
    public IRSeq IRSeq(IRStmt... stmts) {
        return new IRSeq(stmts);
    }

    @Override
    public IRSeq IRSeq(List<IRStmt> stmts) {
        return new IRSeq(stmts);
    }

    @Override
    public IRTemp IRTemp(String name) {
        return new IRTemp(name);
    }

    @Override
    public String generateFreshHeaderLabel() {
        numHeaders++;
        return ".LH" + numHeaders;
    }

    @Override
    public String generateFreshTrueLabel() { // finish arrays then decide if you still need this
        numTrueLabels++;
        return getCurrentTrueLabel();
    }

    @Override
    public String generateIntermediateFreshTrueLabel() { // no longer need
        numTrueIntermediateLabels++;
        return ".LTT" + numTrueIntermediateLabels;
    }

    @Override
    public String generateIntermediateFreshFalseLabel() { // no longer need
        numFalseIntermediateLabels++;
        return ".LFF" + numFalseIntermediateLabels;
    }

    @Override
    public String generateFreshFalseLabel() { // finish arrays then decide if you still need this
        numFalseLabels++;
        return getCurrentFalseLabel();
    }

    @Override
    public String generateFreshEndLabel() {
        numEndLabels++;
        return getCurrentEndLabel();
    }

    @Override
    public String getCurrentTrueLabel() {
        return ".LT" + numTrueLabels;
    } // finish arrays then decide if you still
      // need this... you probably don't

    @Override
    public String getCurrentFalseLabel() {
        return ".LF" + numFalseLabels;
    } // finish arrays then decide if you still
      // need this... you probably don't

    @Override
    public String getCurrentEndLabel() {
        return ".LE" + numEndLabels;
    } // probably don't need anymore?

    @Override
    public IRTemp generateFreshTemp() {
        numTemps++;
        return IRTemp("t" + numTemps);
    }

    @Override
    public String generateFreshLabel() {
        numLabels++;
        return "._L" + numLabels;
    }

    @Override
    public String getCurrentLabel() {
        return "._L" + numLabels;
    }

}
