package main.java.jdr299zdh5cew256ans96.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters.UuidConverter;

import java.util.Collections;
import java.util.Arrays;

import java_cup.lalr_item;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.ir.BasicBlock;
import main.java.jdr299zdh5cew256ans96.ir.IRCJump;
import main.java.jdr299zdh5cew256ans96.ir.IRCallStmt;
import main.java.jdr299zdh5cew256ans96.cli;

/**
 * this class is used to represent a Control Flow Graph (CFG)
 * for our IR representation of a program. It implements a singly-directed Graph
 * as an Adjacency List
 */
public class BlockGraph {
  // I'm making some changes in crunch time, so just making everything public
  // rather than doing getters and setters.
  public HashMap<BasicBlock, ArrayList<BasicBlock>> cfg = new HashMap<>();
  public HashMap<BasicBlock, ArrayList<BasicBlock>> jumpLabelPairs = new HashMap<>();

  public HashMap<String, ArrayList<BasicBlock>> jumps = new HashMap<>();
  public HashMap<String, BasicBlock> labels = new HashMap<>();

  private BasicBlock nullBlock = new BasicBlock();

  public void addEdge(BasicBlock s, BasicBlock d) {
    // System.out.println("\n\nBEFORE:\n" + printEdges());
    // System.out.println("BlockGraph 1");

    if (d == null) {
      // System.out.println("d is null");
      // System.out.println("s is " + s.getName());
      // return;
      d = nullBlock;
    }

    // System.out.println("BlockGraph 2");
    if (cfg.containsKey(s)) {

      // System.out.println("BlockGraph 3");
      ArrayList<BasicBlock> children = cfg.get(s);

      // update outgoing edges
      if (!children.contains(d)) {
        // System.out.println("BlockGraph 4");
        children.add(d);
        // cfg.remove(s);
        cfg.put(s, children);
      }
    } else {
      // System.out.println("BlockGraph 5");
      if (!d.equals(s)) {
        ArrayList<BasicBlock> dList = new ArrayList<>();
        dList.add(d);
        cfg.put(s, dList);
      }

    }

    // System.out.println("\n\nAFTER:\n" + printEdges());

  }

  /** Remove outgoing edges from a given node in the Graph */
  public void removeEdge(BasicBlock n) {
    cfg.remove(n);
  }

  public HashMap<BasicBlock, ArrayList<BasicBlock>> getCFG() {
    return cfg;
  }

  /**
   * Merge any number of BlockGraphs into this BlockGraph, with consolidated
   * nodes and edges
   */
  public void merge(BlockGraph... blocks) {
    /*
     * this will delete duplicate keys, but I don't actually think that's an
     * issue for merging CFG for each function
     */
    for (BlockGraph b : blocks) {
      // System.out.println("Blockgraph.merge");
      // System.out.println("cfg toString: \n" + cfg.toString());
      // System.out.println("b.cfg toString: \n" + b.cfg.toString());
      cfg.putAll(b.cfg);
      jumpLabelPairs.putAll(b.jumpLabelPairs);
      jumps.putAll(b.jumps);
      labels.putAll(b.labels);
    }

  }

