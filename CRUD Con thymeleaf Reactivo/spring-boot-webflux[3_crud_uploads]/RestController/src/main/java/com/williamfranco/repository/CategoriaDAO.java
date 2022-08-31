package com.williamfranco.repository;

import com.williamfranco.model.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaDAO extends ReactiveMongoRepository<Categoria, String>{

}
