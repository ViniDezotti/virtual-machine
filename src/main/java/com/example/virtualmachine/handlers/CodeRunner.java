package com.example.virtualmachine.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
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
        this.stackPointer = -1;
        this.programCounter = 0;


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
        String label;
        String instruction;
        String arg1;
        String arg2;

        for (int i = 0; i < code.size(); i++) {
            label = code.get(i).substring(0, 3).strip();
            instruction = code.get(i).substring(4, 11).strip();
            arg1 = code.get(i).substring(12, 15).strip();
            arg2 = code.get(i).substring(16, 19).strip();

            if (!label.isEmpty()) {
                switch (instruction) {
                    case "LDC", "LDV" -> {
                        stackPointer++;
                        memory.push(arg1);
                    }
                    case "ADD" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(value2 + value1));
                        stackPointer--;
                    }
                    case "SUB" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(value2 - value1));
                        stackPointer--;
                    }
                    case "MULT" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(value2 * value1));
                        stackPointer--;
                    }
                    case "DIVI" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(value2 / value1));
                        stackPointer--;
                    }
                    case "INV" -> {
                        int value = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(-value));
                    }
                    case "AND" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(value2 & value1));
                        stackPointer--;

                    }
                    case "OR" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(value2 | value1));
                        stackPointer--;
                    }
                    case "NEG" -> {
                        int value = Integer.parseInt(memory.pop());
                        memory.push(String.valueOf(1 - value));
                    }
                    case "CME" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        if (value2 < value1) memory.push("1");
                        else memory.push("0");
                        stackPointer--;
                    }
                    case "CMA" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        if (value2 > value1) memory.push("1");
                        else memory.push("0");
                        stackPointer--;
                    }
                    case "CEQ" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        if (value2 == value1) memory.push("1");
                        else memory.push("0");
                        stackPointer--;
                    }
                    case "CDIF" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        if (value2 != value1) memory.push("1");
                        else memory.push("0");
                        stackPointer--;
                    }
                    case "CMEQ" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        if (value2 <= value1) memory.push("1");
                        else memory.push("0");
                        stackPointer--;
                    }
                    case "CMAQ" -> {
                        int value1 = Integer.parseInt(memory.pop());
                        int value2 = Integer.parseInt(memory.pop());
                        if (value2 >= value1) memory.push("1");
                        else memory.push("0");
                        stackPointer--;
                    }
                }
            }
        }
    }


    public void changArgForLine(String arg) {

    }

//    public


}
