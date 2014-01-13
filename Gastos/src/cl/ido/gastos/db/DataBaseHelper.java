package cl.ido.gastos.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cl.ido.gastos.model.Categoria;
import cl.ido.gastos.model.Gasto;
import cl.ido.gastos.model.SubCategoria;
import cl.ido.gastos.util.Constants;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseHelper extends SQLiteAssetHelper {

	
    private static final String DATABASE_NAME = "gastos_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DataBaseHelper";
    
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }


    /**
     * Devuelve un HashMap de Subcategorías, donde el ID de cada listado de ellas es el ID de la Categoría
     */
    public HashMap<String, ArrayList<SubCategoria>> getSubCategorias() {
    	Log.i(TAG, "getSubCategorias()");

    	SQLiteDatabase db = getReadableDatabase();
    	String query = "SELECT s.id_sub_categoria " +
    						", s.nombre as subcat " +
    						", c.id_categoria " +
    						", c.nombre as cat "+
    						", c.tipo " +
    					 "FROM sub_categoria s "+
    						", categoria c " + 
    					"WHERE c.id_categoria = s.id_categoria " +
    					"ORDER BY c.id_categoria " +
    					    ", s.id_sub_categoria ASC";
    	Cursor c = db.rawQuery(query, null);

    	HashMap<String, ArrayList<SubCategoria>> subCat = new HashMap<String, ArrayList<SubCategoria>>();
    	ArrayList<SubCategoria> subcategorias = new ArrayList<SubCategoria>();
    	
    	if ( c != null ) {
    		//Primer id de categoría
    		Categoria cat;
    		SubCategoria subcat;
    		c.moveToFirst();
    		int idCat = c.getInt( 2 );
    		while ( !c.isAfterLast() ) {
    			cat = new Categoria();
    			cat.setId( c.getInt(2) );
    			cat.setNombre( c.getString(3) );
    			cat.setTipo( c.getString(4).charAt(0));
    			
    			subcat = new SubCategoria();
    			subcat.setCategoria(cat);
    			subcat.setId( c.getInt(0) );
    			subcat.setNombre( c.getString(1) );
    			
    			if ( idCat != subcat.getCategoria().getId() ) {
    				subCat.put(String.valueOf(idCat), subcategorias);
    				subcategorias = new ArrayList<SubCategoria>();
    				subcategorias.add(subcat);
    				idCat = subcat.getCategoria().getId();
    			} else {
    				subcategorias.add(subcat);
    			}

    			c.moveToNext();
    			//Si es el último se agrega al HASH
    			if ( c.isAfterLast() )
    				subCat.put(String.valueOf(idCat), subcategorias);
    		}
    		c.close();
    		db.close();
    	}

    	for (String key : subCat.keySet()) {
    	    subcategorias = subCat.get(key);
    	    for (SubCategoria sc : subcategorias) {
    	    	Log.i(TAG, "Cat: (" + sc.getCategoria().getId() + ") " + sc.getCategoria().getNombre() +", SubCat: (" + sc.getId() + ") " + sc.getNombre());
    	    }
    	}
        return subCat;
    }


    /**
     * Devuelve una Categoría según su ID
     */
    public Categoria getCategoria(int idCat) {
    	Log.i(TAG, "getCategorias()");    	
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.query("Categoria", null, "id_categoria=" + idCat, null, null, null, null);
        Categoria categoria = new Categoria(); 
        if (cur.moveToFirst()) {
        	categoria.setId( cur.getInt( Categoria.INDEX_ID ));
        	categoria.setNombre( cur.getString(Categoria.INDEX_NOMBRE) );
        	categoria.setTipo( cur.getString(Categoria.INDEX_TIPO).charAt(0) );
        }
        db.close();
        cur.close();
        
        return categoria;
    }


    /**
     * Devuelve un listado de objetos Categoría (ordenadas por ID)
     */
    public ArrayList<Categoria> getCategorias() {
    	Log.i(TAG, "getCategorias()");    	
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query("Categoria", null, null, null, null, null, "id_categoria ASC");
        
		ArrayList<Categoria> categorias = new ArrayList<Categoria>();
		if ( c != null ) {
			c.moveToFirst();
			Categoria categ;
			while ( !c.isAfterLast() ) {
				categ = new Categoria();
				categ.setNombre( c.getString( 1 ) );
				categ.setId( c.getInt( 0 ) );
				categ.setTipo( c.getString( 2 ).charAt(0) );
				categorias.add(categ);
				c.moveToNext();
			}
			db.close();
			c.close();
		}
        return categorias;
    }


    /**
     * Devuelve un Listado con solo los nombres de las Categorías (ordenadas por ID)
     */
    public ArrayList<String> getListCategorias() {
    	Log.i(TAG, "getListCategorias()");
    	ArrayList<Categoria> alCateg = getCategorias();

    	ArrayList<String> txtCateg = new ArrayList<String>();
    	for (Categoria cat : alCateg) {
    		txtCateg.add(cat.getNombre());
		}
		
		return txtCateg;
    }


    /**
     * Inserta el gasto y recalcula los totales correspondientes
     */
    public void insertGasto(Gasto gasto) {
    	Log.i(TAG, "insertGasto()");
    	SQLiteDatabase db = getReadableDatabase();

    	//(1) Se inserta el gasto
    	ContentValues newGasto = new ContentValues();
    	newGasto.put("descripcion", gasto.getDescripcion());
    	newGasto.put("fecha", new SimpleDateFormat("yyyy-MM-dd").format(gasto.getFecha()));
    	newGasto.put("monto", gasto.getMonto());
    	newGasto.put("id_sub_categoria", gasto.getSubCategoria().getId());
    	db.insert("gasto", null, newGasto);

    	//(1) Se Actualiza o Inserta el Total de las SubCategorías
    	
    	if (gasto.getSubCategoria().getCategoria().getTipo() == Constants.CATEGORIA_GASTO) {
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(gasto.getFecha());
        	int mes = cal.get(Calendar.MONTH) + 1;
        	int anno = cal.get(Calendar.YEAR);
        	String query = "SELECT monto FROM total_sub_categoria WHERE id_sub_categoria = " + gasto.getSubCategoria().getId() + " AND mes = " + mes + " AND anno = " + anno;
        	Log.d(TAG, "QUERY: " + query);
        	
        	Cursor cur = db.rawQuery(query, null);
        	if ( cur != null ) {

        		int monto = 0;
        		
        		if (cur.moveToFirst())
        			monto = cur.getInt( 0 );

        		newGasto = new ContentValues();
        		if (monto > 0) {
        			Log.d(TAG, "Se actualiza la tabla de subcategorias");
        			newGasto.put("monto", monto + gasto.getMonto());
        			db.update("total_sub_categoria", newGasto,  "id_sub_categoria = " + gasto.getSubCategoria().getId() + " AND mes = " + mes + " AND anno = " + anno, null);
        		} else {
        			Log.d(TAG, "Se inserta un nuevo registro en la tabla de subcategorias");
        			newGasto.put("id_sub_categoria", gasto.getSubCategoria().getId());
        			newGasto.put("monto", gasto.getMonto());
        			newGasto.put("mes", mes);
        			newGasto.put("anno", anno);
        			db.insert("total_sub_categoria", null, newGasto);
        		}
        		
            	//(2) Se Actualiza o Inserta el Total de las Categorías 
            	query = "SELECT monto FROM total_categoria WHERE id_categoria = " + gasto.getSubCategoria().getCategoria().getId() + " AND mes = " + mes + " AND anno = " + anno;

            	cur = db.rawQuery(query, null);
            	if ( cur != null ) {
            		monto = 0;
            		if (cur.moveToFirst())
            			monto = cur.getInt( 0 );

            		newGasto = new ContentValues();
            		if (monto > 0) {
            			Log.d(TAG, "Se actualiza la tabla de categorias");
            			newGasto.put("monto", monto + gasto.getMonto());
            			db.update("total_categoria", newGasto,  "id_categoria = " + gasto.getSubCategoria().getCategoria().getId() + " AND mes = " + mes + " AND anno = " + anno, null);
            		} else {
            			Log.d(TAG, "Se inserta un nuevo registro en la tabla de categorias");
            			newGasto.put("id_categoria", gasto.getSubCategoria().getCategoria().getId());
            			newGasto.put("monto", gasto.getMonto());
            			newGasto.put("mes", mes);
            			newGasto.put("anno", anno);
            			db.insert("total_categoria", null, newGasto);
            		}
            	}
            	
        	cur.close();
        	} else {
        		Log.d(TAG, "El cursor era nulo. No se realizaron inserciones");
        	}

    	} 
    	db.close();
    }


    /**
     * Devuelve el total por categorías para un mes y año dado
     */
    public ArrayList<Gasto> getMontosCategorias(int mes, int anno) {
    	Log.i(TAG, "getMontosCategorias()");
    	String query =  "SELECT c.id_categoria, c.nombre, t.monto " +
				    	  "FROM total_categoria t " +
				    	     ", categoria c " +
				    	 "WHERE t.id_categoria = c.id_categoria " +
				    	   "AND c.tipo = '"  + Constants.CATEGORIA_GASTO + "' " +
				    	   "AND t.mes  = " + mes + " " +
				    	   "AND t.anno = " + anno;
    	
    	SQLiteDatabase db = getReadableDatabase();
    	
    	Cursor c = db.rawQuery(query, null);
    	ArrayList<Gasto> gens = new ArrayList<Gasto>();
    	Gasto gen = new Gasto();

    	if (c != null) {
    		if (c.moveToFirst()) {
    			while ( !c.isAfterLast() ) {
    				gen = new Gasto();
    				gen.setId(c.getInt(0));
    				gen.setNombre(c.getString(1));
    				gen.setGasto(c.getInt(2));
    				gens.add(gen);
    				c.moveToNext();
    			}
    		}
    	}
    	db.close();
    	c.close();
    	return gens;
    }

    /**
     * Devuelve el total por categorías para un mes y año dado
     */
    public ArrayList<Gasto> getMontosSubCategorias(int idCategoria, int mes, int anno) {
    	String query =  "SELECT s.id_sub_categoria, s.nombre, t.monto, c.nombre " +
						  "FROM total_sub_categoria t " +
						     ", sub_categoria s " +
						     ", categoria c " +
						 "WHERE t.id_sub_categoria = s.id_sub_categoria " +
						   "AND s.id_categoria     = c.id_categoria " +
						   "AND c.id_categoria     = " + idCategoria + " " +
				    	   "AND t.mes              = " + mes + " " +
				    	   "AND t.anno             = " + anno;
    	
    	SQLiteDatabase db = getReadableDatabase();
    	
    	Cursor c = db.rawQuery(query, null);
    	ArrayList<Gasto> gens = new ArrayList<Gasto>();
    	Gasto gen = new Gasto();

    	if (c != null) {
    		if (c.moveToFirst()) {
    			while ( !c.isAfterLast() ) {
    				gen = new Gasto();
    				gen.setId(c.getInt(0));
    				gen.setNombre(c.getString(1));
    				gen.setGasto(c.getInt(2));
    				gen.setDescripcion(c.getString(3));
    				gens.add(gen);
    				c.moveToNext();
    			}
    		}
    	}
    	db.close();
    	c.close();
    	return gens;
    }

    /**
     * Limpia todas las tablas, no paramétricas, de la aplicación
     */
    public void deleteAllTables() {
    	Log.i(TAG, "deleteAllTables()");
    	SQLiteDatabase db = getReadableDatabase();
    	db.delete("total_categoria", null, null);
    	db.delete("total_sub_categoria", null, null);
    	db.delete("gasto", null, null);
    	db.close();
    }
    
    public int getIngresosMesActual() {
    	Log.i(TAG, "getIngresosMesActual()");
		
    	Calendar cal = Calendar.getInstance();
    	int mes = cal.get(Calendar.MONTH) + 1;
    	
    	int monto = 0;
    	
    	SQLiteDatabase db = getReadableDatabase();
    	String query =  "SELECT SUM(g.monto) " +
						  "FROM gasto g " +
						     ", sub_categoria s " +
						     ", categoria c " +
						 "WHERE g.id_sub_categoria      = s.id_sub_categoria " +
						   "AND s.id_categoria          = c.id_categoria " +
						   "AND c.tipo                  = '"  + Constants.CATEGORIA_INGRESO + "'" +
						   "AND strftime('%m', g.fecha) = '" + String.format("%02d", mes) + "'";
    	Cursor cur = db.rawQuery(query, null);
    	if (cur != null) {
    		
    		if (cur.moveToFirst()) {
    			monto = cur.getInt(0);
    		} else {
    			Log.d(TAG, "getIngresosMesActual(). Cursor vacío");
    		}
    	} else {
    		Log.d(TAG, "getIngresosMesActual(). Cursor nulo");
    	}
    	return monto;
    }
    
    
    /**
     * Inserta una nueva Categoría
     */
    public void insertCategoria(Categoria categoria) {
    	Log.i(TAG, "insertCategoria()");
    	SQLiteDatabase db = getReadableDatabase();

    	ContentValues newRecord = new ContentValues();
    	newRecord.put("nombre", categoria.getNombre());
    	newRecord.put("tipo", String.valueOf(categoria.getTipo()));
    	db.insert("categoria", null, newRecord);

    	db.close();
    }
    
    /**
     * Inserta una nueva SubCategoría
     */
    public void insertSubCategoria(SubCategoria subCategoria) {
    	Log.i(TAG, "insertSubCategoria()");
    	SQLiteDatabase db = getReadableDatabase();

    	ContentValues newRecord = new ContentValues();
    	newRecord.put("nombre", subCategoria.getNombre());
    	newRecord.put("id_categoria", subCategoria.getCategoria().getId());
    	db.insert("sub_categoria", null, newRecord);

    	db.close();
    }
}