package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.Stack;

/**
 * Holds a stack of temps currently on the Simulator stack used by
 * the actual compiler.
 * Used for register allocation
 */
public abstract class TempStack {
  private static Stack<String> tempsOnStack = new Stack<>();
  private static int registerSwitch = 0;

  public static void push(String s) {
    tempsOnStack.push(s);
  }

  public static String pop() {
    return tempsOnStack.pop();
  }

  public static int indexOf(String t) {
    int index = tempsOnStack.search(t);
    if (index == -1) {
      return -1;
    }

    // size returns a sort of inverse index (ie: top of
    // stack always has index 1)
    return tempsOnStack.size() - index + 1;

  }

  // can access things on stack through memory, as this
  // stack is just a representation

  public static boolean isEmpty() {
    return tempsOnStack.isEmpty();
  }

  /** Return argument register and increment it. */
  public static String getReg() {
    String reg = switch (registerSwitch) {
      case 0 -> "rax";
      case 1 -> "rcx";
      case 2 -> "rdx";
      default -> "error in TempMap";
    };
    registerSwitch = (registerSwitch + 1) % 3;
    return reg;
  }

}
