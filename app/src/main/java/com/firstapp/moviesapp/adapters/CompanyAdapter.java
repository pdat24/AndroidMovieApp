package com.firstapp.moviesapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.models.ProductionCompany;
import com.firstapp.moviesapp.utils.Constants;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    List<ProductionCompany> companies;
    Context context;

    public CompanyAdapter(List<ProductionCompany> companies) {
        this.companies = companies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_company, parent, false
            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductionCompany company = companies.get(position);
        holder.name.setText(company.name);
        if (company.logo_path != null)
            Glide.with(context)
                .asDrawable()
                .load(Constants.IMAGE_HOST_URL + company.logo_path)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                        @NonNull Drawable resource,
                        @Nullable Transition<? super Drawable> transition
                    ) {
                        holder.logo.setBackground(resource);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        TextView name = itemView.findViewById(R.id.tvName);
        ImageView logo = itemView.findViewById(R.id.ivLogo);
    }
}
