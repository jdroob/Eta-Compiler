package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.CallStack;
import main.java.jdr299zdh5cew256ans96.assembly.Const;
import main.java.jdr299zdh5cew256ans96.assembly.Enter;
import main.java.jdr299zdh5cew256ans96.assembly.Instruction;
import main.java.jdr299zdh5cew256ans96.assembly.Jmp;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.MemRegister;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.assembly.Label;
import main.java.jdr299zdh5cew256ans96.assembly.RegisterAllocation;
import main.java.jdr299zdh5cew256ans96.cli;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.InsnMapsBuilder;
import main.java.jdr299zdh5cew256ans96.ir.Graph;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.BlockGraph;
import main.java.jdr299zdh5cew256ans96.ir.JumpMap;
import main.java.jdr299zdh5cew256ans96.ir.LabelMap;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;

/** An IR function declaration */
public class IRFuncDecl extends IRNode_c {

    private String name;
    public static int numSpilled;
    private IRStmt body;
    private BlockGraph IRcfg;
    // = new BlockGraph();

    // private ArrayList<String> TempList = new ArrayList<>();

    public static ArrayList<String> TRAregs = new ArrayList<>(
            Arrays.asList(
                    "r10", "r11", "r12"));

    public IRFuncDecl(String name, IRStmt body) {
        this.name = name;
        this.body = body;
        this.IRcfg = new BlockGraph();
    }

    public IRFuncDecl(String name, IRStmt body, BlockGraph IRcfg) {
        this.name = name;
        this.body = body;
        this.IRcfg = IRcfg;
    }

    public BlockGraph IRcfg() {
        return IRcfg;
    }

    public String name() {
        return name;
    }

    public IRStmt body() {
        return body;
    }

