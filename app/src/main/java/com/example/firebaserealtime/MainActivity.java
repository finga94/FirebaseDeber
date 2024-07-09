package com.example.firebaserealtime;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailEditText, passwordEditText;
    private Spinner typeSpinner;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        typeSpinner = findViewById(R.id.typeSpinner);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String selectedType = typeSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }


                loginUser(email, password, selectedType);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password, String selectedType) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                            if (selectedType.equals("Persona")) {

                                Intent intent = new Intent(MainActivity.this, PersonaActivity.class);
                                startActivity(intent);
                            } else if (selectedType.equals("Producto")) {

                                Intent intent = new Intent(MainActivity.this, ProductoActivity.class);
                                startActivity(intent);
                            } else if (selectedType.equals("Inventario")) {

                                Intent intent = new Intent(MainActivity.this, InventarioActivity.class);
                                startActivity(intent);
                            } else {

                                Toast.makeText(MainActivity.this, "Tipo de usuario no soportado.", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {

                            Toast.makeText(MainActivity.this, "Error de autenticación. Verifique correo y contraseña.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
