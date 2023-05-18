package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayList;

public class Mov extends Instruction {
  private Operand target;
  private Operand src;

  public Mov(Operand target, Operand src) {
    this.target = target;
    this.src = src;
  }

  public Mov(String target, String src) {
    this.target = new Register(target);
    this.src = new Register(src);
  }

  public Mov(String target, int src) {
    this.target = new Register(target);
    this.src = new Const(src);
  }

  @Override
  public String toString() {
    return "mov " + target + ", " + src;
  }

  @Override
  public void calculateUse() {
    ArrayList<Register> use = src.getRegs();
    if (target.isMem()) {
      use.addAll(target.getRegs());
    }
    setUse(use);
  }

  @Override
  public void calculateDef() {
    if (!target.isMem()) {
      setDef(target.getRegs());
    }
  }

  public ArrayList<Register> getRegs() {
    ArrayList<Register> regs = new ArrayList<>(src.getRegs());
    regs.addAll(target.getRegs());
    return regs;
  }

  @Override
  public ArrayList<Register> getAbstractTemps() {
    ArrayList<Register> abstractTemps = new ArrayList<>();
    abstractTemps.addAll(target.getAbstractTemps());
    abstractTemps.addAll(src.getAbstractTemps());
    return abstractTemps;
  }

 @Override
  public ArrayList<Register> getPrecoloredTemps() {
    ArrayList<Register> precoloredTemps = new ArrayList<>();
   precoloredTemps.addAll(target.getPrecoloredTemps());
   precoloredTemps.addAll(src.getPrecoloredTemps());
    return precoloredTemps;
  }

  @Override
  public void allocateRegisters() {
    target.allocateRegisters();
    src.allocateRegisters();
    Instruction.regCounter = 10;
  }

  public Operand getTarget() {
    return target;
  }

  public Operand getSrc() {
    return src;
  }

}
