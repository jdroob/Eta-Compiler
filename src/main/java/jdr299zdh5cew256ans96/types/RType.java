package main.java.jdr299zdh5cew256ans96.types;

import java.util.ArrayList;

public class RType extends Type {

    private ReturnType returns;

    public RType(ReturnType r){
        super("R");
        returns=r;
    }

    public RType(String s){
        super(s);
        returns=new ReturnType(new ArrayList<>());
    }

    public RType(String s, ReturnType r){
        super(s);
        returns=r;
    }

    public RType(){
        super("R");
        returns=new ReturnType(new ArrayList<>());
    }

    public boolean isEmpty() {
		return returns.isEmpty();
	}

    public ReturnType getReturns(){
        return returns;
    }
}