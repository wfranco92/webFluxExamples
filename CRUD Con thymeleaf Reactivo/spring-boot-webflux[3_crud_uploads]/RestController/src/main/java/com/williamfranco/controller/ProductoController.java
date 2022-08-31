package com.williamfranco.controller;


import com.williamfranco.model.Producto;
import com.williamfranco.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String path;

/*
	//Simple metod.. return flux from service
	@GetMapping
	public Flux<Producto> getAllProducts(){
		return productoService.getAllProducts();
	}*/

	/*	@GetMapping
	// other method, whith new responseEntity
	public ResponseEntity<Flux<Producto>> getAllProducts() {
		return new ResponseEntity<Flux<Producto>>(productoService.getAllProducts(), HttpStatus.OK);
	}*/

    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> getAllProducts() {
        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.getAllProducts())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> getProductById(@PathVariable String id) {
        return productoService.getProductById(id)
                .map(producto -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(producto))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createProduct(@Valid @RequestBody Mono<Producto> producto) {

        Map<String, Object> response = new HashMap<>();

        return producto.flatMap(prod -> {
            if (prod.getCreatedAt() == null) {
                prod.setCreatedAt(new Date());
            }
            return productoService.saveProduct(prod)
                    .map(producto1 -> {

						response.put("producto", producto1);
                        response.put("Message", "Producto guardado con exito");
                        response.put("fecha", new Date());
                        return ResponseEntity
                                .created(URI.create("api/v1/productos".concat(producto1.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response);
                    });
        }).onErrorResume(error -> {
            return Mono.just(error).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    .flatMapMany(errors -> Flux.fromIterable(errors))
                    .map(e -> "el campo " + e.getField() + " " + e.getDefaultMessage())
                    .collectList()
                    .flatMap(list -> {
						response.put("errors", list);
                        response.put("Message", "Bad Request");
                        response.put("fecha", new Date());
						return Mono.just(ResponseEntity.badRequest().body(response));
					});
        });

    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> getProductById(@RequestBody Producto product, @PathVariable String id) {
        return productoService.getProductById(id)
                .flatMap(producto -> {
                    producto.setName(product.getName());
                    producto.setPrice(product.getPrice());
                    producto.setCategoria(product.getCategoria());
                    return productoService.saveProduct(producto);
                }).map(prod -> ResponseEntity
                        .created(URI.create("api/v1/productos".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(prod))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteProduct(@PathVariable String id) {
        return productoService.getProductById(id)
                .flatMap(producto -> productoService.deleteProduct(producto)
                        .thenReturn(ResponseEntity.accepted().build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("upload/{id}")
    public Mono<ResponseEntity<Producto>> uploadProduct(@PathVariable String id, @RequestPart FilePart file) {
        return productoService.getProductById(id)
                .flatMap(producto -> {
                    producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                            .replace(" ", "")
                            .replace(":", "")
                            .replace("\\", ""));

                    return file.transferTo(new File(path + producto.getFoto()))
                            .then(productoService.saveProduct(producto));
                }).map(producto -> ResponseEntity.ok(producto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/v2")
    public Mono<ResponseEntity<Producto>> createProductWithImage(Producto producto, @RequestPart FilePart file) {
        if (producto.getCreatedAt() == null) {
            producto.setCreatedAt(new Date());
        }
        producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));
        return file.transferTo(new File(path + producto.getFoto()))
                .then(productoService.saveProduct(producto))
                .map(prod -> ResponseEntity
                        .created(URI.create("api/v1/productos".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(prod));
    }
}
