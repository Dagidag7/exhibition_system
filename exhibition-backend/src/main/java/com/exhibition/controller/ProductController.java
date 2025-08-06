package com.exhibition.controller;

import com.exhibition.model.Product;
import com.exhibition.service.ProductService;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProductController {

    public static void registerRoutes(Router router, ProductService productService) {
        router.post("/products").handler(ctx -> createProduct(ctx, productService));
        router.get("/products").handler(ctx -> getAllProducts(ctx, productService));
        router.get("/products/:id").handler(ctx -> getProductById(ctx, productService));
        router.get("/exhibitors/:exhibitorId/products").handler(ctx -> getProductsByExhibitor(ctx, productService));
        router.put("/products/:id").handler(ctx -> updateProduct(ctx, productService));
        router.delete("/products/:id").handler(ctx -> deleteProduct(ctx, productService));
    }

    private static void createProduct(RoutingContext ctx, ProductService productService) {
        String body = ctx.getBody() != null ? ctx.getBody().toString() : null;
        
        if (body == null || body.trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Request body is required\"}");
            return;
        }
        
        try {
            Product product = Json.decodeValue(body, Product.class);
            productService.registerProduct(product, res -> {
                if (res.succeeded()) {
                    ctx.response()
                       .setStatusCode(201)
                       .putHeader("content-type", "application/json")
                       .end("{\"message\": \"Product created successfully\"}");
                } else {
                    ctx.response()
                       .setStatusCode(500)
                       .putHeader("content-type", "application/json")
                       .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
                }
            });
        } catch (Exception e) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Invalid JSON format: " + e.getMessage() + "\"}");
        }
    }

    private static void getAllProducts(RoutingContext ctx, ProductService productService) {
        productService.listProducts(res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(500).end(res.cause().getMessage());
            }
        });
    }

    private static void getProductById(RoutingContext ctx, ProductService productService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        productService.getProduct(id, res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(404).end("Product not found");
            }
        });
    }

    private static void getProductsByExhibitor(RoutingContext ctx, ProductService productService) {
        int exhibitorId = Integer.parseInt(ctx.pathParam("exhibitorId"));
        productService.getProductsByExhibitor(exhibitorId, res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(500).end(res.cause().getMessage());
            }
        });
    }

    private static void updateProduct(RoutingContext ctx, ProductService productService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String body = ctx.getBody() != null ? ctx.getBody().toString() : null;
        
        if (body == null || body.trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Request body is required\"}");
            return;
        }
        
        try {
            Product product = Json.decodeValue(body, Product.class);
            product.setProductId(id);
            productService.updateProduct(product, res -> {
                if (res.succeeded()) {
                    ctx.response()
                       .putHeader("content-type", "application/json")
                       .end("{\"message\": \"Product updated successfully\"}");
                } else {
                    ctx.response()
                       .setStatusCode(500)
                       .putHeader("content-type", "application/json")
                       .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
                }
            });
        } catch (Exception e) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Invalid JSON format: " + e.getMessage() + "\"}");
        }
    }

    private static void deleteProduct(RoutingContext ctx, ProductService productService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        productService.removeProduct(id, res -> {
            if (res.succeeded()) {
                ctx.response()
                   .putHeader("content-type", "application/json")
                   .end("{\"message\": \"Product deleted successfully\"}");
            } else {
                ctx.response()
                   .setStatusCode(500)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
            }
        });
    }
}