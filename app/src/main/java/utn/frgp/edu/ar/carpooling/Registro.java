package utn.frgp.edu.ar.carpooling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Rol;
import utn.frgp.edu.ar.carpooling.entities.Usuario;
import utn.frgp.edu.ar.carpooling.utils.Validadores;

public class Registro extends AppCompatActivity {

    private EditText nombre, apellido, telefono, nacimiento, password, reingresoPassword, dni;
    Context context;
    private Button registrar;
    private String regExpNoNumbers = "^([^0-9]*)$";
    private String regExpHasNonNumericChar = "[^0-9]";
    private String regExpEmail = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";
    private String regExpPassword = "\"^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$\"";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Carpooling");
        context = this;
        nombre = findViewById(R.id.etEditarPerfilNombre);
        apellido = findViewById(R.id.etEditarPerfilApellido);
        telefono = findViewById(R.id.etEditarPerfilTelefono);
        nacimiento = findViewById(R.id.etEditarPerfilNacimiento);
        password = findViewById(R.id.etEditarPerfilPassword);
        reingresoPassword = findViewById(R.id.etEditarPerfilRepetirPassword);
        registrar = (Button) findViewById(R.id.btnEditarPerfilInformacionPersonal);
        nacimiento.setFocusable(false);
        nacimiento.setFocusableInTouchMode(false);
        nacimiento.setInputType(InputType.TYPE_NULL);


        /*
        nombre.setText("Tobias");
        apellido.setText("Olea");
        telefono.setText("+54 9 11 6920 3645");
        nacimiento.setText("12/05/1997");
        password.setText("12345678");
        reingresoPassword.setText("12345678");
        */
        
        // Seteo de eventos
        registrar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                boolean isValid = true;
                isValid = Validadores.validarNombre(isValid,nombre);
                isValid = Validadores.validarApellido(isValid,apellido);
                isValid = Validadores.validarTelefono(isValid,telefono);
                isValid = Validadores.validarNacimiento(isValid,nacimiento);
                isValid = Validadores.validarPassword(isValid,password, true);
                isValid = Validadores.validarReingresoPassword(isValid,reingresoPassword,password, true);

                if (!isValid) return;

                Rol rol = new Rol();
                rol.setId(getIntent().getStringExtra("rol").toString());

                Integer anio = Integer.parseInt(nacimiento.getText().toString().substring(6,10));
                Integer mes = Integer.parseInt(nacimiento.getText().toString().substring(3,5));
                Integer dia = Integer.parseInt(nacimiento.getText().toString().substring(0,2));

                Usuario usuario = new Usuario(
                        getIntent().getStringExtra("email"),
                        rol,
                        password.getText().toString(),
                        nombre.getText().toString(),
                        apellido.getText().toString(),
                        LocalDate.of(anio,mes,dia),
                        telefono.getText().toString(),
                        getIntent().getStringExtra("dni").toString(),
                        true
                );
                new InsertarUsuario(usuario).execute();
            }
        });
    }

    public void onClickFechaNacimiento(View view) {

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                String selectedDate =
                        (day < 10 ? "0" + day :  day) + "/" +
                                (month + 1 < 10 ? "0" + (month+1) : (month+1)) +"/" +
                                year;
                nacimiento.setText(selectedDate);
                nacimiento.setError(null);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    private class InsertarUsuario extends AsyncTask<Void,Integer,Boolean> {

        Usuario Usuario;

        public InsertarUsuario(Usuario _usuario) {
            Usuario = _usuario;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                query += "INSERT INTO Usuarios";
                query += "(Email,";
                query += "Rol,";
                query += "Pass,";
                query += "Nombre,";
                query += "Apellido,";
                query += "Nacimiento,";
                query += "Telefono,";
                query += "Dni,";
                query += "EstadoRegistro)";
                query += "VALUES";
                query += "(";
                query +=  "'" + Usuario.getEmail() + "',";
                query +=  "'" + Usuario.getRol().getId() + "',";
                query +=  "'" + Usuario.getPassword() + "',";
                query +=  "'" + Usuario.getNombre() + "',";
                query +=  "'" + Usuario.getApellido() + "',";
                query +=  "'" + Usuario.getNacimiento().toString() + "',";
                query +=  "'" + Usuario.getTelefono() + "',";
                query +=  "'" + Usuario.getDni() + "',";
                query +=  "1";
                query += ")";

                int resultado = st.executeUpdate(query);

                query = "SELECT Id, Dni FROM Usuarios WHERE Email = '" + Usuario.getEmail()  + "' AND Rol = '" + Usuario.getRol().getId() + "'";
                ResultSet rs = st.executeQuery(query);
                rs.next();
                Integer id = rs.getInt("Id");
                String dni = rs.getString("Dni");

                if(resultado > 0) {
                    SharedPreferences sharedPreference = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putString("Email", Usuario.getEmail());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Dni",  dni);
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Nombre",  Usuario.getNombre());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Apellido",  Usuario.getApellido());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Rol",  Usuario.getRol().getId());
                    editor.commit();

                    editor = sharedPreference.edit();
                    editor.putString("Id",  id.toString());
                    editor.commit();

                    return true;
                }
                else {
                    return false;
                }


            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                Toast.makeText(context, "Registo completo ¡BIENVENIDO!.", Toast.LENGTH_SHORT).show();

                Intent nextForm = new Intent();

                if(Usuario.getRol().getId().equals("CON")){
                    nextForm = new Intent(context, Home.class);
                }
                if(Usuario.getRol().getId().equals("PAS")) {
                    nextForm = new Intent(context, Home.class);
                }
                nextForm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nextForm);

            }else{
                Toast.makeText(context, "No se pudo completar el registro.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}