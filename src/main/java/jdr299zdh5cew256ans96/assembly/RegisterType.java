package main.java.jdr299zdh5cew256ans96.assembly;

// TODO: see if needed
public enum RegisterType {

  /**
   * Enum for all registers in x86-64
   */
  // callee-owned
  RAX("rax"), // return value
  RDI("rdi"),
  RSI("rsi"),
  RDX("rdx"),
  RCX("rcx"),
  R8("r8"),
  R9("r9"),
  R10("r10"),
  R11("r11"),
  // caller-owned
  RSP("rsp"), // stack pointer
  RBX("rbx"),
  RBP("rbp"), // frame pointer
  R12("r12"),
  R13("r13"),
  R14("r14"),
  R15("r15"),
  RIP("rip"); // instruction pointer

  /**
   * String representation of register
   */
  private final String reg;

  /**
   * Is the value in this register currently needed?
   */
  private boolean live;

  /**
   * Constructor for register
   * TODO: could also add other attributes, like disallowing assignment for
   * certain registers or
   * something idk
   * 
   * @param reg - operator string
   */
  RegisterType(String reg) {
    this.reg = reg;
    live = false;
  }

  /**
   *
   * @return name of register
   */
  public String toString() {
    return reg;
  }

  /*
   * For now, there's no actual values stored in these classes because I don't
   * care what they are. That might change in the future.
   */

  /** Call this when you push this register onto the stack */
  public void push() {
    live = false;
  }

  public void pull() throws Exception {
    if (live) {
      throw new Exception("overwriting live value in" + toString());
    }
    live = true;
  }

  /**
   * Right now, value in this register is "live" if is contents have not been
   * pushed to the stack.
   * 
   * @return whether the value in this register is currently live.
   */
  public boolean live() {
    return live;
  }

}
