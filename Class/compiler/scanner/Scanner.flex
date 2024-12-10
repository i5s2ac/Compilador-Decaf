/* Scanner.flex */

package compiler.scanner;

import java_cup.runtime.Symbol;
import compiler.parser.sym;
import compiler.ast.*;

/* Declaración del scanner */
%%
%public
%class Scanner
%unicode
%cup
%function next_token
%type java_cup.runtime.Symbol

%{

/* Código de usuario */

/* Variables para el seguimiento de líneas y columnas */
private int curr_line = 1;
private int curr_column = 1;

/* Actualizar la posición actual */
private void updatePosition() {
    yyline = curr_line - 1;
    yycolumn = curr_column - 1;
}

/* Métodos para crear símbolos */
private Symbol symbol(int type) {
    debug("Token encontrado: " + sym.terminalNames[type] + " en línea " + (yyline + 1) + ", columna " + (yycolumn + 1));
    return new Symbol(type, yyline + 1, yycolumn + 1);
}

private Symbol symbol(int type, Object value) {
    debug("Token encontrado: " + sym.terminalNames[type] + " con valor '" + value + "' en línea " + (yyline + 1) + ", columna " + (yycolumn + 1));
    return new Symbol(type, yyline + 1, yycolumn + 1, value);
}

/* Método para depuración */
private void debug(String message) {
    System.out.println("[DEBUG] " + message);
}

%}

/* Declaración del estado COMMENT */
%state COMMENT

/* Definiciones de patrones */

WHITESPACE = [ \t\f]+
NEWLINE = \r\n?|\n
DIGIT = [0-9]
LETTER = [a-zA-Z]
ALPHANUM = ({LETTER}|{DIGIT})
ID = {LETTER}{ALPHANUM}*
INT_LITERAL = {DIGIT}+
HEX_LITERAL = 0[xX][0-9a-fA-F]+
CHAR_LITERAL = \' ( [^\'\\\n] | \\ [btnfr0\'\"\\] ) \'
STRING_LITERAL = \" ( [^\"\\\n] | \\ [btnfr0\'\"\\] )* \"

%%

/* Reglas del scanner */

/* Ignorar espacios en blanco */
{WHITESPACE}        { curr_column += yylength(); }

/* Manejo de nuevas líneas */
{NEWLINE}           { curr_line++; curr_column = 1; }

/* Comentarios de una línea */
"//".*              { curr_column += yylength(); }

/* Comentarios de múltiples líneas */
"/*"                { curr_column += 2; yybegin(COMMENT); }

<COMMENT>{
    "*/"            { curr_column += 2; yybegin(YYINITIAL); }
    [^*\n]+         { curr_column += yylength(); }
    "\n"            { curr_line++; curr_column = 1; }
    "*"             { curr_column += 1; }
}

/* Palabras reservadas */
"class"             { updatePosition(); curr_column += yylength(); return symbol(sym.CLASS, yytext()); }
"int"               { updatePosition(); curr_column += yylength(); return symbol(sym.INT, yytext()); }
"boolean"           { updatePosition(); curr_column += yylength(); return symbol(sym.BOOLEAN, yytext()); }
"void"              { updatePosition(); curr_column += yylength(); return symbol(sym.VOID, yytext()); }
"true"              { updatePosition(); curr_column += yylength(); return symbol(sym.TRUE, yytext()); }
"false"             { updatePosition(); curr_column += yylength(); return symbol(sym.FALSE, yytext()); }
"if"                { updatePosition(); curr_column += yylength(); return symbol(sym.IF, yytext()); }
"else"              { updatePosition(); curr_column += yylength(); return symbol(sym.ELSE, yytext()); }
"for"               { updatePosition(); curr_column += yylength(); return symbol(sym.FOR, yytext()); }
"while"             { updatePosition(); curr_column += yylength(); return symbol(sym.WHILE, yytext()); }
"return"            { updatePosition(); curr_column += yylength(); return symbol(sym.RETURN, yytext()); }
"break"             { updatePosition(); curr_column += yylength(); return symbol(sym.BREAK, yytext()); }
"continue"          { updatePosition(); curr_column += yylength(); return symbol(sym.CONTINUE, yytext()); }
"callout"           { updatePosition(); curr_column += yylength(); return symbol(sym.CALLOUT, yytext()); }
"char"              { updatePosition(); curr_column += yylength(); return symbol(sym.CHAR, yytext()); }
"new"               { updatePosition(); curr_column += yylength(); return symbol(sym.NEW, yytext()); }

