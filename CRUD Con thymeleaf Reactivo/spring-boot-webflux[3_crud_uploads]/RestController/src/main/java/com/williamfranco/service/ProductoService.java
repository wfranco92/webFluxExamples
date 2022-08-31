package com.williamfranco.service;

import com.williamfranco.model.Categoria;
import com.williamfranco.model.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {
	
	public Flux<Producto> getAllProducts();
	public Mono<Producto> getProductById(String id);
	public Mono<Producto> saveProduct(Producto product);
	public Mono<Void> deleteProduct(Producto product);
	
	public Flux<Categoria> getAllCategorias();
	public Mono<Categoria> getCategoriaById(String id);
	public Mono<Categoria> saveCategoria(Categoria categoria);
	public Mono<Void> deleteCategoria(Categoria categoria);

}
