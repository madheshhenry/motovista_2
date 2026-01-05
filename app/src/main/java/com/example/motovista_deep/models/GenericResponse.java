package com.example.motovista_deep.models;

public class GenericResponse {
    private String status;
    private boolean success; // Added to match new backend responses
    private String message;
    private Object data;
    public String getStatus(){return status;}
    public boolean isSuccess(){return success;}
    public String getMessage(){return message;}
    public Object getData(){return data;}
}