    @Override
    public String label() {
        return "FUNC " + name;
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRStmt stmt = (IRStmt) v.visit(this, body);

        if (stmt != body)
            return v.nodeFactory().IRFuncDecl(name, stmt);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(body));
        return result;
    }

    public Assembly tile() {

        int spilledRets = 0;
        int numArgs = getNumArgs(name);
        int numRets = 0;

        int returnIndex = name.lastIndexOf("_");
        if (name.charAt(returnIndex + 1) == 't') {
            numRets = Integer.parseInt(String.valueOf(name.charAt(returnIndex + 2)));
            if (numRets > 2) {
                spilledRets = numRets - 2;
                CallStack.setMultipleReturns(true);
            }
        }

        int spilledArgs = Math.max(numArgs - 6, 0);
        if (numRets > 2) {
            spilledArgs = Math.max(numArgs - 5, 0);
        }

        if ((spilledArgs + spilledRets) % 2 == 1) {
            CallStack.setOddStackSpilled(true);
        }

        Assembly funcAssembly = new Assembly();
        IRSeq bodyStmts = (IRSeq) body;
        funcAssembly.addInstructions(munch()); // add abi name for each function as a
        funcAssembly.addInstruction(new Enter(0));
        for (IRStmt s : bodyStmts.stmts()) {
            funcAssembly.addInstructions(s.munch());
        }

        return funcAssembly;
    }

    public static int getNumArgs(String abiName) {
        int index = getArgsStartIndex(abiName);
        return getNumUnits(abiName.substring(index));
    }

    public static int getNumUnits(String chars) {
        int numUnits = 0;
        for (int i = 0; i < chars.length(); i++) {
            if (chars.charAt(i) == 'i' || chars.charAt(i) == 'b') {
                numUnits++;
            }
        }
        return numUnits;
    }

    public static int getArgsStartIndex(String name) {
        int returnIndex = name.lastIndexOf("_");

        if (name.charAt(returnIndex + 1) == 't') {
            int numRets = Integer.parseInt(String.valueOf(name.charAt(returnIndex + 2)));
            return returnIndex + 3 + getArgsStartIndexHelper(numRets,
                    name.substring(returnIndex + 3));
        } else if (name.charAt(returnIndex + 1) == 'p') {
            return returnIndex + 2;
        } else {
            return returnIndex + 1 + getArgsStartIndexHelper(1,
                    name.substring(returnIndex + 1));
        }
    }

    public static int getArgsStartIndexHelper(int numRets, String chars) {
        int numUnits = 0;
        for (int i = 0; i < chars.length(); i++) {
            if (chars.charAt(i) == 'i' || chars.charAt(i) == 'b') {
                numUnits++;
            }
            if (numUnits == numRets) {
                if (i + 1 > chars.length()) {
                    return -1;
                }
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * parses each instruction in function
     * stores names of abstract temps in instructions in TempList
     * replaces abstract temps with unique location in memory [rbp - k]
     *
     * Returns real x86 assembly
     */

    public Assembly convertToX86(boolean isAbstract) {
        Assembly abstractAssembly = tile();
        if (isAbstract) {
            return abstractAssembly;
        }

        Assembly newAsm;
        if (Arrays.asList(cli.turnedOnOpts).contains("reg")) {
            newAsm = RegisterAllocation.registerAllocate(abstractAssembly);
        } else {
            newAsm = RegisterAllocation.trivialAllocate(abstractAssembly);
        }
        int i = 0;
        for (Instruction insn : newAsm.getInsns()) {
            if (insn instanceof Enter) {
                newAsm.getInsns().set(i, new Enter(alloc()));
            }
            i++;
        }

        if (Arrays.asList(cli.turnedOnOpts).contains("dce")) {
            deadCodeElimination(newAsm);
        }

        numSpilled = 0;
        return newAsm;
    }

    private void deadCodeElimination(Assembly abstractAssembly) {
        removeRedundantMoves(abstractAssembly);
        removeOverwriteMoves(abstractAssembly);

    }

    private void removeOverwriteMoves(Assembly abstractAssembly) {
        Assembly newAsm = new Assembly();

        String currentTarget = "";
        for (Instruction insn : abstractAssembly.getInsns()) {
            if (!(insn instanceof Mov m)) {
                newAsm.addInstruction(insn);
                currentTarget = "";
            } else if (m.getTarget() instanceof Register r &&
                    !(m.getTarget() instanceof MemRegister)) {
                if (r.getReg().equals(currentTarget)) {
                    newAsm.getInsns().remove(newAsm.getInsns().size() - 1);
                    newAsm.addInstruction(insn);
                } else {
                    newAsm.addInstruction(insn);
                    currentTarget = r.getReg();
                }
            } else {
                newAsm.addInstruction(insn);
                currentTarget = "";
            }
        }

        abstractAssembly.clear();
        abstractAssembly.addInstructions(newAsm);
    }

    private void removeRedundantMoves(Assembly abstractAssembly) {
        Assembly newAsm = new Assembly();
        for (Instruction insn : abstractAssembly.getInsns()) {
            if (!(insn instanceof Mov m)) {
                newAsm.addInstruction(insn);
            } else if (m.getTarget() instanceof Register t
                    && m.getSrc() instanceof Register s) {
                if (!t.getReg().equals(s.getReg()) || t.isMem() || s.isMem()) {
                    newAsm.addInstruction(insn);
                }
            } else {
                newAsm.addInstruction(insn);
            }
        }

        abstractAssembly.clear();
        abstractAssembly.addInstructions(newAsm);
    }

    private static int alloc() {
        // System.out.println(RegisterAllocation.TempList.size());
        // return RegisterAllocation.TempList.size() % 2 != 0 ? 8
        // * (RegisterAllocation.TempList.size() + 1) : 8 *
        // RegisterAllocation.TempList.size();
        // System.out.println("numSpilled: " + numSpilled);
        if (Arrays.asList(cli.turnedOnOpts).contains("reg")) {
            return numSpilled % 2 != 0 ? 8
                    * (numSpilled + 1) : 8 * numSpilled;
        }
        return RegisterAllocation.TempList.size() % 2 != 0 ? 8
                * (RegisterAllocation.TempList.size() + 1)
                : 8 *
                        RegisterAllocation.TempList.size();
    }

    public Assembly munch() {
        Assembly e = new Assembly();
        e.addInstruction(new Label(name()));
        return e;
    }

    @Override
    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        v.addNameToCurrentIndex(name);
        v.addInsn(this);
        return v;
    }

    @Override
    public IRNode buildInsnMaps(InsnMapsBuilder v) {
        return this;
    }

    public void optimizeIR(IRNodeFactory factory) {
        if (Arrays.asList(cli.turnedOnOpts).contains("dce")) {
            IRcfg.deadCodeElimination();
            body = factory.IRSeq(IRcfg.flatten());

        }
        // IRcfg.valueNumbering();

    }

    /**
     * Precondition: Assume every basic block begins with a label.
     * Precondition: Assume every basic block ends with a JUMP, CJUMP, RETURN,
     * or an ordinary statement followed by a label.
     *
     * Postcondition: Return an IRFuncDecl with lowered statements in optimized
     * order from block reordering
     */

    public IRFuncDecl lower(IRNodeFactory factory) {
        IRFuncDecl loweredIRFuncDecl = factory.IRFuncDecl(name,
                body.lower(factory));
        IRSeq bodyStmts = (IRSeq) loweredIRFuncDecl.body();

        // System.out.println("Initial Statements:");
        // for (IRStmt stmt : bodyStmts.stmts()) {
        // System.out.println(stmt.toString());

        // }
        // System.out.println();

        ArrayList<BasicBlock> blockList = getBasicBlocks(bodyStmts);

        // System.out.println("Initial Blocks:");
        // for (BasicBlock block : blockList) {

        // System.out.println(block.getName() + "\n" + block.toString());

        // }

        ArrayList<BasicBlock> reorderedList = new ArrayList<>();
        try {
            // ArrayList<BasicBlock> reorderedList = greedyReorder(blockList);
            reorderedList = greedyReorder(blockList);

        } catch (Exception e) {
            System.out.print("IRFuncDecl.lower() 1 error: ");
            System.out.println(e);

        }

        ArrayList<BasicBlock> cleanedUpList = new ArrayList<>();
        try {
            // ArrayList<BasicBlock> cleanedUpList = cleanUp(reorderedList);
            cleanedUpList = cleanUp(reorderedList);
        } catch (Exception e) {
            System.out.print("IRFuncDecl.lower() 2 error: ");
            System.out.println(e);

        }

        // Make the global Graph object hold the CFG

        // try {
        // buildCFG(cleanedUpList);
        // } catch (Exception e) {
        // System.out.print("Error when building CFG ");
        // System.out.println(e);

        // }

        // consolidate code from all BasicBlock objects
        IRSeq cleanedUpStmts = null;

        try {
            cleanedUpStmts = extractCode(cleanedUpList);
        } catch (Exception e) {
            System.out.print("IRFuncDecl.lower() 3 error: ");
            System.out.println(e);

        }

        // remove unnecessary jumps
        // remove jumps to next line and unreferenced labels

        IRSeq evenCleanerStmts = null;
        try {
            evenCleanerStmts = removeUnnecessaryCode(cleanedUpStmts);
        } catch (Exception e) {
            System.out.print("IRFuncDecl.lower() 4 error: ");
            System.out.println(e);

        }

        // try {
        // buildCFG2(evenCleanerStmts.stmts());
        // } catch (Exception e) {
        // System.out.print("Error when building CFG ");
        // System.out.println(e);

        // }

        ArrayList<BasicBlock> cleanedBlockList = getBasicBlocks2(evenCleanerStmts);

        // System.out.println("Cleaner Statements:");
        // for (IRStmt stmt : evenCleanerStmts.stmts()) {
        // System.out.println(stmt.toString());

        // }
        // System.out.println();

        try {
            buildCFG3(cleanedBlockList);
        } catch (Exception e) {
            System.out.print("Error when building CFG ");
            System.out.println(e);

        }

        // //////System.out.println(blockList.size());
        ////// System.out.println("========================================================");
        // for (BasicBlock b : blockList) {
        // //////System.out.println("Name: "+b.getName());
        // //////System.out.println("Start label: "+b.getStartLabel());
        // //////System.out.println("Next label: "+b.getNextBlockLabel()+"\n\n");
        // }
        // //////System.out.println("========================================================");
        // //////System.out.println("SETTING PREDECESSORS");
        // //////System.out.println("========================================================");
        // setPredecessors(blockList); // greedyReorder helper 1
        // //////System.out.println("PREDECESSORS HAVE BEEN SET");
        // //////System.out.println("========================================================\n");
        // //////System.out.println("========================================================");
        // //////System.out.println("CHOOSING BLOCK");
        // //////System.out.println("========================================================");
        // BasicBlock chosenBlock = chooseBlock(blockList); // greedyReorder helper 2
        // //////System.out.println("========================================================");
        // //////System.out.println("BLOCK HAS BEEN CHOSEN");
        // //////System.out.println("========================================================\n");
        // //////System.out.println("========================================================");
        // //////System.out.println("CHOOSING TRACE");
        // //////System.out.println("========================================================");
        // ArrayList<String> trace = getTrace(blockList, chosenBlock); // greedyReorder
        // helper 3
        // //////System.out.println("========================================================");
        // //////System.out.println("TRACE HAS BEEN CHOSEN");
        // //////System.out.println("========================================================\n");
        // //////System.out.println("========================================================\n");
        // //////System.out.println("MARKING BLOCKS THAT APPEARED IN TRACE");
        // //////System.out.println("========================================================");
        // ArrayList<BasicBlock> subOutputList = markAdd(blockList, trace); //
        // greedyReorder helper 4
        // //////System.out.println("========================================================");
        // //////System.out.println("BLOCKS THAT APPEARED IN TRACE HAVE BEEN MARKED &
        // ADDED TO OUTPUT LIST");
        // //////System.out.println("========================================================\n");
        // if (chosenBlock != null) {
        // //////System.out.println("Chose this block: "+chosenBlock.getName());
        // //////System.out.println("Trace for this block: ");
        // for (int i = 0; i < trace.size(); i++) {
        // //////System.out.println(trace.get(i));
        // }
        // } else {
        // //////System.out.println("Chosen block was null...uh oh");
        // }

        // for (BasicBlock b : blockList) {
        // //////System.out.println("Name: "+b.getName());
        // //////System.out.println("Start label: "+b.getStartLabel());
        // //////System.out.println("Next label: "+b.getNextBlockLabel());
        // if (b.isMarked()) {
        // //////System.out.println("This block is marked!");
        // } else {
        // //////System.out.println("This block is NOT marked.");
        // }
        //// if (b.getPredecessors().size() != 0) {
        //// for (int i = 0; i < b.getPredecessors().size(); i++) {
        //// //////System.out.println("Predecessor: "+
        ////// b.getPredecessor(i).getName()+"\n");
        //// }
        //// } else {
        //// //////System.out.println("Predecessor: null\n\n");
        //// }
        // }
        ////// System.out.println("========================================================");
        ////// System.out.println("REORDERED LIST");
        ////// System.out.println("========================================================");

        // for (BasicBlock b : reorderedList) {
        // //////System.out.println("Name: "+b.getName());
        // //////System.out.println("Start label: "+b.getStartLabel());
        // //////System.out.println("Next label: "+b.getNextBlockLabel());
        // if (b.isMarked()) {
        // //////System.out.println("This block is marked!");
        // } else {
        // //////System.out.println("This block is NOT marked.");
        // }
        //// if (b.getPredecessors().size() != 0) {
        //// for (int i = 0; i < b.getPredecessors().size(); i++) {
        //// //////System.out.println("Predecessor: "+
        //// b.getPredecessor(i).getName()+"\n");
        //// }
        //// } else {
        //// //////System.out.println("Predecessor: null\n\n");
        //// }
        // }
        ////// System.out.println("========================================================");
        ////// System.out.println("CLEANED LIST");
        ////// System.out.println("========================================================");

        // for (BasicBlock b : cleanedUpList) {
        // //////System.out.println("Name: "+b.getName());
        // //////System.out.println("Start label: "+b.getStartLabel());
        // //////System.out.println("Next label: "+b.getNextBlockLabel());
        // if (b.isMarked()) {
        // //////System.out.println("This block is marked!");
        // } else {
        // //////System.out.println("This block is NOT marked.");
        // }
        // if (b.getPredecessors().size() != 0) {
        // for (int i = 0; i < b.getPredecessors().size(); i++) {
        // //////System.out.println("Predecessor: "+
        // b.getPredecessor(i).getName()+"\n");
        // }
        // } else {
        // //////System.out.println("Predecessor: null\n\n");
        // }
        // }

        // Next, need to Greedy reorder basic blocks
        // Then, eliminate unnessessary jumps
        // Then, eliminate unnessary labels

        // uncomment below return for non-block-reordered body
        // return factory.IRFuncDecl(name,
        // body.lower(factory));
        //
        // returns body after block reordering
        // return factory.IRFuncDecl(name,
        // reorderedStmts);

        // System.out.println("Printing toplevel instruction sequence:");
        // System.out.println(evenCleanerStmts.stmts().toString());

        // TODO: try printing out the CFG before and after calling copyPropagation()

        IRSeq flattenedCFG = factory.IRSeq(IRcfg.flatten());
        return factory.IRFuncDecl(name,
                flattenedCFG, IRcfg);

        // Return final, block-reordered code
        // return factory.IRFuncDecl(name,
        // evenCleanerStmts, IRcfg);
    }

    /**
     * If a node exits with a jump (or a label), its control flow will be different.
     * returns 3 if regular Jump, 2 if call statement, 1 if CJump, 0 if not a jump
     */
    private int isBlockJump(HashMap<String, ArrayList<BasicBlock>> jumps, BasicBlock block) {
        // this will only actually be a label if s is IRJump or IRLabel, doesn't matter
        // tho
        IRStmt s = block.getExitStmt();
        String label = s.targetString();
        boolean duplicate = false;
        if (jumps.containsKey(label)) {
            duplicate = true;

        }

        if (s instanceof IRJump) {
            if (duplicate) {
                jumps.get(label).add(block);
            } else {
                // jumps holds the target for the block's exit, along with the blocks that
                // target that label
                ArrayList<BasicBlock> sList = new ArrayList<>();
                sList.add(block);
                jumps.put(s.targetString(), sList);
            }
            return 3;
        }
        if (s instanceof IRCallStmt) {
            if (duplicate) {
                jumps.get(label).add(block);
            } else {
                ArrayList<BasicBlock> sList = new ArrayList<>();
                sList.add(block);
                jumps.put(s.targetString(), sList);
            }
            return 2;
        }
        if (s instanceof IRCJump) {
            if (duplicate) {
                jumps.get(label).add(block);
            } else {
                ArrayList<BasicBlock> sList = new ArrayList<>();
                sList.add(block);
                jumps.put(s.targetString(), sList);
            }
            return 1;
        } else {
            return 0;
        }

    }

    private boolean isBlockLabel(HashMap<String, BasicBlock> labels, BasicBlock block) {
        IRStmt s = block.getEntryStmt();
        // System.out.println("BlockLabel " + block.getName());
        if (s instanceof IRLabel) {
            // System.out.println(block.getName() + " is a label");
            labels.put(s.targetString(), block);
            return true;
        } else {
            return false;
        }

    }

    private void addBlockJumpsAndLabels(HashMap<String, ArrayList<BasicBlock>> jumps,
            HashMap<String, BasicBlock> labels,
            BasicBlock... b) {
        for (BasicBlock block : b) {
            isBlockJump(jumps, block);
            isBlockLabel(labels, block);
        }
    }

    // these need to be global data because of different function declarations
    // Actually, now this is not the case
    // HashMap<String, ArrayList<BasicBlock>> block_jumps = IRcfg.jumps;
    // HashMap<String, BasicBlock> block_labels = IRcfg.labels;

    // this one makes a CFG with basic blocks as the nodes... that should make it
    // easier to flatten
    private void buildCFG3(ArrayList<BasicBlock> blocks) {

        // for (BasicBlock block : blocks) {
        // System.out.println(block.getName() + "\n" + block.toString() + "\n");
        // }

        // System.out.println("IRFuncDecl 1");
        HashMap<String, ArrayList<BasicBlock>> jumps = IRcfg.jumps;
        HashMap<String, BasicBlock> labels = IRcfg.labels;
        /*
         * assuming that basic blocks start with labels and end with jumps, can
         * just check the entry statements to see if they are labels, and
         * exit statements to check if they are jumps
         */
        for (int i = 0; i < blocks.size(); i++) {
            // System.out.println("IRFuncDecl 2");
            BasicBlock block = blocks.get(i);
            BasicBlock next = null;
            if (i + 1 <= blocks.size() - 1) {
                next = blocks.get(i + 1);
            }
            addBlockJumpsAndLabels(jumps, labels, block);
            if (isBlockJump(jumps, block) < 3) {
                // System.out.println("This block: " + block.getName());
                // if (next != null)
                // System.out.println("Next block: " + next.getName());
                // else {
                // System.out.println("No next block");
                // }
                IRcfg.addEdge(block, next);

            }
        }

        // System.out.println("JUMPS");
        // System.out.println(jumps.toString());

        // System.out.println("LABELS");
        // System.out.println(labels.toString());

        for (Map.Entry<String, ArrayList<BasicBlock>> jumpNodes : jumps.entrySet()) {
            // System.out.println("IRFuncDecl 3");
            String jumpLabel = jumpNodes.getKey();
            ArrayList<BasicBlock> jumpNodeList = jumpNodes.getValue();
            BasicBlock label = labels.get(jumpLabel);

            for (BasicBlock jump : jumpNodeList) {
                // System.out.println("IRFuncDecl 4");
                // boolean isCJump = (isBlockJump(jumps, jump)) == 2;
                boolean isCJump = (jump.getExitStmt() instanceof IRCallStmt);
                if (isCJump && label != null) {
                    // if (label != null) {
                    // System.out.println("IRFuncDecl 5");
                    IRcfg.removeEdge(jump);
                    // System.out.println("IRFuncDecl 6");
                }

                // System.out.println("IRFuncDecl 7");

                IRcfg.addEdge(jump, label);
                // System.out.println("IRFuncDecl 8");
            }
            // System.out.println("IRFuncDecl 9");

        }
        // System.out.println("IRFuncDecl 10");

    }

    /**
     * If a node is a jump (or a label), its control flow will be different.
     * returns 3 if regular Jump, 2 if call statement, 1 if CJump, 0 if not a jump
     */
    private int isJump(HashMap<String, ArrayList<IRStmt>> jumps, IRStmt s) {
        // this will only actually be a label if s is IRJump or IRLabel, doesn't matter
        // tho
        String label = s.targetString();
        boolean duplicate = false;
        if (jumps.containsKey(label)) {
            duplicate = true;

        }

        if (s instanceof IRJump) {
            if (duplicate) {
                jumps.get(label).add(s);
            } else {
                ArrayList<IRStmt> sList = new ArrayList<>();
                sList.add(s);
                jumps.put(s.targetString(), sList);
            }
            return 3;
        }
        if (s instanceof IRCallStmt) {
            if (duplicate) {
                jumps.get(label).add(s);
            } else {
                ArrayList<IRStmt> sList = new ArrayList<>();
                sList.add(s);
                jumps.put(s.targetString(), sList);
            }
            return 2;
        }
        if (s instanceof IRCJump) {
            if (duplicate) {
                jumps.get(label).add(s);
            } else {
                ArrayList<IRStmt> sList = new ArrayList<>();
                sList.add(s);
                jumps.put(s.targetString(), sList);
            }
            return 1;
        } else {
            return 0;
        }

    }

    private boolean isLabel(HashMap<String, IRStmt> labels, IRStmt s) {
        if (s instanceof IRLabel) {
            labels.put(s.targetString(), s);
            return true;
        } else {
            return false;
        }

    }

    private void addJumpsAndLabels(HashMap<String, ArrayList<IRStmt>> jumps, HashMap<String, IRStmt> labels,
            IRStmt... s) {
        for (IRStmt stmt : s) {
            isJump(jumps, stmt);
            isLabel(labels, stmt);
        }
    }

    // these need to be global data because of different function declarations
    HashMap<String, ArrayList<IRStmt>> jumps = JumpMap.jumps;
    HashMap<String, IRStmt> labels = LabelMap.labels;

    private void buildCFG2(List<IRStmt> stmts) {
        // System.out.println("\nNEW IRFUNCDECL" + name + "\n");

        // HashMap<String, ArrayList<IRStmt>> jumps = new HashMap<>();
        // HashMap<String, IRStmt> labels = new HashMap<>();

        /*
         * For each other statement in this block, add an edge from each node to the
         * next
         */
        // int i = 0;
        // for (IRStmt stmt : body) {
        for (int i = 0; i < stmts.size() - 1; i++) {
            IRStmt stmt = stmts.get(i);
            IRStmt next = stmts.get(i + 1);
            addJumpsAndLabels(jumps, labels, stmt);
            if (next instanceof IRCJump) {
                // System.out.println("Destination body CJump " + next.toString());

            }
            if (isJump(jumps, stmt) < 3) {
                Graph.addEdge(stmt, next);
            }
            // i++;
        }

        // System.out.println("JUMPS");
        // System.out.println(jumps.toString());

        // System.out.println("LABELS");
        // System.out.println(labels.toString());

        for (Map.Entry<String, ArrayList<IRStmt>> jumpNodes : jumps.entrySet()) {
            String jumpLabel = jumpNodes.getKey();
            ArrayList<IRStmt> jumpNodeList = jumpNodes.getValue();
            IRStmt label = labels.get(jumpLabel);

            for (IRStmt jump : jumpNodeList) {
                // System.out.println("adding edge :" + jump.toString() + "->" +
                // label.toString());
                if (jump instanceof IRCallStmt && label != null) {
                    Graph.removeEdge(jump);
                }

                Graph.addEdge(jump, label);
            }

        }

    }

    private void buildCFG(ArrayList<BasicBlock> blocks) {

        // HashMap<String, ArrayList<IRStmt>> jumps = new HashMap<>();
        // HashMap<String, IRStmt> labels = new HashMap<>();

        for (BasicBlock block : blocks) {
            ArrayList<BasicBlock> preds = block.getPredecessors();
            /*
             * for each predecessor of this block, add edge from the pred's exit node
             * to this block's entry node
             */
            IRStmt entry = block.getEntryStmt();
            IRStmt exit = block.getExitStmt();
            addJumpsAndLabels(jumps, labels, exit, entry);
            for (BasicBlock pred : preds) {
                IRStmt predExit = pred.getExitStmt();

                addJumpsAndLabels(jumps, labels, predExit);

                if (entry instanceof IRCJump) {
                    System.out.println("Destination CJump " + entry.toString());

                }

                if (isJump(jumps, predExit) < 2) { // if not a regular jump, add fall through edge
                    Graph.addEdge(predExit, entry);
                }
            }
            /*
             * For each other statement in this block, add an edge from each node to the
             * next
             */
            ArrayList<IRStmt> body = block.getBody();
            if (body.size() > 0) {

                // do we need to add this entry statement to first body edge?
                // ie: does the body include the entry/exit statement?

                if (body.get(0) instanceof IRCJump) {
                    System.out.println("Destination entry CJump " + body.get(0));

                }
                if (isJump(jumps, entry) < 2) { // if not a regular jump, add fall through edge
                    Graph.addEdge(entry, body.get(0));
                }

                // int i = 0;
                // for (IRStmt stmt : body) {
                for (int i = 0; i < body.size() - 2; i++) {
                    IRStmt stmt = body.get(i);
                    IRStmt next = body.get(i + 1);
                    addJumpsAndLabels(jumps, labels, stmt);
                    if (next instanceof IRCJump) {
                        System.out.println("Destination body CJump " + next.toString());

                    }
                    if (isJump(jumps, stmt) < 2) {
                        Graph.addEdge(stmt, next); // just added a +1 here
                    }
                    // i++;
                }
                IRStmt lastStmt = body.get(body.size() - 1);
                if (exit instanceof IRCJump) {
                    System.out.println("Destination exit CJump " + exit.toString());

                }
                if (isJump(jumps, lastStmt) < 2) {
                    Graph.addEdge(lastStmt, exit);
                }

            } else {
                if (exit instanceof IRCJump) {
                    System.out.println("Destination exit2 CJump " + exit.toString());

                }

                if (!entry.equals(exit))
                    Graph.addEdge(entry, exit);
            }

        }

        // System.out.println("JUMPS");
        // System.out.println(jumps.toString());

        // System.out.println("LABELS");
        // System.out.println(labels.toString());

        for (Map.Entry<String, ArrayList<IRStmt>> jumpNodes : jumps.entrySet()) {
            String jumpLabel = jumpNodes.getKey();
            ArrayList<IRStmt> jumpNodeList = jumpNodes.getValue();
            IRStmt label = labels.get(jumpLabel);
            for (IRStmt jump : jumpNodeList) {
                // System.out.println("adding edge :" + jump.toString() + "->" +
                // label.toString());
                Graph.addEdge(jump, label);
            }

        }

    }

    /**
     * Reorders list of blocks in a way to reduce number of jumps
     */
    private ArrayList<BasicBlock> greedyReorder(ArrayList<BasicBlock> rawBlockList) {
        // All blocks begin as unmarked (isMarked initialized to false for all blocks)
        ArrayList<BasicBlock> outputBlockList = new ArrayList<>();
        // counter - just here for testing
        // int i = 1;
        // Step 1: set predecessors of each block in rawBlockList
        // This is required for us to efficiently choose each block
        setPredecessors(rawBlockList);
        // How adding blocks works:
        // choose an unmarked block from rawBlockList
        // append this block to outputBlockList
        // starting from this block, find maximal unmarked trace
        // append all blocks in that trace to outputBlockList
        // all blocks in outputBlockList should be marked before next iteration
        while (!allMarked(rawBlockList)) {
            // Step 2: choose block - ideally, with no unmarked predecessors
            // if this is not possible, just choose an unmarked block
            // otherwise, return null
            BasicBlock chosenBlock = chooseBlock(rawBlockList);
            // Step 3: get trace - this will be a list of sequentially executed
            // unmarked blocks
            ArrayList<String> trace = getTrace(rawBlockList, chosenBlock);
            // Step 4: mark blocks used in trace and add them to temporary list
            ArrayList<BasicBlock> subOutputList = markAdd(rawBlockList, trace);
            // Step 5: Append these blocks to outputBlockList
            outputBlockList.addAll(subOutputList);
            // Repeat steps 2-5 until all blocks in rawBlockList have been marked
            // i +=1;
        }
        return outputBlockList;
    }

    /**
     * After block reordering, some cjumps need to be inverted for execution
     * to be carried out properly.
     *
     * Other cjumps require a jump to the original fall-through statement.
     *
     * This method inverts cjumps as needed and adds in necessary jumps.
     */
    private ArrayList<BasicBlock> cleanUp(ArrayList<BasicBlock> reorderedList) {
        ArrayList<BasicBlock> cleanedUpList = new ArrayList<>();

        int i = 0;
        for (BasicBlock b : reorderedList) {
            // detect if exit statement of b is CJump
            // if so, invert CJump
            if (b.getExitStmt() instanceof IRCJump) {
                // detect if CJump true label == start label of next block
                if (needsCJumpInversion(reorderedList, i)) {
                    cleanedUpList.add(invertCJump(b));
                } else {
                    // otherwise, need to add in jump to original fall-through
                    cleanedUpList.add(addJumpToOrigFT(b));
                }
            } else {
                cleanedUpList.add(b);
            }
            i += 1;
        }

        return cleanedUpList;
    }

    // invertCJumpsAsNeeded helper
    private boolean needsCJumpInversion(ArrayList<BasicBlock> reorderedList,
            int i) {
        // We know exit statement of block reorderedList[i] is a CJump
        // Need to detect if trueLabel of CJump matches start label of next block
        IRCJump ircJump = (IRCJump) reorderedList.get(i).getExitStmt();
        return ircJump.trueLabel().contains(reorderedList.get(i + 1).getStartLabel());
    }

    // invertCJumpsAsNeeded helper
    private BasicBlock invertCJump(BasicBlock b) {
        // Create new IRCJump statement and add that as new exit statement of block
        IRCJump ircJump = (IRCJump) b.getExitStmt();
        String newTrueLabel = b.getCJumpFTLabel();

        b.setNextBlockLabel(newTrueLabel);

        IRExpr exprToBeInverted = ircJump.cond();
        IRExpr newExpr = new IRBinOp(IRBinOp.OpType.XOR, new IRConst(1),
                exprToBeInverted);

        IRCJump newIRCJump = new IRCJump(newExpr, newTrueLabel);

        b.addEndOfBlock(newIRCJump);

        return b;
    }

    // invertCJumpsAsNeeded helper
    private BasicBlock addJumpToOrigFT(BasicBlock b) {
        // add CJump exit statement to body
        b.addToBody(b.getExitStmt());

        // Add new exit stmt: jump to original fall-through label
        b.addEndOfBlock(new IRJump(new IRName(b.getCJumpFTLabel())));

        return b;
    }

    /**
     * @param bodyStmts - IRSeq containing flattened sequence
     *                  of IRStmts in this IRFuncDecl
     *
     *                  Returns ArrayList of all basic blocks that appear in this
     *                  IRFuncDecl
     */
    private ArrayList<BasicBlock> getBasicBlocks(IRSeq bodyStmts) {
        ArrayList<BasicBlock> blockList = new ArrayList<>();
        ArrayList<IRStmt> funcBody = new ArrayList<IRStmt>(bodyStmts.stmts());

        int stmtNum = 0;
        boolean skip = false;
        BasicBlock block = new BasicBlock();
        for (IRStmt s : funcBody) {

            if (skip) {
                skip = false;
                stmtNum += 1;
                continue;
            }

            // test if statement is label
            // if it is, create new BasicBlock
            if (s instanceof IRLabel) {
                // weird hacky fix
                if (funcBody.get(stmtNum + 1) instanceof IRLabel) {
                    block = new BasicBlock(s);
                    block.addToBody(funcBody.get(stmtNum + 1));
                    // need to skip next iteration since we've added
                    // 2 statements to block here
                    skip = true;
                } else {
                    block = new BasicBlock(s);
                }
            }

            // Test if statement is an end statement
            else if (s instanceof IRJump
                    || s instanceof IRCJump
                    || s instanceof IRReturn) {
                block.addEndOfBlock(s);
                blockList.add(block);

                if (s instanceof IRJump) {
                    IRJump irJump = (IRJump) s;
                    block.setNextBlockLabel(irJump.target().label());
                } else if (s instanceof IRCJump) {
                    IRCJump ircJump = (IRCJump) s;
                    block.setNextBlockLabel(ircJump.trueLabel());
                    // for CJump inverting purposes
                    block.setCJumpFTStmt(funcBody.get(stmtNum + 1));
                } else {
                    if ((stmtNum + 1 < funcBody.size()
                            && funcBody.get(stmtNum + 1) instanceof IRJump)
                            || stmtNum + 1 == funcBody.size() - 1) {
                        skip = true; // skip useless statements after a return in a basic block
                    }
                    block.setNextBlockLabel("RETURN");
                }
            }
            // test if statement is an end statement (special case)
            else if (funcBody.get(stmtNum + 1) instanceof IRLabel) {
                block.addToBody(s);
                IRLabel nextStmt = (IRLabel) funcBody.get(stmtNum + 1);
                String nextLabel = block.removeParens(nextStmt.label());
                IRJump irJump = new IRJump(new IRName(nextLabel));
                block.addEndOfBlock(irJump);
                block.setNextBlockLabel(nextLabel);
                blockList.add(block);
            }

            // otherwise, s is an ordinary statement so add to body of basic block
            else {
                block.addToBody(s);
            }
            stmtNum += 1;
        }

        return blockList;
    }

    /**
     * @param bodyStmts - IRSeq containing flattened sequence
     *                  of IRStmts in this IRFuncDecl.
     *
     *                  Returns ArrayList of all basic blocks that appear in this
     *                  IRFuncDecl.
     * 
     *                  This is a streamlined version that doesn't require the
     *                  assumption that each block begins with a label (they
     *                  don't always--you have to account for jump fallthroughs)
     *                  I can't replace the v1.0 with this because there's other
     *                  code that relies on that assumption.
     */
    private ArrayList<BasicBlock> getBasicBlocks2(IRSeq bodyStmts) {
        ArrayList<BasicBlock> blockList = new ArrayList<>();
        ArrayList<IRStmt> funcBody = new ArrayList<IRStmt>(bodyStmts.stmts());

        int stmtNum = 0;
        BasicBlock block = new BasicBlock();
        boolean start = true; // true whenever next statement is start of basic block

        for (IRStmt s : funcBody) {
            if (start) {
                block.addStartOfBlock(s);
            }
            if (s instanceof IRJump
                    || s instanceof IRCJump
                    || s instanceof IRReturn
                    || funcBody.get(stmtNum + 1) instanceof IRLabel) {
                block.addEndOfBlock(s);
                blockList.add(block);
                start = true;
                block = new BasicBlock();

            } else {
                if (start) {
                    start = false;
                } else {
                    block.addToBody(s);
                }
            }
            stmtNum += 1;

        }

        return blockList;
    }

    // greedyReorder helper
    // return true if all blocks have been marked, else return false
    private boolean allMarked(ArrayList<BasicBlock> rawBlockList) {
        for (BasicBlock b : rawBlockList) {
            // if a block is still unmarked return false
            if (!b.isMarked()) {
                return false;
            }
        }
        // all blocks are marked
        return true;
    }

    // greedyReorder helper
    // mark all blocks that appeared in trace and add those to subOutputList
    // subOutputList will be returned, then use addAll to add all blocks in
    // subOutputList to OutputList (in greedyReorder)
    private ArrayList<BasicBlock> markAdd(ArrayList<BasicBlock> rawBlockList,
            ArrayList<String> trace) {
        ArrayList<BasicBlock> subOutputList = new ArrayList<>();
        for (BasicBlock b : rawBlockList) {
            if (inStrList(b.getName(), trace)) {
                b.mark(); // mark
                subOutputList.add(b); // add
            }
        }
        return subOutputList;
    }

    // markAdd helper
    // return true if block.getName() appears in trace, false otherwise
    private boolean inStrList(String name, ArrayList<String> strList) {
        for (String s : strList) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    // greedyReorder helper
    // get a reasonably large trace starting from chosenBlock
    private ArrayList<String> getTrace(ArrayList<BasicBlock> rawBlockList,
            BasicBlock chosenBlock) {
        ArrayList<String> trace = new ArrayList<>();
        trace.add(chosenBlock.getName());
        BasicBlock nextBlock = chosenBlock;
        // Starting from chosenBlock, find longest unmarked trace
        int i = 0;
        for (BasicBlock b : rawBlockList) {
            if (!b.isMarked()
                    && (b.getStartLabel().contains(nextBlock.getNextBlockLabel())
                            || (i >= 1
                                    // nextBlock is curreently a block with a FALL-THROUGH nextLabel
                                    && rawBlockList.get(i - 1).getName() == nextBlock.getName()))) {
                trace.add(b.getName());
                nextBlock = b;
            }
            i += 1;
        }
        return trace;
    }

    // greedyReorder() helper
    // Ideally, choose a block with no unmarked predecessors
    // Return null if no unmarked blocks exitst
    private BasicBlock chooseBlock(ArrayList<BasicBlock> rawBlockList) {
        BasicBlock thisBlock = null;
        // Create arrayList of blocks we've already tried and don't ever repeat
        ArrayList<BasicBlock> alreadyVisited = new ArrayList<>();
        int numCandidates = 0;
        boolean foundSufficientBlock = false;
        for (BasicBlock b : rawBlockList) {
            if (foundSufficientBlock) {
                break;
            }

            // if a block is unmarked
            if (!b.isMarked()) {
                // naively assign b to thisBlock
                thisBlock = b;
                numCandidates += 1;
                // if b has predecessors
                boolean noMoreUnmarkedPreds = false;
                // Search for a block until you've found one with no
                // unmarked predecessors OR you've already searched through
                // 10000 candidate blocks
                // TODO: consider setting limit on numCandidates
                while (!noMoreUnmarkedPreds) {
                    if (thisBlock.getPredecessors().size() == 0) {
                        noMoreUnmarkedPreds = true;
                        foundSufficientBlock = true;
                    } else {
                        // check if any of the predecessors are unmarked
                        for (int i = 0; i < thisBlock.getPredecessors().size(); i++) {
                            BasicBlock candidate = thisBlock.getPredecessors().get(i);
                            // if the candidate block is unmarked
                            // AND the candidate is not a block we've already tried
                            if (!candidate.isMarked()
                                    && !inList(candidate, alreadyVisited)) {
                                alreadyVisited.add(thisBlock);
                                thisBlock = candidate;
                                numCandidates += 1;
                                break;
                            } else {
                                // if all predecessors have been marked
                                if (i == thisBlock.getPredecessors().size() - 1) {
                                    noMoreUnmarkedPreds = true;
                                }
                            }
                        }
                    }
                }
                // thisBlock has no predecessors
            }
        }

        // all blocks are marked
        return thisBlock;
    }

    // chooseBlock helper
    // return true if b in list, false otherwise
    private boolean inList(BasicBlock b, ArrayList<BasicBlock> list) {
        for (BasicBlock k : list) {
            if (b.equals(k)) {
                return true;
            }
        }
        return false;
    }

    // greedyReorder() helper
    // Set predecessors of each block
    private void setPredecessors(ArrayList<BasicBlock> rawBlockList) {
        // if start label contains _I, no predecessor
        // if start label contains l_t, previous block in raw list is pred
        // else, predecessor is first block with next label that contains
        // current block's start label
        int i = 0;
        for (BasicBlock b : rawBlockList) {
            // if a block is still unmarked return false
            if (b.getStartLabel().contains("_I")) {
                i += 1;
                continue;
            } else {
                if (b.getStartLabel().contains("l_t")
                        && rawBlockList.get(i - 1).getName() != b.getName()) {
                    b.addPredecessor(rawBlockList.get(i - 1));
                }
            }
            for (int j = 0; j < rawBlockList.size(); j++) {
                if (rawBlockList.get(j).getNextBlockLabel().contains(
                        b.getStartLabel()) && rawBlockList.get(j).getName() != b.getName()) {

                    b.addPredecessor(rawBlockList.get(j));

                }
            }
            i += 1;
        }
    }

    // helper to extract statements from ArrayList of blocks and return in single
    // IRSeq
    private IRSeq extractCode(ArrayList<BasicBlock> reorderedList) {
        ArrayList<IRStmt> stmts = new ArrayList<>();
        for (BasicBlock b : reorderedList) {
            b.printCJumpFTStmt();
            // get entry stmt
            stmts.add(b.getEntryStmt());
            // get body
            ArrayList<IRStmt> body = b.getBody();
            for (IRStmt bodyStmt : body) {
                stmts.add(bodyStmt);
            }
            // get exit stmt
            if (b.getExitStmt() != null) {
                stmts.add(b.getExitStmt());
            }
        }
        return new IRSeq(stmts);
    }

    // Removes unnecessary jumps and labels
    private IRSeq removeUnnecessaryCode(IRSeq cleanedUpStmts) {
        ArrayList<IRStmt> stmts = new ArrayList<>(cleanedUpStmts.stmts());
        ArrayList<IRStmt> output = new ArrayList<>();
        BasicBlock b = new BasicBlock();
        for (int i = 0; i < stmts.size(); i++) {
            if (stmts.get(i) instanceof IRJump && i < stmts.size() - 1
                    && stmts.get(i + 1) instanceof IRLabel) {
                IRJump tmpJump = (IRJump) stmts.get(i);
                String jmpTarget = b.removeParens(tmpJump.target().label());

                IRLabel tmpLabel = (IRLabel) stmts.get(i + 1);
                String dst = b.removeParens(tmpLabel.label());
                if (!jmpTarget.equals(dst)) {
                    output.add(stmts.get(i));
                }

            } else {
                output.add(stmts.get(i));
            }
        }
        return removeUnnecessaryLabels(new IRSeq(output));
    }

    // Removes unreferenced labels
    private IRSeq removeUnnecessaryLabels(IRSeq rujOutput) {
        // get arrayList of jmpTargets (includes CJump targets)
        ArrayList<String> jmpTargets = getJmpTargets(rujOutput);
        ArrayList<IRStmt> stmts = new ArrayList<>(rujOutput.stmts());
        ArrayList<IRStmt> output = new ArrayList<>();
        BasicBlock b = new BasicBlock();

        // for each label in the IRSeq, test if it appears in jmpTarget
        // if it does, keep the label. Otherwise, don't include the label

        for (IRStmt s : stmts) {
            if (s instanceof IRLabel) {
                IRLabel tmpLabel = (IRLabel) s;
                String dst = b.removeParens(tmpLabel.label());
                // include label if it is referenced by a Jump or CJump
                // or if it is function header
                if (inStrList(dst, jmpTargets) || dst.contains("_I")) {
                    output.add(s);
                }
            } else {
                output.add(s);
            }
        }
        return new IRSeq(output);
    }

    // removeUnnecessaryLabels helper
    private ArrayList<String> getJmpTargets(IRSeq rujOutput) {
        ArrayList<String> jmpTargets = new ArrayList<>();
        ArrayList<IRStmt> stmts = new ArrayList<>(rujOutput.stmts());
        BasicBlock b = new BasicBlock();

        for (IRStmt s : stmts) {
            if (s instanceof IRJump) {
                IRJump tmpJump = (IRJump) s;
                String jmpTarget = b.removeParens(tmpJump.target().label());
                jmpTargets.add(jmpTarget);
            }

            if (s instanceof IRCJump) {
                IRCJump tmpCJump = (IRCJump) s;
                String cjmpTarget = tmpCJump.trueLabel();
                jmpTargets.add(cjmpTarget);
            }
        }
        return jmpTargets;
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("FUNC");
        p.printAtom(name);
        body.printSExp(p);
        p.endList();
    }
}