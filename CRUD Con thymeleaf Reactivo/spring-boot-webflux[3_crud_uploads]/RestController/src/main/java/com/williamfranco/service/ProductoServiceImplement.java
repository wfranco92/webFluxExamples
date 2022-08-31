package com.williamfranco.service;

import com.williamfranco.model.Categoria;
import com.williamfranco.model.Producto;
import com.williamfranco.repository.CategoriaDAO;
import com.williamfranco.repository.ProductoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImplement implements ProductoService{
	
	@Autowired
	private ProductoDAO productoDAO;
	
	@Autowired
	private CategoriaDAO categoriaDAO;

	@Override
	public Flux<Producto> getAllProducts() {
		return productoDAO.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		});
	}

	@Override
	public Mono<Producto> getProductById(String id) {
		return productoDAO.findById(id);
	}

	@Override
	public Mono<Producto> saveProduct(Producto product) {
		return productoDAO.save(product);
	}

	@Override
	public Mono<Void> deleteProduct(Producto product) {
		return productoDAO.delete(product);
	}

	@Override
	public Flux<Categoria> getAllCategorias() {
		// TODO Auto-generated method stub
		return categoriaDAO.findAll();
	}

	@Override
	public Mono<Categoria> getCategoriaById(String id) {
		// TODO Auto-generated method stub
		return categoriaDAO.findById(id);
	}

	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		// TODO Auto-generated method stub
		return categoriaDAO.save(categoria);
	}

	@Override
	public Mono<Void> deleteCategoria(Categoria categoria) {
		// TODO Auto-generated method stub
		return null;
	}

}
