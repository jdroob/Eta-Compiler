package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.*;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Specific type of statement that spans only a single line. Also abstract
 * since there are many different kinds of single line statements
 */
public abstract class SingleLine extends Statement {

	/**
	 * Constructor for creating a single line statement
	 * 
	 * @param pos - position of single line statement in program file
	 */
	public SingleLine(String pos) {
		super(pos);
	}

	/**
	 * need a special translation for assigning to an array index. Only should
	 * happen in single line statements
	 */
	public IRStmt aaTranslate(IRNodeFactory factory, ArrayAccess a, Expression expr3) {
		/*
		 * If there's a second expression, then instead of doing
		 * the last MOVE to put e3 in the array,
		 * do the same thing as did in Array Access to get mem location of
		 * inner array, and then re-use bottom code after setting ta and ti
		 * to inner array and second index, respectively
		 */
		Expression id = a.getId();
		Expression expr1 = a.getExpr1();

		IRExpr arrayExpr = id.translate(factory);
		IRExpr access1 = expr1.translate(factory);

		IRTemp ta = factory.generateFreshTemp();
		IRTemp ti = factory.generateFreshTemp();

		ArrayList<IRStmt> stmts = new ArrayList<>();
		if (a.expr2isPresent()) {
			Expression expr2 = a.getExpr2();
			IRExpr access2 = expr2.translate(factory);

			// index to access from outer array
			IRTemp tn = factory.generateFreshTemp();
			// starting mem location of outer array
			IRTemp tm = factory.generateFreshTemp();

			IRMove tnMove = factory.IRMove(tn, access1);
			stmts.add(tnMove);
			IRMove tmMove = factory.IRMove(tm, arrayExpr);
			stmts.add(tmMove);

			IRExpr UGT = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),
					factory.IRBinOp(
							IRBinOp.OpType.ULT,
							tn,
							factory.IRMem(
									factory.IRBinOp(
											IRBinOp.OpType.SUB,
											tm,
											factory.IRConst(8)))));

			// bounds checking
			String ok_label_outer = factory.generateFreshTrueLabel();
			String error_label_outer = factory.generateFreshFalseLabel();
			String safeGuard_outer = factory.generateFreshTrueLabel();

			IRCJump cond_outer = factory.IRCJump(UGT, error_label_outer);
			IRLabel okLabel_outer = factory.IRLabel(ok_label_outer);
			IRLabel error_Label_outer = factory.IRLabel(error_label_outer);
			IRLabel safeGuardLabel_outer = factory.IRLabel(safeGuard_outer);
			ArrayList<IRExpr> exprList_outer = new ArrayList<>();

			IRCallStmt out_of_bounds_error_outer = factory.IRCallStmt(
					factory.IRName("_eta_out_of_bounds"), (long) 0, exprList_outer);

			IRSeq errorState_outer = factory.IRSeq(factory.IRJump(factory.IRName(safeGuard_outer)),
					error_Label_outer, out_of_bounds_error_outer, safeGuardLabel_outer);

			stmts.add(errorState_outer);
			stmts.add(cond_outer);
			stmts.add(okLabel_outer);
			/*
			 * get inner array mem location from outer array
			 */

