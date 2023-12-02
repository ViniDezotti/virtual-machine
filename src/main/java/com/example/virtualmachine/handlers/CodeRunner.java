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
    private int stackPointer;
    private int programCounter;

    public CodeRunner(String objFile) {
        this.code = saveObjFile(objFile);
        this.memory = new Stack<>();
        this.stackPointer = -1;
        this.programCounter = 0;
    }

    private List<String> saveObjFile(String objFile){
        File file = new File(objFile);
        List<String> code;

        try {
           code = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return code;
    }

    public void execute(){
        String label, instruction, arg1, arg2;

        for (int i = 0; i < code.size(); i++) {
            label = code.get(i).substring(0, 3).strip();
            instruction = code.get(i).substring(4, 11).strip();
            arg1 = code.get(i).substring(12, 15).strip();
            arg2 = code.get(i).substring(16, 19).strip();

            if (!label.isEmpty()) {

            }
        }
    }

    public void changArgForLine (String arg) {

    }

//    public



}
