package main.java.jdr299zdh5cew256ans96.assembly;

import main.java.jdr299zdh5cew256ans96.tiles.BinopTempsTile;
import main.java.jdr299zdh5cew256ans96.tiles.BinopTile;
import main.java.jdr299zdh5cew256ans96.tiles.CJumpTile;
import main.java.jdr299zdh5cew256ans96.tiles.MemTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveConstToRegTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveExprToMemTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveExprToTempTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveMemToExprTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveMemToTempTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveNameToTempTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveRegToRegTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveTempToExprTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveMemTempToMemTempTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveTile;
import main.java.jdr299zdh5cew256ans96.tiles.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class Assembly {

	// master list of x86 instructions prioritized by tile size
	public static LinkedHashSet<Tile> insnList = new LinkedHashSet<>(
			Arrays.asList(
					new MoveMemTempToMemTempTile(),
					new MoveMemToTempTile(),
					new MoveMemToExprTile(),
					new MoveExprToMemTile(),
					new MoveNameToTempTile(),
					new MoveConstToRegTile(),
					new MoveRegToRegTile(),
					new MoveExprToTempTile(),
					new MoveTempToExprTile(),
					new MoveTile(),
					new BinopTempsTile(),
					new BinopTile(),
					new MemTile(),
					new CJumpTile()
			)
	);
	public static LinkedHashSet<String> x86regs = new LinkedHashSet<>(
			Arrays.asList(
					"rax", "eax", "ax", "al",   // accumulator
					"rbx", "ebx", "bx", "bl",   // base
					"rcx", "ecx", "cx", "cl",   // counter
					"rdx", "edx", "dx", "dl",   // data
					"rsp", "esp", "sp", "spl",  // stack pointer
					"rbp", "ebp", "bp", "bpl",  // base pointer
					"rsi", "esi", "si", "sil",  // source
					"rdi", "edi", "di", "dil",  // destination
					"r8", "r9", "r10", "r11",
					"r12", "r13", "r14", "r15",
					"rip"
			)
	);
	private ArrayList<Instruction> insns; // x86 instructions to be added for
	private String shuttleTemp;
	private static int shuttleTempNum;
	private static int freshLabel;

	public Assembly() {
		insns = new ArrayList<>();
	}

	public Assembly(ArrayList<Instruction> list) {
		insns = list;
	}

	public Assembly(Instruction s) {
		insns = new ArrayList<>();
		insns.add(s);
	}

	public ArrayList<Instruction> getInsns() {
		return insns;
	}

	public void clear() {
		insns = new ArrayList<>();
	}

	public void setShuttleTemp(String shuttleTemp) {
		this.shuttleTemp = shuttleTemp;
	}

	public static String generateFreshTemp() {
		shuttleTempNum++;
		return "_r"+shuttleTempNum;
	}

	public static String generateFreshLabel() {
		freshLabel++;
		return ".L"+freshLabel;
	}

	public String getShuttleTemp() {
		return shuttleTemp;
	}

	public void addInstruction(Instruction insn) {
		insns.add(insn);
	}

	public void addInstructions(Assembly a) {
		insns.addAll(a.insns);
	}

	public String getAssembly() {
		String assembly = "";
		for (Instruction insn : insns) {
			if (!(insn instanceof Label)
                    && !(insn instanceof Directive)) {
				assembly += "\t"+insn.toString()+"\n";
			} else {
				assembly += insn.toString()+"\n";
			}
		}
		return assembly;
	}

	// test if operand is a number
	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}