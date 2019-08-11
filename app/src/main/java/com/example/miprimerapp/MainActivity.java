package com.example.miprimerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
private ZXingScannerView escanerView;

private EditText et_numerotarea, et_libro, et_decripcion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_numerotarea = (EditText)findViewById(R.id.id_numtarea);
        et_libro = (EditText)findViewById(R.id.id_nombre_libro);
        et_decripcion = (EditText)findViewById(R.id.id_descripcion_tarea);

    }

    //Metoo para dar de alta los productos
    public void Registrar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_numerotarea.getText().toString();
        String descripcion = et_libro.getText().toString();
        String descripciondos = et_decripcion.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !descripciondos.isEmpty() ){
            ContentValues registro = new ContentValues();

            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("descripciondos", descripciondos);

            //---------------------------------------
            //Guardar los datos dentro de la tabla
            //---------------------------------------
            BaseDeDatos.insert("tb_tareas", null, registro);
            BaseDeDatos.close();
            et_numerotarea.setText("");
            et_libro.setText("");
            et_decripcion.setText("");
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }


    //Método para consultar un artículo o producto
    public void Buscar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        String codigo = et_numerotarea.getText().toString();

        if(!codigo.isEmpty()){
            Cursor fila = BaseDeDatabase.rawQuery
                    ("select descripcion, descripciondos from tb_tareas where codigo =" + codigo, null);

            if(fila.moveToFirst()){
                et_libro.setText(fila.getString(0));
                et_decripcion.setText(fila.getString(1));
                BaseDeDatabase.close();
            } else {
                Toast.makeText(this,"No existe el artículo", Toast.LENGTH_SHORT).show();
                BaseDeDatabase.close();
            }

        } else {
            Toast.makeText(this, "Debes introducir el código del artículo", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para eliminar un producto o Artículo
    public void Eliminar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper
                (this, "administracion", null, 1);
        SQLiteDatabase BaseDatabase = admin.getWritableDatabase();

        String codigo = et_numerotarea.getText().toString();

        if(!codigo.isEmpty()){

            int cantidad = BaseDatabase.delete("tb_tareas", "codigo=" + codigo, null);
            BaseDatabase.close();

            et_numerotarea.setText("");
            et_libro.setText("");
            et_decripcion.setText("");

            if(cantidad == 1){
                Toast.makeText(this, "Artículo eliminado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El artículo no existe", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debes de introducir el código del artículo", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para modificar un artículo o producto
    public void Modificar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDatabase = admin.getWritableDatabase();

        String codigo = et_numerotarea.getText().toString();
        String descripcion = et_libro.getText().toString();
        String descripciondos = et_decripcion.getText().toString();

        if(!codigo.isEmpty() && !descripcion.isEmpty() && !descripciondos.isEmpty()){

            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("descripciondos", descripciondos);

            int cantidad = BaseDatabase.update("tb_tareas", registro, "codigo=" + codigo, null);
            BaseDatabase.close();

            if(cantidad == 1){
                Toast.makeText(this, "Artículo modificado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El artículo no existe", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void EscanerQR(View view){
        escanerView = new ZXingScannerView(this);
        setContentView(escanerView);
        escanerView.setResultHandler(this);
        escanerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        escanerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String dato = result.getText();     //Obtengo el contenido del qr (El contenido debe estar separado por ";" )
        String [] elementos = dato.split(";"); //el separador es un ;

        setContentView(R.layout.activity_main);
        escanerView.stopCamera();

        //Obtengo los ids de los campos de texto:
        et_numerotarea = (EditText)findViewById(R.id.id_numtarea);
        et_libro = (EditText)findViewById(R.id.id_nombre_libro);
        et_decripcion = (EditText)findViewById(R.id.id_descripcion_tarea);

        //Valido si el formato del qr trae al menos 3 datos (No atividad, Libro, Descripcion de la tarea
        if (elementos.length >= 2) {
            //et_libro.setText(dato);
            et_numerotarea.setText(elementos[0].toString());
            et_libro.setText(elementos[1].toString());
            et_decripcion.setText(elementos[2].toString());
        }
        else {
            et_numerotarea.setText("");
            et_libro.setText("QR incorrecto");
            et_decripcion.setText("");
        }

        //Codigo para poner un mensaje con el contenido del qr
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultado del scaner");
        builder.setMessage(result.getText());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        */

    }
}