/* Operadores y símbolos */
"="                 { updatePosition(); curr_column += yylength(); return symbol(sym.ASSIGN, yytext()); }
"+="                { updatePosition(); curr_column += yylength(); return symbol(sym.PLUS_ASSIGN, yytext()); }
"-="                { updatePosition(); curr_column += yylength(); return symbol(sym.MINUS_ASSIGN, yytext()); }
";"                 { updatePosition(); curr_column += yylength(); return symbol(sym.SEMI, yytext()); }
","                 { updatePosition(); curr_column += yylength(); return symbol(sym.COMMA, yytext()); }
"{"                 { updatePosition(); curr_column += yylength(); return symbol(sym.LBRACE, yytext()); }
"}"                 { updatePosition(); curr_column += yylength(); return symbol(sym.RBRACE, yytext()); }
"("                 { updatePosition(); curr_column += yylength(); return symbol(sym.LPAREN, yytext()); }
")"                 { updatePosition(); curr_column += yylength(); return symbol(sym.RPAREN, yytext()); }
"["                 { updatePosition(); curr_column += yylength(); return symbol(sym.LBRACKET, yytext()); }
"]"                 { updatePosition(); curr_column += yylength(); return symbol(sym.RBRACKET, yytext()); }

"&&"                { updatePosition(); curr_column += yylength(); return symbol(sym.AND, yytext()); }
"||"                { updatePosition(); curr_column += yylength(); return symbol(sym.OR, yytext()); }
"!"                 { updatePosition(); curr_column += yylength(); return symbol(sym.NOT, yytext()); }
"=="                { updatePosition(); curr_column += yylength(); return symbol(sym.EQ, yytext()); }
"!="                { updatePosition(); curr_column += yylength(); return symbol(sym.NEQ, yytext()); }
"<="                { updatePosition(); curr_column += yylength(); return symbol(sym.LE, yytext()); }
">="                { updatePosition(); curr_column += yylength(); return symbol(sym.GE, yytext()); }
"<"                 { updatePosition(); curr_column += yylength(); return symbol(sym.LT, yytext()); }
">"                 { updatePosition(); curr_column += yylength(); return symbol(sym.GT, yytext()); }
"+"                 { updatePosition(); curr_column += yylength(); return symbol(sym.PLUS, yytext()); }
"-"                 { updatePosition(); curr_column += yylength(); return symbol(sym.MINUS, yytext()); }
"*"                 { updatePosition(); curr_column += yylength(); return symbol(sym.TIMES, yytext()); }
"/"                 { updatePosition(); curr_column += yylength(); return symbol(sym.DIVIDE, yytext()); }
"%"                 { updatePosition(); curr_column += yylength(); return symbol(sym.MOD, yytext()); }

/* Identificadores y literales */
{ID}                { updatePosition(); curr_column += yylength(); return symbol(sym.ID, yytext()); }

{HEX_LITERAL}       { updatePosition(); curr_column += yylength(); return symbol(sym.INT_LITERAL, yytext()); }

{INT_LITERAL}  { 
    updatePosition(); 
    curr_column += yylength(); 
    return symbol(sym.INT_LITERAL, Integer.parseInt(yytext())); 
}

{CHAR_LITERAL}      { updatePosition(); curr_column += yylength(); return symbol(sym.CHAR_LITERAL, yytext()); }

{STRING_LITERAL}    { updatePosition(); curr_column += yylength(); return symbol(sym.STRING_LITERAL, yytext()); }

/* Manejo de caracteres ilegales */
.                   {
    System.err.println("Caracter ilegal: " + yytext() + " en línea " + curr_line + ", columna " + curr_column);
    curr_column += yylength();
}