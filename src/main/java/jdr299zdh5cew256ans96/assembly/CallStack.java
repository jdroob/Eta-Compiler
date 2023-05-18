package main.java.jdr299zdh5cew256ans96.assembly;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class CallStack {

	private static Deque<String> pushArgStmts = new ArrayDeque<>();

	private static boolean moreThanTwoReturns;

	private static boolean oddStackSpilled;

	public static void push(String s) {
		pushArgStmts.push(s);
	}

	public static String pop() {
		return pushArgStmts.pop();
	}

	public static boolean isEmpty() {
		return pushArgStmts.isEmpty();
	}

	public static void setMultipleReturns(boolean moreThanTwoReturns) {
		CallStack.moreThanTwoReturns = moreThanTwoReturns;
	}

	public static boolean isOddStackSpilled() {
		return oddStackSpilled;
	}

	public static void setOddStackSpilled(boolean oddStackSpilled) {
		CallStack.oddStackSpilled = oddStackSpilled;
	}

	public static boolean isMoreThanTwoReturns() {
		return moreThanTwoReturns;
	}

	public static int size() {
		return pushArgStmts.size();
	}
}
