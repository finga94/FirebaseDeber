package com.example.firebaserealtime;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VentasActivity extends AppCompatActivity {

    private EditText codigoEditText, cantidadEditText;
    private TextView nombreTextView, stockTextView, precioVentaTextView, totalPagarTextView;
    private Button buscarButton, recalcularButton, venderButton;

    private DatabaseReference productosRef;
    private Producto productoActual;
    private int stockActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        codigoEditText = findViewById(R.id.editTextCodigo);
        cantidadEditText = findViewById(R.id.editTextCantidad);
        nombreTextView = findViewById(R.id.textViewNombreProducto);
        stockTextView = findViewById(R.id.textViewStock);
        precioVentaTextView = findViewById(R.id.textViewPrecioVenta);
        totalPagarTextView = findViewById(R.id.textViewTotalPagar);
        buscarButton = findViewById(R.id.buttonBuscar);
        recalcularButton = findViewById(R.id.buttonRecalcular);
        venderButton = findViewById(R.id.buttonVender);
        productosRef = FirebaseDatabase.getInstance().getReference().child("productos");


        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProducto();
            }
        });

        recalcularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularTotalPagar();
            }
        });

        venderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarVenta();
            }
        });
    }

    private void buscarProducto() {
        String codigo = codigoEditText.getText().toString().trim();

        if (TextUtils.isEmpty(codigo)) {
            Toast.makeText(this, "Ingrese un código para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        productosRef.orderByChild("codigo").equalTo(codigo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productoActual = snapshot.getValue(Producto.class);
                        if (productoActual != null) {
                            nombreTextView.setText(productoActual.getNombre());
                            stockActual = productoActual.getStock();
                            stockTextView.setText(String.valueOf(stockActual));
                            precioVentaTextView.setText(String.valueOf(productoActual.getPrecioVenta()));
                            calcularTotalPagar(); // Calculate initial total based on default quantity
                            return; // Exit after finding the first matching product
                        }
                    }
                } else {
                    Toast.makeText(VentasActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                    limpiarDatosProducto();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VentasActivity.this, "Error al buscar el producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularTotalPagar() {
        String cantidadStr = cantidadEditText.getText().toString().trim();

        if (TextUtils.isEmpty(cantidadStr)) {
            Toast.makeText(this, "Ingrese la cantidad deseada", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);

        if (cantidad > stockActual) {
            Toast.makeText(this, "No hay stock suficiente", Toast.LENGTH_SHORT).show();
            return;
        }

        double precioVenta = productoActual.getPrecioVenta();
        double totalPagar = cantidad * precioVenta;

        totalPagarTextView.setText("Total a pagar: $" + totalPagar);
    }

    private void realizarVenta() {
        String cantidadStr = cantidadEditText.getText().toString().trim();

        if (TextUtils.isEmpty(cantidadStr)) {
            Toast.makeText(this, "Ingrese la cantidad deseada", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);

        if (cantidad > stockActual) {
            Toast.makeText(this, "No hay stock suficiente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calcular el nuevo stock después de la venta
        int nuevoStock = stockActual - cantidad;

        // Obtener el código del producto actual
        String codigoProducto = productoActual.getCodigo();

        // Obtener la referencia directa al nodo del producto en Firebase utilizando el código
        productosRef.orderByChild("codigo").equalTo(codigoProducto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String productoKey = snapshot.getKey(); // Obtener la clave del producto en Firebase

                        // Actualizar el stock del producto en Firebase Realtime Database
                        productosRef.child(productoKey).child("stock").setValue(nuevoStock)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(VentasActivity.this, "Venta realizada correctamente", Toast.LENGTH_SHORT).show();
                                            limpiarDatosProducto();
                                        } else {
                                            Toast.makeText(VentasActivity.this, "Error al realizar la venta", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(VentasActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                    limpiarDatosProducto();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VentasActivity.this, "Error al buscar el producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void limpiarDatosProducto() {
        nombreTextView.setText("");
        stockTextView.setText("");
        precioVentaTextView.setText("");
        totalPagarTextView.setText("Total a pagar: $0");

        productoActual = null;
        stockActual = 0;
    }
}
