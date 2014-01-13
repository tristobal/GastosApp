package cl.ido.gastos.model;

public class Categoria {
	
	private String nombre;
	private int id; //id_categoria en BDD
	private char tipo;

	public static final int INDEX_ID = 0;
	public static final int INDEX_NOMBRE = 1;
	public static final int INDEX_TIPO = 2;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public char getTipo() {
		return tipo;
	}
	public void setTipo(char tipo) {
		this.tipo = tipo;
	}
	
	public int compareTo(Categoria c) {
	     return(id - c.getId());
	}

	@Override
	//Se sobreescribe este método para que sea el campo Nombre el que se muestre en los Spinner
	public String toString() {
		return this.getNombre();
	}

}
