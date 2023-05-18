package main.java.jdr299zdh5cew256ans96.assembly;

public class GloblDirective extends Directive {

    public GloblDirective(String d) {
        super(d);
    }

    @Override
    public String toString() {
        return ".globl "+directive;
    }


}
