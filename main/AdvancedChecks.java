package oop.ex6.main;

import oop.ex6.main.Exceptions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Constants.*;

/**
 * A class that represents the deeper level syntax check, such as checking statement/functions and their given
 * parameters -in case if given-, deals also with reassigning new value to variables and defining new ones.
 */
class AdvancedChecks {

	/*
	 * A function that deals with calling a function inside another.
	 * @param line The line of the function call.
	 * @throws FunctionException  An exception thrown in case the problem is in the function name.
	 * @throws FunctionParametersException  An exception thrown if the problem is in the function parameters.
	 * @throws VariableException An exception thrown if the problem is in the parameters types.
	 */
	static void callFunction(String line)
			throws FunctionException, FunctionParametersException, VariableException {
		int index = ZERO;
		if (line.charAt(index) == SPACE_CHAR) {
			index += ONE;
		}
		String funcName = line.substring(index, line.indexOf(OPENED_BRACKET)).trim();
		if (!METHODS.containsKey(funcName)) {
			throw new FunctionException();
		}
		int firstBracketIndex = line.indexOf(OPENED_BRACKET);
		int lastBracketIndex = line.indexOf(CLOSED_BRACKET);
		legalCalledMethodParams(line, funcName, firstBracketIndex, lastBracketIndex);
	}

	/*
	 * A function that checks that the called function parameters are legal.
	 * @param line The line of the called function.
	 * @param funcName The function name.
	 * @param firstBracketIndex The index of the parameters start "(".
	 * @param lastBracketIndex The index of the parameters end ")".
	 * @throws VariableException An exception thrown when a variable name error is found.
	 * @throws FunctionParametersException An exception thrown when the parameters list is illegal.
	 */
	private static void legalCalledMethodParams(String line, String funcName, int firstBracketIndex,
												int lastBracketIndex)
			throws VariableException, FunctionParametersException {
		String paramsLine;
		paramsLine = line.substring(firstBracketIndex + ONE, lastBracketIndex);

		String[] params = paramsLine.trim().split(COMMA);
		ArrayList<Variable> methodsParams = METHODS.get(funcName);
		if (methodsParams.isEmpty() && paramsLine.trim().equals(EMPTY_STRING)) {
		} else if (methodsParams.size() == params.length) {
			for (int i = ZERO; i < params.length; i++) {
				Variable var = METHODS.get(funcName).get(i);
				if (!checkLocalFormat(funcName, var.type, params[i].trim() + EOL)) {
					throw new FunctionParametersException();
				}
			}
		} else {
			throw new FunctionParametersException();
		}
	}

