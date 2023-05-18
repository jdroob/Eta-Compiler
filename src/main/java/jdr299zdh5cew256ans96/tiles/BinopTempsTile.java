package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.assembly.Pop;

public class BinopTempsTile extends Tile {
	@Override
	public boolean isMatch(IRNode node) {
		if (node instanceof IRBinOp) {
			IRBinOp binopNode = (IRBinOp) node;
			return binopNode.left() instanceof IRTemp &&
					binopNode.right() instanceof IRTemp;
		}
		return false;
	}

	public Assembly generateAssembly(IRNode node) {
		IRBinOp binopNode = (IRBinOp) node;
		IRTemp leftTemp = (IRTemp) binopNode.left();
		IRTemp rightTemp = (IRTemp) binopNode.right();

		String rightName = rightTemp.name();

		Assembly binop = new Assembly();

		if (rightTemp.name().contains("_RV")) {
			int numRegRight = Integer.parseInt(
					String.valueOf(
							rightTemp.name().charAt(rightTemp.name().length() - 1)));

			if (numRegRight == 1) {
				rightName = "rax";
			} else if (numRegRight == 2) {
				rightName = "rdx";
			} else {
				String freshTemp = Assembly.generateFreshTemp();
				binop.addInstruction(new Pop(freshTemp));
				rightName = freshTemp;
			}
		}

		Assembly tempsAssembly = BinopTile.genAsmWithTemps(binopNode.opType(),
				leftTemp.name(),
				rightName);

		binop.addInstructions(tempsAssembly);
		binop.setShuttleTemp(tempsAssembly.getShuttleTemp());

		return binop;
	}
}
