package com.example.motovista_deep.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.CustomerDetailsActivity;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.GetCustomersResponse;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<GetCustomersResponse.CustomerItem> customerList;
    private List<GetCustomersResponse.CustomerItem> customerListFiltered;

    public CustomerAdapter(List<GetCustomersResponse.CustomerItem> customerList) {
        this.customerList = customerList;
        this.customerListFiltered = new ArrayList<>(customerList);
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        GetCustomersResponse.CustomerItem customer = customerListFiltered.get(position);

        holder.tvCustomerName.setText(customer.full_name);
        holder.tvCustomerPhone.setText(customer.phone);

        // Click listener for WHOLE CARD
        holder.cardCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDetails(customer, holder.itemView);
            }
        });

        // Click listener for ARROW ONLY
        holder.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDetails(customer, holder.itemView);
            }
        });
    }

    private void openCustomerDetails(GetCustomersResponse.CustomerItem customer, View itemView) {
        Intent intent = new Intent(itemView.getContext(), CustomerDetailsActivity.class);
        intent.putExtra("customer_id", customer.id);
        itemView.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return customerListFiltered.size();
    }

    public void updateList(List<GetCustomersResponse.CustomerItem> newList) {
        this.customerList = newList;
        this.customerListFiltered = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        customerListFiltered.clear();
        if (text.isEmpty()) {
            customerListFiltered.addAll(customerList);
        } else {
            text = text.toLowerCase();
            for (GetCustomersResponse.CustomerItem customer : customerList) {
                if (customer.full_name.toLowerCase().contains(text) ||
                        customer.phone.contains(text)) {
                    customerListFiltered.add(customer);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerPhone;
        CardView cardCustomer;
        ImageView ivArrow;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            cardCustomer = itemView.findViewById(R.id.cardCustomer);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}