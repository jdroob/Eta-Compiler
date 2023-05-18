package main.java.jdr299zdh5cew256ans96;

import java_cup.runtime.Symbol;
import main.java.jdr299zdh5cew256ans96.cup.sym;
import main.java.jdr299zdh5cew256ans96.lexertokens.EtaToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class LexFileWriter 
    {

        private Reader reader;
        private String outputFileName;
        private String inputFile;
        private boolean toFile;

        public LexFileWriter(Reader reader, String outputFileName, String inputFile, boolean toFile) {
            this.reader = reader;
            this.outputFileName = outputFileName;
            this.inputFile = inputFile;
            this.toFile = toFile;
        }

        public boolean lexToFile() {
            Symbol content;
            boolean error = false;
            try {
                if (inputFile.substring(inputFile.indexOf('.') + 1).equals(
                        "rh")
                    || inputFile.substring(inputFile.indexOf('.') + 1).equals(
                                "ri") ) {
                    HelperCli.isRho = true;
                }
                EtaLexer lex = new EtaLexer(reader);


                File file;
                FileWriter fw;
                BufferedWriter bw = null;

                if (toFile) {
                    file = new File(outputFileName+".lexed");
                    fw = new FileWriter(file);
                    bw = new BufferedWriter(fw);
                }

                do {
                    content = lex.next_token();
                    if (content.sym != sym.EOF){
                        EtaToken token = (EtaToken)(content.value);
                        String outputStr = token.getLexedString();
                        if (content.sym == sym.ERROR) {
                            System.out.println("Lexical error beginning at "+inputFile+":"+outputStr);
                            error = true;
                        }
                        if (toFile) {
                            bw.append(outputStr);
                            bw.flush();
                        }

                    }
                } while (content.sym != sym.EOF && content.sym != sym.ERROR);
            } catch (FileNotFoundException f) {
                System.out.println("Error in reading input files: " + f.getMessage());
            } catch (Exception e) {
                /* ignore - already caught by parser */
            }
            return error;
        }

        /**
        * Appends a string to the object file
        *
        * precondition: -Expects java_cup.runtime.Symbol object
        *
        * postcondition: -Writes output of getLexedString() to BufferedWriter object                
        *
        * Notes: -type of content.value is Object
        *
        *        -EtaToken is a subclass of Symbol
        *
        *        -Symbol is a subclass of Object
        *
        *        -Therefore, we can cast content.value
        *        to be an EtaToken. 
        *
        *        -From there, we can call getLexedString
        *         which will always use version from specific
        *         EtaToken that was lexed.
        *        
        *        e.g. EtaIdentifier.getLexedString()
        */
  }