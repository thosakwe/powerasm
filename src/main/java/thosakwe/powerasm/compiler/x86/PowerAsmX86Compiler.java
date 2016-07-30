package thosakwe.powerasm.compiler.x86;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.apache.commons.cli.CommandLine;
import thosakwe.powerasm.CompilerError;
import thosakwe.powerasm.antlr4.PowerAsmParser;
import thosakwe.powerasm.compiler.PowerAsmCompiler;

public class PowerAsmX86Compiler extends PowerAsmCompiler {
    public PowerAsmX86Compiler(CommandLine commandLine) {
        super(commandLine);
    }

    public void debug(String text) {
        if (verbose) {
            if (!commandLine.hasOption("stdout"))
                System.out.println(text);
            println("; " + text);
        }
    }

    public void error(String error, ParserRuleContext source) {
        if (source == null) {
            getCompilerErrors().add(new CompilerError(-1, -1, error));
            return;
        }

        Token start = source.start;
        int line = start.getLine();
        int pos = start.getCharPositionInLine();
        getCompilerErrors().add(new CompilerError(line, pos, error));
    }

    public void print(String text) {
        print(text, true);
    }

    public void print(String text, boolean trim) {

        if (trim && text.trim().length() == 0)
            return;

        for (int i = 0; i < tabs; i++) {
            outputText += "  ";
        }

        outputText += trim ? text.trim() : text;
    }

    public void println(String text) {
        println(text, true);
    }

    public void println(String text, boolean trim) {
        print(text, trim);
        print("\n", false);
    }

    public void warn(String error, ParserRuleContext source) {
        if (source == null) {
            getCompilerWarnings().add(new CompilerError(-1, -1, error));
            return;
        }

        Token start = source.start;
        int line = start.getLine();
        int pos = start.getCharPositionInLine();
        getCompilerWarnings().add(new CompilerError(line, pos, error));
    }

    public void write(String text) {
        write(text, true);
    }

    public void write(String text, boolean trim) {
        outputText += trim ? text.trim() : text;
    }

    public void writeln(String text) {
        writeln(text, true);
    }

    public void writeln(String text, boolean trim) {
        write(text, trim);
        write("\n", false);
    }

    @Override
    public Object visitCompilationUnit(PowerAsmParser.CompilationUnitContext ctx) {
        println("; Compiled via PowerASM");
        return super.visitCompilationUnit(ctx);
    }
}
