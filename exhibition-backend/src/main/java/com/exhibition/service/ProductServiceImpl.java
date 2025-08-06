package com.exhibition.service;

import com.exhibition.model.Product;
import com.exhibition.repository.ProductRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void registerProduct(Product product, Handler<AsyncResult<Void>> resultHandler) {
        // Add business logic here if needed (e.g., validation)
        productRepository.addProduct(product, resultHandler);
    }

    @Override
    public void getProduct(int id, Handler<AsyncResult<Product>> resultHandler) {
        productRepository.getProductById(id, resultHandler);
    }

    @Override
    public void listProducts(Handler<AsyncResult<List<Product>>> resultHandler) {
        productRepository.getAllProducts(resultHandler);
    }

    @Override
    public void getProductsByExhibitor(int exhibitorId, Handler<AsyncResult<List<Product>>> resultHandler) {
        productRepository.getProductsByExhibitor(exhibitorId, resultHandler);
    }

    @Override
    public void updateProduct(Product product, Handler<AsyncResult<Void>> resultHandler) {
        productRepository.updateProduct(product, resultHandler);
    }

    @Override
    public void removeProduct(int id, Handler<AsyncResult<Void>> resultHandler) {
        productRepository.deleteProduct(id, resultHandler);
    }
}