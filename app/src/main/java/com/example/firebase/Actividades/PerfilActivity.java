package com.example.firebase.Actividades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.modelo.Clientes;
import com.example.firebase.proveedores.ProveedoresCliente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends AppCompatActivity {
    private String urlGuardada;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnSeleccionarImagen, btnSubirImagen;
    private TextView txtNombreImagen;
    private TextInputEditText txtNombreArchivo;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imagenUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask storageTask;
    private Button btnModificarDatos;
    private TextInputEditText txtNombreUsuario, txtTelefonoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        btnSeleccionarImagen = findViewById(R.id.button_choose_image);
        btnSubirImagen = findViewById(R.id.button_upload);
        txtNombreImagen = findViewById(R.id.text_view_show_uploads);
        txtNombreArchivo = findViewById(R.id.edit_text_file_name);
        imageView = findViewById(R.id.image_view);
        progressBar = findViewById(R.id.progress_bar);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtTelefonoUsuario = findViewById(R.id.txtTelefonoUsuario);
        btnModificarDatos = findViewById(R.id.btnModificarCambios);

        storageReference = FirebaseStorage.getInstance().getReference("Imagenes");
        databaseReference = FirebaseDatabase.getInstance().getReference("Imagenes");

        // Cargar la imagen de perfil al iniciar la actividad
        urlGuardada = obtenerURLImagenDePreferencias();
        if (!urlGuardada.isEmpty()) {
            Picasso.with(this).load(urlGuardada).into(imageView);
            ajustarImagenAlCirculo();
        }

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnSubirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storageTask != null && storageTask.isInProgress()) {
                    Toast.makeText(PerfilActivity.this, "Cargando archivo", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        txtNombreImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Puedes agregar lógica adicional aquí si es necesario
            }
        });


        cargarDatosUsuario();

        btnModificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoNombre = txtNombreUsuario.getText().toString().trim();
                String nuevoTelefono = txtTelefonoUsuario.getText().toString().trim();

                // Actualizar los datos en la base de datos
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                ProveedoresCliente proveedoresCliente = new ProveedoresCliente();

                if (user != null) {
                    String clienteId = user.getUid();

                    // Actualizar el nombre y el teléfono del cliente en la base de datos
                    proveedoresCliente.actualizarDatos(clienteId, nuevoNombre, nuevoTelefono)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PerfilActivity.this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PerfilActivity.this, "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
    private void cargarDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ProveedoresCliente proveedoresCliente = new ProveedoresCliente();

        if (user != null) {
            String clienteId = user.getUid();

            // Obtener información del cliente por su ID
            proveedoresCliente.obtenerClientePorId(clienteId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Clientes cliente = snapshot.getValue(Clientes.class);
                                if (cliente != null) {
                                    String nombreUsuario = cliente.getNombreUsuario();
                                    String telefonoUsuario = cliente.getTelefono();

                                    txtNombreUsuario.setText(nombreUsuario);
                                    txtTelefonoUsuario.setText(telefonoUsuario);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imagenUri = data.getData();
            Picasso.with(this).load(imagenUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imagenUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imagenUri));

            storageTask = fileReference.putFile(imagenUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);

                            Toast.makeText(PerfilActivity.this, "Subido correctamente", Toast.LENGTH_SHORT).show();

                            // Obtener la URL de descarga
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Subir subir = new Subir(txtNombreArchivo.getText().toString().trim(), uri.toString());
                                    String uploadId = databaseReference.push().getKey();
                                    databaseReference.child(uploadId).setValue(subir);

                                    // Guardar la URL en preferencias compartidas
                                    guardarURLImagenEnPreferencias(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PerfilActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No seleccionó nada", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarURLImagenEnPreferencias(String url) {
        // Puedes ajustar este método para que se adapte a tus necesidades de preferencias compartidas
        // Aquí se utiliza un ejemplo simple usando SharedPreferences
        getPreferences(MODE_PRIVATE).edit().putString("URL_IMAGEN", url).apply();
    }

    private String obtenerURLImagenDePreferencias() {
        // Puedes ajustar este método para que se adapte a tus necesidades de preferencias compartidas
        // Aquí se utiliza un ejemplo simple usando SharedPreferences
        return getPreferences(MODE_PRIVATE).getString("URL_IMAGEN", "");
    }

    private void ajustarImagenAlCirculo() {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                // Obtener dimensiones del círculo
                int anchoCirculo = imageView.getWidth();
                int altoCirculo = imageView.getHeight();

                // Ajustar la escala de la imagen para que se adapte al círculo
                Picasso.with(PerfilActivity.this)
                        .load(urlGuardada)
                        .resize(anchoCirculo, altoCirculo)
                        .centerCrop()
                        .into(imageView);
            }
        });
    }
}