			IRMem arrayExpr2 = factory.IRMem(
					factory.IRBinOp(
							IRBinOp.OpType.ADD,
							tm,
							factory.IRBinOp(
									IRBinOp.OpType.MUL,
									factory.IRConst(8),
									tn)));
			// put index to access in inner array inside t_i
			IRMove tiMove = factory.IRMove(ti, access2);
			// put mem location of inner array in t_a
			IRMove taMove = factory.IRMove(ta, arrayExpr2);
			stmts.add(taMove);
			stmts.add(tiMove);

		} else {
			IRMove taMove = factory.IRMove(ta, arrayExpr);
			IRMove tiMove = factory.IRMove(ti, access1);
			stmts.add(taMove);
			stmts.add(tiMove);

		}

		// if ti < length of array, fall through. Otherwise, out of bounds error
		IRExpr UGT = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),
				factory.IRBinOp(
						IRBinOp.OpType.ULT,
						ti,
						factory.IRMem(
								factory.IRBinOp(
										IRBinOp.OpType.SUB,
										ta,
										factory.IRConst(8)))));

		String ok_label = factory.generateFreshTrueLabel();
		String error_label = factory.generateFreshFalseLabel(); // change this to regular label?
		String safeGuard = factory.generateFreshTrueLabel();
		IRCJump cond = factory.IRCJump(UGT, error_label);
		IRLabel okLabel = factory.IRLabel(ok_label);
		IRLabel error_Label = factory.IRLabel(error_label);
		IRLabel safeGuardLabel = factory.IRLabel(safeGuard);
		ArrayList<IRExpr> exprList = new ArrayList<>();

		IRCallStmt out_of_bounds_error = factory.IRCallStmt(
				factory.IRName("_eta_out_of_bounds"), (long) 0, exprList);

		IRSeq errorState = factory.IRSeq(factory.IRJump(factory.IRName(safeGuard)),
				error_Label, out_of_bounds_error, safeGuardLabel);

		stmts.add(errorState);
		stmts.add(cond);
		stmts.add(okLabel);

		IRMem access = factory.IRMem(
				factory.IRBinOp(
						IRBinOp.OpType.ADD,
						ta,
						factory.IRBinOp(
								IRBinOp.OpType.MUL,
								factory.IRConst(8),
								ti)));

		IRMove store = factory.IRMove(access, expr3.translate(factory));
		stmts.add(store);

		return factory.IRSeq(stmts);

	}

	/*
	 * special translation for declaring arrays
	 * ptr is used for the recursive call for mem location of the inner array
	 */
	public IRStmt adTranslate(IRNodeFactory factory,
			Parameter arrayDec, boolean multi, Optional<IRTemp> ptr) {

		TypeNode arr0 = arrayDec.getType();

		/*
		 * for array x : int[a][b], must allocate 8+a.length×(8+b.length×8) bytes
		 * this general form works for 1D arrays, as well.
		 * In terms of the actual allocation if we have a 2D array:
		 * - Need to allocate space for the internal arrays and then store the
		 * pointer to that internal array in the outer array
		 */

		ArrayTypeNode arr = (ArrayTypeNode) arr0;
		Expression index = null;
		if (arr.getBracketContent().isPresent()) {
			index = (Expression) arr.getBracketContent().get();
		}
		String var = arrayDec.getName();

		// temp to represent length of the array
		IRTemp tn = factory.generateFreshTemp();
		// temp to represent memory location of the array
		IRTemp tm = factory.generateFreshTemp();

		/*
		 * if this is an internal array, the mem temp should be the temp that was
		 * passed in. Nothing needs to be returned because the value doesn't get
		 * altered, but the name needs to be the same so assembly translation will
		 * correctly identify values
		 */
		if (ptr.isPresent()) {
			tm = ptr.get();
		}

		ArrayList<IRStmt> stmts = new ArrayList<>();

		// TODO: if you don't give an array length do you alloc as if it's
		//  length 1 or 0?
		if (index != null) {
			IRMove translateIndex = factory.IRMove(
					tn, index.translate(factory));
			stmts.add(translateIndex);
		} else {
			IRMove noIndexGiven = factory.IRMove(
					tn, factory.IRConst(0));
			stmts.add(noIndexGiven);
		}

		// allocate memory on the Simulator heap for the array
		IRMove malloc = factory.IRMove(
				tm,
				factory.IRCall(factory.IRName("_eta_alloc"),

						factory.IRBinOp(
								IRBinOp.OpType.ADD,
								factory.IRBinOp(IRBinOp.OpType.MUL, tn, factory.IRConst(8)),
								factory.IRConst(8))));
		stmts.add(malloc);
		// store the length of the array in memory
		IRMove movLength = factory.IRMove(
				factory.IRMem(tm),
				tn);
		stmts.add(movLength);

		/** copy len */
		IRTemp copyLen = factory.generateFreshTemp();
		stmts.add(
				factory.IRMove(
						copyLen,
						tn));

		// store the memory location + 8 bytes in the variable name
		IRMove storeVar = factory.IRMove(
				factory.IRTemp(var),
				tm);
		stmts.add(storeVar);

		IRMove incVar = factory.IRMove(
				factory.IRTemp(var),
				factory.IRBinOp(
						IRBinOp.OpType.ADD,
						factory.IRTemp(var),
						factory.IRConst(8)));
		stmts.add(incVar);

		// need loop here to init values to 0
		String freshHeaderLabel = factory.generateFreshHeaderLabel();
		IRLabel lh = factory.IRLabel(freshHeaderLabel);
		stmts.add(lh);

		String falseLabel = factory.generateFreshFalseLabel();

		IRCJump checkCounter = factory.IRCJump(
				factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),
						factory.IRBinOp(IRBinOp.OpType.GT, tn, factory.IRConst(0))),
				falseLabel);
		stmts.add(checkCounter);

		// For Basic Block purposes
		stmts.add(factory.IRLabel(factory.generateFreshTrueLabel()));

		/** move increment from bottom to here */
		IRMove incTm = factory.IRMove(
				tm,
				factory.IRBinOp(IRBinOp.OpType.ADD, tm, factory.IRConst(8)));
		stmts.add(incTm);

		if (multi) { // if this is a multiarray, cast to multiarray
			MultiArrayTypeNode arr2 = (MultiArrayTypeNode) arr;
			Expression index2 = null;
			if (arr2.getBracketContent2().isPresent()) {
				index2 = (Expression) arr2.getBracketContent2().get();
			}
			String arrType = arr2.getStoredType().toString();
			ArrayTypeNode arr2_2 = new ArrayTypeNode(arrType, arrayDec.getPos(), index2);

			Parameter arrayDec2 = new Parameter(var + "[]", arr2_2, arrayDec.getPos());

			// temp to represent mem location of the internal array. Thus can store
			// a Mem(tm_i) in the outer array
			IRTemp tm_i = factory.generateFreshTemp();
			Optional<IRTemp> inner_ptr = Optional.of(tm_i);

			// allocate the space for the inner array
			IRSeq allocInnerArray = (IRSeq) adTranslate(factory, arrayDec2, false,
					inner_ptr);

			stmts.add(allocInnerArray);

			// move base address of each nested array into each element (pointer) of the
			// outer array
			IRMove innerArrayPtr = factory.IRMove(
					factory.IRMem(tm),
					factory.IRBinOp(
							IRBinOp.OpType.SUB,
							tm_i,
							factory.IRBinOp(
									IRBinOp.OpType.SUB,
									factory.IRBinOp(
											IRBinOp.OpType.MUL,
											factory.IRConst(8),
											copyLen),
									factory.IRConst(8))));
			stmts.add(innerArrayPtr);

		} else {
			IRMove zeroElement = factory.IRMove(
					factory.IRMem(tm),
					factory.IRConst(0));
			stmts.add(zeroElement);
		}

		IRMove decrementTn = factory.IRMove(
				tn,
				factory.IRBinOp(IRBinOp.OpType.SUB, tn, factory.IRConst(1)));
		stmts.add(decrementTn);

		IRJump restartLoop = factory.IRJump(factory.IRName(freshHeaderLabel));
		stmts.add(restartLoop);

		// exit loop
		stmts.add(factory.IRLabel(falseLabel));
		return factory.IRSeq(stmts);

	}

	/**
	 * Pretty printing function to print parsed AST node to file. Different
	 * AST nodes pretty print differently, so this method is just a stub
	 *
	 * @param c - printer object that is used to pretty print node
	 * @return the printer object after it is modified with node's pretty print
	 */
	@Override
	public abstract CodeWriterSExpPrinter prettyPrint(CodeWriterSExpPrinter c);

}