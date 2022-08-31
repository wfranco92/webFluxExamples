package com.williamfranco.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categorias")
public class Categoria {

	@Id
	private String id;
	private String nombreCategoria;
	
	public Categoria() {
	}
	
	public Categoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombreCategoria() {
		return nombreCategoria;
	}
	public void setNombreCategoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}
}
