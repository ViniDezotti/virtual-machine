package com.example.virtualmachine.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Assembly {
    private SimpleIntegerProperty line;
    private SimpleStringProperty label;
    private SimpleStringProperty instruction;
    private SimpleStringProperty arg1;
    private SimpleStringProperty arg2;

    public Assembly(Integer line, String label, String instruction,
                    String arg1, String arg2) {
        this.line = new SimpleIntegerProperty(line);
        this.label = new SimpleStringProperty(label);
        this.instruction = new SimpleStringProperty(instruction);
        this.arg1 = new SimpleStringProperty(arg1);
        this.arg2 = new SimpleStringProperty(arg2);
    }

    public int getLine() {
        return line.get();
    }

    public void setLine(int line) {
        this.line = new SimpleIntegerProperty(line);
    }

    public String getLabel() {
        return label.get();
    }


    public void setLabel(String label) {
        this.label = new SimpleStringProperty(label);
    }

    public String getInstruction() {
        return instruction.get();
    }

    public void setInstruction(String instruction) {
        this.instruction = new SimpleStringProperty(instruction);
    }

    public String getArg1() {
        return arg1.get();
    }

    public void setArg1(String arg1) {
        this.arg1 = new SimpleStringProperty(arg1);
    }

    public String getArg2() {
        return arg2.get();
    }

    public void setArg2(String arg2) {
        this.arg2 = new SimpleStringProperty(arg2);
    }
}
