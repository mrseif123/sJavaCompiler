package oop.ex6.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A class that represents the shared constants between the oop.ex6.main package.
 */
class Constants {
	static final String IO_ERROR = "ERROR: Illegal file name";
	static final char SLASH = '/';
	static final char EOL = ';';
	static final char STATEMENT_STARTER_CHAR = '{';
	static final char STATEMENT_FINISHER_CHAR = '}';
	static final int EMPTY = 0;
	static final int ONE = 1;
	static final int ZERO = 0;
	static final String FINAL = "final";
	static final String IS_METHOD = "[a-zA-Z]\\w*";
	static final char FINAL_WORD_ENDING = 'l';
	static final String IS_SPACE = " *";
	static final String FUNCTION_ENDING = "\\) *\\{";
	static final String OPENED_BRACKET_REGEX = "\\(";
	static final String CLOSED_BRACKET_REGEX = "\\)";
	static final String STATEMENT_STARTER = "^ *(if|while) *\\(";
	static final String STATEMENT_FINISHER = "\\) *\\{ *$";
	static final String ILLEGAL_PARAM_STARTER = "^ *(\\|\\||\\&\\&)";
	static final String ILLEGAL_PARAM_FINISHER = " *(\\|\\||\\&\\&) *$";
	static final String SEPARATOR = "\\|\\||&&";
	static final String D_D_FALSE_TRUE = "(-?\\d+(\\.\\d*)?|false|true)";
	static final String RETURN = " *return *; *";
	static final String METHOD_CALL = " *\\(.*\\) *; *";
	static final int ERROR_OUTPUT = 2;
	static final int LEGAL_OUTPUT = 0;
	static final int INVALID = -1;
	static final String SPACE_REMOVER_REGEX = "\\s+";
	static final String SPACE = " ";
	static final int THREE = 3;
	static final String EQUALS = "=";
	static final char UNDER_SCORE = '_';
	static final char ZERO_CHAR = '0';
	static final char NINE_CHAR = '9';
	static final String INT = "int";
	static final String DOUBLE = "double";
	static final String BOOLEAN = "boolean";
	static final String CHAR = "char";
	static final String STRING = "String";
	static final String COMMA = ",";
	static final String EMPTY_STRING = "";
	static final char SPACE_CHAR = ' ';
	static final int TWO = 2;
	static final String VOID = "void";
	static final int FUNCTION_NAME_START = 4;
	static final String STATEMENT_REGEX = " *(if|while) *\\(.*\\) *\\{";
	static final String STATEMENT_STARTS = "{";
	static final String STATEMENT_ENDS = "}";
	static final String OPENED_BRACKET = "(";
	static final String CLOSED_BRACKET = ")";
	static final String IS_INT = "-?\\d+;";
	static final String IS_DOUBLE = "-?\\d+(\\.\\d*)?;";
	static final String IS_BOOLEAN = "(-?\\d+(\\.\\d*)?|false|true);";
	static final String IS_CHAR = "'.{1}';";
	static final String IS_STRING = "\".*\";";
	static final String NORMAL_STRING = "\\w+";
	static final List<String> TYPES_LIST = Arrays
			.asList("int", "double", "boolean", "char", "String");
	static HashMap<String, String> NON_FINAL_VARIABLES = new HashMap<>();
	static HashMap<String, String> FINAL_VARIABLES = new HashMap<>();
	static HashMap<String, String> NOT_INITIALIZED_VARIABLES = new HashMap<>();
	static HashMap<String, ArrayList<Variable>> METHODS = new HashMap<>();
	static ArrayList<String> METHODS_LINES = new ArrayList<>();
	static ArrayList<HashMap<Variable, Variable>> LOCAL_PARAMS = new ArrayList<>();

}
