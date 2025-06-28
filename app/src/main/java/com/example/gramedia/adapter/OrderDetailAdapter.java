package com.example.gramedia.adapter;

import static com.example.gramedia.api.ServerAPI.BASE_URL_Image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gramedia.R;
import com.example.gramedia.model.OrderItem;

import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderItemViewHolder> {

    private Context context;
    private List<OrderItem> orderItems;

    public OrderDetailAdapter(Context context, List<OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        holder.tvProductName.setText(item.getMerk());
        holder.tvProductPrice.setText(String.format(Locale.getDefault(), "Rp %,.0f", item.getHargajual()));
        holder.tvProductQty.setText(String.format(Locale.getDefault(), "x%d", item.getQty()));
        holder.tvProductTotal.setText(String.format(Locale.getDefault(), "Rp %,.0f", item.getHargajual() * item.getQty()));

        // Load product image
        Glide.with(context)
                .load(BASE_URL_Image + item.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvProductQty, tvProductTotal;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQty = itemView.findViewById(R.id.tvProductQty);
            tvProductTotal = itemView.findViewById(R.id.tvProductTotal);
        }
    }
}
