package com.example.gramedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gramedia.OrderDetailActivity;
import com.example.gramedia.R;
import com.example.gramedia.model.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Format order ID
        holder.tvOrderId.setText(String.format("ORDER #%d", order.getOrderId()));

        // Format date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(order.getDate());
            holder.tvOrderDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvOrderDate.setText(order.getDate());
        }

        // Format total amount
        holder.tvOrderTotal.setText(String.format(Locale.getDefault(), "Rp %,.0f", order.getTotal()));

        // Set item count
        holder.tvItemCount.setText(String.format(Locale.getDefault(), "%d items", order.getItemCount()));

        // Set status
        switch (order.getStatus()) {
            case 0:
                holder.tvOrderStatus.setText("Menunggu Pembayaran");
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
                break;
            case 1:
                holder.tvOrderStatus.setText("Pembayaran Diterima");
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                break;
            case 2:
                holder.tvOrderStatus.setText("Pesanan Diproses");
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorInfo));
                break;
            case 3:
                holder.tvOrderStatus.setText("Pesanan Dikirim");
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess));
                break;
            case 4:
                holder.tvOrderStatus.setText("Pesanan Selesai");
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorSuccessDark));
                break;
            default:
                holder.tvOrderStatus.setText("Status Tidak Diketahui");
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("order_id", order.getOrderId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateData(List<Order> newOrders) {
        orderList.clear();
        orderList.addAll(newOrders);
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvItemCount, tvOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}
