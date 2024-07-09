package com.example.firebaserealtime;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonaActivity extends AppCompatActivity {

    private static final String TAG = "PersonaActivity";

    private EditText cedulaEditText, nombreEditText, provinciaEditText;
    private RadioGroup genderRadioGroup;
    private Spinner countrySpinner;
    private TextView emailTextView;
    private Button updateButton, exitButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    // Variable para almacenar el RadioButton seleccionado
    private RadioButton selectedGenderRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona);

        cedulaEditText = findViewById(R.id.cedulaEditText);
        nombreEditText = findViewById(R.id.nombreEditText);
        provinciaEditText = findViewById(R.id.provinciaEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        countrySpinner = findViewById(R.id.countrySpinner);
        emailTextView = findViewById(R.id.emailTextView);
        updateButton = findViewById(R.id.updateButton);
        exitButton = findViewById(R.id.exitButton);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("personas");

        // Set email to emailTextView
        if (currentUser != null) {
            Log.d(TAG, "Correo actual: " + currentUser.getEmail());
            emailTextView.setText(currentUser.getEmail());
            // Cargar datos del usuario desde Realtime Database
            loadUserData(currentUser.getUid());
        } else {
            Log.d(TAG, "Usuario actual es nulo");
        }

        // Populate country spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void loadUserData(String userId) {
        databaseReference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        Persona persona = dataSnapshot.getValue(Persona.class);
                        if (persona != null) {
                            // Mostrar los datos en los EditText, RadioButtons y Spinner correspondientes
                            cedulaEditText.setText(persona.getCedula());
                            nombreEditText.setText(persona.getNombre());
                            provinciaEditText.setText(persona.getProvincia());

                            // Seleccionar el género correcto
                            if (persona.getGenero() != null) {
                                if (persona.getGenero().equals("Hombre")) {
                                    genderRadioGroup.check(R.id.radioButtonMasculino);
                                } else if (persona.getGenero().equals("Mujer")) {
                                    genderRadioGroup.check(R.id.radioButtonFemenino);
                                }
                            } else {
                                // Aquí puedes establecer un valor predeterminado o dejar sin selección
                                genderRadioGroup.clearCheck();
                            }

                            // Seleccionar el país correcto en el spinner
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) countrySpinner.getAdapter();
                            int position = adapter.getPosition(persona.getPais());
                            if (position != -1) {
                                countrySpinner.setSelection(position);
                            } else {

                                countrySpinner.setSelection(0);
                            }
                        } else {
                            Log.d(TAG, "Persona es nula");
                        }
                    } else {
                        Log.d(TAG, "No se encontraron datos para el usuario con ID: " + userId);
                    }
                } else {
                    Log.d(TAG, "Error al cargar datos del usuario: " + task.getException().getMessage());
                }
            }
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(PersonaActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual para evitar volver atrás con el botón de retroceso
        Log.d(TAG, "Usuario desconectado");
    }

    private void updateUserDetails() {
        String cedula = cedulaEditText.getText().toString().trim();
        String nombre = nombreEditText.getText().toString().trim();
        String provincia = provinciaEditText.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Seleccione un género", Toast.LENGTH_SHORT).show();
            return;
        } else {
            selectedGenderRadioButton = findViewById(selectedGenderId);
        }

        String gender = selectedGenderRadioButton.getText().toString();

        if (TextUtils.isEmpty(cedula) || TextUtils.isEmpty(nombre) || TextUtils.isEmpty(provincia)) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Usuario actualizado: " + currentUser.getEmail());
            String userId = currentUser.getUid();
            Persona persona = new Persona(cedula, nombre, provincia, gender, country, currentUser.getEmail());

            databaseReference.child(userId).setValue(persona)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PersonaActivity.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PersonaActivity.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Log.d(TAG, "Usuario actual es nulo al intentar actualizar");
        }
    }
}
