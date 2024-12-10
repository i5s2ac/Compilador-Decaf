package compiler;

import compiler.scanner.Scanner;
import compiler.parser.sym;
import compiler.parser.Parser;
import compiler.ast.Program;
import compiler.ast.ASTPrinter;
import compiler.ast.ASTDotGenerator;
import compiler.semantic.SemanticAnalyzer; 
import java_cup.runtime.Symbol;
import compiler.irt.*;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.List;

public class Compiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            printHelp();
            System.exit(1);
        }

        String filename = "";
        String output = "output.txt";
        String target = "codegen"; // Default target
        boolean debug = false;

        // Process arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if (i + 1 < args.length) {
                        output = args[++i];
                    } else {
                        System.err.println("Error: Expected filename after -o.");
                        printHelp();
                        System.exit(1);
                    }
                    break;
                case "-target":
                    if (i + 1 < args.length) {
                        target = args[++i];
                    } else {
                        System.err.println("Error: Expected target after -target.");
                        printHelp();
                        System.exit(1);
                    }
                    break;
                case "-debug":
                    debug = true;
                    break;
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                default:
                    filename = args[i];
            }
        }

        if (filename.isEmpty()) {
            System.err.println("Error: No input file specified.");
            printHelp();
            System.exit(1);
        }

        try {
            switch (target) {
                case "scan":
                    runScan(filename, output, debug);
                    break;
                case "parse":
                    runParse(filename, output, debug);
                    break;
                case "dot":
                    runDot(filename, output, debug);
                    break;
                case "ir":  // New case for IR
                    runIR(filename, output, debug);
                    break;
                default:
                    System.err.println("Unknown target: " + target);
                    printHelp();
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para ejecutar el análisis sintáctico y semántico, y generar el AST.
     *
     * @param filename Archivo de entrada.
     * @param output   Archivo de salida.
     * @param debug    Bandera para activar el modo debug.
     * @throws IOException Si ocurre un error de E/S.
     */
    private static void runParse(String filename, String output, boolean debug) throws IOException {
        // Crear el archivo de salida para el parsing normal
        try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
            // Indicar el inicio de la etapa de parsing
            writer.println("stage: parsing");
            System.out.println("stage: parsing");

            // Inicializar Scanner y Parser
            Scanner scanner = new Scanner(new FileReader(filename));
            Parser parser = new Parser(scanner);

            // Realizar el parsing
            Symbol result = parser.parse();

            // Verificar si el parsing resultó en un AST válido
            if (result == null || result.value == null) {
                writer.println("Error: No se pudo generar el AST.");
                if (debug) {
                    System.err.println("Error: No se pudo generar el AST.");
                }
                return;
            }

            // Obtener el nodo raíz del AST
            Program program = (Program) result.value;

            // Confirmar que el parsing fue exitoso
            writer.println("Parsing completed successfully.");
            if (debug) {
                System.out.println("Debug: Parsing completed successfully.");
            }

            // Realizar el análisis semántico
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            program.accept(semanticAnalyzer);

            List<String> semanticErrors = semanticAnalyzer.getErrors();
            if (!semanticErrors.isEmpty()) {
                writer.println("Errores semánticos encontrados:");
                System.err.println("Errores semánticos encontrados:");
                for (String error : semanticErrors) {
                    writer.println(error);
                    System.err.println(error);
                }
                // Terminar si hay errores semánticos
                return;
            } 

            // Indicar el inicio de la impresión del AST
            writer.println("AST:");
            ASTPrinter printer = new ASTPrinter(writer);

            // Recorrer el AST y generar la representación
            program.accept(printer);
            writer.println(); // Añadir una línea en blanco al final

            // Generar el archivo DOT
            String dotFile = output.substring(0, output.lastIndexOf('.')) + ".dot";
            try (PrintWriter dotWriter = new PrintWriter(new FileWriter(dotFile))) {
                ASTDotGenerator dotGenerator = new ASTDotGenerator(dotWriter);
                dotGenerator.beginGraph();
                program.accept(dotGenerator);
                dotGenerator.endGraph();
                System.out.println("Archivo DOT generado exitosamente en " + dotFile);

                // Generar PDF automáticamente
                String pdfFile = dotFile.replaceAll("\\.dot$", "") + ".pdf";
                try {
                    // Usar el comando 'dot' del sistema
                    ProcessBuilder pb = new ProcessBuilder("dot", "-Tpdf", dotFile, "-o", pdfFile);

                    // Redirigir el error estándar al output estándar
                    pb.redirectErrorStream(true);

                    // Ejecutar el proceso
                    Process process = pb.start();

                    // Esperar a que termine el proceso
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        System.out.println("PDF generado exitosamente en " + pdfFile);
                    } else {
                        // Leer la salida del proceso para obtener más detalles del error
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()))) {
                            String line;
                            StringBuilder error = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                error.append(line).append("\n");
                            }
                            System.err.println("Error generando PDF. Código de salida: " + exitCode);
                            if (debug) {
                                System.err.println("Detalles del error:\n" + error.toString());
                            }
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    System.err.println("Error generando PDF: " + e.getMessage());
                    if (debug) {
                        e.printStackTrace();
                    }
                }
            }

            if (debug) {
                System.out.println("Debug: AST generado correctamente en " + output);
            }

            // Mensaje de éxito
            System.out.println("Parsing y análisis semántico completados exitosamente. AST generado en " + output);
        } catch (Exception e) {
            System.err.println("Error durante el parsing: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para ejecutar el análisis léxico (scanning).
     *
     * @param filename Archivo de entrada.
     * @param output   Archivo de salida.
     * @param debug    Bandera para activar el modo debug.
     * @throws IOException Si ocurre un error de E/S.
     */
    private static void runScan(String filename, String output, boolean debug) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
            // Indicar el inicio de la etapa de scanning
            writer.println("stage: scanning");
            System.out.println("stage: scanning");

            try (FileReader fileReader = new FileReader(filename)) {
                Scanner scanner = new Scanner(fileReader);
                while (!scanner.yyatEOF()) {
                    Symbol token = scanner.next_token();
                    if (token.sym == sym.EOF) break;

                    String nombreToken = getTokenName(token.sym);
                    String valorToken = (token.value != null) ? token.value.toString() : "N/A";
                    String tipoToken = esReservada(token.sym) ? "reservada" : "no reservada";

                    writer.printf("Token: %s | Valor: %s | Línea: %d | Columna: %d | Tipo: %s%n",
                            nombreToken, valorToken, token.left + 1, token.right + 1, tipoToken);

                    if (debug) {
                        System.out.printf("Token: %s | Valor: %s | Línea: %d | Columna: %d | Tipo: %s%n",
                                nombreToken, valorToken, token.left + 1, token.right + 1, tipoToken);
                    }
                }
            } catch (Exception e) {
                writer.println("Error durante el análisis de escaneo: " + e.getMessage());
                System.err.println("Error durante el análisis de escaneo: " + e.getMessage());
                if (debug) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Método para ejecutar la generación del archivo DOT y PDF.
     *
     * @param filename Archivo de entrada.
     * @param output   Archivo de salida.
     * @param debug    Bandera para activar el modo debug.
     * @throws IOException Si ocurre un error de E/S.
     */
    private static void runDot(String filename, String output, boolean debug) throws IOException {
        String dotFile = output;
        String pdfFile = output.replaceAll("\\.dot$", "") + ".pdf";

        try {
            // Inicializar Scanner y Parser
            Scanner scanner = new Scanner(new FileReader(filename));
            Parser parser = new Parser(scanner);

            // Realizar el parsing
            Symbol result = parser.parse();

            if (result != null && result.value != null) {
                Program program = (Program) result.value;

                // Realizar el análisis semántico
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
                program.accept(semanticAnalyzer);

                List<String> semanticErrors = semanticAnalyzer.getErrors();
                if (!semanticErrors.isEmpty()) {
                    System.err.println("Errores semánticos encontrados:");
                    for (String error : semanticErrors) {
                        System.err.println(error);
                    }
                    // Terminar si hay errores semánticos
                    return;
                } else {
                    if (debug) {
                        System.out.println("Debug: Análisis semántico completado sin errores.");
                    }
                }

                // Crear el generador DOT y escribir únicamente la sintaxis DOT
                try (PrintWriter writer = new PrintWriter(new FileWriter(dotFile))) {
                    ASTDotGenerator dotGenerator = new ASTDotGenerator(writer);

                    // Generar el archivo DOT
                    dotGenerator.beginGraph();
                    program.accept(dotGenerator);
                    dotGenerator.endGraph();
                }

                // Obtener rutas absolutas para facilitar la localización
                File dotFileObj = new File(dotFile);
                File pdfFileObj = new File(pdfFile);
                String dotFilePath = dotFileObj.getAbsolutePath();
                String pdfFilePath = pdfFileObj.getAbsolutePath();

                System.out.println("Archivo DOT generado exitosamente en " + dotFilePath);

                // Generar PDF automáticamente
                try {
                    // Usar el comando 'dot' del sistema
                    ProcessBuilder pb = new ProcessBuilder("dot", "-Tpdf", dotFilePath, "-o", pdfFilePath);

                    // Redirigir el error estándar al output estándar
                    pb.redirectErrorStream(true);

                    // Imprimir información de depuración
                    if (debug) {
                        System.out.println("Ejecutando comando: dot -Tpdf " + dotFilePath + " -o " + pdfFilePath);
                    }

                    // Ejecutar el proceso
                    Process process = pb.start();

                    // Leer la salida del proceso
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    StringBuilder outputBuilder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        outputBuilder.append(line).append("\n");
                    }

                    // Esperar a que termine el proceso
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        System.out.println("PDF generado exitosamente en " + pdfFilePath);
                    } else {
                        System.err.println("Error generando PDF. Código de salida: " + exitCode);
                        System.err.println("Salida del comando 'dot':\n" + outputBuilder.toString());
                    }
                } catch (IOException | InterruptedException e) {
                    System.err.println("Error generando PDF: " + e.getMessage());
                    if (debug) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("Error: No se pudo generar el AST.");
                if (debug) {
                    System.err.println("Error: No se pudo generar el AST.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error generando el archivo DOT/PDF: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Método para determinar si un token es una palabra reservada.
     *
     * @param tokenSym Símbolo del token.
     * @return true si es reservada, false en caso contrario.
     */
    private static boolean esReservada(int tokenSym) {
        switch (tokenSym) {
            case sym.CLASS:
            case sym.INT:
            case sym.BOOLEAN:
            case sym.VOID:
            case sym.TRUE:
            case sym.FALSE:
            case sym.IF:
            case sym.ELSE:
            case sym.FOR:
            case sym.WHILE:
            case sym.RETURN:
            case sym.BREAK:
            case sym.CONTINUE:
            case sym.CALLOUT:
            case sym.CHAR:
            case sym.NEW:
            case sym.ASSIGN:
            case sym.PLUS_ASSIGN:
            case sym.MINUS_ASSIGN:
            case sym.SEMI:
            case sym.COMMA:
            case sym.LBRACE:
            case sym.RBRACE:
            case sym.LPAREN:
            case sym.RPAREN:
            case sym.LBRACKET:
            case sym.RBRACKET:
            case sym.AND:
            case sym.OR:
            case sym.NOT:
            case sym.EQ:
            case sym.NEQ:
            case sym.LE:
            case sym.GE:
            case sym.LT:
            case sym.GT:
            case sym.PLUS:
            case sym.MINUS:
            case sym.TIMES:
            case sym.DIVIDE:
            case sym.MOD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Método para obtener el nombre de un token a partir de su símbolo.
     *
     * @param tokenSym Símbolo del token.
     * @return Nombre del token o "UNKNOWN" si no se encuentra.
     */
    private static String getTokenName(int tokenSym) {
        try {
            Field[] fields = sym.class.getFields();
            for (Field field : fields) {
                if (field.getType() == int.class && field.getInt(null) == tokenSym) {
                    return field.getName();
                }
            }
        } catch (IllegalAccessException e) {
            // Manejar excepción si es necesario
        }
        return "UNKNOWN";
    }

    // Añade el nuevo método runIR:
        private static void runIR(String filename, String output, boolean debug) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
            // Indicate the start of the IR generation stage
            writer.println("stage: IR generation");
            System.out.println("stage: IR generation");

            // Initialize Scanner and Parser
            Scanner scanner = new Scanner(new FileReader(filename));
            Parser parser = new Parser(scanner);

            // Perform parsing
            Symbol result = parser.parse();

            if (result == null || result.value == null) {
                writer.println("Error: Could not generate AST.");
                return;
            }

            // Get the AST
            Program program = (Program) result.value;

            // Perform semantic analysis
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            program.accept(semanticAnalyzer);

            List<String> semanticErrors = semanticAnalyzer.getErrors();
            if (!semanticErrors.isEmpty()) {
                writer.println("Semantic errors found:");
                for (String error : semanticErrors) {
                    writer.println(error);
                }
                return;
            }

            // Generate IR
            IRTreeGenerator irGenerator = new IRTreeGenerator();
            IRResult irResult = irGenerator.generateAndOptimize(program);

            if (irResult.errors.isEmpty()) {
                // Print the generated IR
                if (irResult.ir != null) {
                    writer.println(irResult.ir.toString());
                } else {
                    writer.println("Error: The generated IR is null.");
                }

            } else {
                writer.println("Errors during IR generation:");
                for (String error : irResult.errors) {
                    writer.println(error);
                }
            }

            // Print the tree structure using IRTPrinter
                IRTPrinter printer = new IRTPrinter();
                irResult.ir.accept(printer, ""); 

            String dotFile = output.replaceAll("\\.txt$", "") + ".dot";
            try (PrintWriter dotWriter = new PrintWriter(new FileWriter(dotFile))) {
                IRDotGenerator dotGenerator = new IRDotGenerator(dotWriter);
                dotGenerator.beginGraph();
                irResult.ir.accept(dotGenerator, "");
                dotGenerator.endGraph();
                System.out.println("Archivo DOT generado: " + dotFile);
            }

            String pdfFile = dotFile.replaceAll("\\.dot$", "") + ".pdf";
            try {
                ProcessBuilder pb = new ProcessBuilder("dot", "-Tpdf", dotFile, "-o", pdfFile);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("PDF generado exitosamente en: " + pdfFile);
                } else {
                    System.err.println("Error generando PDF. Código de salida: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error al convertir DOT a PDF: " + e.getMessage());
            }


        } catch (Exception e) {
            System.err.println("Error during IR generation: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Método para imprimir la ayuda y uso del compilador.
     */
    private static void printHelp() {
        System.out.println("Usage: java compiler.Compiler [option] <filename>");
        System.out.println("-o <outname>: Specifies the output filename.");
        System.out.println("-target <stage>: scan, parse, dot, ir.");
        System.out.println("-debug: Enables debug mode.");
        System.out.println("-h: Shows this help message.");
    }
}
