package cl.ido.gastos;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cl.ido.ejemplo.R;
import cl.ido.gastos.adapter.GastosAdapter;
import cl.ido.gastos.db.DataBaseHelper;
import cl.ido.gastos.model.Gasto;

public class MainActivity extends Activity {
	ArrayList<Gasto> gastos;

	private static int[] COLORS = new int[] { Color.GREEN
											, Color.BLUE
											, Color.MAGENTA
											, Color.YELLOW
											, Color.RED
											, Color.DKGRAY
											, Color.BLACK };
	
	private static final String TAG = "MainActivity";
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		cargarGastos();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {

			GraphicalView mChartView = null;
			DefaultRenderer mRenderer = new DefaultRenderer();
			CategorySeries mSeries = new CategorySeries("Expenses");

			int count = gastos.size();
			double[] values = new double[count];
			String[] categoryNames = new String[count];
			for (int i = 0; i < count; i++) {
			    values[i] = gastos.get(i).getGasto();
			    categoryNames[i] = gastos.get(i).getNombre();
			}
			int i = 0;
			for (i = 0; i < count; i++) {
				mSeries.add(categoryNames[i], values[i]);
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
				renderer.setDisplayChartValues(true);
				mRenderer.addSeriesRenderer(renderer);
			}

			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			
			
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			mChartView.setLayoutParams(params);
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chartsRelativeLayout);
			layout.addView(mChartView);
			
		} else {

			GastosAdapter adapter = new GastosAdapter(this, gastos);
			ListView lstGastos = (ListView) findViewById(R.id.listPrincipalGastos);
			lstGastos.setAdapter(adapter);
			
			lstGastos.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent i = new Intent(MainActivity.this, GastosBySubCategoriaActivity.class);
					Gasto gasto = (Gasto) parent.getItemAtPosition(position);
					i.putExtra("idCategoria", gasto.getId());
					startActivity(i);
				}
			});

			DataBaseHelper db = new DataBaseHelper(this); 
			int montoIngresos = db.getIngresosMesActual();
			NumberFormat numberFormat = NumberFormat.getInstance();
			TextView txtIngresosMonto = (TextView) findViewById(R.id.txtIngresosMonto); 
			txtIngresosMonto.setText( "$ " + numberFormat.format( montoIngresos ) );
		}

    	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ) {
			getMenuInflater().inflate(R.menu.main, menu);
		}
		return true;
	}

	/**
	 * Carga el listado con los gastos
	 */
	public void cargarGastos() {

		DataBaseHelper dbh = new DataBaseHelper(this); 

    	Calendar cal = Calendar.getInstance();
    	int mes = cal.get(Calendar.MONTH) + 1;
    	int anno = cal.get(Calendar.YEAR);
    	Log.i(TAG, mes + "/" + anno);
		gastos = dbh.getMontosCategorias(mes, anno);

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
		} else if (item.getItemId() == R.id.action_add_categoria) {
			Intent intent = new Intent(this, AddCategoriaActivity.class);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.action_add_sub_categoria) {
			Intent intent = new Intent(this, AddSubCategoriaActivity.class);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);	
		}
		
	}

}