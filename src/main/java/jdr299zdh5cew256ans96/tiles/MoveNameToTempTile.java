package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Binop;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRName;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;

public class MoveNameToTempTile extends Tile {
	@Override
	public boolean isMatch(IRNode node) {
		if (node instanceof IRMove) {
			IRMove move = (IRMove) node;
			return move.target() instanceof IRTemp &&
					move.source() instanceof IRName;
		}
		return false;
	}

	@Override
	public Assembly generateAssembly(IRNode node) {
		IRMove move = (IRMove) node;
		IRExpr target = move.target();
		IRExpr src = move.source();
		String targetName = ((IRTemp) target).name();
		String srcName = ((IRName) src).name();
		Assembly a = new Assembly();
		String freshTemp = Assembly.generateFreshTemp();

		a.addInstruction(new Binop("lea", new Register(freshTemp),
				new MemBinop(new Register(srcName), "+", new Register("rip"))));
		a.addInstruction(new Mov(targetName, freshTemp));

		return a;
	}
}
