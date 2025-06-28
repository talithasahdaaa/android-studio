package com.example.gramedia.ui.order;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gramedia.R;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderModel> list;
    private OnUploadClickListener uploadClickListener;

    public interface OnUploadClickListener {
        void onUpload(OrderModel model);
    }

    public void setOnUploadClickListener(OnUploadClickListener listener) {
        this.uploadClickListener = listener;
    }

    public OrderHistoryAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel model = list.get(position);

        holder.tvOrderId.setText("Order ID: #" + model.nomer_id);
        holder.tvTanggal.setText("Tanggal: " + model.tgl_order);
        holder.tvTotalBayar.setText("Total: Rp " + model.total_bayar);
        holder.tvStatus.setText("Status: " + model.status);

        // ✅ LOGIC FIX
        String bukti = model.buktiBayar != null ? model.buktiBayar.trim() : "";
        Log.d("AdapterCheck", "buktiBayar: [" + bukti + "]");

        if (bukti.isEmpty()) {
            holder.btnUploadBukti.setVisibility(View.VISIBLE);
            holder.btnUploadBukti.setOnClickListener(v -> {
                if (uploadClickListener != null) {
                    uploadClickListener.onUpload(model);
                }
            });
        } else {
            holder.btnUploadBukti.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTanggal, tvTotalBayar, tvStatus;
        Button btnUploadBukti;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvTotalBayar = itemView.findViewById(R.id.tvTotalBayar);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnUploadBukti = itemView.findViewById(R.id.btnUploadBukti);
        }
    }
}
