package com.williamfranco.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection="productos")
public class Producto {
	
	@Id
	private String id;
	
	@NotEmpty
	private String name;
	
	@NotNull
	private Double price;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdAt;
	@NotNull
	private Categoria categoria;

	private String foto;
	
	public Producto() {
	}
	
	public Producto(String name, Double price) {
		this.name = name;
		this.price = price;
	}
	
	public Producto(String name, Double price, Categoria categoria) {
		this.name = name;
		this.price = price;
		this.categoria = categoria;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	@Override
	public String toString() {
		return "Producto [id=" + id + ", name=" + name + ", price=" + price + ", createdAt=" + createdAt + "]";
	}
	
	 

}