  public String printEdges() {
    String s = "";
    for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> node : cfg.entrySet()) {
      BasicBlock block = node.getKey();
      s = s + block.getName() + " = [";
      ArrayList<BasicBlock> blocks = node.getValue();
      for (BasicBlock successor : node.getValue()) {
        s = s + successor.getName() + ", ";

      }
      s = s + "], ";

    }
    return s;
  }

  public String toString() {
    String s = "";
    for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> node : cfg.entrySet()) {
      BasicBlock block = node.getKey();
      s = s + block.toString() + " = [";
      ArrayList<BasicBlock> blocks = node.getValue();
      for (BasicBlock successor : node.getValue()) {
        s = s + successor.getName() + ", ";

      }
      s = s + "], \n";

    }
    return s;

  }

  /**
   * Flatten an IR Graph back into a list of statements. The control flow should
   * not change
   */
  public List<IRStmt> flatten() {
    ArrayList<IRStmt> stmts = new ArrayList<>();
    HashMap<Integer, BasicBlock> numberedBlocks = new HashMap<>();
    ArrayList<Integer> blockNumbers = new ArrayList<>();

    for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> node : cfg.entrySet()) {
      BasicBlock block = node.getKey();
      int blockNumber = block.getCount();
      numberedBlocks.put(blockNumber, block);
      blockNumbers.add(blockNumber);

    }

    // Collections.sort(blockNumbers, Collections.reverseOrder());
    Collections.sort(blockNumbers);

    for (Integer blockNumber : blockNumbers) {
      BasicBlock block = numberedBlocks.get(blockNumber);
      IRStmt entry = block.getEntryStmt();
      IRStmt exit = block.getExitStmt();
      stmts.add(entry);
      stmts.addAll(block.getBody());
      // account for the exit/entry implementation not allowing
      // for one-instruction blocks
      if (!entry.equals(exit)) {
        stmts.add(exit);
      }

    }

    // System.out.println("Flattened BlockGraph CFG: \n" + stmts);

    return stmts;

  }

  /**
   * Work within a basic block and numbers each IRExpr sequentially.
   * This works inside-out. eg: in the binop (i + x), number as (i1 + x2)3
   */
  public void valueNumbering() {
    int blocks = cfg.entrySet().size();
    assert blocks == 1 : "Error in BlockGraph.valueNumbering(): "
        + "Expected only one block, got " + blocks;

    int i = 0;
    // should be just one block
    List<IRStmt> stmts = flatten();
    for (IRStmt stmt : stmts) {
      if (stmt instanceof IRMove) {
        IRMove mov = (IRMove) stmt;
        IRExpr src = mov.source();
        IRExpr tgt = mov.target();
        i = src.valueNumbering(i);
        // tgt should just be a temp
        i = tgt.setNumber(i - 1);
        System.out.println("BlockGraph: " + tgt.toString() + " value number " + tgt.number());

      }
    }

  }

  public void copyPropagation() {
    // this alters the CFG nodes by reference
    /*
     * Walk through statements.
     * Need a list of copies. eg: hashmap where keys are temps, vals are what they
     * are copies of
     * - So when you see any variable, check to see if it's a copy first.
     * - If it is, you replace that variable with its copy
     * If a statement introduces a copy, save what that copy is in the list of
     * copies
     * - In which case, add that node's children to the worklist (you need to make
     * sure to re-visit them because variables changed)
     */
    // boolean dce = false;
    // if (Arrays.asList(cli.turnedOnOpts).contains("dce")) {
    // dce = true;
    // }
    List<IRStmt> stmtList = flatten();
    List<IRStmt> worklist = flatten();

    ArrayList<IRMove> movs = new ArrayList<>();
    // store temps that represent copies
    HashMap<String, String> copies = new HashMap<>();
    HashMap<String, String> mems = new HashMap<>();
    HashMap<String, String> rvs = new HashMap<>();
    IRStmt prevNode = null;
    while (worklist.size() > 0) {
      boolean copy = true;
      IRStmt node = worklist.remove(0);
      // ONLY Move statements represent variable assignment
      if (node instanceof IRMove) {
        IRMove n = (IRMove) node;
        IRMove pn = null;
        if (prevNode != null) {
          pn = (IRMove) prevNode;
        }

        // System.out.println("current mems:" + mems.toString());

        /*
         * if a temp contains a memory location, make it so it (and
         * any variables relying on it) doesn't get copied
         */
        IRExpr source = n.source();

        if (source instanceof IRName) {
          // System.out.println(source.toString() + " is a memema");
          copy = false;
        }
        List<IRTemp> memTemps = source.temps();
        // System.out.println("memTemps: " + memTemps.toString());
        for (IRTemp memTemp : memTemps) {
          if (mems.containsKey(memTemp.toString())) {
            // System.out.println("found memTemp: " + memTemp.toString());
            copy = false;

          }

          if (rvs.containsKey(memTemp.toString())) {
            copy = false;

          }
        }

        if (!copy) {
          mems.put(n.target().toString(), "");
        } else {
          // Only care about variable assignment to variable
          // TODO: also want to change variables inside expressions

          if (source instanceof IRTemp && n.target() instanceof IRTemp) {
            IRTemp src = (IRTemp) source;
            String srcName = src.name();
            IRTemp tgt = (IRTemp) n.target();
            String tgtName = tgt.name();

            // if the src variable is a copy, propagate the original
            if (copies.containsKey(srcName)) {
              // System.out.println("setting: " + srcName + " to be " + copies.get(srcName));
              // as long as the src temp isn't an argument or return value register
              // if (!(srcName.contains("_RV") || (srcName.contains("_ARG")) ||
              // !(srcName.contains("T_t")))) {
              if (!(srcName.contains("_RV") || (srcName.contains("_ARG")) || (srcName.contains("largest")))) {
                // System.out.println("*yes");
                srcName = copies.get(srcName);
                // IRTemp pSrc = (IRTemp) pn.source();
                // String pSrcName = pSrc.name();
                // IRTemp pTgt = (IRTemp) pn.target();
                // String pTgtName = pTgt.name();
                // System.out.println("prev: mov" + pTgtName + " " + pSrcName);
                // if (pSrcName == pTgtName) {
                // System.out.println("marking: " + pSrcName + " mov to itself dead");
                // prevNode.markDead(true);
                // }
                src.setName(srcName);

              } else if (srcName.contains("_RV")) {
                rvs.put(tgtName, "");

              }
            }

            // save that the target is a copy of the source variable
            if (!(srcName.contains("_RV") || (srcName.contains("_ARG")) || (srcName.contains("largest")))) {
              copies.put(tgtName, srcName);
            } else if (srcName.contains("_RV")) {
              rvs.put(tgtName, "");

            }

            // System.out.println("found: " + tgtName + " is now a copy of " + srcName);

            // // save that the target is a copy of the source variable
            // copies.put(tgtName, srcName);

            // out[n] was modified, so push child nodes onto the worklist
            // since only Move instructions modify, can just add the fall-through
            // node (there won't be any jumps at this point)
            int child = stmtList.indexOf(n) + 1;
            if (child < stmtList.size() - 1) { // bounds check
              worklist.add(stmtList.get(child));
            }

            movs.add(n);

          }

          prevNode = node;

        }

        // instead just call dce separately
        // if (dce) {
        // deadCodeElimination();
        // }
      }

    }

    for (IRMove mov : movs) {
      // all of the IRMove's must have temps
      String tgtName = ((IRTemp) mov.target()).name();
      String srcName = ((IRTemp) mov.source()).name();
      if (tgtName == srcName) {
        mov.markDead(true);
        // System.out.println("marking: mov " + tgtName + " " + srcName);

      }

    }

  }

  // ** Mark assignments as dead if their definition is not used. */
  public void reachingDefinitions() {
    List<IRStmt> stmts = flatten();
    // Map definition var to the statement that defined it
    // HashMap<IRExpr, IRStmt> unusedDefs = new HashMap<>();
    HashMap<String, IRStmt> unusedDefs = new HashMap<>();
    // variables that get memory addr assigned to them don't become dead,
    // even if they don't have a typical "use"
    HashMap<String, IRStmt> memoryDefs = new HashMap<>();
    HashMap<String, IRStmt> memAllocOutput = new HashMap<>();
    boolean alloc = false;
    for (IRStmt node : stmts) {
      if (node instanceof IRCallStmt) {
        IRCallStmt call = (IRCallStmt) node;
        if (call.toString().contains("_eta_alloc")) {
          alloc = true;
        }

      }
      if (node instanceof IRMove) {
        IRMove n = (IRMove) node;
        IRExpr tgt = n.target();
        String tgtString = tgt.toString();
        IRExpr src = n.source();
        String srcString = tgt.toString();
        // is this node a "use" for a previous def?
        // System.out.println("unusedDefs: " + unusedDefs.toString());

        if (alloc) {
          memAllocOutput.put(tgtString, node);
          alloc = false;
        }

        for (IRTemp tmp : src.temps()) {
          String tmpString = tmp.toString();
          // System.out.println("src: " + src.toString() + " temp: " + tmp.toString());
          // if this src expression contains a tmp that has prev
          // been defined, remove its original def stmt from unusedDefs
          if (unusedDefs.containsKey(tmpString)) {
            // System.out.println("Def for " + tmp.toString() +
            // ": " + unusedDefs.get(tmpString) + " not dead");
            unusedDefs.remove(tmpString);
          }

          // what the hell. Trying to prevent any temp dependent on RV from eta_alloc
          // from being dead
          if (memAllocOutput.containsKey(tmpString)) {
            memAllocOutput.put(tgtString, node);
          }
        }

        // after removing defs that were used, is there a re-defining of a
        // variable that has an old unusedDef?
        if (unusedDefs.containsKey(tgtString)) {
          // if so, mark the old definition as dead
          IRStmt deadNode = unusedDefs.get(tgtString);
          // System.out.println("Marking node " + deadNode.toString() + " as dead");
          if (!(deadNode.toString().contains("_RV") || (deadNode.toString().contains("_ARG"))))
            deadNode.markDead(true);
        }

        // new definition for whatever temp is the target of this move
        // System.out.println("New def for " + tgt.toString() + ": " + node.toString());
        // as long as it's not an identity definition
        if (tgtString != srcString) {
          // make sure to not put mem targets into defs because they'll get
          // marked as dead code otherwise

          if (src instanceof IRMem || memoryDefs.containsKey(srcString)) {
            // Do i just want to put the initial mem def in?
            memoryDefs.put(tgtString, node);

          }
          if (tgt instanceof IRMem) {
            IRMem mem = (IRMem) tgt;
            List<IRTemp> tmps = mem.temps();
            for (IRTemp tmp : tmps) {
              String tmpString = tmp.toString();
              if (unusedDefs.containsKey(tmpString)) {
                unusedDefs.remove(tmpString);
              }
            }
          } else {

            unusedDefs.put(tgtString, node);
          }
        }

      } else if (node instanceof IRCJump) {
        // might also need variables in the condidion of a CJUMP. Anything else I'm
        // forgetting?
        // can't redefine, so just check if there's a use
        IRCJump cj = (IRCJump) node;
        List<IRTemp> tmps = cj.cond().temps();
        for (IRTemp tmp : tmps) {
          String tmpString = tmp.toString();
          if (unusedDefs.containsKey(tmpString)) {
            unusedDefs.remove(tmpString);
          }
        }

      }
    }

    // System.out.println("Final dead code marking:");
    for (Map.Entry<String, IRStmt> deadDefs : unusedDefs.entrySet()) {
      IRStmt deadNode = deadDefs.getValue();
      String s = deadDefs.getKey();
      if (!(deadNode.toString().contains("_RV")
          || deadNode.toString().contains("_ARG") || memoryDefs.containsKey(s)
          || memAllocOutput.containsKey(s))) {

        // System.out.println("Marking dead: " + deadNode.toString());
        deadNode.markDead(true);
      }
    }

  }

  public void deadCodeElimination() {
    // first do reaching definitions to mark more nodes as dead
    reachingDefinitions();
    ArrayList<BasicBlock> newBlocks = new ArrayList<>();
    for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> node : cfg.entrySet()) {

      BasicBlock block = node.getKey();
      List<IRStmt> stmts = block.getBody();
      // if the entry statement is dead, remove it and replace with next live stmt
      IRStmt entry = block.getEntryStmt();
      // System.out.println("BlockGraph dce 1: \n" + block.toString());
      while (entry.isDead()) {
        if (stmts.size() > 0) {
          IRStmt firstBodyStmt = stmts.get(0);
          // System.out.println("removing " + entry.toString());
          block.addStartOfBlock(firstBodyStmt);
          entry = block.getEntryStmt();
          // ArrayList<BasicBlock> v = cfg.get(block);
          // cfg.remove(block);
          // block.getBody().remove(firstBodyStmt);
          // cfg.put(block, v);
          stmts.remove(firstBodyStmt);
          // System.out.println("BlockGraph dce 2: \n" + block.toString());

        } else {
          block.addStartOfBlock(block.getExitStmt());
          // System.out.println("BlockGraph dce 3: \n" + block.toString());
        }

      }
      // for (IRStmt stmt : stmts) {
      int stmtNum = stmts.size();
      for (int i = 0; i < stmtNum - 1; i++) {
        IRStmt stmt = stmts.get(i);
        if (stmt.isDead()) {
          // System.out.println("removing2 " + stmt.toString());
          // ArrayList<BasicBlock> v = cfg.get(block);
          // cfg.remove(block);
          // block.getBody().remove(stmt);
          // cfg.put(block, v);
          stmts.remove(stmt);

          // System.out.println("BlockGraph dce 4: \n" + block.toString());

          i--;
          stmtNum--;
        }

      }

      newBlocks.add(block);
      // TODO: exit statements?
      // I don't think exit statement should ever be dead, as it should be a jump,
      // return, or label. Could it be unreachable though?

    }

    // for (BasicBlock block : newBlocks) {
    // ArrayList<BasicBlock> v = cfg.get(block);
    // cfg.remove(block);
    // cfg.put(block, v);
    // }

  }

  public String printString() {

    String dot = "digraph finite_state_machine {\n"
        + "\tfontname=\"Helvetica,Arial,sans-serif\"\n"
        + "\tnode [fontname=\"Helvetica,Arial,sans-serif\"]\n"
        + "\tedge [fontname=\"Helvetica,Arial,sans-serif\"]\n"
        + "\trankdir=LR;\n"
        + "\tnode [shape = circle];\n";

    ArrayList<String> edges = new ArrayList<>();

    // ArrayList<IRStmt, ArrayList<IRStmt>>
    for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> node : cfg.entrySet()) {
      BasicBlock block = node.getKey();
      String blockNumber = block.getName().toString();
      // set each node to display the code in the basic block
      dot = dot + "\t" + blockNumber + " [label=\"" + block.toStringInline() + "\"];\n";

      ArrayList<BasicBlock> leaves = new ArrayList<>();

      for (BasicBlock successor : node.getValue()) {

        // if (!successor.equals(nullBlock)) {
        // TODO: make this check more efficient
        if (!(successor.toString() == "")) {

          String succLabel = successor.getName().toString();
          // String edge = "\t" + blockNumber + " -> " + succLabel + " ;\n";
          // edges.add(edge);
          dot = dot + "\t" + blockNumber + " -> " + succLabel + " ;\n";
        }

      }

    }

    // for (String edge : edges) {
    // dot = dot + edge;
    // }

    dot = dot + "}";
    dot = dot.replaceAll("[.]", "");
    return dot;

  }

  public static void main(String[] args) {

  }

}
