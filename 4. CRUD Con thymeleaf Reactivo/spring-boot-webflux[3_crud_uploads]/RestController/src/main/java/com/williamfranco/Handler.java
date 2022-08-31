package com.williamfranco;

import com.williamfranco.model.Categoria;
import com.williamfranco.model.Producto;
import com.williamfranco.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@Component
public class Handler {

    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String path;

    public Mono<ServerResponse> getProducts(ServerRequest request){
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.getAllProducts(), Producto.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request){

        String id = request.pathVariable("id");
        return productoService.getProductById(id)
                .flatMap(producto -> {
                    return ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(producto));
                })
                .switchIfEmpty(ServerResponse
                                .notFound()
                                .build()
                        );
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request){

        Mono<Producto> producto = request.bodyToMono(Producto.class);

        return producto.flatMap(p ->{
            if(p.getCreatedAt() == null){
                p.setCreatedAt(new Date());
            }
            return productoService.saveProduct(p);
        }).flatMap(p -> ServerResponse
                .created(URI.create("api/v2/productos".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(p)));
    }

    public Mono<ServerResponse> editProduct(ServerRequest request){

        Mono<Producto> producto = request.bodyToMono(Producto.class);
        String id = request.pathVariable("id");

        Mono<Producto> productFinded = productoService.getProductById(id);

        return productFinded.zipWith(producto, (pf, pa) ->{

            pf.setPrice(pa.getPrice());
            pf.setName(pa.getName());
            pf.setCategoria(pa.getCategoria());

            return pf;
        }).flatMap(p -> productoService.saveProduct(p))
                .flatMap(p -> ServerResponse
                        .created(URI.create("api/v2/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request){

        String id = request.pathVariable("id");

        return productoService.getProductById(id)
                .flatMap(producto -> productoService.deleteProduct(producto))
                .then(ServerResponse
                        .accepted()
                        .build())
                //.body(Mono.just("no se encontro el producto a elminiar"), String.class))  instruction to send a message personalizated
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> uploadImage(ServerRequest request){

        String id = request.pathVariable("id");

        return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap( filePart -> productoService.getProductById(id)
                        .flatMap(producto -> {
                            producto.setFoto(UUID.randomUUID() + "-" +filePart.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", ""));
                            return filePart.transferTo(new File(path + producto.getFoto()))
                                    .then(productoService.saveProduct(producto));
                        })).flatMap(producto ->  ServerResponse.created(URI.create("api/v2/productos".concat(producto.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(producto)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveProductWhitImage(ServerRequest request){

        Mono<Producto> producto = request.multipartData().map(multipart -> {
            FormFieldPart name = (FormFieldPart) multipart.toSingleValueMap().get("name");
            FormFieldPart price = (FormFieldPart) multipart.toSingleValueMap().get("price");
            FormFieldPart categoriaId = (FormFieldPart) multipart.toSingleValueMap().get("categoria.id");
            FormFieldPart categoriaNombreCategoria = (FormFieldPart) multipart.toSingleValueMap().get("categoria.nombreCategoria");


            Categoria categoria = new Categoria(categoriaNombreCategoria.value());
            categoria.setId(categoriaId.value());

            return new Producto(name.value(), Double.valueOf(price.value()), categoria);
        });

        return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap( filePart -> producto
                        .flatMap(producto1 -> {
                            producto1.setFoto((UUID.randomUUID() + "-" + filePart.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", "")));
                            return filePart.transferTo(new File(path + producto1.getFoto()))
                                    .then(productoService.saveProduct(producto1));
                        })).flatMap(prod ->  ServerResponse.created(URI.create("api/v2/productos".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(producto)));
    }
}
