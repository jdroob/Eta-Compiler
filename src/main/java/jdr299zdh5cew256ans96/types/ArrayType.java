package main.java.jdr299zdh5cew256ans96.types;

/**
 * AST Node for the Array type. It can be used anywhere
 * where a type can be used. Can contain a node for what
 * goes between the brackets of the array.
 */
public class ArrayType extends Type {

    public ArrayType(String type) {
        super(type + " array");
    }

//    public String toString() {
//        return getType()+" array";
//    }

    @Override
    public String getABIName() {
        return "a"+super.getABIName();
    }

}
