package cl.ido.gastos.model;

public class GastoGeneric {

	private String nombre;
	private int gasto;
	private int porcentajeGasto;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getGasto() {
		return gasto;
	}
	public void setGasto(int gasto) {
		this.gasto = gasto;
	}
	public int getPorcentajeGasto() {
		return porcentajeGasto;
	}
	public void setPorcentajeGasto(int porcentajeGasto) {
		this.porcentajeGasto = porcentajeGasto;
	}

}
