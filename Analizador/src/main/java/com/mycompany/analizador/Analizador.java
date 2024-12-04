
package com.mycompany.analizador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Analizador {

    // Palabras reservadas y operadores del lenguaje
    /* wacha-imprimir   palabanda-public    decompas-bool   rola-scanner    void-void   yabasta-break   muevelas-do
    noquiso-else    porcada-for     sidiosquiere-if     quietoprieto-static     mientras-while  si-true no-false    
    */
    private static final Set<String> PALABRAS_CLAVE = new HashSet<>(Arrays.asList(
            "entero", "flota", "decompas", "cadena",
            "final", "sidiosquiere", "noquiso", "opcion", 
            "situazion", "yabasta", "mientras", "muevelas", "porcada",
            "palabanda", "void", "wacha", "rola", "si", "no"
    ));
    private static final Set<String> PALABRAS_BOOL = new HashSet<>(Arrays.asList(
            "si", "no"
    ));
    private static final Map<String, String> OPERADOR_ARITMETICO = new HashMap<>();
    static {
        OPERADOR_ARITMETICO.put("+", "Suma");
        OPERADOR_ARITMETICO.put("-", "Resta");
        OPERADOR_ARITMETICO.put("*", "Multiplicación");
        OPERADOR_ARITMETICO.put("/", "División");
    }
    private static final Set<String> OPERADOR_RELACIONAL = new HashSet<>(Arrays.asList(
            ">", "<", "==", ">=", "<=", "!="
    ));
    private static final Set<String> OPERADOR_LOGICO = new HashSet<>(Arrays.asList(
             "!"
    ));
    private static final Set<String> OPERADOR_INC_DEC = new HashSet<>(Arrays.asList(
            "++", "--"
    ));


    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/danielaperez/NetBeansProjects/lexico/src/main/resources/archivo.txt"))) {
            String linea;
            int numLinea = 1;
            while ((linea = br.readLine()) != null) {
                System.out.println("Línea " + numLinea + ": " + linea);
                analizarLinea(linea, numLinea);
                numLinea++;
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
    
    private static void analizarLinea(String linea, int numLinea) {
        List<Token> tokens = analizarLexico(linea);
        if (!tokens.isEmpty()) {
            for (Token token : tokens) {
                Parser parser = new Parser(tokens);
                parser.parse();
                System.out.println("Token: |" + token.valor + "|  tipo: " + token.tipo + "  línea: " + numLinea);
            }
            System.out.println("");
        }
    }


    // Analizador Léxico (genera tokens)
    /**/
    private static List<Token> analizarLexico(String linea) {
        List<Token> tokens = new ArrayList<>();
        String tokenActual = "";
        for (int i = 0; i < linea.length(); i++) {
            char caracter = linea.charAt(i);
            if (Character.isWhitespace(caracter)) {
                if (!tokenActual.isEmpty()) {
                    tokens.add(new Token(tokenActual, clasificarToken(tokenActual)));
                    tokenActual = "";
                }
            }
            else if (caracter == '"' && tokenActual.isEmpty()) { // Comienza una cadena
                tokenActual += caracter;
                while (++i < linea.length() && linea.charAt(i) != '"') {
                    tokenActual += linea.charAt(i);
                }
                tokenActual += linea.charAt(i); // Agrega la " de cierre
                tokens.add(new Token(tokenActual, TipoToken.CADENA));
                tokenActual = "";
            } 
            /*
            else if (caracter == '"' && tokenActual.isEmpty()) { // Comienza una cadena
                tokenActual += caracter; // Añadimos la primera comilla
                while (++i < linea.length() && linea.charAt(i) != '"') {
                    tokenActual += linea.charAt(i); // Añade los caracteres dentro de la cadena
                }
                if (i < linea.length() && linea.charAt(i) == '"') {
                    tokenActual += linea.charAt(i); // Añade la comilla de cierre
                }
                tokens.add(new Token(tokenActual, TipoToken.CADENA)); // Crea el token de tipo cadena
                tokenActual = ""; // Reiniciar tokenActual para siguiente token
            }*/
            
            else if (caracter == '#' && tokenActual.isEmpty()) {
                tokens.add(new Token("#", TipoToken.FIN_DE_LINEA));
            } else if (caracter == '=' && tokenActual.isEmpty()) {
                tokens.add(new Token("=", TipoToken.IGUAL));
            } else if (caracter == '(' && tokenActual.isEmpty()) {
                tokens.add(new Token("(", TipoToken.PARENTESIS_AP ));
            } else if (caracter == ')' && tokenActual.isEmpty()) {
                tokens.add(new Token(")", TipoToken.PARENTESIS_CIERRE));
            } else if (caracter == '{' && tokenActual.isEmpty()) {
                tokens.add(new Token("{", TipoToken.LLAVE_AP));
            } else if (caracter == '}' && tokenActual.isEmpty()) {
                tokens.add(new Token("}", TipoToken.LLAVE_CIERRE));
            } else {
                tokenActual += caracter;
            }
        }
        if (!tokenActual.isEmpty()) {
            tokens.add(new Token(tokenActual, clasificarToken(tokenActual)));
        }
        return tokens;
    }

    private static TipoToken clasificarToken(String token) {
        if (PALABRAS_CLAVE.contains(token)) {
            return TipoToken.PALABRA_RESERVADA;
        } else if (OPERADOR_ARITMETICO.containsKey(token)) {
            return TipoToken.OPERADOR_ARITMETICO;
        } else if (OPERADOR_RELACIONAL.contains(token)) {
            return TipoToken.OPERADOR_RELACIONAL;
        } else if (OPERADOR_LOGICO.contains(token)) {
            return TipoToken.OPERADOR_LOGICO;
        } else if (OPERADOR_INC_DEC.contains(token)) {
            return TipoToken.OPERADOR_INC_DEC;
        } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return TipoToken.ID;
        } else if (token.matches("\\d+")) {
            return TipoToken.NUMERO;
        } 
        else if (token.matches("^[a-zA-Z]+$")){
            return TipoToken.CADENA;
        }
        else if (token.matches("\\d+\\.\\d*")) {
            return TipoToken.NUMERO_F;
        }
        else if (token.equals("si")) {
            return TipoToken.PALABRAS_BOOL;  // Cambiar aquí para detectar 'si' y 'no'
        }
        else if (token.equals("no")) {
            return TipoToken.PALABRAS_BOOL;  // Cambiar aquí para detectar 'si' y 'no'
        }
        else if (token.equals("#")) {
            return TipoToken.FIN_DE_LINEA;
        }
        else if (token.equals("\"")) {  // Comillas dobles
            return TipoToken.COMILLA_DOBLE;
        }
        return TipoToken.DESCONOCIDO;
    }
}


class Token {
    String valor;
    TipoToken tipo;

    Token(String valor, TipoToken tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }
}

// Enumeración para los tipos de token


// Clase para el parser
