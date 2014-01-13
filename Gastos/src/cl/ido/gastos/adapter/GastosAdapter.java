package cl.ido.gastos.adapter;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import cl.ido.ejemplo.R;
import cl.ido.gastos.model.Gasto;

public class GastosAdapter extends ArrayAdapter<Gasto> {

	private Context context;
	private ArrayList<Gasto> gastos;
	private LayoutInflater inflater;
	
	public GastosAdapter(Context context, ArrayList<Gasto> objects) {
		super(context, -1, objects);
		this.context = context;
		this.gastos = objects;
		this.inflater = LayoutInflater.from(this.context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		GastoHolder gastoHolder;
		View item = convertView;
		
		if ( item == null ) {
			item = inflater.inflate(R.layout.row_gasto, null);
			
			gastoHolder = new GastoHolder();

			gastoHolder.tvNombreGasto = (TextView) item.findViewById(R.id.txtNombreGasto);
			gastoHolder.tvMontoGasto = (TextView) item.findViewById(R.id.txtMontoGasto);
			gastoHolder.pbPorcentajeGasto = (ProgressBar) item.findViewById(R.id.progressBarGasto);

			item.setTag(gastoHolder);
		}

		gastoHolder = (GastoHolder) item.getTag();

		gastoHolder.tvNombreGasto.setText( gastos.get(position).getNombre() );

		NumberFormat numberFormat = NumberFormat.getInstance();
		gastoHolder.tvMontoGasto.setText( "$" + numberFormat.format( gastos.get(position).getGasto() )  );

		gastoHolder.pbPorcentajeGasto.setProgress( gastos.get(position).getPorcentajeGasto() );

		return item;
	}

	public class GastoHolder {
		public TextView tvNombreGasto;
		public TextView tvMontoGasto;
		public ProgressBar pbPorcentajeGasto;
	}

}