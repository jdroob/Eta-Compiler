package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Directive;
import main.java.jdr299zdh5cew256ans96.assembly.GloblDirective;
import main.java.jdr299zdh5cew256ans96.assembly.TypeDirective;
import main.java.jdr299zdh5cew256ans96.assembly.IntelSyntaxDirective;
import main.java.jdr299zdh5cew256ans96.assembly.TextDirective;
import main.java.jdr299zdh5cew256ans96.assembly.DataDirective;
import main.java.jdr299zdh5cew256ans96.assembly.GlobalPrefix;
import main.java.jdr299zdh5cew256ans96.assembly.Global;
import main.java.jdr299zdh5cew256ans96.assembly.Label;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.BlockGraph;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/** An intermediate representation for a compilation unit */
public class IRCompUnit extends IRNode_c {
    private final String name;
    private final Map<String, IRFuncDecl> functions;
    private final List<String> ctors;
    private final Map<String, IRData> dataMap;
    private final BlockGraph IRcfg;

    public IRCompUnit(String name) {
        this(name, new LinkedHashMap<>(), new ArrayList<>(), new LinkedHashMap<>(), new BlockGraph());
    }

    public IRCompUnit(String name, Map<String, IRFuncDecl> functions) {
        this(name, functions, new ArrayList<>(), new LinkedHashMap<>(), new BlockGraph());
    }

    public IRCompUnit(
            String name,
            Map<String, IRFuncDecl> functions,
            List<String> ctors,
            Map<String, IRData> dataMap,
            BlockGraph cfg) {
        this.name = name;
        this.functions = functions;
        this.ctors = ctors;
        this.dataMap = dataMap;
        if (cfg == null) {
            cfg = new BlockGraph();
        }
        this.IRcfg = cfg;
    }

    public void appendFunc(IRFuncDecl func) {
        functions.put(func.name(), func);
    }

    public void appendCtor(String functionName) {
        ctors.add(functionName);
    }

    public void appendData(IRData data) {
        dataMap.put(data.name(), data);
    }

    public BlockGraph IRcfg() {
        return IRcfg;
    }

    public String name() {
        return name;
    }

    public Map<String, IRFuncDecl> functions() {
        return functions;
    }

    public IRFuncDecl getFunction(String name) {
        return functions.get(name);
    }

    public List<String> ctors() {
        return ctors;
    }

    public Map<String, IRData> dataMap() {
        return dataMap;
    }

    public IRData getData(String name) {
        return dataMap.get(name);
    }

    @Override
    public String label() {
        return "COMPUNIT";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        Map<String, IRFuncDecl> results = new LinkedHashMap<>();
        for (IRFuncDecl func : functions.values()) {
            IRFuncDecl newFunc = (IRFuncDecl) v.visit(this, func);
            if (newFunc != func)
                modified = true;
            results.put(newFunc.name(), newFunc);
        }

        if (modified)
            return v.nodeFactory().IRCompUnit(name, results);

        return this;
    }

    public Assembly tile(boolean isAbstract) {
        Assembly directive = new Assembly();
        directive.addInstruction(new IntelSyntaxDirective());
        directive.addInstruction(new DataDirective());
        directive.addInstructions(getDataTypeDirectives());

        Assembly programAssembly = new Assembly();
        programAssembly.addInstructions(directive);
        programAssembly.addInstructions(getDataLabels());
        programAssembly.addInstruction(new TextDirective());
        programAssembly.addInstructions(getGloblDirectives());
        programAssembly.addInstructions(getFuncTypeDirectives());
        for (String funcName : functions.keySet()) {
            IRFuncDecl func = functions.get(funcName);
            Assembly funcAssembly = func.convertToX86(isAbstract);
            programAssembly.addInstructions(funcAssembly);
        }
        return programAssembly;
    }

    private Assembly getDataLabels() {
        Assembly globals = new Assembly();
        for (IRData data : dataMap().values()) {
            globals.addInstruction(new GlobalPrefix(data.name(), data.data().length));
            globals.addInstruction(new Label(data.name()));
            if (data.data().length == 0) {
                globals.addInstruction(new Global(0 + ""));
            }
            for (long l : data.data()) { // TODO: if uninitialized get length
                // and do quad 0s
                globals.addInstruction(new Global(l + ""));
            }
        }
        return globals;
    }

    private Assembly getGloblDirectives() {
        Assembly globlDirs = new Assembly();
        for (String funcName : functions.keySet()) {
            globlDirs.addInstruction(new GloblDirective(funcName));
        }
        return globlDirs;
    }

    private Assembly getDataTypeDirectives() {
        Assembly typeDirs = new Assembly();
        for (IRData data : dataMap().values()) {
            typeDirs.addInstruction(new TypeDirective(data.name()));
        }

        return typeDirs;
    }

    private Assembly getFuncTypeDirectives() {
        Assembly typeDirs = new Assembly();
        for (String funcName : functions.keySet()) {
            typeDirs.addInstruction(new TypeDirective(funcName));
        }

        return typeDirs;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRFuncDecl func : functions.values())
            result = v.bind(result, v.visit(func));
        return result;
    }

    public void optimizeIR(IRNodeFactory factory) {

        if (Arrays.asList(cli.turnedOnOpts).contains("copy")) {
            IRcfg.copyPropagation();
        }

        for (String funcName : functions.keySet()) {
            functions.get(funcName).optimizeIR(factory);

        }

        if (Arrays.asList(cli.turnedOnOpts).contains("dce")) {
            // System.out.println("Before dce: " + IRcfg.toString());
            IRcfg.deadCodeElimination();
            // System.out.println("After dce: " + IRcfg.toString());

            // Map<String, IRFuncDecl> loweredFunctions = new HashMap<>();

        }

    }

    public IRCompUnit lower(IRNodeFactory factory) {
        // System.out.println("\nNEW IRCOMPUNIT" + name + "\n");
        Map<String, IRFuncDecl> loweredFunctions = new HashMap<>();
        for (String funcName : functions.keySet()) {
            IRFuncDecl loweredFunction = functions.get(funcName).lower(factory);
            loweredFunctions.put(funcName,
                    loweredFunction);
            // build the CompUnit's CFG from the function's CFG's
            // System.out.println(loweredFunction.name());
            // System.out.println(loweredFunction.IRcfg().printString());
            IRcfg.merge(loweredFunction.IRcfg());
            // System.out.println("Printing flattened CFG up through function " + funcName);
            // IRcfg.flatten();

        }

        // I think here is where you would do the optimizations. Can do that on
        // each function and then just return same IRCompUnit with altered
        // sequence.
        return new IRCompUnit(name, loweredFunctions,
                new ArrayList<>(), dataMap, IRcfg);
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("COMPUNIT");
        p.printAtom(name);
        for (String ctor : ctors) {
            p.printAtom(ctor);
        }
        for (IRData data : dataMap.values()) {
            p.startList();
            p.printAtom("DATA");
            p.printAtom(data.name());
            p.startList();
            for (long value : data.data()) {
                p.printAtom(String.valueOf(value));
            }
            p.endList();
            p.endList();
        }
        for (IRFuncDecl func : functions.values())
            func.printSExp(p);
        p.endList();
    }
}
