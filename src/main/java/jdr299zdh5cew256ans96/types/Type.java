package main.java.jdr299zdh5cew256ans96.types;

import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Type {
    private String type; // for type checking purposes

    public Type(String t) {
        type = t;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return getBaseType(this).trim();
    }

    public boolean equalsStr(String s) {
        return toString().equals(s);
    }

    public boolean equals(Type t) {
        return (toString().equals(t.toString()));
    }

    public CodeWriterSExpPrinter prettyPrint(CodeWriterSExpPrinter c) {
        c.printAtom(type);
        return c;
    }

    public String getABIName() {
        return type.charAt(0) + "";
    }

    // private String getBaseType(Type type) {
    // if (type.getType().equals("return")) {
    // StringBuilder typeStr = new StringBuilder();
    // ReturnType ret = (ReturnType) type;
    // ArrayList<Type> returnTypes = ret.getReturnTypes();
    // for (Type t : returnTypes) {
    // typeStr.append(t.getType()).append(" ");
    // }
    // return typeStr.toString();
    // } else {
    // return this.type;
    // }
    // }

    private String getBaseType(Type type) {
        if (type.getType().equals("return")) {
            // System.out.println("Type.getBaseType()");
            String typeStr = "";
            ReturnType ret = (ReturnType) type;
            ArrayList<Type> returnTypes = ret.getReturnTypes();
            for (Type t : returnTypes) {
                typeStr += t.getType() + " ";
            }

            while (typeStr.contains("return")) {
                String intermediateTypeStr = "";
                ArrayList<Type> intermediateTypeList = new ArrayList<>();
                for (Type t : returnTypes) {
                    if (t.getType().equals("return")) {
                        ReturnType ret2 = (ReturnType) t;
                        ArrayList<Type> returnTypes2 = ret2.getReturnTypes();
                        for (Type t2 : returnTypes2) {
                            intermediateTypeStr += t2.getType() + " ";
                        }
                        intermediateTypeList.addAll(returnTypes2);
                    } else {
                        intermediateTypeStr += t.getType();
                        intermediateTypeList.add(t);
                    }
                }
                typeStr = intermediateTypeStr;
                returnTypes = intermediateTypeList;
            }

            return typeStr;
        } else {
            return this.type;
        }
    }

}