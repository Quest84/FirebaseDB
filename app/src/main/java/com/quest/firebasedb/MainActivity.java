package com.quest.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quest.firebasedb.Modelos.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<Persona> arrayAdapterPersona;
    EditText nombP, appP, correoP, passwordP;
    ListView listView_personas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Persona personaselected;
    MenuItem addItem;
    private List<Persona> listPersona = new ArrayList<Persona>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        inicializarFirebase();

        listarDatos();

        listView_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaselected = (Persona)  parent.getItemAtPosition(position);
                nombP.setText(personaselected.getNombre());
                appP.setText(personaselected.getApellidos());
                correoP.setText(personaselected.getCorreo());
                passwordP.setText(personaselected.getPassword());
                ocultarAdd(addItem);

            }
        });

    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPersona.clear();
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Persona p = objSnapshot.getValue(Persona.class);
                    listPersona.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1,listPersona);
                    listView_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initComponents(){
        nombP = findViewById(R.id.editTextNombre);
        appP = findViewById(R.id.editTextApellido);
        correoP = findViewById(R.id.editTextCorreo);
        passwordP = findViewById(R.id.editTextContraseña);

        listView_personas = findViewById(R.id.listView_datosPersonas);
    }

    public void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        addItem = menu.findItem(R.id.icon_add);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombre = nombP.getText().toString();
        String apellidos = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();

        /* Switch para las opciones */
        switch(item.getItemId()){
            case R.id.icon_new:{
                limpiarCajas();
                mostrarAdd(addItem);
                return true;
            }

            case R.id.icon_add:{
                if (nombre.equals("") || apellidos.equals("") || correo.equals("") || password.equals("")){
                    validacion();
                } else {
                    agregar();
                    Toast.makeText(this, "Registro agregado", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }
                return true;
            }
            case R.id.icon_save:{
                guardar();
                Toast.makeText(this, "Registro actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                mostrarAdd(addItem);
                return true;
            }
            case R.id.icon_delete:{
                eliminar();
                Toast.makeText(this, "Registro eliminado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                mostrarAdd(addItem);
                return true;
            }

            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void agregar(){
        String nombre = nombP.getText().toString();
        String apellidos = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();

        Persona p = new Persona();
        p.setUid(UUID.randomUUID().toString());
        p.setNombre(nombre);
        p.setApellidos(apellidos);
        p.setCorreo(correo);
        p.setPassword(password);

        databaseReference.child("Persona").child(p.getUid()).setValue(p);
    }

    private void limpiarCajas() {
        nombP.setText("");
        appP.setText("");
        correoP.setText("");
        passwordP.setText("");
    }

    private void validacion() {
        String nombre = nombP.getText().toString();
        String apellidos = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();

        if(nombre.equals("")){
            nombP.setError("Required");
        }

        if(apellidos.equals("")){
            appP.setError("Required");
        }

        if(correo.equals("")){
            correoP.setError("Required");
        }

        if(password.equals("")){
            passwordP.setError("Required");
        }
    }

    private void guardar(){
        Persona p = new Persona();
        p.setUid(personaselected.getUid());
        p.setNombre(nombP.getText().toString().trim());
        p.setApellidos(appP.getText().toString().trim());
        p.setCorreo(correoP.getText().toString().trim());
        p.setPassword(passwordP.getText().toString().trim());
        databaseReference.child("Persona").child(p.getUid()).setValue(p);
    }

    private void eliminar(){
        Persona p = new Persona();
        p.setUid(personaselected.getUid());
        databaseReference.child("Persona").child(p.getUid()).removeValue();

    }

    private void ocultarAdd(MenuItem item){
        this.addItem.setVisible(false);
    }

    private void mostrarAdd(MenuItem item){
        this.addItem.setVisible(true);
    }

}
