package thosakwe.powerasm.codegen;

import java.io.PrintStream;

/**
 * Generates Assembly code.
 */
public abstract class AssemblyCodegen {
    /**
     * Prints generated code to an output stream.
     *
     * @param output The stream to write code to.
     */
    protected abstract void generate(PrintStream output);
}
