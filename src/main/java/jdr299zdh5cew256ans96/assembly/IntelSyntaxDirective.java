package main.java.jdr299zdh5cew256ans96.assembly;

public class IntelSyntaxDirective extends Directive {

    public IntelSyntaxDirective() {
        super("INTEL_SYNTAX_DIRECTIVE");
    }

    @Override
    public String toString() {
        return ".intel_syntax noprefix";
    }
}
