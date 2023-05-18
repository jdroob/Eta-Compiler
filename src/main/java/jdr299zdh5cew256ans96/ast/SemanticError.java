package main.java.jdr299zdh5cew256ans96.ast;

/**
 * Class for throwing a Semantic Error in type checking
 */
public class SemanticError extends Throwable {

    /**
     * Error message for semantic error
     */
    private String errorMessage;

    /**
     * Constructor for creating a semantic error object
     * @param e - error message
     */
    public SemanticError(String e) {
        errorMessage = e;
    }

    /**
     *
     * @return error message
     */
    public String getMessage() {
        return errorMessage;
    }
}