	/*
	 * A function that makes sure  that the if/while statement is  legal.
	 * @param methodName The method name we are in, as statement cannot be outside of a function.
	 * @param line The line we want to check.
	 * @return Boolean whether the statement is legal or not.
	 * @throws StatementException An exception the statement is illegal in matter of format.
	 * @throws VariableException An exception thrown when a variable name error is found.
	 */
	static boolean isLegalStatement(String methodName, String line)
			throws StatementException, VariableException {
		if (activateRegexPartially(line, STATEMENT_STARTER) &&
			activateRegexPartially(line, STATEMENT_FINISHER)) {
			legalStatementParams(methodName, line);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * A function that checks that the parameters in the statement are legal.
	 * @param methodName The method name we are in, as statement cannot be outside of a function.
	 * @param line The line we want to check.
	 * @throws StatementException An exception the statement is illegal in matter of format.
	 * @throws VariableException An exception thrown when a variable name error is found.
	 */
	private static void legalStatementParams(String methodName, String line)
			throws StatementException, VariableException {
		int beginIndex = line.indexOf(OPENED_BRACKET) + ONE;
		int endIndex = line.indexOf(CLOSED_BRACKET);
		String expression = line.substring(beginIndex, endIndex);
		if (activateRegexPartially(expression, ILLEGAL_PARAM_STARTER) ||
			activateRegexPartially(expression, ILLEGAL_PARAM_FINISHER)) {
			throw new StatementException();
		}
		String[] lst = expression.split(SEPARATOR);
		for (String exp : lst) {
			exp = exp.trim();
			if (!(activateRegex(exp, D_D_FALSE_TRUE) ||
				  isInitialized(methodName, exp, BOOLEAN))) {
				throw new StatementException();
			}
		}
	}

	/*
	 * A method that makes sure that the given method and  variable are initialized an can be used.
	 * @param methodName The method name we want to check.
	 * @param varName The variable name we want to check.
	 * @param type The variable type.
	 * @return True if initialized, false otherwise.
	 * @throws VariableException Throws an exception in case the given variable type isn't initialized.
	 */
	private static boolean isInitialized(String methodName, String varName, String type)
			throws VariableException {
		Variable var;
		Variable variable = new Variable(varName);
		for (int i = LOCAL_PARAMS.size() - ONE; i >= ZERO; i--) {
			if (LOCAL_PARAMS.get(i).containsKey(variable)) {
				var = LOCAL_PARAMS.get(i).get(variable);
				return var.isInitialized && (var.type.equals(type) || canBeUpCasted(type, var.type));
			}
		}
		if (NOT_INITIALIZED_VARIABLES.containsKey(varName)) {
			throw new VariableException();
		}
		return existsInMethodParamsOrGlobal(methodName, varName, type, variable);
	}

	/*
	 * A function that checks if the given variable is existent in the given method.
	 * @param methodName  The method we want to check if the given variable is in.
	 * @param varName The variable name.
	 * @param type The type of the variable.
	 * @param variable The variable we want to check.
	 * @return True if exists, false otherwise.
	 */
	private static boolean existsInMethodParamsOrGlobal(String methodName, String varName, String type,
														Variable variable) {
		Variable var;
		int varIndex = METHODS.get(methodName).indexOf(variable);
		if (varIndex != INVALID) {
			var = METHODS.get(methodName).get(varIndex);
			return var.isInitialized && (var.type.equals(type) || canBeUpCasted(type, var.type));
		} else {
			return (FINAL_VARIABLES.containsKey(varName) && (FINAL_VARIABLES.get(varName).equals(type) ||
															 canBeUpCasted(type,
																		   FINAL_VARIABLES.get(varName)))) ||
				   (NON_FINAL_VARIABLES.containsKey(varName) &&
					(NON_FINAL_VARIABLES.get(varName).equals(type) ||
					 canBeUpCasted(type, NON_FINAL_VARIABLES.get(varName))));
		}
	}

	/**
	 * A function that check what is the line type (method, statement, variable...).
	 * @param line The line we want to check.
	 * @param list The line as a list of strings.
	 * @param commandFileBuffer Our file reader -in case we want to read more lines-.
	 * @throws IllegalException An exception thrown when a (var, method, statement) error is noticed.
	 * @throws IOException An exception thrown when we have a problem in reading the file.
	 */
	protected static void decideSyntaxType(String line, String[] list, BufferedReader commandFileBuffer)
			throws IllegalException, IOException {
		//if line starts with "final"
		if (list[ZERO].equals(FINAL) && TYPES_LIST.contains(list[ONE])) {
			doFinalVar(list, line.trim(), false, ZERO, EMPTY_STRING);
		}
		//if line starts with a legal variable type
		else if (TYPES_LIST.contains(list[ZERO])) {
			doVar(list, line.trim(), false, ZERO, EMPTY_STRING);
		}
		//assignment line
		else if (line.contains(EQUALS)) {
			doReassignment(line, false, EMPTY_STRING);
		}
		//method line
		else if (list[ZERO].equals(VOID)) {
			doFunction(line.trim());
			storeLines(line.trim(), commandFileBuffer);
		}
		//outerScope statement
		else if (isStatement(line)) {
			throw new IllegalException();
		}
		//any other illegal line
		else {
			throw new IllegalException();
		}
	}

	/*
	 * A function used to store the next lines -in case we are in a method- so we can return and check them.
	 * @param line The line we want to store.
	 * @param commandFileBuffer Our file reader -to read more lines-.
	 * @throws IOException An exception thrown when we have a problem in reading the file.
	 */
	private static void storeLines(String line, BufferedReader commandFileBuffer) throws IOException {
		METHODS_LINES.add(line);
		int unclosedBrackets = ZERO;
		line = commandFileBuffer.readLine();
		while (line != null) {
			line = line.replaceAll(SPACE_REMOVER_REGEX, SPACE).trim();
			METHODS_LINES.add(line);
			if (isStatement(line)) {
				unclosedBrackets += ONE;
			} else if (endOfStatement(line)) {
				if (unclosedBrackets == ZERO) {
					return;
				} else {
					unclosedBrackets -= ONE;
				}
			}
			line = commandFileBuffer.readLine();
		}
	}

	/*
	 * Check if the line is an end of statement line.
	 * @param line The line we want to check.
	 * @return True if end of statement, false otherwise.
	 */
	private static boolean endOfStatement(String line) {
		return activateRegex(line, STATEMENT_ENDS);
	}

	/*
	 * Check if the line is a statement line.
	 * @param line The line we want to check.
	 * @return True if statement, false otherwise.
	 */
	private static boolean isStatement(String line) {
		return activateRegex(line, STATEMENT_REGEX);
	}

	/*
	 * A function that deals with creating a function lines.
	 * @param line The line that defines the function we want to deal with.
	 * @throws FunctionException An exception thrown in case the function name or definition is illegal.
	 * @throws VariableException An exception thrown in case A variable is illegal.
	 * @throws FunctionParametersException An exception thrown in case function parameters are illegal.
	 * @throws EndOfLineException An exception thrown in case that a line ends incorrectly.
	 */
	private static void doFunction(String line)
			throws FunctionException, VariableException, FunctionParametersException, EndOfLineException {
		int leftBracketNum = line.length() - line.replaceAll(OPENED_BRACKET_REGEX, EMPTY_STRING).length();
		int rightBracketNum = line.length() - line.replaceAll(CLOSED_BRACKET_REGEX, EMPTY_STRING).length();
		if (leftBracketNum != ONE || rightBracketNum != ONE) {
			throw new FunctionException();
		}
		int firstBracketIndex = line.indexOf(OPENED_BRACKET);
		int lastBracketIndex = line.indexOf(CLOSED_BRACKET);
		String params;
		params = line.substring(firstBracketIndex + ONE, lastBracketIndex);
		String functionName = line.substring(FUNCTION_NAME_START, firstBracketIndex);
		functionName = functionName.trim();
		checkFunctionName(functionName);
		ArrayList<Variable> parametersList = checkParametersList(params);

		if (!activateRegex(line.substring(lastBracketIndex), FUNCTION_ENDING)) {
			throw new EndOfLineException();
		}
		METHODS.put(functionName, parametersList);
	}

	/*
	 * A function that collects the given parameters list and check they are valid.
	 * @param line
	 * @return An arrayList containing the parameters.
	 * @throws FunctionException An exception thrown in case the function name or definition is illegal.
	 * @throws VariableException An exception thrown in case A variable is illegal.
	 * @throws FunctionParametersException An exception thrown in case function parameters are illegal.
	 */
	private static ArrayList<Variable> checkParametersList(String line)
			throws FunctionException, VariableException, FunctionParametersException {
		String[] newline = line.split(COMMA);
		for (int i = 0; i < newline.length; i++) {
			newline[i] = newline[i].trim();
		}
		if (activateRegex(line.trim(), IS_SPACE)) {
			return new ArrayList<>();
		}
		return checkParametersListHelper(newline);
	}

	/*
	 * A function that works as a helper for the function -checkParametersList-.
	 * @param newline The line of the parameters as a list.
	 * @return An arrayList containing the parameters.
	 * @throws FunctionException An exception thrown in case the function name or definition is illegal.
	 * @throws VariableException An exception thrown in case A variable is illegal.
	 * @throws FunctionParametersException An exception thrown in case function parameters are illegal.
	 */
	private static ArrayList<Variable> checkParametersListHelper(String[] newline)
			throws FunctionException, VariableException, FunctionParametersException {
		ArrayList<Variable> parametersList = new ArrayList<>();
		HashSet<String> methodParametersNames = new HashSet<>();
		for (String s : newline) {
			String[] tmpLine = s.split(SPACE);
			if (tmpLine.length == THREE) {
				if (!tmpLine[ZERO].equals(FINAL)) {
					throw new FunctionException();
				}
				checkParameter(methodParametersNames, tmpLine, ONE, TWO);
				Variable var = new Variable(tmpLine[TWO], tmpLine[ONE], true, true);
				parametersList.add(var);

			} else if (tmpLine.length == TWO) {
				checkParameter(methodParametersNames, tmpLine, ZERO, ONE);
				Variable var = new Variable(tmpLine[ONE], tmpLine[ZERO], true, false);
				parametersList.add(var);
			} else {
				throw new FunctionParametersException();
			}
		}
		return parametersList;
	}

	/*
	 * A function that checks if the parameters in the given hashSet are valid.
	 * @param methodParametersNames The set of the methods processed until now.
	 * @param tmpLine A line containing the parameters as a list.
	 * @param type  The type index in the TYPES_LIST.
	 * @param varName The varName index int the methodParametersNames.
	 * @throws FunctionException An exception thrown in case the function name or definition is illegal.
	 * @throws VariableException An exception thrown in case A variable is illegal.
	 * @throws FunctionParametersException An exception thrown in case function parameters are illegal.
	 */
	private static void checkParameter(HashSet<String> methodParametersNames, String[] tmpLine, int type,
									   int varName)
			throws FunctionException, VariableException, FunctionParametersException {
		if (!TYPES_LIST.contains(tmpLine[type])) {
			throw new FunctionException();
		}
		if (!checkVariableName(tmpLine[varName])) {
			throw new VariableException();
		}

		if (methodParametersNames.contains(tmpLine[varName])) {
			throw new FunctionParametersException();
		}
		methodParametersNames.add(tmpLine[varName]);
	}

	/*
	 * A function that does the variable reassignment.
	 * @param line The line we want to deal with.
	 * @param isLocal A boolean representing if the variable we want to reassign is local or global.
	 * @param methodName The methode we are currently in.
	 * @throws VariableException An exception thrown in case A variable is illegal.
	 * @throws ValueException An exception thrown in case the value is not appropriate to the variable type.
	 */
	static void doReassignment(String line, boolean isLocal, String methodName)
			throws VariableException, ValueException {

		String[] expression = line.split(EQUALS);
		if (expression.length != TWO) {
			throw new ValueException();
		}
		String varName = expression[ZERO].trim();

		if (isLocal) {
			Variable var;
			Variable variable = new Variable(varName);
			for (int i = LOCAL_PARAMS.size() - ONE; i >= ZERO; i--) {
				if (LOCAL_PARAMS.get(i).containsKey(variable)) {
					var = LOCAL_PARAMS.get(i).get(variable);
					reassigned(methodName, expression[ONE].trim(), var);
					return;
				}
			}
			int varIndex = METHODS.get(methodName).indexOf(variable);
			if (varIndex != INVALID) {
				var = METHODS.get(methodName).get(varIndex);
				reassigned(methodName, expression[ONE].trim(), var);
				return;
			}
			if (FINAL_VARIABLES.containsKey(varName)) {
				throw new VariableException();
			}
		}
		reassignVariable(isLocal, expression[ONE], varName);
	}

	/*
	 * A function that re-assign the given variable value in the static sets -acts as a helper for the
	 * previous function.
	 * @param isLocal A boolean representing if the variable we want to reassign is local or global.
	 * @param s The string we want to split the
	 * @param varName The variable name we want to add.
	 * @throws VariableException An exception thrown in case A variable is illegal.
	 * @throws ValueException An exception thrown in case the value is not appropriate to the variable type.
	 */
	private static void reassignVariable(boolean isLocal, String s, String varName)
			throws VariableException, ValueException {
		HashMap<String, String> map = null;
		boolean isValidVariableName = false;
		String type;
		boolean isValidValue;
		if (NOT_INITIALIZED_VARIABLES.containsKey(varName)) {
			isValidVariableName = true;
			map = NOT_INITIALIZED_VARIABLES;
		} else if (NON_FINAL_VARIABLES.containsKey(varName)) {
			isValidVariableName = true;
			map = NON_FINAL_VARIABLES;
		}

		if (!isValidVariableName) {
			throw new VariableException();
		}
		type = map.get(varName);
		isValidValue = checkFormat(type, s.trim());
		if (!isValidValue) {
			throw new ValueException();
		}
		if (map == NOT_INITIALIZED_VARIABLES && !isLocal) {
			map.remove(varName);
			NON_FINAL_VARIABLES.put(varName, type);
		}
	}

	/*
	 * A function that checks the syntax of a adding variable line and adds the variable to the variables
	 * sets.
	 * @param list The line as a list.
	 * @param line The line as a string.
	 * @param isLocal A boolean telling if the variable is local false if global.
	 * @param unclosedBrackets The index of the starting bracket.
	 * @param methodName The method we are in.
	 * @throws IllegalException An exception thrown when a (var, method, statement) error is noticed.
	 */
	static void doVar(String[] list, String line, boolean isLocal,
					  int unclosedBrackets, String methodName) throws IllegalException {
		int index = 0;
		while (line.charAt(index) != SPACE_CHAR) {
			index++;
		}
		String newLine = line.substring(index + 1);
		String[] splitLine = newLine.split(COMMA);
		Set<String> vars = new HashSet<>();
		if (!list[list.length - 1].endsWith(String.valueOf(EOL))) {
			throw new EndOfLineException();
		}
		for (int i = 0; i < splitLine.length - 1; i++) {
			checkVariableAndValueFormat(list[ZERO], splitLine, i, vars, isLocal, unclosedBrackets,
										methodName);
		}
		splitLine[splitLine.length - 1] = splitLine[splitLine.length - 1]
				.substring(ZERO, splitLine[splitLine.length - 1].length() - 1);
		checkVariableAndValueFormat(list[ZERO], splitLine, splitLine.length - 1, vars, isLocal,
									unclosedBrackets, methodName);
	}

	/*
	 * A function that checks the given variables values and their types.
	 */
	private static void checkVariableAndValueFormat(String type, String[] splitLine, int i, Set<String> vars,
													boolean isLocal, int unclosedBrackets, String methodName)
			throws ValueException, VariableException {

		if (splitLine[i].contains(EQUALS)) {
			initializeVariable(type, splitLine[i], vars,
							   NON_FINAL_VARIABLES, isLocal, unclosedBrackets, methodName);

		} else {
			String varName = splitLine[i].trim();
			varNameExists(isLocal, unclosedBrackets, methodName, varName);
			if (!vars.contains(varName)) {
				varShowedInTheSameLine(type, vars, NOT_INITIALIZED_VARIABLES, isLocal,
									   unclosedBrackets, varName);
			} else {
				throw new VariableException();
			}
		}
	}

	/*
	 * A function that adds the variables to the static sets.
	 */
	private static void initializeVariable(String currentType, String str, Set<String> vars,
										   HashMap<String, String> set, boolean isLocal,
										   int unclosedBrackets, String methodName)
			throws ValueException, VariableException {
		String[] expression = str.split(EQUALS);
		if (expression.length != TWO) {
			throw new ValueException();
		}
		String varName = expression[ZERO].trim();
		varNameExists(isLocal, unclosedBrackets, methodName, varName);
		boolean isValidValue;
		if (isLocal) {
			isValidValue = checkLocalFormat(methodName, currentType, expression[ONE].trim() + EOL);
		} else {
			isValidValue = checkFormat(currentType, expression[ONE].trim() + EOL);
		}
		if (!isValidValue) {
			throw new ValueException();
		}
		if (!vars.contains(varName)) {
			varShowedInTheSameLine(currentType, vars, set, isLocal, unclosedBrackets, varName);
		} else {
			throw new VariableException();
		}
	}

	/*
	 * A function that marks the given variable as initialized.
	 */
	private static void reassigned(String methodName, String valName, Variable var)
			throws VariableException, ValueException {
		String type;
		boolean isValidValue;
		if (var.isFinal) {
			throw new VariableException();
		}
		type = var.type;
		isValidValue = checkLocalFormat(methodName, type, valName);
		if (!isValidValue) {
			throw new ValueException();
		}
		var.isInitialized = true;
	}

	/*
	 * A function that checks if more than one variable is in the same line, adds them to the sets.
	 */
	private static void varShowedInTheSameLine(String currentType, Set<String> vars,
											   HashMap<String, String> set, boolean isLocal,
											   int unclosedBrackets, String varName) {
		if (isLocal) {
			Variable variable = null;
			if (set == FINAL_VARIABLES) {
				variable = new Variable(varName, currentType, true, true);
			} else if (set == NON_FINAL_VARIABLES) {
				variable = new Variable(varName, currentType, true, false);
			} else if (set == NOT_INITIALIZED_VARIABLES) {
				variable = new Variable(varName, currentType, false, false);
			}
			LOCAL_PARAMS.get(unclosedBrackets).put(variable, variable);
		} else {
			set.put(varName, currentType);
		}
		vars.add(varName);
	}

	/*
	 * A function that checks if the given variable  has a valid naming and isn't  defined before
	 * according to
	 * the Sjava rules, throws an error if already defined.
	 */
	private static void varNameExists(boolean isLocal, int unclosedBrackets, String methodName,
									  String varName) throws VariableException {
		boolean isValidVariableName = checkVariableName(varName);
		boolean exists;
		if (isLocal) {
			Variable variable = new Variable(varName);
			exists = LOCAL_PARAMS.get(unclosedBrackets).containsKey(variable);
			if (unclosedBrackets == ZERO) {
				exists = exists || METHODS.get(methodName).contains(variable);
			}
		} else {
			exists = globalVariableExists(varName);
		}
		isValidVariableName = isValidVariableName && !exists;

		if (!isValidVariableName) {
			throw new VariableException();
		}
	}

	/*
	 * A function that deals with the Final variables, throws the appropriate errors in case  needed.
	 */
	static void doFinalVar(String[] list, String line, boolean isLocal, int unclosedBrackets,
						   String methodName) throws VariableException, ValueException, EndOfLineException {
		if (list.length >= THREE) {
			int index = line.indexOf(FINAL_WORD_ENDING) + TWO;
			while (line.charAt(index) != SPACE_CHAR) {
				index++;
			}

			String newLine = line.substring(index + 1);
			String[] splitLine = newLine.split(COMMA);
			Set<String> vars = new HashSet<>();
			if (!list[list.length - 1].endsWith(String.valueOf(EOL))) {
				throw new EndOfLineException();
			}
			for (int i = ZERO; i < splitLine.length - ONE; i++) {
				checkInitialization(list[ONE], splitLine, vars, i, isLocal, unclosedBrackets, methodName);

			}
			splitLine[splitLine.length - ONE] = splitLine[splitLine.length - ONE]
					.substring(ZERO, splitLine[splitLine.length - ONE].length() - ONE);
			checkInitialization(list[ONE], splitLine, vars, splitLine.length - ONE, isLocal,
								unclosedBrackets, methodName);
		} else {
			throw new VariableException();
		}
	}

	/*
	 * A function that checks if the given variable is initialized.
	 */
	private static void checkInitialization(String type, String[] splitLine, Set<String> vars, int i,
											boolean isLocal, int unclosedBrackets, String methodName)
			throws ValueException, VariableException {
		if (splitLine[i].contains(EQUALS)) {
			initializeVariable(type, splitLine[i], vars, FINAL_VARIABLES, isLocal, unclosedBrackets,
							   methodName);
		} else {
			throw new VariableException();
		}
	}

	/*
	 * A function that checks if the given function is valid and whether its already defined.
	 */
	private static void checkFunctionName(String name) throws FunctionException {
		if (!activateRegex(name, IS_METHOD) || METHODS.containsKey(name)) {
			throw new FunctionException();
		}
	}

	/*
	 * A function that checks if the given variable name is valid.
	 */
	private static boolean checkVariableName(String varName) {
		boolean isValid = false;
		if (activateRegex(varName, NORMAL_STRING)) {
			if (varName.charAt(ZERO) == UNDER_SCORE) {
				if (varName.length() > ONE) {
					isValid = true;
				}
			} else if (!(varName.charAt(ZERO) >= ZERO_CHAR && varName.charAt(ZERO) <= NINE_CHAR)) {
				isValid = true;
			}
		}
		return isValid;
	}

	/*
	 * A function that checks if the given variable is defined as global.
	 */
	private static boolean globalVariableExists(String varName) {
		return FINAL_VARIABLES.containsKey(varName) || NON_FINAL_VARIABLES.containsKey(varName) ||
			   NOT_INITIALIZED_VARIABLES.containsKey(varName);
	}

	/*
	 * A function that checks if the format of the variables is valid.
	 */
	private static boolean checkFormat(String currentType, String valName) {
		String subSubString = valName.substring(ZERO, valName.length() - ONE);
		if (NON_FINAL_VARIABLES.containsKey(subSubString)) {
			return NON_FINAL_VARIABLES.get(subSubString).equals(currentType) ||
				   canBeUpCasted(currentType, NON_FINAL_VARIABLES.get(subSubString));
		} else if (FINAL_VARIABLES.containsKey(subSubString)) {
			return FINAL_VARIABLES.get(subSubString).equals(currentType) ||
				   canBeUpCasted(currentType, FINAL_VARIABLES.get(subSubString));
		}
		return checkType(currentType, valName);
	}

	/*
	 * A function that checks if the the variable is from the given currentType.
	 */
	private static boolean checkType(String currentType, String valName) {
		switch (currentType) {
		case INT:
			return activateRegex(valName, IS_INT);
		case DOUBLE:
			return activateRegex(valName, IS_DOUBLE);

		case BOOLEAN:
			return activateRegex(valName, IS_BOOLEAN);

		case CHAR:
			return activateRegex(valName, IS_CHAR);

		case STRING:
			return activateRegex(valName, IS_STRING);
		default:
			return false;
		}
	}

	/*
	 * A function that checks the format of the local variables.
	 */
	private static boolean checkLocalFormat(String methodName, String currentType, String valName)
			throws VariableException {
		String subSubString = valName.substring(ZERO, valName.length() - ONE);
		if (isInitialized(methodName, subSubString, currentType)) {
			return true;
		}
		return checkType(currentType, valName);
	}

	/*
	 * A function that verifies that the given regex expression is available in the given string.
	 */
	static boolean activateRegex(String substring, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(substring);
		return matcher.matches();
	}

	/**
	 * A function that verifies that the given regex expression is available in the given string  -used if we
	 * want to find only a partial regex-.
	 */
	private static boolean activateRegexPartially(String substring, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(substring);
		return matcher.find();
	}

	/**
	 * A  function used to allow  putting  and integer in a double variable.
	 */
	private static boolean canBeUpCasted(String typeToAssign, String referenceType) {
		return (typeToAssign.equals(DOUBLE) && referenceType.equals(INT)) ||
			   (typeToAssign.equals(BOOLEAN) && (referenceType.equals(INT) || referenceType.equals(DOUBLE)));
	}

	/*
	 * A function that prints the  result of the  analyzing process and resets the sets data.
	 */
	static void resetSetsAndPrintResult(String message) {
		System.out.println(message);
		resetData();
	}

	/*
	 * A function that resets the variables data structures after each call to our main function.
	 */
	private static void resetData() {
		NON_FINAL_VARIABLES = new HashMap<>();
		FINAL_VARIABLES = new HashMap<>();
		NOT_INITIALIZED_VARIABLES = new HashMap<>();
		METHODS = new HashMap<>();
		METHODS_LINES = new ArrayList<>();
		LOCAL_PARAMS = new ArrayList<>();
	}

}


