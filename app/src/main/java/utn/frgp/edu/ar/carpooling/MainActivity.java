package utn.frgp.edu.ar.carpooling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Rol;
import utn.frgp.edu.ar.carpooling.entities.Usuario;
import utn.frgp.edu.ar.carpooling.utils.Helper;

public class MainActivity extends AppCompatActivity {

    private String regExpEmail = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";
    TextView email, password,register;
    private Button login;
    Context context;
    private Spinner spRol;
    int c=1;
    private View Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        SharedPreferences spSesion2 = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSesion2.edit();
        editor.clear();
        editor.commit();*/


        try {
            Info = findViewById(R.id.tvEditarPerfilInformacionPersona);
            SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
            String emailUsuario = spSesion.getString("Email","");

            if(!emailUsuario.equals("")) {
                Intent nextForm = new Intent(this, Home.class);
                startActivity(nextForm);
                finish();
            }

        }
        catch(Exception e) {

        }

        context=this;
        email = findViewById(R.id.etMainActivityEmail);
        password = findViewById(R.id.etMainActivityPassword);
        login=findViewById(R.id.btnMainActivityLogin);
        register=findViewById(R.id.tvMainActivitySeparador);
        spRol = (Spinner) findViewById(R.id.spActivityMain);

        /*
        email.setText("tobi@mail.com");
        password.setText("password");
        */

        // Carga de roles
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("Conductor");
        roles.add("Pasajero");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRol.setAdapter(adapter);




    }

    public void onClickRegistrar(View view) {
        Intent nextForm = new Intent(this, PreRegistro.class);
      //  login.setEnabled(true);
        startActivity(nextForm);
        //finish();
    }

    public void onClickLogin(View view) {

        boolean isValid = true;
        isValid = validarEmail(isValid);
        isValid = validarPassword(isValid);

        if(!isValid) return;

        if(spRol.getSelectedItem().equals("Conductor")){
            new IngresoConductor().execute();
        }else{
            new IngresoPasajero().execute();
        }
    }

    // Validaciones
    private boolean validarEmail(boolean flag) {
        if(email.getText().toString().equals("")) {
            email.setError("Campo obligatorio");
            return false;
        }
        if(!email.getText().toString().matches(regExpEmail)) {
            email.setError("Formato requerido: ejemplo@dominio.com");
            return false;
        }
        if(email.getText().toString().length() >= 30) {
            email.setError("Este campo admite un maximo de 30 characteres");
            return false;
        }
        email.setError(null);
        return flag;
    }

    private boolean validarPassword(boolean flag) {
        if(password.getText().toString().equals("")) {
            password.setError("Campo obligatorio");
            return false;
        }
        password.setError(null);
        return flag;
    }

    private class IngresoConductor extends AsyncTask<Void,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

              String query = "";
               query += "SELECT * FROM Usuarios WHERE Email = '";
                query += Helper.RemoverCaracteresSQLInjection(email.getText().toString());
                query+="' AND Pass ='";
                query += password.getText();
                query += "' AND Rol ='CON'";

           // query="Select * FROM Usuarios WHERE Email='"+Helper.RemoverCaracteresSQLInjection(email.getText().toString())+"' AND Pass='"+password.getText()+"' AND Rol='PAS'";

                return st.executeQuery(query);

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                boolean exists = false;
                Usuario usuario = new Usuario();
                while (resultados.next()) {
                    exists = true;
                    usuario.setId(resultados.getInt("Id"));
                    usuario.setNombre(resultados.getString("Nombre"));
                    usuario.setApellido(resultados.getString("Apellido"));
                    usuario.setEmail(resultados.getString("Email"));
                    usuario.setDni(resultados.getString("Dni"));
                    usuario.setRol(new Rol(spRol.getSelectedItem().toString().equals("Conductor") ? "CON" : "PAS"));
                }

                if(exists) {
                    //INGRESO AL MENU CONDUCTOR
                    c=0;
                    login.setEnabled(true);

                    SharedPreferences sharedPreference = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putString("Email", usuario.getEmail());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Dni", usuario.getDni());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Id",  usuario.getId().toString());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Nombre",  usuario.getNombre());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Apellido",  usuario.getApellido());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Rol",  usuario.getRol().getId());
                    editor.commit();

                    Intent pagConductor= new Intent(context,Home.class);
                    startActivity(pagConductor);

                    finish();

                }else{
                    if(c==3){
                        Toast.makeText(MainActivity.this, "Numeros de intentos maximos.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Cierre la aplicacion y vuelva a intentar.", Toast.LENGTH_LONG).show();
                        login.setEnabled(false);
                        register.setEnabled(false);

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Datos incorrectos. Intentos "+c+" de 3.", Toast.LENGTH_SHORT).show();
                        c++;


                    }

                }

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class IngresoPasajero extends AsyncTask<Void,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT * FROM Usuarios WHERE Email = '";
                query += Helper.RemoverCaracteresSQLInjection(email.getText().toString());
                query+="' AND Pass = '";
                query += password.getText();
                query += "' AND Rol ='PAS'";
                return st.executeQuery(query);

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                boolean exists = false;
                Usuario usuario = new Usuario();
                while (resultados.next()) {
                    exists = true;
                    usuario.setId(resultados.getInt("Id"));
                    usuario.setNombre(resultados.getString("Nombre"));
                    usuario.setApellido(resultados.getString("Apellido"));
                    usuario.setEmail(resultados.getString("Email"));
                    usuario.setDni(resultados.getString("Dni"));
                    usuario.setRol(new Rol(spRol.getSelectedItem().toString().equals("Conductor") ? "CON" : "PAS"));
                }

                if(exists) {

                    c=0;
                    login.setEnabled(true);

                    SharedPreferences sharedPreference = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putString("Email", usuario.getEmail());
                    editor.commit();
                    
                    editor = sharedPreference.edit();
                    editor.putString("Dni", usuario.getDni());
                    editor.commit();
                    
                    editor = sharedPreference.edit();
                    editor.putString("Nombre",  usuario.getNombre());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Apellido",  usuario.getApellido());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Rol",  usuario.getRol().getId());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Id",  usuario.getId().toString());
                    editor.commit();

                    Intent pagPasajero= new Intent(context,Home.class);
                    startActivity(pagPasajero);
                    finish();

                }
                else {
                    if(c==3){
                        Toast.makeText(MainActivity.this, "Números de intentos máximos. Cierre la aplicacion para volver intentar.", Toast.LENGTH_SHORT).show();
                        login.setEnabled(false);
                        register.setEnabled(false);
                    }
                    else{
                        Toast.makeText(MainActivity.this, " Datos Incorrectos Intentos "+c+" de 3.", Toast.LENGTH_SHORT).show();
                        c++;
                    }

                }

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}