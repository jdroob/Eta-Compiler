/*
 * THIS IS THE USER CODE SECTION
 *
 * UserCode
 *
 * Options and declarations
 *
 * Lexical rules
 */
package main.java.jdr299zdh5cew256ans96;

import main.java.jdr299zdh5cew256ans96.lexertokens.*;
import main.java.jdr299zdh5cew256ans96.cup.sym;
import java_cup.runtime.Symbol;

%%

%public
%class EtaLexer
%cup
%function next_token
%type java_cup.runtime.Symbol
%line
%column
%standalone
%unicode
%pack

%state COMMENT 
%state MULTILINECOMMENT 
%state STRINGLIT
%state CHARACTER
%state ERROR

%{
    // string holds the stream of chars following a "
    StringBuffer string = new StringBuffer();  

    // Holds column number of a ' or "  or (/*)
    public int startCol;

    // Holds line number of a ' or " (/*)
    public int startLine;

    // errorCode is updated when a lexing error is scanned
    public EtaError.ERROR_CODE errorCode = EtaError.ERROR_CODE.NO_ERROR;

    /* Returns a new java_cup.runtime.Symbol object
     * 
     * Parameters: type corresponds to one of the terminal symbol values in sym.java
     *
     * Precondition: type is a valid .sym value
     *               token is a valid EtaToken class object
     *
     * Postcondition: a new Symbol object is returned with correct: .sym value, EtaToken type,
     *                column number, and line number
    */
    private Symbol symbol(int type, EtaToken token) {
      if (type == sym.STRING_LITERAL || type == sym.CHARACTER_LITERAL
      || (type == sym.ERROR
      && (errorCode == EtaError.ERROR_CODE.CHAR_ERROR
      || errorCode == EtaError.ERROR_CODE.UNMATCHED_CHAR_ERROR
      || errorCode == EtaError.ERROR_CODE.UNMATCHED_STRING_ERROR
      || errorCode == EtaError.ERROR_CODE.STRING_ERROR
      || errorCode == EtaError.ERROR_CODE.COMMENT_UNCLOSED
      || errorCode == EtaError.ERROR_CODE.INVALID_ESCAPE
      || errorCode == EtaError.ERROR_CODE.TOKEN_NOT_FOUND_ERROR))) {
        token.setCol(startCol);
        token.setLine(startLine);
      } else {
        token.setCol(yycolumn+1);
        token.setLine(yyline+1);
      }
      if (type == sym.STRING_LITERAL) {
        // Clear buffer
        string.delete(0,string.length());
      }
      return new Symbol(type, token);
    }

    // Returns yyline + 1 (yyline starts at 0) 
    public int lineNumber() { return yyline + 1; }
    
    // Returns yycolumn + 1 (yycolumn starts at 0)
    public int column() { return yycolumn + 1; }
%}


