package com.example.firebaserealtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InventarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inventario, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_ventas) {

            startActivity(new Intent(this, VentasActivity.class));
            return true;
        } else if (itemId == R.id.action_producto) {

            startActivity(new Intent(this, ListaProductosActivity.class));
            return true;
        } else if (itemId == R.id.action_inventario) {

            startActivity(new Intent(this, ComprasActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
