package com.example.firebase.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.firebase.R;
import com.example.firebase.modelo.Clientes;
import com.example.firebase.proveedores.ProveedoresAutenticacion;
import com.example.firebase.proveedores.ProveedoresCliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class RegisterActivity extends AppCompatActivity {
    ProveedoresAutenticacion proveedoresAutenticacion;
    ProveedoresCliente proveedoresCliente;
    Button registrar;
    TextInputEditText inputEmail;
    TextInputEditText inputPassword;
    TextInputEditText inputNombreUsuario;
    TextInputEditText inputTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        proveedoresAutenticacion = new ProveedoresAutenticacion();
        proveedoresCliente = new ProveedoresCliente();
        registrar = findViewById(R.id.btnRegistrarCliente);
        inputEmail = findViewById(R.id.txtInputEmail);
        inputPassword = findViewById(R.id.txtInputPassword);
        inputNombreUsuario = findViewById(R.id.txtInputNombre);
        inputTelefono = findViewById(R.id.txtInputTelefono);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarse();
            }
        });
    }

    public void registrarse() {
        final String email = inputEmail.getText().toString();
        final String nombreUsuario = inputNombreUsuario.getText().toString();
        final String contrasenia = inputPassword.getText().toString();
        final String telefono = inputTelefono.getText().toString();

        if (!email.isEmpty() && !nombreUsuario.isEmpty() && !contrasenia.isEmpty() && !telefono.isEmpty()){
            if (contrasenia.length() >= 6){
                registrarCliente(nombreUsuario, email, contrasenia, telefono);
            }
            else {
                FancyToast.makeText(RegisterActivity.this, "Mínimo 6 caracteres", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        }
        else {
            FancyToast.makeText(RegisterActivity.this, "Faltan campos por rellenar", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
    }

    void registrarCliente(final String nombreUsuario, String email, String contrasenia, String telefono){
        proveedoresAutenticacion.registrarse(email, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Clientes clientes = new Clientes(id, email, nombreUsuario, telefono);
                    Crear(clientes);
                    FancyToast.makeText(RegisterActivity.this, "Su registro fue exitoso", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                }
                else {
                    FancyToast.makeText(RegisterActivity.this, "El correo ya esta en uso.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            }
        });
    }

    void Crear(Clientes clientes){
        proveedoresCliente.create(clientes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //FancyToast.makeText(RegistrarActividad.this, "El registro fue exitoso", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    FancyToast.makeText(RegisterActivity.this, "El correo ya esta en uso.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            }
        });
    }
}