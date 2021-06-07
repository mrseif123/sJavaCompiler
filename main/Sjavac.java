package oop.ex6.main;

import oop.ex6.main.Exceptions.IllegalException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static oop.ex6.main.AdvancedChecks.resetSetsAndPrintResult;
import static oop.ex6.main.AnalyzerFactory.analyzeMethods;
import static oop.ex6.main.Constants.*;
import static oop.ex6.main.FundamentalsChecks.checkFundamentals;


/**
 * The main class that includes the main function which checks  the Sjava syntax.
 */
public class Sjavac {

	public static void main(String[] args) {

		//	 Try to open the command file.
		try (Reader commandFile = new FileReader(args[0]);
			 BufferedReader commandFileBuffer = new BufferedReader(commandFile)) {
			String line = commandFileBuffer.readLine();

			// While more lines available.
			while (line != null) {
				line = line.replaceAll(SPACE_REMOVER_REGEX, SPACE).trim();
				String[] lineAsList = line.split(SPACE);

				// Analyze global lines - not methods-.
				try {
					checkFundamentals(line, lineAsList, commandFileBuffer);
				} catch (IllegalException e) {
					resetSetsAndPrintResult(e.getMessage());
					return;
				}
				line = commandFileBuffer.readLine();
			}
			// Analyze methods and their lines.
			try {
				analyzeMethods();
			} catch (IllegalException e) {
				resetSetsAndPrintResult(e.getMessage());
				return;
			}
		} catch (IOException e) {
			System.err.println(IO_ERROR);
			resetSetsAndPrintResult(String.valueOf(ERROR_OUTPUT));
		}
		resetSetsAndPrintResult(String.valueOf(LEGAL_OUTPUT));
	}
}