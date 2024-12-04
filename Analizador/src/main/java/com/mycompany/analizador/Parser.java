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
                else if (currentToken().valor.equals("porcada")) {
                    sentenciaControlFor();
                }
                else if (currentToken().valor.equals("final")) {
                    declaracionConstante();
                }
                else if (currentToken().valor.equals("wacha")) {
                    declaracionRecibir();
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
    private void declaracionConstante() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume 'final'

        // Determine the constant type
        Token tipoToken = currentToken();
        if (tipoToken.valor.equals("entero") || 
            tipoToken.valor.equals("flota") || 
            tipoToken.valor.equals("cadena") || 
            tipoToken.valor.equals("decompas")) {
            consume(TipoToken.PALABRA_RESERVADA); // Consume the data type
        } else {
            throw new RuntimeException("Error de sintaxis: tipo de constante no válido");
        }

        consume(TipoToken.ID); // Consume the constant identifier
        consume(TipoToken.IGUAL); // Consume '='

        // Validate the value based on the constant type
        if (tipoToken.valor.equals("entero")) {
            // For integer constants
            if (currentToken().tipo == TipoToken.NUMERO) {
                consume(TipoToken.NUMERO);
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba un número entero para la constante");
            }
        } else if (tipoToken.valor.equals("flota")) {
            // For float constants
            if (currentToken().tipo == TipoToken.NUMERO_F) {
                consume(TipoToken.NUMERO_F);
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba un número flotante para la constante");
            }
        } else if (tipoToken.valor.equals("cadena")) {
            // For string constants
            consume(TipoToken.COMILLA_DOBLE); // First double quote
            if (currentToken().tipo == TipoToken.CADENA) {
                consume(TipoToken.CADENA);
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba una cadena para la constante");
            }
            consume(TipoToken.COMILLA_DOBLE); // Closing double quote
        } else if (tipoToken.valor.equals("decompas")) {
            // For boolean constants
            if (currentToken().tipo == TipoToken.PALABRAS_BOOL && 
                (currentToken().valor.equals("si") || currentToken().valor.equals("no"))) {
                consume(TipoToken.PALABRAS_BOOL);
            } else {
                throw new RuntimeException("Error de sintaxis: se esperaba 'si' o 'no' para la constante booleana");
            }
        }

        consume(TipoToken.FIN_DE_LINEA); // Consume '#'
    }
    private void declaracionRecibir() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume 'wacha'

        // Determine the type of data being received
        Token tipoToken = currentToken();
        if (tipoToken.valor.equals("entero") || 
            tipoToken.valor.equals("flota") || 
            tipoToken.valor.equals("cadena") || 
            tipoToken.valor.equals("decompas")) {
            consume(TipoToken.PALABRA_RESERVADA); // Consume the data type
        } else {
            throw new RuntimeException("Error de sintaxis: tipo de dato no válido para wacha");
        }

        // Consume the 'rola' identifier
        consume(TipoToken.PUNTO);
        consume(TipoToken.ID);

        // Consume the end of line marker
        consume(TipoToken.FIN_DE_LINEA);
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
        
        tokenActual = currentToken();
        if (tokenActual != null && tokenActual.tipo == TipoToken.OPERADOR_ARITMETICO) {
            consume(TipoToken.OPERADOR_ARITMETICO); // Consume el operador
            
            // Consumir el segundo valor
            tokenActual = currentToken();
            if (tokenActual.tipo == TipoToken.NUMERO_F) {
                consume(TipoToken.NUMERO_F); // Consume número flotante
            } 
            else if (tokenActual.tipo == TipoToken.NUMERO) {
                consume(TipoToken.NUMERO); // Consume número entero
            }
            else {
                throw new RuntimeException("Error de sintaxis: se esperaba un segundo valor después del operador aritmético");
            }
        }
    }


    private void funcion() {
        consume(TipoToken.PALABRA_RESERVADA); // Consume 'palabanda'
        consume(TipoToken.PALABRA_RESERVADA); // Consume 'void'
        consume(TipoToken.ID); // Consume el identificador (nombre de la función)
        consume(TipoToken.PARENTESIS_AP); // Consume '('
        consume(TipoToken.PARENTESIS_CIERRE); // Consume ')'
        consume(TipoToken.LLAVE_AP); // Consume '{'

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

            // Verificamos la condición después de ejecutar el bloque
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

    // FOR
    private void sentenciaControlFor() {
        if (currentToken().valor.equals("porcada")) {
            consume(TipoToken.PALABRA_RESERVADA); // Consume 'porcada'
            consume(TipoToken.PARENTESIS_AP);
            consume(TipoToken.PALABRA_RESERVADA); // Consume 'entero'
            consume(TipoToken.ID); // Consume la variable (x)
            consume(TipoToken.IGUAL); // Consume '='
            valor(); 
            consume(TipoToken.FIN_DE_LINEA);
            
            consume(TipoToken.ID); 
            consume(TipoToken.OPERADOR_RELACIONAL); 
            valor();
            consume(TipoToken.FIN_DE_LINEA);

            consume(TipoToken.ID ); 
            consume(TipoToken.OPERADOR_INC_DEC);
            consume(TipoToken.PARENTESIS_CIERRE);
            
            consume(TipoToken.LLAVE_AP); // Consume '{'

            // Procesamos el cuerpo del for
            while (currentToken() != null && currentToken().tipo != TipoToken.LLAVE_CIERRE) {
                if (currentToken().tipo == TipoToken.PALABRA_RESERVADA) {
                    // Si encontramos una palabra reservada, procesamos como declaración de variables o sentencias
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
        } else {
            throw new RuntimeException("Error de sintaxis: sentencia de control desconocida, se esperaba 'porcada'");
        }
    }

    
}
