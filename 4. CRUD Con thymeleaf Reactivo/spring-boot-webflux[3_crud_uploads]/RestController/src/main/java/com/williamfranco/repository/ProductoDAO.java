package com.williamfranco.repository;

import com.williamfranco.model.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoDAO extends ReactiveMongoRepository<Producto, String> {

}
