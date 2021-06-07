package oop.ex6.main;

import oop.ex6.main.Exceptions.EndOfLineException;
import oop.ex6.main.Exceptions.IllegalCommentException;
import oop.ex6.main.Exceptions.IllegalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

import static oop.ex6.main.AdvancedChecks.decideSyntaxType;
import static oop.ex6.main.Constants.*;


/**
 * A package defined class that does the fundamentals checks such as checking if a line is  empty or illegal
 * comment placements or an illegal character in line.
 */
class FundamentalsChecks {

	/**
	 * A static function that checks for basic errors in line  such as bad comment practice or an illegal
	 * character in line or a line starting with a space.
	 * @param line The line we  want to check.
	 * @param list The line as a list.
	 * @param commandFileBuffer The rest of lines so in case that no errors are noticed we continue to
	 * 		check.
	 * @throws IllegalException An exception thrown when one of the fundamental syntax errors is noticed.
	 * @throws IOException An exception thrown in cas of error reading a file or invalid filename.
	 */
	static void checkFundamentals(String line, String[] list, BufferedReader commandFileBuffer)
			throws IllegalException, IOException {

		// if the current line is empty
		if (line.length() == EMPTY) {
			return;
		}
		boolean startWithSpace = false;
		//if the line start with space/s
		if (list.length >= TWO && list[ZERO].equals(EMPTY_STRING)) {
			startWithSpace = true;
			list = Arrays.copyOfRange(list, ONE, list.length);
		}
		//checks if the line starts with an illegal comment
		if (list[ZERO].charAt(ZERO) == SLASH &&
			(startWithSpace || list[ZERO].length() == ONE || list[ZERO].charAt(ONE) != SLASH)) {
			throw new IllegalCommentException();
		}
		//checks if it has a legal comment
		else if (list[ZERO].charAt(ZERO) == SLASH && list[ZERO].charAt(ONE) == SLASH) {
			return;
		}
		noSlashInMiddle(line);
		startsOrEndsCorrectly(line, list);
		decideSyntaxType(line, list, commandFileBuffer);
	}

	/*
	 * A function that   checks that the given line doesn't has an illegal placement of the / charachter.
	 * @param line the line we want to check
	 * @throws IllegalCommentException An exception thrown when we see a / in wrong position.
	 */
	private static void noSlashInMiddle(String line) throws IllegalCommentException {
		for (int j = ZERO; j < line.length(); j++) {
			if (line.charAt(j) == SLASH) {
				throw new IllegalCommentException();
			}
		}
	}

	/*
	 * A function that   checks that the given line starts and ends appropriately.
	 * @param line the line we want to check.
	 * @param list the line as a list.
	 * @throws EndOfLineException An exception thrown when a line ends in a wrong way.
	 */
	private static void startsOrEndsCorrectly(String line, String[] list) throws EndOfLineException {
		boolean[] triggers = checkLineValidityHelper(list, line);
		//the line doesn't end with ";", "{" or starts with "}"
		if (!triggers[ZERO]) {
			throw new EndOfLineException();
		}
		//line is "}"
		else if (triggers[ONE]) {
			throw new EndOfLineException();
		}
	}

	/**
	 * A function that counts the ending line and starting line valid chars such as - {, }, ; -.
	 * @param list The line as a list.
	 * @param line The line we want to check.
	 * @return An array representing: {trigger = found any of these  symbols, isStatementFinisher = is it an
	 * 		end of a  statement}.
	 * @throws EndOfLineException Throws an exception if the line doesn't end legally.
	 */
	protected static boolean[] checkLineValidityHelper(String[] list, String line) throws EndOfLineException {

		final char lastChar = list[list.length - ONE].charAt(list[list.length - ONE].length() - ONE);
		final long eolCount = line.chars().filter(ch -> ch == EOL).count();
		final long starterCount = line.chars().filter(ch -> ch == STATEMENT_STARTER_CHAR).count();
		final long finisherCount = line.chars().filter(ch -> ch == STATEMENT_FINISHER_CHAR).count();
		return getResult(list, lastChar, eolCount, starterCount, finisherCount);
	}

	/**
	 * A function the  checks if  the  line-ending is valid and whether it is an end of a statement.
	 */
	private static boolean[] getResult(String[] list, char lastChar, long eolCount, long starterCount,
									   long finisherCount) throws EndOfLineException {
		boolean trigger = false;
		boolean isStatementFinisher = false;
		switch (lastChar) {
		case EOL:
			trigger = true;
			if (eolCount != ONE || starterCount + finisherCount != ZERO || list[ZERO].charAt(ZERO) == (EOL)) {
				throw new EndOfLineException();
			}
			break;

		case STATEMENT_STARTER_CHAR:
			trigger = true;
			if (starterCount != ONE || finisherCount + eolCount != ZERO ||
				list[ZERO].charAt(ZERO) == (STATEMENT_STARTER_CHAR)) {
				throw new EndOfLineException();
			}
			break;

		case STATEMENT_FINISHER_CHAR:
			trigger = true;
			if (finisherCount != ONE || list[ZERO].charAt(ZERO) != (STATEMENT_FINISHER_CHAR)) {
				throw new EndOfLineException();
			}
			isStatementFinisher = true;
			break;
		}
		return new boolean[]{trigger, isStatementFinisher};
	}
}
