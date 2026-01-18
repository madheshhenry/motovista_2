package com.example.motovista_deep.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.BrandBikesActivity;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.InventoryBrand;
import com.example.motovista_deep.utils.ImageUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryBrandAdapter extends RecyclerView.Adapter<InventoryBrandAdapter.ViewHolder> {

    private Context context;
    private List<InventoryBrand> brandList;
    private Map<String, String> brandLogoMap;

    private static final int TYPE_BRAND = 0;
    private static final int TYPE_ADD = 1;

    private OnAddBrandClickListener addListener;

    public interface OnAddBrandClickListener {
        void onAddBrandClick();
    }

    public InventoryBrandAdapter(Context context, List<InventoryBrand> brandList, OnAddBrandClickListener addListener) {
        this.context = context;
        this.brandList = brandList;
        this.addListener = addListener;
        initializeBrandMap();
    }

    private void initializeBrandMap() {
        brandLogoMap = new HashMap<>();
        brandLogoMap.put("Ducati", "https://lh3.googleusercontent.com/aida-public/AB6AXuDXEH2teEHriUaslUV94LmZ8wDCek3eP1JtROAnoGmvDF8rswALK3sUrGw1iWvU2Q3VILl8GYFVxIMu18uoqcTd9EbTvOIiEIIZEiGEi3YoF6__uUXALq1jNIWTM5-mrDBwHtR6yhJja6Kmusy_OeVwRWfuQf8Dg6eGkWz7_kqhpJO7-fmLiymE0Xu9K0_BOzZOP93cdh0nB8hjbdwBrjvjHMGCQdQbgtocos2DaYJ_zd8_0XvmdUXxXM0_ma-hkFUiEbfE1iTt8grD");
        brandLogoMap.put("BMW", "https://lh3.googleusercontent.com/aida-public/AB6AXuBBxqqItT-ATORwXbuXWbBzvIoBsZtp8y2ok8hBdIZa-dBVCaeUaV2nfauSzFjULgbfdY7W26IBjo0CQ8DusfiqrNXO-rQ23nY01wbhwizKn6AzWIbtSx5xn_RXNnqfpc85kpSMcwwVJjCoUA5-Md0_F39VqfLfJ6oRlJrzVSbk0zFnurvKXtdOJTWNt_91dEUnS0apvcd1EJK5oT2m6csW_vPU933q97Xc96caDw__hNCxLhb5OW8Kw4rr5wScCbLZzHd8vVLixLtP");
        brandLogoMap.put("Kawasaki", "https://lh3.googleusercontent.com/aida-public/AB6AXuCs9Wf0qeaCZTY9PZzBipBajafhGLQiSrXBmPVgMu01pV9ZO4XVSt8EYGnS1R4NKtJ9k4wLlICewuBLtDMPl4y6Wo9hac_U-qaKWqFLluQ7dQBkiI-43j7toOZO50aWVouzOfsBhVtotSW-XMA5UvkWARwKjFoRqD1tGtyRl0cRzhgG14R-3qhtUNvjazEmG9FVmv1qATuozwZi5xGkDBZ22hXte8DyhSxGTI78SqzZRLEtpanL7VGZ6J6h7xS4JxEQszB3kv-TMk_M");
        brandLogoMap.put("Triumph", "https://lh3.googleusercontent.com/aida-public/AB6AXuA8Gy18F1ynrw7M2kPUNqV33aX338HUZHT0JjxCPU_lwc1Y8ELfwxBGRRyCkbEZQ_3CSZ3tKeXFaRFdiPOyf-VZKltkHjV1uHlrNsQBOdln2eYo6O5SA0yAHOvZwmdbzXRhYonpW3GHBIMxdPObTgj7qcDz3UHhZEE1EVYqIGusoOGLr-OrOuf1-pcvsl0_xAFEc8d3lbdEziGeOJpj5iOxW7pDfzdf-fS4XpdSsY75VCG3z---pkTTpBJBsSZh2RDq7QGgJC1tdaKW");
        brandLogoMap.put("Harley-Davidson", "https://lh3.googleusercontent.com/aida-public/AB6AXuAyJtvlNU6vPPvoJfHiiS0CZ9mzKRITT5WgVgqgbOfT-1wDiNu9nH_Oi2OAKh8JQU2hFnZIB-KQIJW9lePhyDj5bdKo1PdyOzZpPlLejTr87Loux460zPZ9OQ-jahVwCiTfJcj3fxmLOHea3AwgQEzC7hHOiswcNFTx-ppFpu6-bWRVQKUXKScRbDMle4sK-R9U8PlfvRAizV1Vb3d_fmLIsXP-qdoBjN5tlA5LvRRTk5spnsLPrVivmzZVaXrGWcL6YketbaDKqvNg");
        brandLogoMap.put("Royal Enfield", "https://lh3.googleusercontent.com/aida-public/AB6AXuDgNCSmU8TCiVaFUVrmNw3CbZNGCfFKppzsPikcBbm34waXIDtiJQ51bjq3qv8SOszTg5YQJFVLMJZ-9y3sSNdKrn2nfNYumVecPpDq-iCvNrLWUL8B-a4HVOk3l-gAirpHkahqVMwAefMIcN3fAqVq3-0VVCeOTRe85LclA_lh-KEli6QzX6fVmAwwURmPdQvZWcbaS0OOmSbZsqXIM078xXYXAd_v-ws4IP4GXIB85EWXq2NSEoua2q_p-kCfGKxSPw6wAOUD8ML6");
        brandLogoMap.put("Yamaha", "https://lh3.googleusercontent.com/aida-public/AB6AXuDJCOU8qvWkMmUd5crllBged5BLagtXa9rAo-RBfj5I6kuJ8KbCWmBtwHs1HfPnzRI6g1h2e22N1tQrsdGEjfSSlN6DJyGFfqRDSOoWTlwQ4qxU_eyGpRFr8Ru9rg-rbcdKJ-r_SlW2VaZR2Dhr_qcy_raMySWK-LpRfW_rdiRFyFCZUcHI1U-AP2gI8SpUXU_3uc-h9MqU4Rhf-X8IPxG9FG0NrH1z5eHoQCp9Efe1hiO-EotbspINxAzB8LMCFzeRo_gUZO-K0Wya");
        brandLogoMap.put("Honda", "https://lh3.googleusercontent.com/aida-public/AB6AXuDhKiJ3v1oWDCDaoP115rg3klYOQHh6i2HvzU6nj1ypoYZMHIUTjnWGYI5K-XIKW1MGx-TBBfsEF9eHjufpnBComuS8ZvbQzYN-dHLz-WInEdM_KaJaGvVulvBuyWIsx5ZJ38IX7aW81b21yglnoL-_YhGthJG_v6L3Gyr34pPyDHNrJJqpN2j7owpuFAhK45_tMluMNGPFgUwZJ_p2Q8lc32Tb2OeUrPOFvj84sdM0ywYQ9nA2wPXxIDrq_dL-6dbpQLOrDm9Yw4e8");
    }

    @Override
    public int getItemViewType(int position) {
        if (position == brandList.size()) {
            return TYPE_ADD;
        }
        return TYPE_BRAND;
    }

    @Override
    public int getItemCount() {
        return brandList != null ? brandList.size() + 1 : 1; // +1 for Add Button
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory_brand_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ADD) {
            setupAddView(holder);
        } else {
            InventoryBrand brand = brandList.get(position);
            setupBrandView(holder, brand);
        }
    }

    private void setupAddView(ViewHolder holder) {
        holder.ivBrandLogo.setVisibility(View.GONE);
        holder.ivAddIcon.setVisibility(View.VISIBLE);
        holder.tvBrandName.setText("Add Brand");
        holder.tvBrandName.setTextColor(Color.parseColor("#13c8ec"));
        
        holder.itemView.setOnClickListener(v -> {
            if (addListener != null) {
                addListener.onAddBrandClick();
            }
        });
    }

    private void setupBrandView(ViewHolder holder, InventoryBrand brand) {
        holder.ivBrandLogo.setVisibility(View.VISIBLE);
        holder.ivAddIcon.setVisibility(View.GONE);
        holder.tvBrandName.setText(brand.getBrand());
        holder.tvBrandName.setTextColor(Color.parseColor("#0f172a"));

        // Use centralized ImageUtils for backend logo URL
        String logoUrl = ImageUtils.getFullImageUrl(brand.getLogo(), ImageUtils.PATH_BRANDS);
        
        // Fallback to map if null or empty
        if (brand.getLogo() == null || brand.getLogo().isEmpty()) {
            if (brand.getBrand() != null) {
                 // Check map...
                 String mapUrl = brandLogoMap.get(brand.getBrand());
                 if (mapUrl == null) {
                     for (String key : brandLogoMap.keySet()) {
                         if (brand.getBrand().contains(key) || key.contains(brand.getBrand())) {
                             mapUrl = brandLogoMap.get(key);
                             break;
                         }
                     }
                 }
                 if (mapUrl != null) logoUrl = mapUrl;
            }
        }
        
        if (logoUrl != null && !logoUrl.isEmpty()) {
            Glide.with(context)
                .load(logoUrl)
                .placeholder(R.drawable.ic_bike_placeholder) // Use placeholder while loading
                .into(holder.ivBrandLogo);
        } else {
            holder.ivBrandLogo.setImageResource(R.drawable.ic_bike_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BrandBikesActivity.class);
            intent.putExtra("BRAND_NAME", brand.getBrand());
            // intent.putExtra("BIKE_LIST", (Serializable) brand.getBikes()); // We might fetch bikes fresh in next screen
            // For now pass as is to avoid breaking existing flow? 
            // Existing BrandBikesActivity likely expects list. 
            // IF we changed backend to ONLY return brands, list might be empty.
            // But we can check. For now passing list is fine if model has it.
            intent.putExtra("BIKE_LIST", (Serializable) brand.getBikes());
            context.startActivity(intent);
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBrandLogo, ivAddIcon;
        TextView tvBrandName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBrandLogo = itemView.findViewById(R.id.ivBrandLogo);
            ivAddIcon = itemView.findViewById(R.id.ivAddIcon);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
        }
    }
}
