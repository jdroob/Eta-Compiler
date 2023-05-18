package main.java.jdr299zdh5cew256ans96.assembly;

public class TypeDirective extends Directive {

    public TypeDirective(String t) {
        super(t);
    }

    @Override
    public String toString() {
        // function case
        if (directive.contains("_I")) {
            return ".type "+directive+", @function";
        }
        // global data case
        return ".type "+directive+", @object";
    }
}
