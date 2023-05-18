package main.java.jdr299zdh5cew256ans96;

import java_cup.runtime.*;
import main.java.jdr299zdh5cew256ans96.ast.Context;
import main.java.jdr299zdh5cew256ans96.ast.Definition;
import main.java.jdr299zdh5cew256ans96.ast.Program;
import main.java.jdr299zdh5cew256ans96.ast.SemanticError;
import main.java.jdr299zdh5cew256ans96.ast.Use;
import main.java.jdr299zdh5cew256ans96.cup.parser;
import main.java.jdr299zdh5cew256ans96.ir.IRCompUnit;
import main.java.jdr299zdh5cew256ans96.ir.IRName;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory_c;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.interpret.IRSimulator;
import main.java.jdr299zdh5cew256ans96.ir.Graph;
import main.java.jdr299zdh5cew256ans96.ir.BlockGraph;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Class that provides helper functions for the main cli class
 */
public class HelperCli {

	public static final String[] STANDARD_LIB_FILES = { "io.eti" };
//	public static final String STANDARD_LIB_DIR = "/home/student/shared/src" +
//			"/main/java/jdr299zdh5cew256ans96/standardlibs/";
	public static final String STANDARD_LIB_DIR = "/home/rubio/eta-compiler/src/main/java/jdr299zdh5cew256ans96/standardlibs";

	private boolean lexFlag;
	private boolean parseFlag;
	private boolean typeCheckFlag;
	private boolean irGenFlag;
	private boolean irRunFlag;
	private boolean optirInitialFlag;
	private boolean optirFinalFlag;
	private boolean optcfgInitialFlag;
	private boolean optcfgFinalFlag;
	private String sourceDir;
	private String libDir;
	private String destDir;
	private String assemblyLibPath;
	private String target;
	private boolean abstractAssemblyFlag;
	public static boolean isRho;

	public HelperCli(boolean lexFlag, boolean parseFlag,
					 boolean typeCheckFlag, boolean irGenFlag,
					 boolean irRunFlag, boolean optirInitialFlag,
					 boolean optirFinalFlag, boolean optcfgInitialFlag,
					 boolean optcfgFinalFlag, String sourceDir,
					 String libDir, String destDir, String assemblyLibPath,
					 String target, boolean abstractAssemblyFlag) {
		this.lexFlag = lexFlag;
		this.parseFlag = parseFlag;
		this.typeCheckFlag = typeCheckFlag;
		this.irGenFlag = irGenFlag;
		this.irRunFlag = irRunFlag;
		this.optirInitialFlag = optirInitialFlag;
		this.optirFinalFlag = optirFinalFlag;
		this.optcfgInitialFlag = optcfgInitialFlag;
		this.optcfgFinalFlag = optcfgFinalFlag;
		this.sourceDir = sourceDir;
		this.libDir = libDir;
		this.destDir = destDir;
		this.assemblyLibPath = assemblyLibPath;
		this.target = target;
		this.abstractAssemblyFlag = abstractAssemblyFlag;
	}

	/**
	 * Prints out the list of options that the cli supports
	 *
	 * @param options - list of options cli supports
	 */
	public static void printHelpOptions(Options options) {
		Collection<Option> list = options.getOptions();
		String format = "%-40s%s%n";
		for (Option option : list) {
			String dashes = "--";
			String name = option.getLongOpt();
			if (option.getLongOpt() == null) {
				dashes = "-";
				name = option.getOpt();
			}

			if (option.hasArg()) {
				System.out.printf(format, dashes + name + " <" + option.getArgName() + ">", option.getDescription());
			} else {
				System.out.printf(format, dashes + name, option.getDescription());
			}
		}
	}

	/**
	 * Helper function to start full compilation process of source and
	 * interface files
	 *
	 * @param inputFiles - list of files to compile
	 */
	public void compile(String[] inputFiles) {

		Context c = new Context();

		Use.setLibPath(libDir);
		for (String inputFile : inputFiles) {
			String outputFileName = getOutputFileName(inputFile, destDir);
			lexToFile(inputFile, outputFileName, getFileType(inputFile));
			compileSourceFile(inputFile, outputFileName, c);
		}
	}

