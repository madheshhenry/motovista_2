package com.example.motovista_deep.models;

public class GenericResponse {
    private String status;
    private String message;
    private Object data;
    public String getStatus(){return status;}
    public String getMessage(){return message;}
    public Object getData(){return data;}
}
