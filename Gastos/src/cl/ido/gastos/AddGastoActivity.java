package cl.ido.gastos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cl.ido.ejemplo.R;
import cl.ido.gastos.db.DataBaseHelper;
import cl.ido.gastos.model.Categoria;
import cl.ido.gastos.model.Gasto;
import cl.ido.gastos.model.SubCategoria;

public class AddGastoActivity extends Activity {

	ArrayList<String> listadoSubCategorias;
	
	Spinner spnCategorias;
	Spinner spnSubCategorias;

	TextView txtFecha;
    private int mYear;
    private int mMonth;
    private int mDay;

    private static final String TAG = "AddGastoActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_gasto);
		
		//Spinners
		final DataBaseHelper dbh = new DataBaseHelper(this); 

		 
		
		final HashMap<String, ArrayList<SubCategoria>> hashSubCategoriasById = dbh.getSubCategorias();
		
		spnCategorias = (Spinner) findViewById(R.id.spnCategorias);
		spnSubCategorias = (Spinner) findViewById(R.id.spnSubCategorias);
		
		ArrayAdapter<String> categAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbh.getListCategorias());
		categAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategorias.setAdapter(categAdapter);

        Set<String> llaves = hashSubCategoriasById.keySet();
        String firstKey = llaves.iterator().next();
        
        listadoSubCategorias = getSubCategoriasFromList(hashSubCategoriasById.get(firstKey));
        
        final ArrayAdapter<String> subCategAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listadoSubCategorias );
		subCategAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubCategorias.setAdapter(subCategAdapter);        
        
        spnCategorias.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				//Toast.makeText(AddGastoActivity.this, String.valueOf(spnCategorias.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
				String keySubCateg = String.valueOf( spnCategorias.getSelectedItemPosition() + 1 );
				try {
					subCategAdapter.clear();
					for (String txt: getSubCategoriasFromList( hashSubCategoriasById.get(keySubCateg) ) ) {
						subCategAdapter.add( txt );
					}
					subCategAdapter.notifyDataSetChanged();
				} catch(Exception e) {
					Log.e(this.getClass().getName(), "Exception: " +e.getMessage() );
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        	
        });
        //dbh.close();
        
        //Monto
        final EditText editMonto = (EditText) findViewById(R.id.editMonto);
        
        //Descripción
        final EditText editDescripcion = (EditText) findViewById(R.id.editDescripcion);
        
        //Fecha
        txtFecha = (TextView) findViewById(R.id.txtFechaGasto);
    	final Calendar c = Calendar.getInstance();
    	mYear = c.get(Calendar.YEAR);
    	mMonth = c.get(Calendar.MONTH);
    	mDay = c.get(Calendar.DAY_OF_MONTH);
    	updateTxtFecha();
    	
    	txtFecha.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(0);
			}
		});
    	
    	//Guardar
    	Button btnGuardar = (Button) findViewById(R.id.btnGuardarGasto);
    	btnGuardar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int idCat = spnCategorias.getSelectedItemPosition() + 1;
				ArrayList<SubCategoria> arraySubCat = hashSubCategoriasById.get( String.valueOf(idCat) );
				int idSubCat = arraySubCat.get(spnSubCategorias.getSelectedItemPosition()).getId();

				
				//String texto = "Cat: " + spnCategorias.getSelectedItemPosition() +  " SubCat: " + spnSubCategorias.getSelectedItemPosition() + "\n SubCatID: " + arraySubCat.get(spnSubCategorias.getSelectedItemPosition()).getId() + "\n Fecha: " + txtFecha.getText().toString();
				//Toast.makeText(AddGastoActivity.this, texto, Toast.LENGTH_SHORT).show();
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaGasto = new Date();
				try {
					fechaGasto = dateFormat.parse(txtFecha.getText().toString());
				} catch (Exception e) {
					Log.e(TAG, "Ocurrió un problema al convertir a fecha a " + txtFecha.getText().toString());
				}
				Categoria categoria = dbh.getCategoria(idCat);
	
				SubCategoria subCategoria = new SubCategoria();
				subCategoria.setCategoria(categoria);
				subCategoria.setId(idSubCat);

				Gasto gasto = new Gasto();
				gasto.setSubCategoria(subCategoria);
				gasto.setDescripcion(editDescripcion.getText().toString());
				gasto.setMonto( Integer.parseInt(editMonto.getText().toString()) );
				gasto.setFecha( fechaGasto );
				
				dbh.insertGasto(gasto);
				
				Toast.makeText(AddGastoActivity.this, "Se agregó el registro", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(AddGastoActivity.this, MainActivity.class);
				startActivity(i);
			}
		});
    	
	}

	/**
	 * Devuelve un listado con los nombres de las SubCategorías a partir de un listado de objetos SubCategorías
	 */
	private ArrayList<String> getSubCategoriasFromList( ArrayList<SubCategoria> paramSubCateg ) {
		ArrayList<String> subCategorias = new ArrayList<String>();
		for (SubCategoria subCategoria : paramSubCateg) {
			subCategorias.add(subCategoria.getNombre());
		}
		return subCategorias;
	}
    
	
	/**
	 * Actualiza el texto de la fecha
	 */
    private void updateTxtFecha() {
    	txtFecha.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" "));
    }
    
    private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {

    	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    		mYear = year;
    		mMonth = monthOfYear;
    		mDay = dayOfMonth;
    		updateTxtFecha();
    	}
    };
    
    /** Create a new dialog for date picker */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case 0:
            return new DatePickerDialog(this, pDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }
}
