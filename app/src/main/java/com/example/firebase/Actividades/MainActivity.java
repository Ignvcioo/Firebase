package com.example.firebase.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;
public class MainActivity extends AppCompatActivity {

    TextInputEditText email;
    TextInputEditText password;
    Button loguearse;
    Button registrarse;
    FirebaseAuth autenticacion;
    DatabaseReference baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        loguearse = findViewById(R.id.btnLoguearse);
        registrarse = findViewById(R.id.btnRegistrar);
        autenticacion = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        loguearse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void iniciarSesion() {
        String emailObtenido = email.getText().toString();
        String passwordObtenido = password.getText().toString();

        if (!emailObtenido.isEmpty() && !passwordObtenido.isEmpty()) {
            if (passwordObtenido.length() >= 6) {
                autenticacion.signInWithEmailAndPassword(emailObtenido, passwordObtenido).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
                            startActivity(intent);
                            FancyToast.makeText(MainActivity.this, "Inicio sesión correctamente", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            finish();
                        }
                        else {
                            FancyToast.makeText(MainActivity.this, "La contraseña o correo es incorrecto", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        }
                    }
                });
            }
            else {
                FancyToast.makeText(MainActivity.this, "La contraseña debe tener al menos 6 caracteres", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        }
        else {
            FancyToast.makeText(MainActivity.this, "Faltan campos por llenar", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
    }
}