package utn.frgp.edu.ar.carpooling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.utils.Helper;
import utn.frgp.edu.ar.carpooling.utils.Validadores;

public class PreRegistro extends AppCompatActivity {

    private Spinner spRol;
    private Context contexto;
    private Button btnContinuar;
    private EditText etEmail, etDNI;
    private TextView tvError;
    private String selectedRol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_registro);
        getSupportActionBar().setTitle("Carpooling");

        // Asignacion de variables
        contexto = this;
        spRol = (Spinner) findViewById(R.id.spPreRegistroRol);
        btnContinuar = (Button) findViewById(R.id.btnEditarPerfilInformacionPersonal);
        etEmail = (EditText) findViewById(R.id.etEditarPerfilNombre);
        etDNI = (EditText) findViewById(R.id.etEditarPerfilDni);
        tvError = (TextView) findViewById(R.id.tvPreRegistroError);

        //etEmail.setText("tobiolea97@gmail.com");
        //etDNI.setText("11222333");

        // Carga de roles
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("Conductor");
        roles.add("Pasajero");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRol.setAdapter(adapter);

        // Seteo de eventos
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvError.setText("");
                boolean isValidForm = true;
                isValidForm = Validadores.validarEmail(isValidForm,etEmail);
                isValidForm = Validadores.validarDNI(isValidForm,etDNI);

                if(!isValidForm) return;

                new ValidarEmailRol().execute();

            }
        });

    }

    private class ValidarEmailRol extends AsyncTask<Void,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                selectedRol = spRol.getSelectedItem().equals("Conductor") ? "CON" : "PAS";

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT * FROM Usuarios WHERE Email = '";
                query += Helper.RemoverCaracteresSQLInjection(etEmail.getText().toString());
                query += "' AND Rol = '" + selectedRol + "'";
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
                while (resultados.next()) {
                    exists = true;
                }

                if(exists) {
                    tvError.setText("Email ya registrado como " + spRol.getSelectedItem().toString());
                    return;
                }
                new ValidarDNIRol().execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class ValidarDNIRol extends AsyncTask<Void,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                selectedRol = spRol.getSelectedItem().equals("Conductor") ? "CON" : "PAS";

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT * FROM Usuarios WHERE Dni = '";
                query += Helper.RemoverCaracteresSQLInjection(etDNI.getText().toString());
                query += "' AND Rol = '" + selectedRol + "'";
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
                while (resultados.next()) {
                    exists = true;
                }

                if (exists) {
                    tvError.setText("DNI ya registrado como " + spRol.getSelectedItem().toString());
                    return;
                }

                Intent nextForm = new Intent(contexto, Registro.class);

                selectedRol = spRol.getSelectedItem().equals("Conductor") ? "CON" : "PAS";

                nextForm.putExtra("email", etEmail.getText().toString());
                nextForm.putExtra("rol", selectedRol);
                nextForm.putExtra("dni", etDNI.getText().toString());

                startActivity(nextForm);
                //finish();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}