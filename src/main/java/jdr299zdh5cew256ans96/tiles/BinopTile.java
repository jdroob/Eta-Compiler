package main.java.jdr299zdh5cew256ans96.tiles;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Binop;
import main.java.jdr299zdh5cew256ans96.assembly.Cmp;
import main.java.jdr299zdh5cew256ans96.assembly.Jmp;
import main.java.jdr299zdh5cew256ans96.assembly.Label;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRNode;

import java.util.ArrayList;

public class BinopTile extends Tile {
	@Override
	public boolean isMatch(IRNode node) {
		return node instanceof IRBinOp;
	}

	@Override
	public ArrayList<IRNode> getRecursiveChild(IRNode node) {
		ArrayList<IRNode> children = new ArrayList<>();
		IRBinOp binopNode = (IRBinOp)node;
		children.add(binopNode.left());
		children.add(binopNode.right());
		return children;
	}

	public static Assembly genAsmWithTemps(IRBinOp.OpType op, String left,
	                                       String right) {
		Assembly binop = new Assembly();
		String newShuttleTemp = Assembly.generateFreshTemp();
		switch (op) {
			case ADD:
				binop.addInstruction(new Binop("lea",
						new Register(newShuttleTemp),
						new MemBinop(new Register(left), "+",
								new Register(right))));
				break;
			case MUL:
				binop.addInstruction(new Mov(newShuttleTemp, left));
                binop.addInstruction(new Binop("imul", newShuttleTemp, right));
				break;
			case HMUL:
			//System.out.println("got here");
			// step 1: save rax and rdx
			binop.addInstruction(new Mov("r13", "rax"));
			binop.addInstruction(new Mov("r14", "rdx"));

			//step 2: move multiplicand into rax
			binop.addInstruction(new Mov("rax", left));

			//step 3: move multiplier into r12
			//  save r12
			binop.addInstruction(new Mov("r15", "r12"));
			binop.addInstruction(new Mov("r12", right));

			// step 4: RDX:RAX = RAX * R12
			binop.addInstruction(new Binop("imul", "r12"));

			// step 5: save upper 64 bits
			binop.addInstruction(new Mov(newShuttleTemp, "rdx"));

			// step 6: restore registers
			binop.addInstruction(new Mov("rax", "r13"));
			binop.addInstruction(new Mov("rdx", "r14"));
			binop.addInstruction(new Mov("r12", "r15"));
			break;
			case DIV:
				// How this works (I think)
				// Divisor stored in RDX:RAX
				// Dividend stored in arbitrary register (use r12 for now?)
				// idiv r12 ; does (rdx:rax)/r12
				// quotient stored in rax
				// remainder stored in rdx

				// step 1: save rax and rdx
				binop.addInstruction(new Mov("r13", "rax"));
				binop.addInstruction(new Mov("r14", "rdx"));

				//step 2: move divisor into rax
				binop.addInstruction(new Mov("rax", left));

				//step 3: zero out upper 64 bits of rdx:rax
				//      this is accomplished by xor'ing rdx by itself
				binop.addInstruction(new Binop("xor", "rdx", "rdx"));

				//step 4: move dividend into r12
				//  save r12
				binop.addInstruction(new Mov("r15", "r12"));
				binop.addInstruction(new Mov("r12", right));
				binop.addInstruction(new Binop("idiv", "r12"));

				// step 5: store quotient
				//  Only difference b/w div and mod is that below will be
				//  rdx instead of rax
				binop.addInstruction(new Mov(newShuttleTemp, "rax"));

				// step 6: restore registers
				binop.addInstruction(new Mov("rax", "r13"));
				binop.addInstruction(new Mov("rdx", "r14"));
				binop.addInstruction(new Mov("r12", "r15"));
				break;
			case MOD:
				// How this works (I think)
				// Divisor stored in RDX:RAX
				// Dividend stored in arbitrary register (use r12 for now?)
				// idiv r12 ; does (rdx:rax)/r12
				// quotient stored in rax
				// remainder stored in rdx

				// step 1: save rax and rdx
				binop.addInstruction(new Mov("r13", "rax"));
				binop.addInstruction(new Mov("r14", "rdx"));

				//step 2: move divisor into rax
				binop.addInstruction(new Mov("rax", left));

				//step 3: zero out upper 64 bits of rdx:rax
				//      this is accomplished by xor'ing rdx by itself
				binop.addInstruction(new Binop("xor", "rdx", "rdx"));

				//step 4: move dividend into r12
				//  save r12
				binop.addInstruction(new Mov("r15", "r12"));
				binop.addInstruction(new Mov("r12", right));
				binop.addInstruction(new Binop("idiv", "r12"));

				// step 5: store remainder
				//  Only difference b/w mod and div is that below will be
				//  rax instead of rdx
				binop.addInstruction(new Mov(newShuttleTemp, "rdx"));

				// step 6: restore registers
				binop.addInstruction(new Mov("rax", "r13"));
				binop.addInstruction(new Mov("rdx", "r14"));
				binop.addInstruction(new Mov("r12", "r15"));
				break;
			case AND:
			case OR:
			case XOR:
			case SUB:
				binop.addInstruction(new Mov(newShuttleTemp, left));
				binop.addInstruction(new Binop(op.toString().toLowerCase(),
						newShuttleTemp, right));
				break;
			case EQ:
			case NEQ:
			case LT:
			case ULT:
			case GT:
			case LEQ:
			case GEQ:
				String jump = getJump(op);
				String equalLabel = Assembly.generateFreshLabel();
				String endLabel = Assembly.generateFreshLabel();
				binop.addInstruction(new Cmp(left, right));
				binop.addInstruction(new Jmp(jump, equalLabel));
				binop.addInstruction(new Mov(newShuttleTemp, 0));
				binop.addInstruction(new Jmp("jmp", endLabel));
				binop.addInstruction(new Label(equalLabel));
				binop.addInstruction(new Mov(newShuttleTemp, 1));
				binop.addInstruction(new Label(endLabel));
				break;
			default:
		}

		binop.setShuttleTemp(newShuttleTemp);
		return binop;
	}

	private static String getJump(IRBinOp.OpType op) {
		return switch (op) {
			case EQ -> "jz";
			case NEQ -> "jnz";
			case LT, ULT -> "jl";
			case GT -> "jg";
			case LEQ -> "jle";
			case GEQ -> "jge";
			default -> "";
		};
	}

}
