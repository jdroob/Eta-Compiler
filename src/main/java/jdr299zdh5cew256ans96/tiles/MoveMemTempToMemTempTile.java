package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.MemRegister;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.ir.IRMem;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Pop;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.assembly.Assembly;


public class MoveMemTempToMemTempTile extends Tile {
	@Override
	public boolean isMatch(IRNode node) {
		if (node instanceof IRMove) {
			IRMove move = (IRMove) node;
			if (move.source() instanceof IRMem) {
				IRMem memSrc = (IRMem) move.source();
				if (!(memSrc.expr() instanceof IRTemp)) {
					return false;
				}
			} else {
				return false;
			}

			if (move.target() instanceof IRMem) {
				IRMem memTarget = (IRMem) move.target();
				if (!(memTarget.expr() instanceof IRTemp)) {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public Assembly generateAssembly(IRNode node) {
		IRMove move = (IRMove) node;
		IRMem memSrc = (IRMem) move.source();
		IRTemp memSrcTemp = (IRTemp) memSrc.expr();
		String memSrcTempName = memSrcTemp.name();

		IRMem memTarget = (IRMem) move.target();
		IRTemp memTargetTemp = (IRTemp) memTarget.expr();

		Assembly assembly = new Assembly();

		if (memSrcTempName.contains("_RV")) {
			int numRegRight = Integer.parseInt(
					String.valueOf(
							memSrcTempName.charAt(memSrcTempName.length() - 1)));

			if (numRegRight == 1) {
				memSrcTempName = "rax";
			} else if (numRegRight == 2) {
				memSrcTempName = "rdx";
			} else {
				String freshTemp = Assembly.generateFreshTemp();
				assembly.addInstruction(new Pop(freshTemp));
				memSrcTempName = freshTemp;
			}
		}


		String freshTemp = Assembly.generateFreshTemp();
		assembly.addInstruction(new Mov(new Register(freshTemp),
				new MemRegister(memSrcTempName)));
		assembly.addInstruction(new Mov(new MemRegister(memTargetTemp.name()),
				new Register(freshTemp)));
		return assembly;

	}
}
