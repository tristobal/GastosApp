package cl.ido.gastos.model;

import java.util.Date;

public class Gasto {
	private int id;
	private String descripcion;
	private Date fecha;
	private int monto;
	private SubCategoria subCategoria;

	//TODO BORRAR
	private String nombre;
	private int gasto;
	private int porcentajeGasto;

	public Gasto() {}
	
	public Gasto(String nombre, int gasto, int porcentajeGasto) {
		this.nombre = nombre;
		this.gasto = gasto;
		this.porcentajeGasto = porcentajeGasto;
	}

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getMonto() {
		return monto;
	}

	public void setMonto(int monto) {
		this.monto = monto;
	}

	public SubCategoria getSubCategoria() {
		return subCategoria;
	}

	public void setSubCategoria(SubCategoria subCategoria) {
		this.subCategoria = subCategoria;
	}
	
}