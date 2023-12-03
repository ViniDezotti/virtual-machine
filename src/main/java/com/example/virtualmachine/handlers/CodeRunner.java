package com.example.virtualmachine.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class CodeRunner {
    private final List<String> code;
    private Stack<String> memory;
    private Stack<String> auxMemory;
    private int stackPointer;
    private int programCounter;
    private Map<String, Integer> map;


    public CodeRunner(String objFile) {
        this.code = saveObjFile(objFile);
        this.memory = new Stack<>();
        this.auxMemory = new Stack<>();
        this.stackPointer = -1;
        this.programCounter = 0;
        this.map = new HashMap<>();

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
                    memory.push(arg1);
                }
                case "LDV" -> {
                    String value = memory.get(Integer.parseInt(arg1));

                    stackPointer++;
                    memory.push(value);
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
                    int value = Integer.parseInt(memory.pop());
                    memory.push(String.valueOf(-value));
                }
                case "NEG" -> {
                    int value = Integer.parseInt(memory.pop());
                    memory.push(String.valueOf(1 - value));
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
                    String value = memory.pop();

                    if (memory.size() > Integer.parseInt(arg1)) {
                        memory.set(Integer.parseInt(arg1), value);
                    } else {
                        memory.push(value);
                    }

                    stackPointer--;

//                    while (!memory.isEmpty()) {
//                        if (memory.size() - 1 == Integer.parseInt(arg1)) {
//                            memory.pop();
//                            memory.push(value);
//                            while (!auxMemory.isEmpty()) {
//                                memory.push(auxMemory.pop());
//                            }
//                            break;
//                        }
//                        auxMemory.push(memory.pop());
//                    }
                }
                case "JMP" -> {
                    programCounter = map.get(arg1);
                    pcNotChanged = false;
                }
                case "JMPF" -> {
                    if (Objects.equals(memory.pop(), "0")) {
                        programCounter = map.get(arg1);
                        pcNotChanged = false;
                    } else stackPointer--;
                }
                case "NULL" -> {

                }
                case "RD" -> {
                    stackPointer++;
                    //TODO memory.push(entrada)
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Leia (" + code.get(programCounter+1).substring(12, 16) + "): ");
                    memory.push(scanner.nextLine());
                }
                case "PRN" -> {
                    stackPointer--;
                    //TODO imprimir(memory.pop);
                    System.out.println(memory.pop());
                }
                case "START" -> {
                    this.stackPointer = -1;
                }
                case "ALLOC" -> {
                    stackPointer += Integer.parseInt(arg1);

                    for (int i = 0; i < Integer.parseInt(arg2); i++) {

                        if (memory.empty()) {
                            memory.push("0");
                        } else {
                            memory.push(memory.lastElement());
                        }
                    }
                }
                case "DALLOC" -> {
                    String lastElement;

                    for (int i = 0; i < Integer.parseInt(arg2); i++) {
                        lastElement = memory.pop();

                        if (!memory.empty()) {
                            memory.set(memory.size() - 1, lastElement);
                        }
                    }

                    stackPointer -= Integer.parseInt(arg1);
//                    while (!memory.isEmpty()) {
//                        if (memory.size() - 1 == Integer.parseInt(arg1)) {
//                            int index = 0;
//                            while (index < Integer.parseInt(arg1)) {
//                                auxMemory.pop();
//                                index++;
//                            }
//                            while (!auxMemory.isEmpty()) {
//                                memory.push(auxMemory.pop());
//                            }
//                        }
//                        auxMemory.push(memory.pop());
//                    }
                }
                case "CALL" -> {
                    stackPointer++;
                    memory.push(String.valueOf(programCounter + 1));
                    programCounter = map.get(arg1);
                    pcNotChanged = false;
                }
                case "RETURN" -> {
                    programCounter = Integer.parseInt(memory.pop());
                    pcNotChanged = false;
                    stackPointer--;
                }
                case "HLT" -> {

                }
            }

            if (pcNotChanged) {
                programCounter++;
            } else {
                pcNotChanged = true;
            }
            System.out.println(memory);
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
        int value1 = Integer.parseInt(memory.pop());
        int value2 = Integer.parseInt(memory.pop());

        switch (s) {
            case "+" -> {
                memory.push(String.valueOf(value2 + value1));
            }
            case "-" -> {
                memory.push(String.valueOf(value2 - value1));
            }
            case "*" -> {
                memory.push(String.valueOf(value2 * value1));
            }
            case "/" -> {
                memory.push(String.valueOf(value2 / value1));
            }
            case "&" -> {
                memory.push(String.valueOf(value2 & value1));
            }
            case "|" -> {
                memory.push(String.valueOf(value2 | value1));
            }
        }
        stackPointer--;
    }

    public void changArgForLine() {
        String label = "";

        for (int i = 0; i < code.size() ;i++) {
            label = code.get(i).substring(0, 3).strip();
            if(!label.isEmpty()){
                map.put(label, i);
            }
        }
    }
}
