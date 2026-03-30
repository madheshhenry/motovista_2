package com.example.motovista_deep.models;

import java.util.List;

public class GetCustomersResponse {
    public boolean status;
    public List<CustomerItem> data;

    public static class CustomerItem {
        public int id;
        public String full_name;
        public String phone;
    }
}
