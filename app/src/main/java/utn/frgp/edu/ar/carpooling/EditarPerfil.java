package utn.frgp.edu.ar.carpooling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Rol;
import utn.frgp.edu.ar.carpooling.entities.Usuario;
import utn.frgp.edu.ar.carpooling.utils.Helper;
import utn.frgp.edu.ar.carpooling.utils.Validadores;

public class EditarPerfil extends AppCompatActivity {

    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, idUsuario;
    EditText nombre, apellido, nacimiento, telefono, dni, email, password, reingresoPassword;
    TextView errorDNI, errorEmail;
    Button guardarInfoPersonal, guardarInfoLogin;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        context = this;

        nombre = findViewById(R.id.etEditarPerfilNombre);
        apellido = findViewById(R.id.etEditarPerfilApellido);
        nacimiento = findViewById(R.id.etEditarPerfilNacimiento);
        telefono = findViewById(R.id.etEditarPerfilTelefono);
        dni = findViewById(R.id.etEditarPerfilDni);
        email = findViewById(R.id.etEditarPerfilEmail);
        guardarInfoPersonal = findViewById(R.id.btnEditarPerfilInformacionPersonal);
        guardarInfoLogin = findViewById(R.id.btnEditarPerfilInformacionLogin);
        errorDNI = findViewById(R.id.tvEditarPerfilErrorDNI);
        errorEmail = findViewById(R.id.tvEditarPerfilErrorEmail);
        password = findViewById(R.id.etEditarPerfilPassword);
        reingresoPassword = findViewById(R.id.etEditarPerfilRepetirPassword);

