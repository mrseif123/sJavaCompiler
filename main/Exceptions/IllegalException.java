package oop.ex6.main.Exceptions;

/**
 * The main class  exception class, extends Exception represents the cases we need to print 1 in.
 */
public class IllegalException extends Exception {

	public static final int ILLEGAL_OUTPUT = 1;

	public IllegalException() {
		super(String.valueOf(ILLEGAL_OUTPUT));
	}

}
