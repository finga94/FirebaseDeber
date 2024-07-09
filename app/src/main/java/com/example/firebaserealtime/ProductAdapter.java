package com.example.firebaserealtime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Producto> productList;

    public ProductAdapter(List<Producto> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Producto producto = productList.get(position);
        holder.codigoTextView.setText("Código: " + producto.getCodigo());
        holder.nombreTextView.setText("Nombre: " + producto.getNombre());
        holder.precioCostoTextView.setText("Precio Costo: $" + producto.getPrecioCosto());
        holder.precioVentaTextView.setText("Precio Venta: $" + producto.getPrecioVenta());
        holder.stockTextView.setText("Stock: " + producto.getStock());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView codigoTextView, nombreTextView, precioCostoTextView, precioVentaTextView, stockTextView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            codigoTextView = itemView.findViewById(R.id.text_view_codigo);
            nombreTextView = itemView.findViewById(R.id.text_view_nombre);
            precioCostoTextView = itemView.findViewById(R.id.text_view_precio_costo);
            precioVentaTextView = itemView.findViewById(R.id.text_view_precio_venta);
            stockTextView = itemView.findViewById(R.id.text_view_stock);
        }
    }
}
