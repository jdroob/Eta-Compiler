package main.java.jdr299zdh5cew256ans96.types;

/**
 * AST Node for the Array type. It can be used anywhere
 * where a type can be used. Can contain a node for what
 * goes between the brackets of the array.
 */
public class ArrayAccessType extends Type {

    public ArrayAccessType(String type) {
        super(type);
    }

    public String toString() {
        return "access " + getType();
    }

    @Override
    public String getABIName() {
        return "a" + super.getABIName();
    }

}
