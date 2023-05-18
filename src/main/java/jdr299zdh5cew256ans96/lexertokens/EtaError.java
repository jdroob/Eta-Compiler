package main.java.jdr299zdh5cew256ans96.lexertokens;

public class EtaError extends EtaToken {
    private ERROR_CODE errorCode;
    public EtaError(ERROR_CODE c) {
        errorCode=c;
    }
   
    public String toString() {
        return "";
    }
        public String getLexedString() {
        switch(errorCode) {
            case CHAR_ERROR: 
                return getPos()+" error: invalid character literal\n";
            
            case UNMATCHED_CHAR_ERROR: 
                return getPos()+" error: nonterminating character literal\n";
            
            case STRING_ERROR: 
                return getPos()+" error: invalid string literal\n";
                
            case UNMATCHED_STRING_ERROR: 
                return getPos()+" error: nonterminating string literal\n";
                
            case COMMENT_UNCLOSED: 
                return getPos()+" error: comment unclosed at end of file\n";
            
            case INVALID_ESCAPE: 
                return getPos()+" error: invalid escape sequence\n";
                
            case TOKEN_NOT_FOUND_ERROR: 
                return getPos()+" error: token not found\n";

            default:
                return getPos()+" error "+ "\n";        
        }
    }    

    public enum ERROR_CODE {
        NO_ERROR,
        CHAR_ERROR,
        STRING_ERROR,
        UNMATCHED_CHAR_ERROR,
        UNMATCHED_STRING_ERROR,
        COMMENT_UNCLOSED,
        INVALID_ESCAPE,
        TOKEN_NOT_FOUND_ERROR
    }
}
