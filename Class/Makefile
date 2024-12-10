# Objetivos principales
run-todo: limpiar crear-bin generar-parser generar-scanner compilar-todo ejecutar-todo
run-escaneo: limpiar crear-bin generar-scanner compilar-escaneo ejecutar-escaneo
run-parsing: limpiar crear-bin generar-parser generar-scanner compilar-parsing ejecutar-parsing
run-ir: limpiar crear-bin generar-parser generar-scanner compilar-ir ejecutar-ir

# Limpieza y preparación
limpiar:
	rm -rf bin
	rm -f compiler/parser/Parser.java compiler/parser/sym.java compiler/scanner/Scanner.java output.txt output.ir

crear-bin:
	mkdir -p bin

# Generación de Parser y Scanner
generar-parser:
	java -jar compiler/lib/java-cup-11b.jar -parser Parser -symbols sym -package compiler.parser -destdir compiler/parser compiler/parser/Parser.cup

generar-scanner:
	java -jar compiler/lib/jflex-full-1.9.1.jar -d compiler/scanner/ compiler/scanner/Scanner.flex

# Compilación
compilar-todo:
	javac -cp bin:compiler/lib/java-cup-11b.jar:. -d bin \
		compiler/ast/*.java \
		compiler/irt/*.java \
		compiler/scanner/Scanner.java \
		compiler/parser/Parser.java \
		compiler/semantic/*.java \
		compiler/Compiler.java

compilar-escaneo:
	javac -cp bin:compiler/lib/java-cup-11b.jar:. -d bin \
		compiler/ast/*.java \
		compiler/scanner/Scanner.java \
		compiler/Compiler.java

compilar-parsing:
	javac -cp bin:compiler/lib/java-cup-11b.jar:. -d bin \
		compiler/ast/*.java \
		compiler/scanner/Scanner.java \
		compiler/parser/Parser.java \
		compiler/Compiler.java

compilar-ir:
	javac -cp bin:compiler/lib/java-cup-11b.jar:. -d bin \
		compiler/ast/*.java \
		compiler/irt/*.java \
		compiler/scanner/Scanner.java \
		compiler/parser/Parser.java \
		compiler/semantic/*.java \
		compiler/Compiler.java

# Ejecución
ejecutar-todo:
	java -cp bin:compiler/lib/java-cup-11b.jar:. compiler.Compiler input.txt -o output.txt

ejecutar-escaneo:
	java -cp bin:compiler/lib/java-cup-11b.jar:. compiler.Compiler -target scan input.txt -o output.txt

ejecutar-parsing:
	java -cp bin:compiler/lib/java-cup-11b.jar:. compiler.Compiler -target parse input.txt -o output.txt
	java -cp bin:compiler/lib/java-cup-11b.jar:. compiler.Compiler -target dot input.txt -o output.dot

ejecutar-ir:
	java -cp bin:compiler/lib/java-cup-11b.jar:. compiler.Compiler -target ir input.txt -o output.ir

generar-dot:
	dot -Tpdf output.dot -o output.pdf

generar-dot-IR:
	dot -Tpdf output.ir.dot -o output.ir.pdf

# Objetivos de debug
debug-ir: compilar-ir
	java -cp bin:compiler/lib/java-cup-11b.jar:. compiler.Compiler -target ir input.txt -o output.ir -debug

# Objetivo para tests
test-ir: compilar-ir
	@echo "Probando generación de IR..."
	@echo "class Program { void main() { int x; x = 42; } }" > test.txt
	java -cp bin:compiler/lib/java-cup-11b.jar:. compiler.Compiler -target ir test.txt -o test.ir
	@cat test.ir
	@rm test.txt test.ir

# Objetivo para limpiar archivos temporales
clean-temp:
	rm -f *.ir *.dot *.pdf test.txt