package cl.ido.gastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cl.ido.ejemplo.R;
import cl.ido.gastos.db.DataBaseHelper;
import cl.ido.gastos.model.Categoria;

public class AddCategoriaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_categoria);
		
		final EditText editCategoria = (EditText) findViewById(R.id.editCategoria);
		final Spinner spnTipos = (Spinner) findViewById(R.id.spnTipos);
		final Button btnGuardar = (Button) findViewById(R.id.btnGuardarCategoria);
		
		btnGuardar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Categoria categoria = new Categoria(); 
				categoria.setNombre( editCategoria.getText().toString() );
				categoria.setTipo( String.valueOf(spnTipos.getSelectedItemPosition() + 1).charAt(0) );

				DataBaseHelper dbh = new DataBaseHelper(AddCategoriaActivity.this);
				dbh.insertCategoria(categoria);

				Toast.makeText(AddCategoriaActivity.this, "Se agregó el registro", Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(AddCategoriaActivity.this, MainActivity.class);
				startActivity(i);
				//Toast.makeText(AddCategoriaActivity.this, editCategoria.getText().toString() + " " + String.valueOf(spnTipos.getSelectedItemPosition() + 1), Toast.LENGTH_SHORT).show();
			}
		});
		
	}

}