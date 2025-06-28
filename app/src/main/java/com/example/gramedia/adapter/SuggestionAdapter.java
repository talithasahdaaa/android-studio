package com.example.gramedia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gramedia.api.Product;
import com.example.gramedia.R;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
    private List<Product> suggestionList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public SuggestionAdapter(List<Product> suggestionList, OnItemClickListener listener) {
        this.suggestionList = suggestionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = suggestionList.get(position);
        holder.textViewMerk.setText(product.getMerk());
        // load image pakai Glide/Picasso
        Glide.with(holder.itemView.getContext())
                .load(ServerAPI.BASE_URL_Image + "product/" + product.getFoto())
                .into(holder.imageViewProduct);

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(product);
            int currentViewCount = product.getView();
            currentViewCount++;

            // Update the view count in the database using the API
            RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
            Call<ResponseBody> call = apiService.postView(product.getKode(), currentViewCount);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    public void updateList(List<Product> newList) {
        suggestionList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMerk;
        ImageView imageViewProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMerk = itemView.findViewById(R.id.textViewMerk);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}
