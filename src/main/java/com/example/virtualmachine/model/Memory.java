package com.example.virtualmachine.model;

import javafx.beans.property.SimpleIntegerProperty;

public class Memory {
    private SimpleIntegerProperty Address;
    private SimpleIntegerProperty Value;

    public Memory(int address, int value) {
        Address = new SimpleIntegerProperty(address);
        Value = new SimpleIntegerProperty(value);
    }

    public int getAddress() {
        return Address.get();
    }

    public void setAddress(int address) {
        this.Address = new SimpleIntegerProperty(address);
    }

    public int getValue() {
        return Value.get();
    }

    public void setValue(int value) {
        this.Value = new SimpleIntegerProperty(value);
    }
}
