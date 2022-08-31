package com.williamfranco;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

/*

Example whit anonimous function
    @Bean
    public RouterFunction<ServerResponse> routes(){
        return route(GET("api/v2/productos").or(GET("api/v3/productos")), request -> {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.getAllProducts(), Producto.class);
        });
    }*/


    // example with handler function
    @Bean
    public RouterFunction<ServerResponse> routes(Handler handler){
        return route(GET("api/v2/productos").or(GET("api/v3/productos")), handler::getProducts)
                .andRoute(GET("api/v2/productos/{id}"), handler::getProductById)
                .andRoute(POST("api/v2/productos"), handler::saveProduct)
                .andRoute(PUT("api/v2/productos/{id}"), handler::editProduct)
                .andRoute(DELETE("api/v2/productos/{id}"), handler::deleteProduct)
                .andRoute(POST("api/v2/productos/upload_image/{id}"), handler::uploadImage)
                .andRoute(POST("api/v2/productos/createWhitImage"), handler::saveProductWhitImage);
    }
}