Int = int
Record = record
IntegerLiteral = (0*[1-9]+0*)*     // may have zeros anywhere in string but requires at least 1 non-zero digit
Zero = 0
Bool = bool
True = true
False = false
Whitespace = [ \t\f\r\n]+
Identifier = [a-zA-Z][a-zA-Z0-9_']* | _
Hex = \\x | "0x"
Uni = \\u | "0u"
OPEN_BRACE = "{"
CLOSE_BRACE = "}"
HexSeq = {Hex}{OPEN_BRACE}[0-9A-Fa-f]{1,6}{CLOSE_BRACE}
UniSeq = {Uni}{OPEN_BRACE}[0-9A-Fa-f]{1,6}{CLOSE_BRACE}
SingleQuote = [\']
Quote = [\"]
Comment = "//"
MultiLineComment = "/*"


%%


<YYINITIAL> {

    "-"                               { return symbol(sym.MINUS, new EtaMinus()); }
    "!="                              { return symbol(sym.NOT_EQUAL, new EtaNotEquals()); }
    "*>>"                             { return symbol(sym.HIGH_MULT, new EtaHighMultiplication()); }
    "*"                               { return symbol(sym.TIMES, new EtaMultiplication()); }
    "/"                               { return symbol(sym.DIVIDE, new EtaDivision()); }
    "%"                               { return symbol(sym.MODULO, new EtaModulo()); }
    "+"                               { return symbol(sym.PLUS, new EtaPlus()); }
    "<="                              { return symbol(sym.LEQ, new EtaLEQ()); }
    "<"                               { return symbol(sym.LT, new EtaLT()); }
    ">="                              { return symbol(sym.GEQ, new EtaGEQ()); }
    ">"                               { return symbol(sym.GT, new EtaGT()); }
    "=="                              { return symbol(sym.EQUALS, new EtaEquals()); }
    "!"                               { return symbol(sym.NOT, new EtaNOT()); }
    "&"                               { return symbol(sym.AND, new EtaAND()); }
    "|"                               { return symbol(sym.OR, new EtaOR()); }
    "="                               { return symbol(sym.ASSIGN, new EtaAssign()); }
    "use"                             { return symbol(sym.USE, new EtaUse()); }
    "if"                              { return symbol(sym.IF, new EtaIf()); }
    "while"                           { return symbol(sym.WHILE, new EtaWhile()); }
    "else"                            { return symbol(sym.ELSE, new EtaElse()); }
    "return"                          { return symbol(sym.RETURN, new EtaReturn()); }
    "break"                           {
      	                                    if (HelperCli.isRho) {
      	                                    	return symbol(sym.BREAK, new RhoBreak());
      	                                    }

      	                                    return symbol(sym.IDENTIFIER, new EtaIdentifier(yytext()));
                                      }
    "null"                            {
                                            if (HelperCli.isRho) {
                                            	return symbol(sym.NULL, new RhoNull());
                                            }

                                            return symbol(sym.IDENTIFIER, new EtaIdentifier(yytext()));
                                      }
//    "length"                          { return symbol(sym.LENGTH, new EtaLength()); }
    "("                               { return symbol(sym.OPEN_PAREN, new EtaOpenParen()); }
    ")"                               { return symbol(sym.CLOSE_PAREN, new EtaCloseParen()); }
    "["                               { return symbol(sym.OPEN_BRACKET, new EtaOpenBracket()); }
    "]"                               { return symbol(sym.CLOSE_BRACKET, new EtaCloseBracket()); }
    "{"                               { return symbol(sym.OPEN_BRACE, new EtaOpenBrace()); }
    "}"                               { return symbol(sym.CLOSE_BRACE, new EtaCloseBrace()); }
    ";"                               { return symbol(sym.SEMICOLON, new EtaSemicolon()); }
    ":"                               { return symbol(sym.COLON, new EtaColon()); }
    ","                               { return symbol(sym.COMMA, new EtaComma()); }
    "."                               { return symbol(sym.DOT, new EtaDot()); }

    /*
    * End of file token.
    * This takes care of 
    * the case of empty 
    * programs.
    */
    <<EOF>>                           { return symbol(sym.EOF, new EtaEOF()); }
  
    {Whitespace}                      { yybegin(YYINITIAL); }
  
    {Int}                             { return symbol(sym.INT, new EtaInt()); }

    {Record}                            {
      	                                    if (HelperCli.isRho) {
      	                                        return symbol(sym.RECORD, new RhoRecord());
      	                                    }

      	                                    return symbol(sym.IDENTIFIER, new EtaIdentifier(yytext()));
                                        }
  
    {Bool}                            { return symbol(sym.BOOL, new EtaBool()); }
  
    {True}                            { return symbol(sym.TRUE, new EtaTrue()); }
  
    {False}                           { return symbol(sym.FALSE, new EtaFalse()); }
  
    {IntegerLiteral}                  { return symbol(sym.INTEGER_LITERAL, new EtaIntLiteral(yytext())); }

    {Zero}                            { return symbol(sym.INTEGER_LITERAL, new EtaIntLiteral(yytext())); }
      
    {Identifier}                      { return symbol(sym.IDENTIFIER, new EtaIdentifier(yytext())); }

    {HexSeq}                          {
                                        int dec = Integer.parseInt(yytext().substring(3,yytext().length()-1), 16);
                                        return symbol(sym.INTEGER_LITERAL, new EtaIntLiteral(""+dec));
                                      }
 
    {UniSeq}                          {
                                        int dec = Integer.parseInt(yytext().substring(3,yytext().length()-1));
                                        return symbol(sym.INTEGER_LITERAL, new EtaIntLiteral(""+dec));
                                      }

    {SingleQuote}                     { startCol=yycolumn+1; startLine=yyline+1; yybegin(CHARACTER); }

    {SingleQuote}{SingleQuote}        {
                                        startCol=yycolumn+1;
                                        startLine=yyline+1; 
                                        errorCode = EtaError.ERROR_CODE.CHAR_ERROR; yybegin(ERROR);
                                        return symbol(sym.ERROR, new EtaError(errorCode));
                                      }
    
    {Quote}{Quote}                    { 
                                        startCol=yycolumn+1;
                                        startLine=yyline+1; 
                                        return symbol(sym.STRING_LITERAL, new EtaStrLiteral("")); 
                                      }

    {Quote}                           { startCol=yycolumn+1; startLine=yyline+1; yybegin(STRINGLIT); }

    {Comment}                         { yybegin(COMMENT); }

    {MultiLineComment}                { startCol=yycolumn+1; startLine=yyline+1; yybegin(MULTILINECOMMENT); }

}
<COMMENT> {
    "\n" | "\r"                       { yybegin(YYINITIAL); }
    [^]                               { /*Do Nothing*/ }
}
<MULTILINECOMMENT> {
    "*/"                              { yybegin(YYINITIAL); }

    <<EOF>>                           {   
                                        errorCode = EtaError.ERROR_CODE.COMMENT_UNCLOSED; 
                                        return symbol(sym.ERROR, new EtaError(errorCode)); 
                                      }

    [^]                               { /*Do Nothing*/ }
}
<CHARACTER> {

    /*e.g. '\x{A9F3}'*/
    {HexSeq}{SingleQuote}  
    | {UniSeq}{SingleQuote}           {
                                        int dec = Integer.parseInt(yytext().substring(3,yytext().length()-2), 16);
                                        if (dec >= 32 && dec <= 126) {
                                          String asciiRepresentation = String.valueOf((char)dec);
                                          yybegin(YYINITIAL);
                                          return symbol(sym.CHARACTER_LITERAL, new EtaCharacterLiteral(asciiRepresentation));
                                        } else {
                                            yybegin(YYINITIAL);
                                            return symbol(sym.CHARACTER_LITERAL, new EtaCharacterLiteral(yytext()));
                                        }
                                      }

    /* '\'' */
    \\\'{SingleQuote}                 {
                                        String charVal = String.valueOf(yytext().substring(0,2));
                                        yybegin(YYINITIAL);
                                        return symbol(sym.CHARACTER_LITERAL, new EtaCharacterLiteral(charVal));
                                      }
    /* '\"' */
    \\\"{SingleQuote}                 {
                                        String charVal = String.valueOf(yytext().substring(0,2));
                                        yybegin(YYINITIAL);
                                        return symbol(sym.CHARACTER_LITERAL, new EtaCharacterLiteral(charVal));
                                      }

    /* '\\' -> \ */
    \\\\{SingleQuote}                 {
                                        String charVal = String.valueOf(yytext().substring(0,2));
                                        yybegin(YYINITIAL);
                                        return symbol(sym.CHARACTER_LITERAL, new EtaCharacterLiteral(charVal));
                                      }
    /* Default case */
    [^\\\n\r]{SingleQuote}            {
                                        // Test each incoming char to see if it is an ASCII or not
                                        String text = yytext().substring(0,yytext().length()-1);      // remove ending '
                                        int unicodeVal = Integer.valueOf(Integer.parseInt(text.codePoints().mapToObj(Integer::toHexString).findFirst().get(),16));

                                        // Normal text
                                        if (unicodeVal < 128) {
                                          yybegin(YYINITIAL);
                                          return symbol(sym.CHARACTER_LITERAL, new EtaCharacterLiteral(text));   
                                        } 
                                        // Non-ASCII Unicode
                                        else {
                                          String escapedUni = "\\x{"+Integer.toHexString(unicodeVal)+"}";
                                          yybegin(YYINITIAL);
                                          return symbol(sym.CHARACTER_LITERAL, new EtaCharacterLiteral(escapedUni));
                                        }
                                      }
    /* 
    ERROR CASES 
    ==================================================================================================================================
    */

    /* '\' should be a nonterminating error */
    \\{SingleQuote}                   {
                                        errorCode = EtaError.ERROR_CODE.UNMATCHED_CHAR_ERROR; 
                                        yybegin(ERROR);
                                      }

    /*
    * multi-line char error
    */
    \n | \r                           { errorCode = EtaError.ERROR_CODE.CHAR_ERROR; yybegin(ERROR); }

    /*
    * nonterminating (empty) char error
    */
    <<EOF>>                           { 
                                        errorCode = EtaError.ERROR_CODE.UNMATCHED_CHAR_ERROR; 
                                        return symbol(sym.ERROR, new EtaError(errorCode)); 
                                      }
    
    /*
    * nonterminating char error if
    * no single quote at end of regular
    * expression
    *
    * invalid char error otherwise
    */
    [^\'][^\']+{SingleQuote}?         { 
                                        if (yytext().indexOf('\'') == -1) {
                                            errorCode = EtaError.ERROR_CODE.UNMATCHED_CHAR_ERROR; 
                                        } else {
                                            errorCode = EtaError.ERROR_CODE.CHAR_ERROR; 
                                        }
                                        return symbol(sym.ERROR, new EtaError(errorCode));
                                      }
    /*
    * catches below edge case
    * 'a
    *
    * NOTE: above case catches 'r+, 'rr+' 
    * (where r is any char)
    */
    [^\'][^\']*[\n\r]?                { 
                                        errorCode = EtaError.ERROR_CODE.UNMATCHED_CHAR_ERROR; 
                                        return symbol(sym.ERROR, new EtaError(errorCode));
                                      }

    .                                 { yybegin(ERROR); errorCode = EtaError.ERROR_CODE.CHAR_ERROR; }
}
<STRINGLIT> {
    /*End of String*/
      \"                              { 
                                        yybegin(YYINITIAL); 
                                        String result = string.toString();  // Store result in String before buffer is cleared
                                        return symbol(sym.STRING_LITERAL, new EtaStrLiteral(result)); 
                                      }
                                      
      \\[tfrn\"\'\\]                  { string.append(yytext());}

    /*
    * HexSeq = \x{HHHH}
    * UniSeq = \u{UUUU}
    * invalid char error otherwise
    */
      {HexSeq} | {UniSeq}             {
                                        int dec = Integer.parseInt(yytext().substring(3,yytext().length()-1), 16);
                                        if (dec >= 32 && dec <= 126) {
                                           String asciiRepresentation = String.valueOf((char)dec);
                                           string.append(asciiRepresentation);
                                           } else {
                                           string.append(yytext());
                                         }
                                      }
    
                                      /*Append anything that isn't a new line*/
      [^\n\r]                         { 
                                        // Test each incoming char to see if it is an ASCII or not 
                                        int unicodeVal = Integer.valueOf(Integer.parseInt(yytext().codePoints().mapToObj(Integer::toHexString).findFirst().get(),16));

                                        // Normal text
                                        if (unicodeVal < 128) {
                                          string.append(yytext());   
                                        } 
                                        // Non-ASCII Unicode
                                        else {
                                          String escapedUni = "\\x{"+Integer.toHexString(unicodeVal)+"}";
                                          string.append(escapedUni);
                                        }
                                      }

    /* 
    ERROR CASES 
    ==================================================================================================================================
    */                                      

      \\[^tfrn\"\'\\]                 { errorCode = EtaError.ERROR_CODE.INVALID_ESCAPE; yybegin(ERROR);}
      
                                       /*End of program reached without closing quote*/
      <<EOF>>                         {   
                                        errorCode = EtaError.ERROR_CODE.UNMATCHED_STRING_ERROR; 
                                        return symbol(sym.ERROR, new EtaError(errorCode)); 
                                      }

                                       /*Invalid new line in string*/
      [\n\r][^\"]*{Quote}?            { 
                                        if (yytext().indexOf('\"') == -1) {
                                            errorCode = EtaError.ERROR_CODE.UNMATCHED_STRING_ERROR; 
                                        } else {
                                            errorCode = EtaError.ERROR_CODE.STRING_ERROR;
                                        }
                                            return symbol(sym.ERROR, new EtaError(errorCode));  
                                      }                    
}
<ERROR> {
                                         /*Error state*/
    .?                                 { return symbol(sym.ERROR, new EtaError(errorCode)); }
}
                                         /*Unexpected token*/
.                                     { 
                                        startCol=yycolumn+1;
                                        startLine=yyline+1;
                                        errorCode = EtaError.ERROR_CODE.TOKEN_NOT_FOUND_ERROR; 
                                        yybegin(ERROR);
                                      }