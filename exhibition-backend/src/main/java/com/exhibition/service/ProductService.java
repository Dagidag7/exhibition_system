package com.exhibition.service;

import com.exhibition.model.Product;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface ProductService {
    void registerProduct(Product product, Handler<AsyncResult<Void>> resultHandler);
    void getProduct(int id, Handler<AsyncResult<Product>> resultHandler);
    void listProducts(Handler<AsyncResult<List<Product>>> resultHandler);
    void getProductsByExhibitor(int exhibitorId, Handler<AsyncResult<List<Product>>> resultHandler);
    void updateProduct(Product product, Handler<AsyncResult<Void>> resultHandler);
    void removeProduct(int id, Handler<AsyncResult<Void>> resultHandler);
}