package thosakwe.powerasm;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.cli.*;
import thosakwe.powerasm.antlr4.PowerAsmLexer;
import thosakwe.powerasm.antlr4.PowerAsmParser;
import thosakwe.powerasm.compiler.PowerAsmCompiler;
import thosakwe.powerasm.compiler.x86.PowerAsmX86Compiler;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Cli {
    private class Targets {
        private static final String X86 = "x86";
    }

    public static void main(String[] args) {
        Options options = makeOptions();
        CommandLineParser commandLineParser = new DefaultParser();

        try {
            if (args.length == 0)
                throw new Exception();

            CommandLine commandLine = commandLineParser.parse(options, args);
            if (commandLine.hasOption("help")) {
                throw new Exception();
            }

            String[] commandLineArgs = commandLine.getArgs();
            if (commandLineArgs.length == 0) {
                System.err.println("fatal error: no input files");
                throw new Exception();
            }

            compile(commandLine);

        } catch (Exception exc) {
            new HelpFormatter().printHelp("powerasm [options] <filename>", options);
        }
    }

    private static void compile(CommandLine commandLine) {
        String[] args = commandLine.getArgs();
        List<String> errors = new ArrayList<String>();
        List<String> warnings = new ArrayList<String>();

        for (String filename : args) {
            try {
                ANTLRInputStream inputStream = new ANTLRFileStream(filename);
                PowerAsmLexer lexer = new PowerAsmLexer(inputStream);
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                PowerAsmParser parser = new PowerAsmParser(tokenStream);
                parser.setBuildParseTree(true);
                PowerAsmCompiler compiler = chooseCompiler(commandLine);
                compiler.visitCompilationUnit(parser.compilationUnit());

                for (CompilerError warning : compiler.getCompilerWarnings()) {
                    warnings.add(String.format("warning (%s:%d:%d): %s", filename, warning.getLine(), warning.getPos(), warning.getText()));
                }

                for (CompilerError error : compiler.getCompilerErrors()) {
                    errors.add(String.format("error (%s:%d:%d): %s", filename, error.getLine(), error.getPos(), error.getText()));
                }

                if (compiler.getCompilerErrors().isEmpty()) {
                    PrintStream outputStream = commandLine.hasOption("stdout") ? System.out : null;
                    File outputFile = new File(filename.replaceAll("\\..+$", chooseExtension(commandLine)));

                    if (args.length == 1 && commandLine.hasOption("out")) {
                        outputFile = new File(commandLine.getOptionValue("out"));
                    }

                    try {
                        if (outputStream == null)
                            outputStream = new PrintStream(outputFile);
                        outputStream.print(compiler.compile());
                    } catch (Exception exc) {
                        errors.add(String.format("Could not compile '%s': %s", filename, exc.getMessage()));
                    } finally {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                }
            } catch (Exception exc) {
                System.out.println(String.format("Compilation finished with %d error(s) and %d warning(s).", errors.size(), warnings.size()));
                System.exit(1);
                return;
            }

            System.out.println(String.format("Compilation finished with %d warning(s).", warnings.size()));
        }
    }

    private static IllegalArgumentException invalidTarget(String target) {
        return new IllegalArgumentException(String.format("Cannot generate code for unsupported target '%s'.", target));
    }

    private static String chooseExtension(CommandLine commandLine) throws IllegalArgumentException {
        String target = commandLine.getOptionValue("target", "x86");

        if (target.equals(Targets.X86)) {
            return ".asm";
        } else {
            throw invalidTarget(target);
        }
    }

    private static PowerAsmCompiler chooseCompiler(CommandLine commandLine) throws IllegalArgumentException {
        String target = commandLine.getOptionValue("target", "x86");

        if (target.equals(Targets.X86)) {
            return new PowerAsmX86Compiler(commandLine);
        } else {
            throw invalidTarget(target);
        }
    }

    private static Options makeOptions() {
        Options result = new Options();
        result.addOption(Option.builder("ir")
                .longOpt("emit-llvm")
                .desc("Emit LLVM IR instead of x86 Assembly.")
                .hasArg(false)
                .build());
        result.addOption(Option.builder("o")
                .longOpt("out")
                .desc("Writes output to <filename>.")
                .hasArg()
                .argName("filename")
                .build());
        result.addOption("d", "verbose", false, "Print verbose debug output.");
        result.addOption("x", "stdout", false, "Write output to STDOUT.");
        result.addOption(Option.builder("t")
                .longOpt("target")
                .hasArg()
                .argName("target")
                .desc("Generates code for the given target (x86, x64, llvm, arm)")
                .build());
        return result;
    }
}
