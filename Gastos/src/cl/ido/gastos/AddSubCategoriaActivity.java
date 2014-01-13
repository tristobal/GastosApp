package cl.ido.gastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cl.ido.ejemplo.R;
import cl.ido.gastos.db.DataBaseHelper;
import cl.ido.gastos.model.Categoria;
import cl.ido.gastos.model.SubCategoria;

public class AddSubCategoriaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_sub_categoria);
		
		final DataBaseHelper dbh = new DataBaseHelper(this);

		final EditText editSubCategoria = (EditText) findViewById(R.id.editSubCategoria);
		final Spinner spnCategorias = (Spinner) findViewById(R.id.spnListadoCategorias);
		final Button btnGuardar = (Button) findViewById(R.id.btnGuardarSubCategoria);

		ArrayAdapter<Categoria> categAdapter = new ArrayAdapter<Categoria>(this, android.R.layout.simple_spinner_item, dbh.getCategorias());
		categAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategorias.setAdapter(categAdapter);

		btnGuardar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Categoria categoria = (Categoria) spnCategorias.getSelectedItem();
				SubCategoria subCategoria = new SubCategoria();
				
				subCategoria.setNombre( editSubCategoria.getText().toString() );
				subCategoria.setCategoria(categoria);
				

				DataBaseHelper dbh = new DataBaseHelper(AddSubCategoriaActivity.this);
				dbh.insertSubCategoria(subCategoria);

				Toast.makeText(AddSubCategoriaActivity.this, "Se agregó el registro", Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(AddSubCategoriaActivity.this, MainActivity.class);
				startActivity(i);
			}
		});
	}

}
