package com.example.firebaserealtime;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ProductoActivity extends AppCompatActivity {

    private static final String TAG = "ProductoActivity";

    private EditText codigoEditText, nombreEditText, stockEditText, precioCostoEditText, precioVentaEditText;
    private Button guardarButton, actualizarButton, buscarButton, borrarButton, regresarButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        // Inicialización de vistas
        codigoEditText = findViewById(R.id.editTextCodigo);
        nombreEditText = findViewById(R.id.editTextNombre);
        stockEditText = findViewById(R.id.editTextStock);
        precioCostoEditText = findViewById(R.id.editTextPrecioCosto);
        precioVentaEditText = findViewById(R.id.editTextPrecioVenta);

        guardarButton = findViewById(R.id.buttonGuardar);
        actualizarButton = findViewById(R.id.buttonActualizar);
        buscarButton = findViewById(R.id.buttonBuscar);
        borrarButton = findViewById(R.id.buttonBorrar);
        regresarButton = findViewById(R.id.buttonRegresar);

        // Referencia a la base de datos Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("productos");

        // Acción de botones
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarProducto();
            }
        });

        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProducto();
            }
        });

        actualizarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarProducto();
            }
        });

        borrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarProducto();
            }
        });

        regresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void guardarProducto() {
        String codigo = codigoEditText.getText().toString().trim();
        String nombre = nombreEditText.getText().toString().trim();
        String stockStr = stockEditText.getText().toString().trim();
        String precioCostoStr = precioCostoEditText.getText().toString().trim();
        String precioVentaStr = precioVentaEditText.getText().toString().trim();

        if (TextUtils.isEmpty(codigo) || TextUtils.isEmpty(nombre) || TextUtils.isEmpty(stockStr)
                || TextUtils.isEmpty(precioCostoStr) || TextUtils.isEmpty(precioVentaStr)) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock = Integer.parseInt(stockStr);
        double precioCosto = Double.parseDouble(precioCostoStr);
        double precioVenta = Double.parseDouble(precioVentaStr);

        Producto producto = new Producto(codigo, nombre, stock, precioCosto, precioVenta);

        String productoId = databaseReference.push().getKey();

        databaseReference.child(productoId).setValue(producto)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductoActivity.this, "Producto guardado correctamente", Toast.LENGTH_SHORT).show();
                            limpiarCampos();
                        } else {
                            Toast.makeText(ProductoActivity.this, "Error al guardar el producto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void buscarProducto() {
        String codigo = codigoEditText.getText().toString().trim();

        if (TextUtils.isEmpty(codigo)) {
            Toast.makeText(this, "Ingrese un código para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("codigo").equalTo(codigo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Producto producto = snapshot.getValue(Producto.class);
                        if (producto != null) {
                            nombreEditText.setText(producto.getNombre());
                            stockEditText.setText(String.valueOf(producto.getStock()));
                            precioCostoEditText.setText(String.valueOf(producto.getPrecioCosto()));
                            precioVentaEditText.setText(String.valueOf(producto.getPrecioVenta()));
                            return; // Solo necesitas mostrar los datos del primer producto encontrado
                        }
                    }
                } else {
                    Toast.makeText(ProductoActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductoActivity.this, "Error al buscar el producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarProducto() {
        final String codigo = codigoEditText.getText().toString().trim();
        final String nombre = nombreEditText.getText().toString().trim();
        String stockStr = stockEditText.getText().toString().trim();
        String precioCostoStr = precioCostoEditText.getText().toString().trim();
        String precioVentaStr = precioVentaEditText.getText().toString().trim();

        if (TextUtils.isEmpty(codigo) || TextUtils.isEmpty(nombre) || TextUtils.isEmpty(stockStr)
                || TextUtils.isEmpty(precioCostoStr) || TextUtils.isEmpty(precioVentaStr)) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        final int stock = Integer.parseInt(stockStr);
        final double precioCosto = Double.parseDouble(precioCostoStr);
        final double precioVenta = Double.parseDouble(precioVentaStr);

        // Buscar el producto por su código y actualizarlo
        databaseReference.orderByChild("codigo").equalTo(codigo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String productoKey = snapshot.getKey();

                        Producto productoActualizado = new Producto(codigo, nombre, stock, precioCosto, precioVenta);

                        databaseReference.child(productoKey).setValue(productoActualizado)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProductoActivity.this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(ProductoActivity.this, "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        return;
                    }
                } else {
                    Toast.makeText(ProductoActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductoActivity.this, "Error al buscar el producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void borrarProducto() {
        final String codigo = codigoEditText.getText().toString().trim();

        if (TextUtils.isEmpty(codigo)) {
            Toast.makeText(this, "Ingrese un código para borrar", Toast.LENGTH_SHORT).show();
            return;
        }


        databaseReference.orderByChild("codigo").equalTo(codigo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Obtener la clave única del producto
                        String productoKey = snapshot.getKey();

                        // Eliminar el producto de la base de datos usando su clave única
                        databaseReference.child(productoKey).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProductoActivity.this, "Producto borrado correctamente", Toast.LENGTH_SHORT).show();
                                            limpiarCampos();
                                        } else {
                                            Toast.makeText(ProductoActivity.this, "Error al borrar el producto", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        return;
                    }
                } else {
                    Toast.makeText(ProductoActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductoActivity.this, "Error al buscar el producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCampos() {
        codigoEditText.setText("");
        nombreEditText.setText("");
        stockEditText.setText("");
        precioCostoEditText.setText("");
        precioVentaEditText.setText("");
    }
}
