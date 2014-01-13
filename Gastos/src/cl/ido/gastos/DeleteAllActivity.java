package cl.ido.gastos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cl.ido.ejemplo.R;
import cl.ido.gastos.db.DataBaseHelper;

public class DeleteAllActivity extends Activity {
	private static final String TAG = "DeleteAllActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_all);
		
		Button btnBorrar = (Button) findViewById(R.id.btnBorrarTodo);
		btnBorrar.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	            Builder alert = new AlertDialog.Builder(DeleteAllActivity.this);
	            alert.setTitle(getString(R.string.txtAviso));
	            alert.setMessage(getString(R.string.msgConfirmarDelete));
	            alert.setPositiveButton(getString(R.string.txtSi), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataBaseHelper dbh = new DataBaseHelper(getApplicationContext());
						dbh.deleteAllTables();
						Toast.makeText(getApplicationContext(), getString(R.string.msgFinDelete), Toast.LENGTH_SHORT).show();
						Intent i = new Intent(DeleteAllActivity.this, MainActivity.class);
						startActivity(i);
					}
				});
	            alert.setNegativeButton(getString(R.string.txtNo), null);
	            alert.show();
			}
		});
		
		//Estupideces JSON
		/*String url = "http://indicadoresdeldia.cl/webservice/indicadores.json";

        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(url);
        
        try {
			JSONObject jsonObject = json.getJSONObject("santoral");
			String ayer = jsonObject.getString("ayer");  
			String hoy = jsonObject.getString("hoy");
			String maniana = jsonObject.getString("maniana");

			Toast.makeText(getApplicationContext(), "Santorales\nHoy: " + hoy + "\nAyer: " + ayer + "\nMañana: " + maniana, Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			 Log.e(TAG, "Error al obtener info desde el JSON: " + e.getMessage());
		}*/
		
		//WS
		//http://www.webservicex.net/currencyconvertor.asmx
	}

}