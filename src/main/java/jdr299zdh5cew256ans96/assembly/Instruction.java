package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/** Interface for all x86-64 instruction classes */
public abstract class Instruction {

  public static int regCounter = 10;

  private ArrayList<Instruction> predecessors = new ArrayList<>();
  private ArrayList<Instruction> successors = new ArrayList<>();
  private ArrayList<Register> use = new ArrayList<>();
  private ArrayList<Register> def = new ArrayList<>();
  private Set<String> in = new LinkedHashSet<>();
  private Set<String> out = new LinkedHashSet<>();

  /**
   * @return string representation of this Instruction
   */
  @Override
  public String toString() {return ""; }

  public void addPredecessor(Instruction pre) {
    predecessors.add(pre);
  }

  public void addSuccessor(Instruction succ) {
    successors.add(succ);
  }

  public void clearPredecessors() {
    predecessors.clear();
  }

  public void clearSuccessors() {
    successors.clear();
  }

  public ArrayList<Register> getUse() {
    return use;
  }

  public ArrayList<Register> getDef() { return def; }

  public void setUse(ArrayList<Register> use) {
    this.use = use;
  }

  public void setDef(ArrayList<Register> def) {
    this.def = def;
  }

  public Set<String> getIn() {
    return in;
  }

  public Set<String> getOut() {
    return out;
  }

  public void setOut(Set<String> out) {
    this.out = out;
  }

  public void setIn(Set<String> in) {
    this.in = in;
  }

  public boolean calculateIn() {
    ArrayList<Register> use = getUse();
    Set<String> useSet = new LinkedHashSet<>();
    use.forEach( (u) -> useSet.add(u.getReg()));

    ArrayList<Register> def = getDef();
    Set<String> defSet = new LinkedHashSet<>();
    def.forEach( (d) -> defSet.add(d.getReg()));

    Set<String> out = getOut();
    for (String s : defSet) {
      out.remove(s);
    }

    useSet.addAll(out);

    if (in.containsAll(useSet)) {
      return false;
    }

//    System.out.println("IN");
//    for (String s : in) {
//      System.out.println(s);
//    }
//
//    System.out.println("SET IN");
//    for (String s : useSet) {
//      System.out.println(s);
//    }

    setIn(useSet);
    return true;

  }

  public boolean calculateOut() {
      Set<String> union = new LinkedHashSet<>();
      for (Instruction insn : successors) {
        union.addAll(insn.getIn());
      }
//
//      if (out.containsAll(union)) {
//        return false;
//      }

//    System.out.println("OUT");
//    for (String s : out) {
//      System.out.println(s);
//    }
//
//    System.out.println("SET OUT");
//    for (String s : union) {
//      System.out.println(s);
//    }

      setOut(union);
      return true;
  }

  public void calculateUse() { }

  public void calculateDef() { }

  public ArrayList<Instruction> getPredecessors() {
    return predecessors;
  }

  public ArrayList<Instruction> getSuccessors() {
    return successors;
  }

  public ArrayList<Register> getAbstractTemps() {
    return new ArrayList<>();
  }

  public ArrayList<Register> getPrecoloredTemps() {
    return new ArrayList<>();
  }

  public void allocateRegisters() { }

}
