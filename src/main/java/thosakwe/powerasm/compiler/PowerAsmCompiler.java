package thosakwe.powerasm.compiler;

import org.apache.commons.cli.CommandLine;
import thosakwe.powerasm.CompilerError;
import thosakwe.powerasm.antlr4.PowerAsmBaseVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes PowerASM input code and generates some other representation of it.
 */
public class PowerAsmCompiler extends PowerAsmBaseVisitor {
    protected final CommandLine commandLine;
    private List<CompilerError> compilerErrors = new ArrayList<CompilerError>();
    private List<CompilerError> compilerWarnings = new ArrayList<CompilerError>();
    protected String outputText = "";
    protected int tabs = 0;
    protected boolean verbose = false;

    public PowerAsmCompiler(CommandLine commandLine) {
        this.commandLine = commandLine;
        this.verbose = commandLine.hasOption("verbose");
    }

    public List<CompilerError> getCompilerErrors() {
        return compilerErrors;
    }

    public List<CompilerError> getCompilerWarnings() {
        return compilerWarnings;
    }

    public String compile() {
        if (!compilerErrors.isEmpty()) {
            // No output if compilation failed ;)
            return null;
        }

        return outputText;
    }

    protected void enter() {
        tabs++;
    }

    protected void exit() {
        tabs--;
    }
}
