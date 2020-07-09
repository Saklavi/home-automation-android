package com.wolty.homeautomation.deviceModel;

public class Device {
    public int id;
    public int input;
    public int pin;
    public int status;
    public String name;
    public String type;

    public Device(int id, int pin, int status, String name, String type) {
        this.id = id;
        this.input = input;
        this.pin = pin;
        this.status = status;
        this.name = name;
        this.type = type;
    }
}