	public static void addInterfaceMethods(Context c, String etiName,
										   String libDir) throws SemanticError {
		Reader reader;
		String etiFile = etiName + ".eti";
		try {
			if (isStandardLibFile(etiFile)) {
				reader = createReader(etiFile, STANDARD_LIB_DIR);
			} else {
				reader = createReader(etiFile, libDir);
			}
		} catch (FileNotFoundException e) {
			throw new SemanticError("");
		}

		parser p = null;

		try {
			p = new parser(reader);
			p.setFileType("eti");
			p.setFileName(etiName);
		} catch (IOException io) {
			System.out.println(io.getMessage());
		}

		try {
			Program program = (Program) (p.parse().value);

			ArrayList<Definition> interfaceList = program.getDefs();
			for (Definition d : interfaceList) {
				d.add(c);
			}
		} catch (Exception e) {
			// TODO: handle when there is an error in eti file
		}
	}

	public static boolean isStandardLibFile(String fileName) {
		return Arrays.asList(STANDARD_LIB_FILES).contains(fileName);
	}

	/**
	 * Creates a reader to read in an input file
	 *
	 * @param inputFile - input file to read in
	 * @param inputDir  - where to find input file
	 * @return reader
	 */
	public static Reader createReader(String inputFile, String inputDir) throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(pathAppend(inputFile, inputDir, inputFile));
		return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
	}

	/**
	 * Appends a path to a file
	 *
	 * @param file    - base file to add prefix string to
	 * @param pre     - string to append to the file
	 * @param instead - if pre string is not present, use this string instead
	 * @return final path string after appending prefix
	 */
	private static String pathAppend(String file, String pre, String instead) {
		if (pre != null) {
			if (pre.charAt(pre.length() - 1) == '/') {
				return pre + file;
			} else {
				return pre + "/" + file;
			}
		}
		return instead;
	}

	/**
	 * Gets simple name of file ignoring the path
	 *
	 * @param inputFile - input file to get simple name from
	 * @return simple name
	 */
	private String getSimpleName(String inputFile) {
		String[] parts = inputFile.split("/");
		String name = parts[parts.length - 1];
		return name.substring(0, name.indexOf('.'));
	}

	/**
	 * Parse program to output parsed file
	 *
	 * @param program        - parsed program to print to file
	 * @param outputFileName - where to place located parsed file
	 * @param error          - if printing error message to parsed file
	 * @param errorMessage   - error message to print to parsed file
	 */
	public void parseToFile(Program program, String outputFileName, boolean error, String errorMessage) {
		try {

			if (errorMessage == null) {
				return;
			}

			FileWriter file = new FileWriter(outputFileName + ".parsed");

			PrintWriter output = new PrintWriter(file);

			CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(output);

			if (error) {
				printer.printAtom(errorMessage);
			} else {
				program.prettyPrint(printer);
			}

			printer.flush();
			printer.close();
		} catch (IOException io) {
			System.out.println(io.getMessage());
		}
	}

	/**
	 * Type check to typed file
	 *
	 * @param message        - message to write to typed file
	 * @param outputFileName - where to place typed file
	 */
	public void typeCheckToFile(String message, String outputFileName) {

		try {
			File file = new File(outputFileName + ".typed");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			if (message == null) {
				return;

			}

			bw.write(message);
			bw.flush();
			bw.close();

		}
		// catch (IOException io) {
		catch (IOException io) {
			System.out.println(io.getMessage());
		}

	}

	/**
	 * Gets the file type of the input file. Either .eta or .eti
	 *
	 * @param inputFile - input file which we want the type for
	 * @return file type for input file
	 */
	public String getFileType(String inputFile) {
		return inputFile.substring(inputFile.indexOf('.') + 1);
	}

	/**
	 * Gets the proper path of the output file from the user inputs to the CLI
	 *
	 * @param inputFile - input file to read
	 * @param dest      - destination directory to place the file
	 * @return output file path
	 */
	public String getOutputFileName(String inputFile, String dest) {
		String fileName = getSimpleName(inputFile);

		String origLoc = inputFile.substring(0, inputFile.indexOf('.'));
		return pathAppend(fileName, dest, pathAppend(origLoc, sourceDir,
				origLoc));
	}

	/**
	 * Performs lexical analysis on input file
	 *
	 * @param inputFile      - input file to lex
	 * @param outputFileName - where to place lexed file
	 * @param fileType       - whether we are lexing an eta or eti file
	 */
	public void lexToFile(String inputFile,
						  String outputFileName, String fileType) {

		Reader reader = null;
		try {
			reader = createReader(inputFile, sourceDir);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		LexFileWriter lexFileWriter = new LexFileWriter(reader, outputFileName, getSimpleName(inputFile) + "." + fileType,
				lexFlag);
		lexFileWriter.lexToFile();
	}

	/**
	 * Compile source file
	 *
	 * @param inputFile      - eta input file to compile
	 * @param outputFileName - where to place output file
	 * @param c              - context to use when compiling
	 */
	public void compileSourceFile(String inputFile, String outputFileName,
								  Context c) {

		Reader reader = null;
		try {
			reader = createReader(inputFile, sourceDir);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		parser p = null;

		try {
			String fileType = getFileType(inputFile);
			if (fileType.equals("rh")) {
				isRho = true;
			}
			p = new parser(reader);
			p.setFileType(fileType);
			p.setFileName(getSimpleName(inputFile));
		} catch (IOException io) {
			System.out.println(io.getMessage());
		}

		try {
			Program program = (Program) (p.parse().value);
			isRho = false;
			if (parseFlag) {
				parseToFile(program, outputFileName, false, "");
			}
			boolean error = typeCheckProgram(program, outputFileName, c, getSimpleName(inputFile));

			if (!error) {
				IRCompUnit loweredIRRoot = generateLoweredIR(program);
				IRNodeFactory factory = new IRNodeFactory_c();

				if (optirInitialFlag || optcfgInitialFlag) {
					cli.turnedOnOpts = new String[cli.turnedOnOpts.length];
					// IRNodeFactory factory = new IRNodeFactory_c();
					// IRCompUnit initialIRRoot = program.translate(factory).lower(factory);
					IRCompUnit initialIRRoot = loweredIRRoot;

					if (optirInitialFlag) {
						try {
							printIRToFile(outputFileName + "_intial", initialIRRoot);
						} catch (IOException io) {
							System.out.println(io.getMessage());
						}
					}

					if (optcfgInitialFlag) {
						try {
							writeCFGToFile(outputFileName + "_f_initial", initialIRRoot);
						} catch (IOException io) {
							System.out.println(io.getMessage());
						}
					}
				}

				// perform any optimizations that the flag is set for

				// System.out.println("\n\nBEFORE OPT :\n" + loweredIRRoot.IRcfg().toString());
				loweredIRRoot.optimizeIR(factory);
				// System.out.println("\n\nAFTER OPT :\n" + loweredIRRoot.IRcfg().toString());

				if (optirFinalFlag || optcfgFinalFlag) {
					cli.turnedOnOpts = cli.supportedOpts;
					// IRNodeFactory factory = new IRNodeFactory_c();
					// IRCompUnit finalIRRoot = program.translate(factory).lower(factory);
					IRCompUnit finalIRRoot = loweredIRRoot;

					if (optirFinalFlag) {
						try {
							printIRToFile(outputFileName + "_final", finalIRRoot);
						} catch (IOException io) {
							System.out.println(io.getMessage());
						}
					}

					if (optcfgFinalFlag) {
						try {
							writeCFGToFile(outputFileName + "_f_final", finalIRRoot);
						} catch (IOException io) {
							System.out.println(io.getMessage());
						}
					}
				}

				if (irGenFlag || irRunFlag) {
					try {
						printIRToFile(outputFileName, loweredIRRoot);
					} catch (IOException io) {
						System.out.println(io.getMessage());
					}
				}

				if (irRunFlag) {
					IRSimulator sim = new IRSimulator(loweredIRRoot);
					sim.call("_Imain_paai");
				}

				generateAssembly(inputFile, loweredIRRoot, abstractAssemblyFlag);
			}

		} catch (Exception e) {
			System.out.print("Error in compileSourceFile: ");
			System.out.println(e);
			if (parseFlag) {
				parseToFile(null, outputFileName, true, p.getErrorStr());
			}
			if (typeCheckFlag) {
				typeCheckToFile(p.getErrorStr(), outputFileName);
			}
		}
	}

	/**
	 * Type check program
	 *
	 * @param program        - parsed program to type check
	 * @param outputFileName - where to place output typed file
	 * @param c              - context to type check program
	 * @param fileName       - simple file name to print if error is generated
	 */
	public boolean typeCheckProgram(Program program,
									String outputFileName, Context c,
									String fileName) {

		String message = "Valid Eta Program";
		boolean error = false;
		try {
			program.typeCheck(c);
		} catch (SemanticError ia) {
			message = ia.getMessage();
			System.out.println("Semantic error beginning at " + fileName + ".eta:" + message);
			error = true;
		}

		if (typeCheckFlag) {
			typeCheckToFile(message, outputFileName);
		}
		return error;

	}

	/**
	 * Generate lowered IR
	 *
	 * @param program - AST program to translate to LIR
	 * @return root of LIR AST
	 */
	public IRCompUnit generateLoweredIR(Program program) {
		IRNodeFactory factory = new IRNodeFactory_c();
		IRCompUnit irRoot = program.translate(factory);

		// delete below this
		// IRCompUnit loweredIrRoot = irRoot.lower(factory);
		// FileWriter file = new FileWriter(outputFileName + ".ir");
		// PrintWriter output = new PrintWriter(file);
		// CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(output);
		// printer.printAtom("LOWERED:\n\n\n");
		// loweredIrRoot.printSExp(printer);
		// printer.printAtom("\n\n\n");
		// printer.printAtom("MIR:\n\n\n");
		// irRoot.printSExp(printer);
		// printer.flush();
		// printer.close();
		// return loweredIrRoot;

		// uncomment below and delete above this
		IRCompUnit test = irRoot.lower(factory);
		return test;
	}

	public void writeCFGToFile(String outputFileName, IRCompUnit irRoot) throws IOException {
		FileWriter file = new FileWriter(outputFileName + ".dot");
		PrintWriter output = new PrintWriter(file);
		// TODO: implement
		// output.write(Graph.printString());
		// System.out.println("HelperCli comp unit name:" + irRoot.name());
		// System.out.println("Final flattened IRcfg:");

		// BlockGraph IRcfg = irRoot.IRcfg();
		// System.out.println("Initial IR CFG: \n" + IRcfg.printString());
		// IRcfg.copyPropagation();
		// System.out.println("IR CFG after copy prop: \n" + IRcfg.printString());
		output.write(irRoot.IRcfg().printString());
		// output.write("dot file");
		output.flush();
		output.close();
	}

	/**
	 * Print generated IR code to file
	 *
	 * @param outputFileName - where to place ir file
	 * @param loweredIRRoot  - lowered IR root which to pretty print to file
	 * @throws IOException - if error in file
	 */
	public void printIRToFile(String outputFileName,
							  IRCompUnit loweredIRRoot) throws IOException {
		FileWriter file = new FileWriter(outputFileName + ".ir");
		PrintWriter output = new PrintWriter(file);
		CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(output);

		loweredIRRoot.printSExp(printer);
		printer.flush();
		printer.close();
	}

	/**
	 * Generate x86 assembly
	 *
	 * @param inputFile     - input file to generate assembly for
	 * @param loweredIRRoot - IRCompUnit used to generate assembly
	 */
	public void generateAssembly(String inputFile, IRCompUnit loweredIRRoot,
								 boolean isAbstract) {
		if (target.equals("linux")) {
			try {
				String outputFileName = getOutputFileName(inputFile, assemblyLibPath);
				File file = new File(outputFileName + ".s");
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);

				bw.write(loweredIRRoot.tile(isAbstract).getAssembly());
				// bw.write(loweredIRRoot.convertToX86().getAssembly());
				bw.flush();
				bw.close();
			} catch (IOException io) {
				System.out.println(io.getMessage());
			} catch (Exception e) {
				System.out.print("Exception in generateAssembly(): ");
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println("Cannot support " + target + " OS");
		}
	}

}