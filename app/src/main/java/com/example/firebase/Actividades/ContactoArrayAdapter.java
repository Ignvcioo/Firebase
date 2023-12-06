package com.example.firebase.Actividades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.firebase.modelo.Contacto;

import java.util.List;

public class ContactoArrayAdapter extends ArrayAdapter<Contacto> {
    public ContactoArrayAdapter(Context context, List<Contacto> contactos) {
        super(context, 0, contactos);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Obtener el contacto en la posición actual
        Contacto contacto = getItem(position);

        // Verificar si la vista actual está siendo reutilizada, de lo contrario, inflarla
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Obtener las vistas de la interfaz de usuario
        TextView textView = convertView.findViewById(android.R.id.text1);

        // Mostrar la información del contacto en la vista con el formato deseado
        if (contacto != null) {
            String formattedText = String.format("ID: %s\nNombre: %s\nApellido: %s\nCorreo: %s",
                    contacto.getId(), contacto.getNombre(), contacto.getApellido(), contacto.getCorreo());

            textView.setText(formattedText);
        }

        return convertView;
    }
}
