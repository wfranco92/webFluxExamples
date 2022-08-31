package com.williamfranco;

import com.williamfranco.model.Categoria;
import com.williamfranco.model.Producto;
import com.williamfranco.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class RestControllerApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(RestControllerApplication.class);

	@Autowired
	private ProductoService productoService;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(RestControllerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("productos").subscribe();  // instruction to drop collection, each app init.
		mongoTemplate.dropCollection("categorias").subscribe();

		var electronico = new Categoria("Electronico");
		var computacion = new Categoria("Computacion");

		Flux.just(electronico, computacion).
				flatMap(productoService::saveCategoria)
				.doOnNext(categoria -> log.info("categoria : " + categoria))
				.thenMany(Flux.just(new Producto("TV samsung", 12.2, electronico),
								new Producto("Tablet huawei", 20.98, computacion),
								new Producto("samgung galaxy note 10", 100.20, electronico),
								new Producto("HP Omen", 1200.0, computacion))
						.flatMap(producto -> {
							producto.setCreatedAt(new Date());
							return productoService.saveProduct(producto);
						})).subscribe(element -> log.info(element.toString()));
	}
}