        nombreUsuario = spSesion.getString("Nombre","No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        idUsuario = spSesion.getString("Id","No hay datos");

        nacimiento.setFocusable(false);
        nacimiento.setFocusableInTouchMode(false);
        nacimiento.setInputType(InputType.TYPE_NULL);


        String Rol="";
        if(rolUsuario.equals("CON")){
            Rol="Conductor";
        }else{
            Rol="Pasajero";
        }

        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: " + Rol);

        new CargarDatos().execute();

        guardarInfoPersonal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                boolean isValid = true;
                isValid = Validadores.validarNombre(isValid,nombre);
                isValid = Validadores.validarApellido(isValid,apellido);
                isValid = Validadores.validarTelefono(isValid,telefono);
                isValid = Validadores.validarNacimiento(isValid,nacimiento);
                isValid = Validadores.validarDNI(isValid,dni);

                if (!isValid) return;

                new ValidarDNIRol().execute();

                /*utn.frgp.edu.ar.carpooling.entities.Rol rol = new Rol();
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
                new Registro.InsertarUsuario(usuario).execute();*/
            }
        });

        guardarInfoLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                boolean isValid = true;
                isValid = Validadores.validarPassword(isValid,password,false);
                isValid = Validadores.validarReingresoPassword(isValid,reingresoPassword, password, false);
                isValid = Validadores.validarEmail(isValid,email);

                if (!isValid) return;

                new ValidarEmailRol().execute();
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

    @Override
    public boolean onCreateOptionsMenu(Menu miMenu) {
        SharedPreferences sp = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        if(sp.getString("Rol","No hay datos").equals("CON")) {
            getMenuInflater().inflate(R.menu.menu_conductor, miMenu);
        }

        if(sp.getString("Rol","No hay datos").equals("PAS")) {
            getMenuInflater().inflate(R.menu.menu_pasajero, miMenu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcionMenu) {
        int id = opcionMenu.getItemId();

        SharedPreferences sp = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        if(sp.getString("Rol","No hay datos").equals("CON")) {

            if (id == R.id.misViajes) {
                Intent intent = new Intent(this, MisViajes.class);
                startActivity(intent);
            }

            if (id == R.id.crearViaje) {
                Intent intent = new Intent(this, NuevoViaje.class);
                startActivity(intent);
            }

        }

        if(sp.getString("Rol","No hay datos").equals("PAS")) {
            if (id == R.id.misSolicitudes) {
                Intent intent = new Intent(this, MisViajesModoPasajero.class);
                startActivity(intent);
            }

            if (id == R.id.crearSolicitud) {
                Intent intent = new Intent(this, NuevaSolicitud.class);
                startActivity(intent);
            }
            if (id == R.id.misPeticiones) {
                Intent intent = new Intent(this, MisPeticionesPasajero.class);
                startActivity(intent);
            }
        }

        if (id == R.id.miperfil) {
            finish();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }

        if (id == R.id.notificaciones) {
            Intent intent = new Intent(this, utn.frgp.edu.ar.carpooling.Notificaciones.class);
            startActivity(intent);
        }

        if (id == R.id.editarPerfil) {
            Intent intent = new Intent(this, EditarPerfil.class);
            startActivity(intent);
        }

        if (id == R.id.cerrarSesion) {

            SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = spSesion.edit();
            editor.clear();
            editor.commit();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(opcionMenu);
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem currentOption = menu.findItem(R.id.editarPerfil);
        currentOption.setVisible(false);

        return true;
    }

    private class CargarDatos extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT * FROM Usuarios WHERE Email = '";
                query += emailUsuario;
                query += " ' AND Rol = '";
                query += rolUsuario + "'";

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {

                String nacimientoFormateado = "";

                if (resultados.next()) {
                    nombre.setText(resultados.getString("Nombre"));
                    apellido.setText(resultados.getString("Apellido"));
                    nacimientoFormateado = resultados.getString("Nacimiento").substring(8,10) + "/";
                    nacimientoFormateado += resultados.getString("Nacimiento").substring(5,7) + "/";
                    nacimientoFormateado += resultados.getString("Nacimiento").substring(0,4);
                    nacimiento.setText(nacimientoFormateado);
                    telefono.setText(resultados.getString("Telefono"));
                    dni.setText(resultados.getString("Dni"));
                    email.setText(resultados.getString("Email"));
                }

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

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT * FROM Usuarios WHERE Dni = '";
                query += Helper.RemoverCaracteresSQLInjection(dni.getText().toString());
                query += "' AND Rol = '" + rolUsuario + "' AND Email <> '" + emailUsuario + "'";
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
                    errorDNI.setText("DNI ya registrado como " + rolUsuario);
                    return;
                }
                else {
                    errorDNI.setText("");
                    new ActualizarInfoPersonal().execute();
                    return;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class ValidarEmailRol extends AsyncTask<Void,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT * FROM Usuarios WHERE Email = '";
                query += Helper.RemoverCaracteresSQLInjection(email.getText().toString());
                query += "' AND Rol = '" + rolUsuario + "' AND Email <> '" + emailUsuario + "'";
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
                    errorEmail.setText("Email ya registrado como " + rolUsuario);
                    return;
                }
                else {
                    errorDNI.setText("");
                    new ActualizarDatosLogin().execute();
                    return;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class ActualizarInfoPersonal extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                Integer anio = Integer.parseInt(nacimiento.getText().toString().substring(6,10));
                Integer mes = Integer.parseInt(nacimiento.getText().toString().substring(3,5));
                Integer dia = Integer.parseInt(nacimiento.getText().toString().substring(0,2));

                String query = "";
                query += "UPDATE Usuarios SET ";
                query += "Nombre='" + nombre.getText() + "',";
                query += "Apellido='" + apellido.getText() + "',";
                query += "Nacimiento='" + anio + "-" + mes + "-" + dia + "',";
                query += "Dni='" + dni.getText() + "',";
                query += "Telefono='" + telefono.getText() + "'";
                query += " WHERE Id = " + idUsuario;

                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if (resultado) {
                Toast.makeText(context, "Su información fué actualizada correctamente", Toast.LENGTH_LONG).show();
                SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spSesion.edit();
                editor.clear();
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Id", idUsuario);
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Dni", dni.getText().toString());
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Email", emailUsuario);
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Nombre",  nombre.getText().toString());
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Apellido",  apellido.getText().toString());
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Rol",  rolUsuario);
                editor.commit();

                Intent intent = new Intent(context, Home.class);
                startActivity(intent);

                finish();
            }
            else Toast.makeText(context, "Ocurrio un error, intentelo nuevamente", Toast.LENGTH_LONG).show();
        }
    }

    private class ActualizarDatosLogin extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                Integer anio = Integer.parseInt(nacimiento.getText().toString().substring(6,10));
                Integer mes = Integer.parseInt(nacimiento.getText().toString().substring(3,5));
                Integer dia = Integer.parseInt(nacimiento.getText().toString().substring(0,2));

                String query = "";
                String stringPassword = "";
                query += "UPDATE Usuarios SET ";
                query += "Email='" + email.getText().toString() + "' ";
                stringPassword = password.getText().toString();
                if(!stringPassword.equals(""))
                    query += ", Pass='" + password.getText().toString() + "' ";
                query += " WHERE Id = " + idUsuario;

                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if (resultado) {
                Toast.makeText(context, "Su información fué actualizada correctamente", Toast.LENGTH_LONG).show();
                SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spSesion.edit();
                editor.clear();
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Id", idUsuario);
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Email", email.getText().toString());
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Nombre",  nombreUsuario);
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Apellido",  apellidoUsuario);
                editor.commit();

                editor = spSesion.edit();
                editor.putString("Rol",  rolUsuario);
                editor.commit();

                Intent intent = new Intent(context, Home.class);
                startActivity(intent);

                finish();
            }
            else Toast.makeText(context, "Ocurrio un error, intentelo nuevamente", Toast.LENGTH_LONG).show();
        }
    }
}