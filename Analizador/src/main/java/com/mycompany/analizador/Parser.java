/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizador;

import java.util.List;

/**
 *
 * @author danielaperez
 */
public class Parser {
    private List<Token> tokens;
    private int index;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    private Token currentToken() {
        return index < tokens.size() ? tokens.get(index) : null;
    }

    private void consume(TipoToken tipoEsperado) {
        if (currentToken() != null && currentToken().tipo == tipoEsperado) {
            index++;
        } else {
            throw new RuntimeException("Error de sintaxis: se esperaba token tipo " + tipoEsperado + "  se encontró " + currentToken());
        }
    }

    public void parse() {
        while (index < tokens.size()) {
            if (currentToken().tipo == TipoToken.PALABRA_RESERVADA) {
                if (currentToken().valor.equals("entero") ) {
                    declaraciónVariable();
                }
                else if (currentToken().valor.equals("decompas")) {
                    declaraciónVariableBool();
                }
                else if (currentToken().valor.equals("cadena")) {
                    declaraciónVariableCadena();
                }
                else if (currentToken().valor.equals("flota")) {
                    declaraciónVariableFlotante();
                }
                else if (currentToken().valor.equals("palabanda")) {
                    funcion();
                }
                else if (currentToken().valor.equals("mientras")) {
                    sentenciaControlWhile();
                }
                else if (currentToken().valor.equals("muevelas")) {
                    sentenciaControlDoWhile();
                }
                else {
                    sentenciaControl();
                }
            } else {
                throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
            }
        }
    }

