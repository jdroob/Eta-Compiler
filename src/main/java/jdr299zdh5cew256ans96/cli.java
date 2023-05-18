package main.java.jdr299zdh5cew256ans96;

import java_cup.runtime.*;
import java_cup.runtime.Symbol;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.Reader;
import java.util.Arrays;

/**
 * Command line interface class that reads in and parses CLI commands.
 * Supports entire compilation process with the ability to generate files for
 * parsing, lexing, lowered IR, and assembly for debugging purposes.
 */
public class cli {

        public static String[] turnedOnOpts;

        // TODO: update
        public static String[] supportedOpts = { "cf", "reg", "dce", "copy" };

        // make sure to add a description for each option
        public static String[] supportedOptDescriptions = {
                        "constant folding. Evaluate expressions consisting of"
                                        + " only constants",
                        "register allocation. Efficiently allocate x86 registers"
                                        + " using Chaitin's algorithm.",
                        "dead code elimination. Remove unreachable code.",
                        "copy propagation. Replace variable copies." };

        public static void main(String[] args) {

                // Create valid options for command line interface
                Options options = new Options();

                // All double dash options
                Option help = Option.builder(null)
                                .desc("Print a synopsis of options")
                                .longOpt("help")
                                .build();

                Option abstractAssembly = Option.builder(null)
                                .desc("Output abstract " +
                                                "assembly to .s file")
                                .longOpt("aa")
                                .build();

                Option reportOpts = Option.builder(null)
                                .desc("Print list of supported optimizations")
                                .longOpt("report-opts")
                                .build();

                Option lex = Option.builder(null)
                                .desc("Generate output from lexical analysis")
                                .longOpt("lex")
                                .build();

                Option parse = Option.builder(null)
                                .desc("Generate output from syntactic analysis")
                                .longOpt("parse")
                                .build();

                Option typeCheck = Option.builder(null)
                                .desc("Generate output from semantic analysis")
                                .longOpt("typecheck")
                                .build();

                Option irGen = Option.builder(null)
                                .desc("Generate lowered intermediate code")
                                .longOpt("irgen")
                                .build();

                Option irRun = Option.builder(null)
                                .desc("Generate and run lowered intermediate code")
                                .longOpt("irrun")
                                .build();

                Option optir = Option.builder(null)
                                .argName("phase")
                                .hasArg()
                                .desc("Generate intermediate code at the " +
                                                "specified phase of optimization " +
                                                "(initial = before optimizations, " +
                                                "final = after optimizations)")
                                .longOpt("optir")
                                .build();

                Option optcfg = Option.builder(null)
                                .argName("phase")
                                .hasArg()
                                .desc("Generate control-flow graph at the" +
                                                "specified phase of optimization " +
                                                "(initial = before optimizations, " +
                                                "final = after optimizations)")
                                .longOpt("optcfg")
                                .build();

                Option sourcePath = Option.builder("sourcepath")
                                .argName("path")
                                .hasArg()
                                .desc("Specify where to find input source files. Default is " +
                                                "current directory")
                                .build();

                Option libPath = Option.builder("libpath")
                                .argName("path")
                                .hasArg()
                                .desc("Specify where to find library interface files. Default is " +
                                                "current directory")
                                .build();

                Option d = Option.builder("D")
                                .argName("path")
                                .hasArg()
                                .desc("Specify where to place generated diagnostic files")
                                .build();

                Option assemblyD = Option.builder("d")
                                .argName("path")
                                .hasArg()
                                .desc("Specify where to place generated assembly output files")
                                .build();

                Option target = Option.builder("target")
                                .argName("OS")
                                .hasArg()
                                .desc("Specify the operating system for which to generate code " +
                                                "(linux, windows, or macOS). " +
                                                "Default is linux.")
                                .build();

                Option optArg = Option.builder("O")
                                .argName("opt")
                                .hasArg()
                                .optionalArg(true)
                                .desc("Enable specified optimization opt. See" +
                                                " --report-ops to get a list of " +
                                                "options. Supply no opts (-O) to " +
                                                "disable " +
                                                "all optimizations. All optimizations" +
                                                " are enabled by default.")
                                .build();

                // add all options to a single options object
                options.addOption(help);
                options.addOption(reportOpts);
                options.addOption(lex);
                options.addOption(parse);
                options.addOption(typeCheck);
                options.addOption(irGen);
                options.addOption(irRun);
                options.addOption(optir);
                options.addOption(optcfg);
                options.addOption(sourcePath);
                options.addOption(libPath);
                options.addOption(d);
                options.addOption(assemblyD);
                options.addOption(target);
                options.addOption(optArg);
                options.addOption(abstractAssembly);

                try {
                        // Parse the command line arguments
                        if (args.length >= 2) {
                                if (args[args.length - 2].trim().equals("-O")) {
                                        args[args.length - 2] = args[args.length - 2] + "dum";
                                }
                        }

                        CommandLineParser parser = new DefaultParser();
                        CommandLine line = parser.parse(options, args);
                        String[] inputFiles = line.getArgs();

                        // Perform appropriate action based on parsed arguments
                        if (line.hasOption("report-opts") && !line.hasOption("help")) {
                                int i = 0;
                                for (String o : supportedOpts) {
                                        System.out.print(o);
                                        if (i < supportedOptDescriptions.length) {
                                                String optD = supportedOptDescriptions[i];
                                                System.out.println("  " + optD);
                                        } else {
                                                System.out.println();
                                        }
                                        i++;
                                }
                        } else if (line.hasOption("help") || inputFiles.length == 0) {
                                HelperCli.printHelpOptions(options);
                        } else {
                                String sourceDir = line.getOptionValue("sourcepath");
                                String destDir = line.getOptionValue("D");
                                String libDir = line.getOptionValue("libpath");
                                String assemblyDestDir = line.getOptionValue("d");

                                String targetOS = "linux";
                                if (line.hasOption("target")) {
                                        targetOS = line.getOptionValue(
                                                        "target");
                                }

                                boolean optirInitialFlag = false;
                                boolean optirFinalFlag = false;
                                boolean optcfgInitialFlag = false;
                                boolean optcfgFinalFlag = false;

                                if (line.hasOption("optir")) {
                                        String[] optirPhases = line.getOptionValues(
                                                        "optir");
                                        for (String s : optirPhases) {
                                                if (s.equals("initial")) {
                                                        optirInitialFlag = true;
                                                } else if (s.equals("final")) {
                                                        optirFinalFlag = true;
                                                } else {
                                                        throw new Exception("invalid " +
                                                                        "phase of ir. Must be" +
                                                                        " either initial or " +
                                                                        "final.");
                                                }
                                        }
                                }

                                if (line.hasOption("optcfg")) {
                                        String[] optCfgPhases = line.getOptionValues(
                                                        "optcfg");
                                        for (String s : optCfgPhases) {
                                                if (s.equals("initial")) {
                                                        optcfgInitialFlag = true;
                                                } else if (s.equals("final")) {
                                                        optcfgFinalFlag = true;
                                                } else {
                                                        throw new Exception("invalid " +
                                                                        "phase of ir. Must be" +
                                                                        " either initial or " +
                                                                        "final.");
                                                }
                                        }
                                }

                                if (!line.hasOption("O")) {
                                        turnedOnOpts = supportedOpts;
                                } else {
                                        turnedOnOpts = line.getOptionValues("O");
                                        if (turnedOnOpts == null) {
                                                turnedOnOpts = new String[1];
                                        }
                                }

                                boolean lexFlag = line.hasOption("lex");
                                boolean parseFlag = line.hasOption("parse");
                                boolean typecheckFlag = line.hasOption("typecheck");
                                boolean generateIRFlag = line.hasOption("irgen");
                                boolean runIRFlag = line.hasOption("irrun");
                                boolean abstractAssemblyFlag = line.hasOption("aa");

                                HelperCli helperCli = new HelperCli(lexFlag, parseFlag, typecheckFlag, generateIRFlag,
                                                runIRFlag, optirInitialFlag, optirFinalFlag, optcfgInitialFlag,
                                                optcfgFinalFlag, sourceDir, libDir, destDir,
                                                assemblyDestDir,
                                                targetOS, abstractAssemblyFlag);
                                helperCli.compile(inputFiles);
                        }
                } catch (Exception p) {
                        System.out.println(p.getMessage());
                }

        }

}