package com.exhibition.repository;

import com.exhibition.model.Product;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface ProductRepository {
    void addProduct(Product product, Handler<AsyncResult<Void>> resultHandler);
    void getProductById(int id, Handler<AsyncResult<Product>> resultHandler);
    void getAllProducts(Handler<AsyncResult<List<Product>>> resultHandler);
    void getProductsByExhibitor(int exhibitorId, Handler<AsyncResult<List<Product>>> resultHandler);
    void updateProduct(Product product, Handler<AsyncResult<Void>> resultHandler);
    void deleteProduct(int id, Handler<AsyncResult<Void>> resultHandler);
}