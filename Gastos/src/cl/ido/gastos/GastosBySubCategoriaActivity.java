package cl.ido.gastos;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import cl.ido.ejemplo.R;
import cl.ido.gastos.adapter.GastosAdapter;
import cl.ido.gastos.db.DataBaseHelper;
import cl.ido.gastos.model.Gasto;

public class GastosBySubCategoriaActivity extends Activity {

	private ArrayList<Gasto> gastos;

	private static final String TAG = "GastosBySubCategoriaActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gastos_by_sub_categoria);
		Log.i(TAG, "onCreate()");

		Intent intent = getIntent();
		int idCategoria = intent.getIntExtra("idCategoria", 0);
		
		cargarGastos(idCategoria);
		
		GastosAdapter adapter = new GastosAdapter(this, gastos);
		ListView lstGastos = (ListView) findViewById(R.id.listSubCategoriasGastos);
		lstGastos.setAdapter(adapter);
		
		if (gastos.size() > 0)
			setTitle(gastos.get(0).getDescripcion());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add_gasto) {
			Intent intent = new Intent(this, AddGastoActivity.class);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.action_remove_all) {
			Intent intent = new Intent(this, DeleteAllActivity.class);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);	
		}
		
	}

	/**
	 * Carga el listado con los gastos
	 */
	public void cargarGastos(int idCategoria) {

		DataBaseHelper dbh = new DataBaseHelper(this); 

	   	Calendar cal = Calendar.getInstance();
	   	int mes = cal.get(Calendar.MONTH) + 1;
	   	int anno = cal.get(Calendar.YEAR);
		
	   	gastos = dbh.getMontosSubCategorias(idCategoria, mes, anno);
	
	   	if ( gastos != null && gastos.size() > 0 ) {
	   		int max = gastos.get(0).getGasto();
			for (Gasto g : gastos) {
				if (g.getGasto() >= max)
					max = g.getGasto(); 
			}
		
			for (Gasto g : gastos) {
				float ptjeFloat = ( (float) g.getGasto() / max) * 100;
				int porcentaje = (int) Math.round(ptjeFloat);
				Log.i(this.getLocalClassName(), "gasto: " + g.getGasto()  + ", porcentaje: " + porcentaje  );
				g.setPorcentajeGasto( porcentaje );
			}
		}
	}
}
