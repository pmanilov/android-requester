package com.manilov.requester.dto;

public class RequestBodyDTO {
    private double value;
    private String sensorName;

    public RequestBodyDTO(double value, String sensorName) {
        this.value = value;
        this.sensorName = sensorName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }
}
