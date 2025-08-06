package com.exhibition.repository;

import com.exhibition.model.Product;
import com.exhibition.MainVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    private final SQLClient jdbc = MainVerticle.jdbc;

    @Override
    public void addProduct(Product product, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "INSERT INTO product (name, description, exhibitor_id, category, image_url) VALUES (?, ?, ?, ?, ?)";
        JsonArray params = new JsonArray()
                .add(product.getName())
                .add(product.getDescription())
                .add(product.getExhibitorId())
                .add(product.getCategory())
                .add(product.getImageUrl());

        MainVerticle.vertx.executeBlocking(promise -> {
            jdbc.getConnection(res -> {
                if (res.succeeded()) {
                    SQLConnection connection = res.result();
                    connection.updateWithParams(sql, params, ar -> {
                        connection.close();
                        if (ar.succeeded()) {
                            promise.complete();
                        } else {
                            promise.fail(ar.cause());
                        }
                    });
                } else {
                    promise.fail(res.cause());
                }
            });
        }, false, ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(io.vertx.core.Future.succeededFuture());
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void getProductById(int id, Handler<AsyncResult<Product>> resultHandler) {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        MainVerticle.vertx.executeBlocking(promise -> {
            jdbc.getConnection(res -> {
                if (res.succeeded()) {
                    SQLConnection connection = res.result();
                    connection.queryWithParams(sql, new JsonArray().add(id), ar -> {
                        connection.close();
                        if (ar.succeeded() && !ar.result().getRows().isEmpty()) {
                            JsonObject row = ar.result().getRows().get(0);
                            Product product = new Product();
                            product.setProductId(row.getInteger("product_id"));
                            product.setName(row.getString("name"));
                            product.setDescription(row.getString("description"));
                            product.setExhibitorId(row.getInteger("exhibitor_id"));
                            product.setCategory(row.getString("category"));
                            product.setImageUrl(row.getString("image_url"));
                            promise.complete(product);
                        } else {
                            promise.fail("Product not found");
                        }
                    });
                } else {
                    promise.fail(res.cause());
                }
            });
        }, false, ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(io.vertx.core.Future.succeededFuture((Product) ar.result()));
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void getAllProducts(Handler<AsyncResult<List<Product>>> resultHandler) {
        String sql = "SELECT * FROM product";
        MainVerticle.vertx.executeBlocking(promise -> {
            jdbc.getConnection(res -> {
                if (res.succeeded()) {
                    SQLConnection connection = res.result();
                    connection.query(sql, ar -> {
                        connection.close();
                        if (ar.succeeded()) {
                            List<Product> products = new ArrayList<>();
                            for (JsonObject row : ar.result().getRows()) {
                                Product product = new Product();
                                product.setProductId(row.getInteger("product_id"));
                                product.setName(row.getString("name"));
                                product.setDescription(row.getString("description"));
                                product.setExhibitorId(row.getInteger("exhibitor_id"));
                                product.setCategory(row.getString("category"));
                                product.setImageUrl(row.getString("image_url"));
                                products.add(product);
                            }
                            promise.complete(products);
                        } else {
                            promise.fail(ar.cause());
                        }
                    });
                } else {
                    promise.fail(res.cause());
                }
            });
        }, false, ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(io.vertx.core.Future.succeededFuture((List<Product>) ar.result()));
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void getProductsByExhibitor(int exhibitorId, Handler<AsyncResult<List<Product>>> resultHandler) {
        String sql = "SELECT * FROM product WHERE exhibitor_id = ?";
        MainVerticle.vertx.executeBlocking(promise -> {
            jdbc.getConnection(res -> {
                if (res.succeeded()) {
                    SQLConnection connection = res.result();
                    connection.queryWithParams(sql, new JsonArray().add(exhibitorId), ar -> {
                        connection.close();
                        if (ar.succeeded()) {
                            List<Product> products = new ArrayList<>();
                            for (JsonObject row : ar.result().getRows()) {
                                Product product = new Product();
                                product.setProductId(row.getInteger("product_id"));
                                product.setName(row.getString("name"));
                                product.setDescription(row.getString("description"));
                                product.setExhibitorId(row.getInteger("exhibitor_id"));
                                product.setCategory(row.getString("category"));
                                product.setImageUrl(row.getString("image_url"));
                                products.add(product);
                            }
                            promise.complete(products);
                        } else {
                            promise.fail(ar.cause());
                        }
                    });
                } else {
                    promise.fail(res.cause());
                }
            });
        }, false, ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(io.vertx.core.Future.succeededFuture((List<Product>) ar.result()));
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void updateProduct(Product product, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "UPDATE product SET name=?, description=?, exhibitor_id=?, category=?, image_url=? WHERE product_id=?";
        JsonArray params = new JsonArray()
                .add(product.getName())
                .add(product.getDescription())
                .add(product.getExhibitorId())
                .add(product.getCategory())
                .add(product.getImageUrl())
                .add(product.getProductId());

        MainVerticle.vertx.executeBlocking(promise -> {
            jdbc.getConnection(res -> {
                if (res.succeeded()) {
                    SQLConnection connection = res.result();
                    connection.updateWithParams(sql, params, ar -> {
                        connection.close();
                        if (ar.succeeded()) {
                            promise.complete();
                        } else {
                            promise.fail(ar.cause());
                        }
                    });
                } else {
                    promise.fail(res.cause());
                }
            });
        }, false, ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(io.vertx.core.Future.succeededFuture());
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void deleteProduct(int id, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        MainVerticle.vertx.executeBlocking(promise -> {
            jdbc.getConnection(res -> {
                if (res.succeeded()) {
                    SQLConnection connection = res.result();
                    connection.updateWithParams(sql, new JsonArray().add(id), ar -> {
                        connection.close();
                        if (ar.succeeded()) {
                            promise.complete();
                        } else {
                            promise.fail(ar.cause());
                        }
                    });
                } else {
                    promise.fail(res.cause());
                }
            });
        }, false, ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(io.vertx.core.Future.succeededFuture());
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
            }
        });
    }
}