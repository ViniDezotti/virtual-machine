package com.example.virtualmachine.handlers;

import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class CodeRunner {
    private final List<String> code;
    private List<String> memory;
    private int stackPointer;
    private int programCounter;
    private Map<String, Integer> map;


    public CodeRunner(String objFile) {
        this.code = saveObjFile(objFile);
        this.memory = new Stack<>();
        this.stackPointer = -1;
        this.programCounter = 0;
        this.map = new HashMap<>();

        allocMemory(100);
        changArgForLine();
        execute();
    }

    private List<String> saveObjFile(String objFile) {
        File file = new File(objFile);
        List<String> code;

        try {
            code = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return code;
    }

    public void execute() {
        String instruction = "";
        String arg1 = "";
        String arg2 = "";
        String line = "";
        boolean pcNotChanged = true;

        while (programCounter < code.size()) {
            try {
                line = code.get(programCounter);

                instruction = line.substring(4, 11).strip();
                arg1 = line.substring(12, 15).strip();
                arg2 = line.substring(16, 19).strip();
            } catch (StringIndexOutOfBoundsException ex) {

            }

            switch (instruction) {
                case "LDC" -> {
                    stackPointer++;
                    memory.set(stackPointer, arg1);
                }
                case "LDV" -> {
                    String value = memory.get(Integer.parseInt(arg1));

                    stackPointer++;
                    memory.set(stackPointer, value);
                }
                case "ADD" -> {
                    resolveArithmetic("+");
                }
                case "SUB" -> {
                    resolveArithmetic("-");
                }
                case "MULT" -> {
                    resolveArithmetic("*");
                }
                case "DIVI" -> {
                    resolveArithmetic("/");
                }
                case "AND" -> {
                    resolveArithmetic("&");
                }
                case "OR" -> {
                    resolveArithmetic("|");
                }
                case "INV" -> {
                    int value = - Integer.parseInt(memory.get(stackPointer));
                    memory.set(stackPointer, String.valueOf(value));
                }
                case "NEG" -> {
                    int value = 1 - Integer.parseInt(memory.get(stackPointer));
                    memory.set(stackPointer, String.valueOf(value));
                }
                case "CME" -> {
                    resolveExpression("<");
                }
                case "CMA" -> {
                    resolveExpression(">");
                }
                case "CEQ" -> {
                    resolveExpression("==");
                }
                case "CDIF" -> {
                    resolveExpression("!=");
                }
                case "CMEQ" -> {
                    resolveExpression("<=");
                }
                case "CMAQ" -> {
                    resolveExpression(">=");
                }
                case "STR" -> {
                    String value = memory.get(stackPointer);

                    memory.set(Integer.parseInt(arg1), value);
                    stackPointer--;
                }
                case "JMP" -> {
                    programCounter = map.get(arg1);
                    pcNotChanged = false;
                }
                case "JMPF" -> {
                    if (Objects.equals(memory.get(stackPointer), "0")) {
                        programCounter = map.get(arg1);
                        pcNotChanged = false;
                    }
                    stackPointer--;
                }
                case "NULL" -> {

                }
                case "RD" -> {
                    stackPointer++;
                    //TODO memory.push(entrada)
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Leia (" + code.get(programCounter+1).substring(12, 16) + "): ");
                    memory.set(stackPointer, scanner.nextLine());
                }
                case "PRN" -> {
                    //TODO imprimir(memory.pop);
                    System.out.println("Print: " + memory.get(stackPointer));
                    stackPointer--;
                }
                case "START" -> {
                    this.stackPointer = -1;
                }
                case "ALLOC" -> {
                    int memoryOffset = Integer.parseInt(arg1);
                    int varAmount = Integer.parseInt(arg2);
                    String value;

                    for (int k = 0; k < varAmount; k++) {
                        stackPointer++;
                        value = memory.get(memoryOffset + k);
                        memory.set(stackPointer, value);
                    }
                }
                case "DALLOC" -> {
                    int memoryOffset = Integer.parseInt(arg1);
                    int varAmount = Integer.parseInt(arg2);
                    String value;

                    for (int k = varAmount; k > 0; k--) {
                        value = memory.get(stackPointer);
                        memory.set(memoryOffset + k, value);
                        stackPointer--;
                    }
                }
                case "CALL" -> {
                    String value = String.valueOf(programCounter + 1);

                    stackPointer++;
                    memory.set(stackPointer, value);
                    programCounter = map.get(arg1);
                    pcNotChanged = false;
                }
                case "RETURN" -> {
                    programCounter = Integer.parseInt(memory.get(stackPointer));
                    pcNotChanged = false;
                    stackPointer--;
                }
                case "HLT" -> {
                    return;
                }
            }

            if (pcNotChanged) {
                programCounter++;
            } else {
                pcNotChanged = true;
            }

            System.out.println("Pilha: " + memory);
            System.out.println("Instrução: " + line);
            System.out.println("SP: " + stackPointer);
            System.out.println("");
        }
    }

    private void resolveExpression(String s) {
        int rightValue = Integer.parseInt(memory.pop());
        int leftValue = Integer.parseInt(memory.pop());

        switch (s) {
            case "<" -> {
                if (leftValue < rightValue) memory.push("1");
                else memory.push("0");
            }
            case ">" -> {
                if (leftValue > rightValue) memory.push("1");
                else memory.push("0");
            }
            case "==" -> {
                if (leftValue == rightValue) memory.push("1");
                else memory.push("0");
            }
            case "!=" -> {
                if (leftValue != rightValue) memory.push("1");
                else memory.push("0");
            }
            case "<=" -> {
                if (leftValue <= rightValue) memory.push("1");
                else memory.push("0");
            }
            case ">=" -> {
                if (leftValue >= rightValue) memory.push("1");
                else memory.push("0");
            }
        }

        stackPointer--;
    }

    private void resolveArithmetic(String s) {
        int value1 = Integer.parseInt(memory.get(stackPointer - 1));
        int value2 = Integer.parseInt(memory.get(stackPointer));

        switch (s) {
            case "+" -> {
                memory.set(stackPointer - 1, String.valueOf(value1 + value2));
            }
            case "-" -> {
                memory.set(stackPointer - 1, String.valueOf(value1 - value2));
            }
            case "*" -> {
                memory.set(stackPointer - 1, String.valueOf(value1 * value2));
            }
            case "/" -> {
                memory.set(stackPointer - 1, String.valueOf(value1 / value2));
            }
            case "&" -> {
                memory.set(stackPointer - 1, String.valueOf(value1 & value2));
            }
            case "|" -> {
                memory.set(stackPointer - 1, String.valueOf(value1 | value2));
            }
        }
        stackPointer--;
    }

    private void changArgForLine() {
        String label = "";

        for (int i = 0; i < code.size() ;i++) {
            label = code.get(i).substring(0, 3).strip();
            if(!label.isEmpty()){
                map.put(label, i);
            }
        }
    }

    private void allocMemory(int size) {
        for (int i = 0; i<size;i++) {
            memory.add("0");
        }
    }
}
