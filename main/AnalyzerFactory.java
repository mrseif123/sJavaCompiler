package oop.ex6.main;

import oop.ex6.main.Exceptions.*;

import java.util.Arrays;
import java.util.HashMap;

import static oop.ex6.main.AdvancedChecks.*;
import static oop.ex6.main.Constants.*;
import static oop.ex6.main.FundamentalsChecks.checkLineValidityHelper;

/**
 * A  class that deals with the local parts of code, using a  mix  of Strategy &  Factory design patterns,
 * whereas  we are always dealing with a new line but deal with each line in a different way.
 */
class AnalyzerFactory {

	/**
	 * The function that deals with creating methods/checking statements/calling methods and more.
	 * @throws IllegalException An exception thrown in case illegal syntax is found.
	 */
	static void analyzeMethods() throws IllegalException {
		int iterationNum = ZERO;
		String line;
		boolean inMethod = false;
		boolean canCloseMethod = false;
		int unclosedBrackets = ZERO;
		String methodName = null;

		while (METHODS_LINES.size() != iterationNum) {
			line = METHODS_LINES.get(iterationNum).trim();
			iterationNum += ONE;
			String[] list = line.split(SPACE);

			// if the current line is empty
			if (line.length() == EMPTY || line.equals(SPACE)) {
				continue;
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
				continue;
			}

			// if it contains a slash in the middle of the line
			for (int i = ZERO; i < line.length(); i++) {
				if (line.charAt(i) == SLASH) {
					throw new IllegalCommentException();
				}
			}

			boolean[] triggers = checkLineValidityHelper(list, line);
			//the line doesn't end with ";", "{" or starts with "}"
			if (!triggers[ZERO]) {
				throw new EndOfLineException();
			}

			//if line starts with "final"
			else if (list[ZERO].equals(FINAL) && TYPES_LIST.contains(list[ONE])) {
				doFinalVar(list, line.trim(), true, unclosedBrackets, methodName);
			}

			//if line starts with a legal variable type
			else if (TYPES_LIST.contains(list[ZERO])) {
				doVar(list, line.trim(), true, unclosedBrackets, methodName);
			}

			//assignment line
			else if (line.contains(EQUALS)) {
				doReassignment(line, true, methodName);
			}

			//method line
			else if (list[ZERO].equals(VOID) && line.contains(STATEMENT_STARTS)) {
				if (inMethod) {
					throw new FunctionException();
				} else {
					inMethod = true;
				}
				HashMap<Variable, Variable> var = new HashMap<>();
				LOCAL_PARAMS.add(var);
				int firstBracketIndex = line.indexOf(OPENED_BRACKET);
				methodName = line.trim().substring(FUNCTION_NAME_START, firstBracketIndex);
				methodName = methodName.trim();
			}

			// If a  method is called.
			else if (activateRegex(line.trim(), IS_SPACE + IS_METHOD + METHOD_CALL)) {
				callFunction(line.trim());
			}

			// If a return is noticed.
			else if (activateRegex(line.trim(), RETURN)) {
				canCloseMethod = true;
				continue;
			}

			//line is "}"
			else if (triggers[ONE]) {
				LOCAL_PARAMS.remove(unclosedBrackets);
				if (unclosedBrackets == ZERO) {
					if (!canCloseMethod) {
						throw new ClosingMethodException();
					}
					inMethod = false;
					continue;
				} else {
					unclosedBrackets -= ONE;
				}
			}

			// If line is a statement (if/while)
			else if (isLegalStatement(methodName, line.trim())) {
				unclosedBrackets += ONE;
				HashMap<Variable, Variable> var = new HashMap<>();
				LOCAL_PARAMS.add(var);
			}

			//any other illegal line
			else {
				throw new IllegalException();
			}
			canCloseMethod = false;
		}
		if (unclosedBrackets != ZERO || inMethod) {
			throw new ClosingMethodException();
		}
	}
}
