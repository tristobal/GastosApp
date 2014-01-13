package cl.ido.gastos.model;

public class SubCategoria {
	private String nombre;
	private int id; //id_sub_categoria en BDD	
	private Categoria categoria;
	
	
	public static final int INDEX_ID = 0;
	public static final int INDEX_NOMBRE = 1;
	public static final int INDEX_ID_CATEGORIA = 2;
	
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
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
}
