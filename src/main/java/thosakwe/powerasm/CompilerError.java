package thosakwe.powerasm;

public class CompilerError {
    int getLine() {
        return line;
    }

    int getPos() {
        return pos;
    }

    String getText() {
        return text;
    }

    private final int line;
    private final int pos;
    private final String text;

    public CompilerError(int line, int pos, String text) {
        this.line = line;
        this.pos = pos;
        this.text = text;
    }
}
