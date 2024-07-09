package com.example.firebaserealtime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Producto> productList;

    private DatabaseReference productosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);

        // Inicializa la lista de productos
        productList = new ArrayList<>();

        // Configura la referencia a la colección "productos" en Firebase Realtime Database
        productosRef = FirebaseDatabase.getInstance().getReference().child("productos");

        // Configura el RecyclerView
        recyclerView = findViewById(R.id.recycler_view_productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Cargar datos desde Firebase Realtime Database
        cargarProductosDesdeFirebase();

        // Configura el botón "Regresar"
        Button buttonRegresar = findViewById(R.id.button_regresar);
        buttonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad actual al presionar el botón "Regresar"
            }
        });
    }

    private void cargarProductosDesdeFirebase() {
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpia la lista actual de productos
                productList.clear();

                // Itera sobre los nodos hijos (productos) en dataSnapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtiene el Producto de cada nodo y lo agrega a la lista
                    Producto producto = snapshot.getValue(Producto.class);
                    productList.add(producto);
                }

                // Notifica al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja errores de lectura desde Firebase Realtime Database
            }
        });
    }
}
