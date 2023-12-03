package com.example.virtualmachine.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class CodeRunner {
    private final List<String> code;
    private Stack<String> memory;
    private Stack<String> auxMemory;
    private int stackPointer;
    private int programCounter;

    public CodeRunner(String objFile) {
        this.code = saveObjFile(objFile);
        this.memory = new Stack<>();
        this.auxMemory = new Stack<>();
        this.stackPointer = -1;
        this.programCounter = 0;

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
        String label = "";
        String instruction = "";
        String arg1 = "";
        String arg2 = "";

        for (String s : code) {
            try {
                label = s.substring(0, 3).strip();
                instruction = s.substring(4, 11).strip();
                arg1 = s.substring(12, 15).strip();
                arg2 = s.substring(16, 19).strip();
            } catch (StringIndexOutOfBoundsException ex) {

            }

            if (label.isEmpty()) {
                switch (instruction) {
                    case "LDC" -> {
                        stackPointer++;
                        memory.push(arg1);
                    }
                    case "LDV" -> {
                        String value;

                        while (!memory.isEmpty()) {
                            if (memory.size() - 1 == Integer.parseInt(arg1)) {
                                value = memory.pop();
                                while (!auxMemory.isEmpty()) {
                                    memory.push(auxMemory.pop());
                                }
                                memory.push(value);
                                break;
                            }
                            auxMemory.push(memory.pop());
                        }
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

                        while (!memory.isEmpty()) {
                            if (memory.size() - 1 == Integer.parseInt(arg1)) {
                                memory.pop();
                                memory.push(value);
                                while (!auxMemory.isEmpty()) {
                                    memory.push(auxMemory.pop());
                                }
                                break;
                            }
                            auxMemory.push(memory.pop());
                        }
                    }
                    case "JMP" -> {
                        programCounter = Integer.parseInt(arg1);
                    }
                    case "JMPF" -> {
                        if (Objects.equals(memory.pop(), "0")) {
                            programCounter = Integer.parseInt(arg1);
                        } else stackPointer++;
                    }
                    case "NULL" -> {

                    }
                    case "RD" -> {
                        stackPointer++;
                        //TODO memory.push(entrada)
                    }
                    case "PRN" -> {
                        stackPointer--;
                        //TODO imprimir(memory.pop);
                    }
                    case "START" -> {
                        this.stackPointer = -1;
                    }
                    case "ALLOC" -> {
                        stackPointer += Integer.parseInt(arg1);

                        int cont = 0;
                        while (cont < Integer.parseInt(arg2)) {
                            memory.push("");
                            cont++;
                        }
                    }
                    case "DALLOC" -> {
                        stackPointer -= Integer.parseInt(arg1);
                        while (!memory.isEmpty()) {
                            if (memory.size() - 1 == Integer.parseInt(arg1)) {
                                int index = 0;
                                while (index < Integer.parseInt(arg1)) {
                                    auxMemory.pop();
                                    index++;
                                }
                                while (!auxMemory.isEmpty()) {
                                    memory.push(auxMemory.pop());
                                }
                            }
                            auxMemory.push(memory.pop());
                        }
                    }
                }
            }
            System.out.println(memory);
        }
    }

    private void resolveExpression(String s) {
        int value1 = Integer.parseInt(memory.pop());
        int value2 = Integer.parseInt(memory.pop());

        switch (s) {
            case "<" -> {
                if (value2 < value1) memory.push("1");
                else memory.push("0");
            }
            case ">" -> {
                if (value2 > value1) memory.push("1");
                else memory.push("0");
            }
            case "==" -> {
                if (value2 == value1) memory.push("1");
                else memory.push("0");
            }
            case "!=" -> {
                if (value2 != value1) memory.push("1");
                else memory.push("0");
            }
            case "<=" -> {
                if (value2 <= value1) memory.push("1");
                else memory.push("0");
            }
            case ">=" -> {
                if (value2 >= value1) memory.push("1");
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

    public void changArgForLine(String arg) {

    }}