    private void declaraciónVariable() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume tipo de variable
        consume(TipoToken.ID); // Consume nombre var
        consume(TipoToken.IGUAL); // Consume '='
        valor(); // Consume valor  var
        consume(TipoToken.FIN_DE_LINEA); // Consume '#'
    }
    private void declaraciónVariableFlotante() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume tipo de variable "flota"
        consume(TipoToken.ID); // Consume nombre de la variable
        consume(TipoToken.IGUAL); // Consume '='
        valor(); // Aquí se valida que el valor sea un número flotante
        consume(TipoToken.FIN_DE_LINEA); // Consume '#'
    }
    private void declaraciónVariableCadena() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume tipo de variable "cadena"
        consume(TipoToken.ID); // Consume nombre de la variable
        consume(TipoToken.IGUAL); // Consume '='
        consume(TipoToken.COMILLA_DOBLE); // Consume la primera comilla doble
        valor(); // Aquí se valida que el valor sea una cadena
        consume(TipoToken.COMILLA_DOBLE); // Consume la segunda comilla doble
        consume(TipoToken.FIN_DE_LINEA); // Consume '#'
    }
    private void declaraciónVariableBool() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume tipo de variable 'decompas'
        consume(TipoToken.ID); // Consume nombre var
        consume(TipoToken.IGUAL); // Consume '='
        valor(); // Aquí se valida que el valor sea 'si' o 'no' (en el caso de 'decompas')
        consume(TipoToken.FIN_DE_LINEA); // Consume '#'
    }


    private void valor() {
        Token tokenActual = currentToken();
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba un valor, pero no se encontró ningún token.");
        }
        else if (tokenActual.tipo == TipoToken.PALABRA_RESERVADA && (tokenActual.valor.equals("si") || tokenActual.valor.equals("no"))) {
            consume(TipoToken.PALABRA_RESERVADA); // Consume 'si' o 'no'
        }
        else if (tokenActual.tipo == TipoToken.CADENA) {
            consume(TipoToken.CADENA); // Consume la cadena
        }
        else if (tokenActual.tipo == TipoToken.NUMERO_F) {
            consume(TipoToken.NUMERO_F); // Consume número flotante
        } 
        else if (tokenActual.tipo == TipoToken.NUMERO) {
            consume(TipoToken.NUMERO); // Consume número entero
        }
        else {
            throw new RuntimeException("Error de sintaxis: se esperaba un valor pero se encontró '" + tokenActual.valor + "'");
        }
    }


    private void funcion() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume 'palabanda'

        consume(TipoToken.PALABRA_RESERVADA); // Consume 'void'

        consume(TipoToken.ID); // Consume el identificador (nombre de la función)

        consume(TipoToken.PARENTESIS_AP); // Consume '('

        consume(TipoToken.PARENTESIS_CIERRE); // Consume ')'

        consume(TipoToken.LLAVE_AP); // Consume '{'

        // Aquí comenzamos a procesar el cuerpo de la función, que puede contener declaraciones
        // y estructuras de control.
        while (currentToken() != null && currentToken().tipo != TipoToken.LLAVE_CIERRE) {
            // Dentro de la función pueden haber declaraciones, sentencias, etc.
            if (currentToken().tipo == TipoToken.PALABRA_RESERVADA) {
                // Si encontramos una palabra reservada, podría ser una declaración de variable
                // o una sentencia de control.
                if (currentToken().valor.equals("entero") || currentToken().valor.equals("flota") || currentToken().valor.equals("cadena") || currentToken().valor.equals("decompas")) {
                    declaraciónVariable();
                } else if (currentToken().valor.equals("sidiosquiere") || currentToken().valor.equals("noquiso")) {
                    sentenciaControl();
                } else {
                    throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                }
            } else {
                throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
            }
        }

        // Verifica si encontramos una llave de cierre
        if (currentToken() != null && currentToken().tipo == TipoToken.LLAVE_CIERRE) {
            consume(TipoToken.LLAVE_CIERRE); // Consume '}'
        } else {
            throw new RuntimeException("Error de sintaxis: se esperaba una llave de cierre '}'");
        }
    }

    private boolean esNumeroFlotante(String valor) {
        String regex = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";
        return valor.matches(regex);
    }

    // IF
    private void sentenciaControl() {
        if (currentToken().valor.equals("sidiosquiere")) {
            // Procesa la sentencia if
            consume(TipoToken.PALABRA_RESERVADA); // Consume 'sidiosquiere'
            consume(TipoToken.PARENTESIS_AP); // Consume '('
            consume(TipoToken.ID);
            consume(TipoToken.OPERADOR_RELACIONAL);
            consume(TipoToken.ID);
            consume(TipoToken.PARENTESIS_CIERRE); // Consume ')'
            consume(TipoToken.LLAVE_AP); // Consume '{'

            while (currentToken() != null && currentToken().tipo != TipoToken.LLAVE_CIERRE) {
                if (currentToken().tipo == TipoToken.PALABRA_RESERVADA) {
                    if (currentToken().valor.equals("entero") || currentToken().valor.equals("flota") || currentToken().valor.equals("cadena") || currentToken().valor.equals("decompas")) {
                        declaraciónVariable();
                    } else if (currentToken().valor.equals("sidiosquiere") || currentToken().valor.equals("noquiso")) {
                        sentenciaControl(); 
                    } else {
                        throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                    }
                } else {
                    throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                }
            }

            if (currentToken() != null && currentToken().tipo == TipoToken.LLAVE_CIERRE) {
                consume(TipoToken.LLAVE_CIERRE); //  '}'
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba una llave de cierre '}'");
            }
        } else if (currentToken().valor.equals("noquiso")) {
            consume(TipoToken.PALABRA_RESERVADA); 
            consume(TipoToken.LLAVE_AP); 
            
            while (currentToken() != null && currentToken().tipo != TipoToken.LLAVE_CIERRE) {
                if (currentToken().tipo == TipoToken.PALABRA_RESERVADA) {
                    if (currentToken().valor.equals("entero") || currentToken().valor.equals("flota") || currentToken().valor.equals("cadena") || currentToken().valor.equals("decompas")) {
                        declaraciónVariable(); 
                    } else if (currentToken().valor.equals("sidiosquiere") || currentToken().valor.equals("noquiso")) {
                        sentenciaControl();
                    } else {
                        throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                    }
                } else {
                    throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                }
            }
            // LLAVE DE CIERRE
            if (currentToken() != null && currentToken().tipo == TipoToken.LLAVE_CIERRE) {
                consume(TipoToken.LLAVE_CIERRE); // Consume '}'
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba una llave de cierre '}'");
            }
        } else {
            throw new RuntimeException("Error de sintaxis: sentencia de control desconocida");
        }
    }
    
    // WHILE 
    private void sentenciaControlWhile() {
        if (currentToken().valor.equals("mientras")) {
            // Procesa la sentencia while
            consume(TipoToken.PALABRA_RESERVADA); 
            consume(TipoToken.PARENTESIS_AP); // Consume '('
            consume(TipoToken.ID);
            consume(TipoToken.OPERADOR_RELACIONAL);
            consume(TipoToken.ID);
            consume(TipoToken.PARENTESIS_CIERRE); // Consume ')'
            consume(TipoToken.LLAVE_AP); // Consume '{'
            
            while (currentToken() != null && currentToken().tipo != TipoToken.LLAVE_CIERRE) {
                if (currentToken().tipo == TipoToken.PALABRA_RESERVADA) {
                    if (currentToken().valor.equals("entero") || currentToken().valor.equals("flota") || currentToken().valor.equals("cadena") || currentToken().valor.equals("decompas")) {
                        declaraciónVariable();
                    } else if (currentToken().valor.equals("sidiosquiere") || currentToken().valor.equals("noquiso")) {
                        sentenciaControl(); 
                    } else {
                        throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                    }
                } else {
                    throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                }
            }
            if (currentToken() != null && currentToken().tipo == TipoToken.LLAVE_CIERRE) {
                consume(TipoToken.LLAVE_CIERRE); // Consume '}'
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba una llave de cierre '}'");
            }
            if (currentToken() != null && currentToken().tipo != TipoToken.FIN_DE_LINEA) {
                throw new RuntimeException("Error de sintaxis: después de un 'while', no se espera más código.");
            }
        } else {
            throw new RuntimeException("Error de sintaxis: sentencia de control desconocida");
        }
    }
    
    // DO-WHILE
    private void sentenciaControlDoWhile() {
        if (currentToken().valor.equals("muevelas")) {
            consume(TipoToken.PALABRA_RESERVADA); // Consume 'muevelas' (equivalente a 'do')

            consume(TipoToken.LLAVE_AP); // Consume '{'

            // Procesa el cuerpo del do-while
            while (currentToken() != null && currentToken().tipo != TipoToken.LLAVE_CIERRE) {
                if (currentToken().tipo == TipoToken.PALABRA_RESERVADA) {
                    if (currentToken().valor.equals("entero") || currentToken().valor.equals("flota") || currentToken().valor.equals("cadena") || currentToken().valor.equals("decompas")) {
                        declaraciónVariable();
                    } else if (currentToken().valor.equals("sidiosquiere") || currentToken().valor.equals("noquiso")) {
                        sentenciaControl(); 
                    } else {
                        throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                    }
                } else {
                    throw new RuntimeException("Error de sintaxis en la línea: " + currentToken().valor);
                }
            }

            if (currentToken() != null && currentToken().tipo == TipoToken.LLAVE_CIERRE) {
                consume(TipoToken.LLAVE_CIERRE); // Consume '}'
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba una llave de cierre '}'");
            }

            consume(TipoToken.PALABRA_RESERVADA); // Consume 'mientras'
            consume(TipoToken.PARENTESIS_AP); // Consume '('
            consume(TipoToken.ID);
            consume(TipoToken.OPERADOR_RELACIONAL);
            consume(TipoToken.ID);
            consume(TipoToken.PARENTESIS_CIERRE); // Consume ')'

            if (currentToken() != null && currentToken().tipo != TipoToken.FIN_DE_LINEA) {
                throw new RuntimeException("Error de sintaxis: después de un 'do-while', no se espera más código.");
            }
        } else {
            throw new RuntimeException("Error de sintaxis: sentencia de control desconocida, se esperaba 'muevelas'");
        }
    }


}
