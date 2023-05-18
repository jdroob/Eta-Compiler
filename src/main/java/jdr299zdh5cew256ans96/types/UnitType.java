package main.java.jdr299zdh5cew256ans96.types;

public class UnitType extends RType{

    //add a field to contain Return statements
    //to be used when something is of type unit, but a statement (or statements) 
    //inside of it may return stuff

    public UnitType(){
        super("unit");
    }

    public UnitType(ReturnType r){
        super("unit", r);
    }

}