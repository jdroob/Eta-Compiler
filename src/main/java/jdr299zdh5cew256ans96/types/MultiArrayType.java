package main.java.jdr299zdh5cew256ans96.types;

public class MultiArrayType extends ArrayType {

  public MultiArrayType(String type) {
    super(type + " array");
  }

//  public String toString() {
//    return getType()+" array array";
//  }

  @Override
  public String getABIName() {
    return "aa"+getType().charAt(0);
  }

}