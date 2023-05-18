package main.java.jdr299zdh5cew256ans96.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java_cup.lalr_item;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;

/**
 * this class is used to represent a Control Flow Graph (CFG)
 * for our IR representation of a program. It implements a singly-directed Graph
 * as an Adjacency List
 */
public class Graph {
  private static HashMap<IRStmt, ArrayList<IRStmt>> cfg = new HashMap<>();

  // I wish java had native tuples...
  // ArrayList<ArrayList(IRStmt, ArrayList<IRStmt>)>

  // Add an edge for some source, destination node. Singly directed.
  // static void addEdge(HashMap<IRStmt, ArrayList<IRStmt>> cfg, IRStmt s, IRStmt
  // d) {
  static void addEdge(IRStmt s, IRStmt d) {
    System.out.println("\n\nBEFORE:\n" + Graph.printEdges());
    // for some reason this containsKey isn't detecting duplicates. I think it's
    // because some of the "duplicate keys" are actually different objects with
    // the same string representation. But how???? Where are they coming from?
    // it was because we were calling lower() twice by mistake

    if (d == null) {
      // System.out.println("d is null");
      // System.out.println("s is " + s.toString());
      return;
    }

    if (cfg.containsKey(s)) {
      // System.out.println("Graph 1");
      ArrayList<IRStmt> children = cfg.get(s);

      // update outgoing edges
      if (!children.contains(d)) {
        // System.out.println("Graph 2");
        children.add(d);
        // cfg.remove(s);
        cfg.put(s, children);
      }
    } else {
      // System.out.println("Graph 3");
      if (!d.equals(s)) {
        ArrayList<IRStmt> dList = new ArrayList<>();
        dList.add(d);
        cfg.put(s, dList);
      }

    }

    System.out.println("\n\nAFTER:\n" + Graph.printEdges());

  }

  /** Remove outgoing edges from a given node in the Graph */
  static void removeEdge(IRStmt n) {
    cfg.remove(n);
  }

  public static String printEdges() {
    return cfg.toString();
  }

  /**
   * Flatten an IR Graph back into a list of statements. The control flow should
   * not change
   */
  public static List<IRStmt> flatten() {
    return null;

  }

  public static String printString() {

    String dot = "digraph finite_state_machine {\n"
        + "\tfontname=\"Helvetica,Arial,sans-serif\"\n"
        + "\tnode [fontname=\"Helvetica,Arial,sans-serif\"]\n"
        + "\tedge [fontname=\"Helvetica,Arial,sans-serif\"]\n"
        + "\trankdir=LR;\n"
        + "\tnode [shape = circle];\n";

    ArrayList<String> edges = new ArrayList<>();

    // ArrayList<IRStmt, ArrayList<IRStmt>>
    for (Map.Entry<IRStmt, ArrayList<IRStmt>> node : cfg.entrySet()) {
      String nodeLabel = node.getKey().toString();
      for (IRStmt successor : node.getValue()) {
        String succLabel = successor.toString();
        String edge = "\t" + nodeLabel + " -> " + succLabel + " ;\n";
        // removes duplicates (because I don't feel like figuring out why they exist)
        edges.add(edge);
        // if (!edges.contains(edge)) {
        // edges.add(edge);
        // }

      }

    }

    for (String edge : edges) {
      dot = dot + edge;
    }

    dot = dot + "}";
    dot = dot.replaceAll("[.]", "");
    return dot;

  }

  public static void main(String[] args) {

  }

}
