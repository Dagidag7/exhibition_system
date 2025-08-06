package com.exhibition.repository;

import com.exhibition.MainVerticle;
import com.exhibition.model.Exhibitor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExhibitorRepositoryImpl implements ExhibitorRepository {

    private final SQLClient jdbc;
    
    public ExhibitorRepositoryImpl() {
        this.jdbc = MainVerticle.jdbc;
    }

    @Override
    public void addExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Integer>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                String sql = "INSERT INTO exhibitor (name, contact_person, email, booth_number, product_ids, logo_url, floor_number, password, password_changed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING exhibitor_id";
                
        JsonArray params = new JsonArray()
                .add(exhibitor.getName())
                .add(exhibitor.getContactPerson())
                .add(exhibitor.getEmail())
                .add(exhibitor.getBoothNumber())
                .add(exhibitor.getProductIds())
                    .add(exhibitor.getLogoUrl())
                    .add(exhibitor.getFloorNumber())
                    .add("Welcome123") // Default password
                    .add(false); // password_changed = false

                jdbc.queryWithParams(sql, params, res -> {
                if (res.succeeded()) {
                        ResultSet rs = res.result();
                        if (rs.getNumRows() > 0) {
                            Integer id = rs.getResults().get(0).getInteger(0);
                            promise.complete(id);
                        } else {
                            promise.fail("Failed to insert exhibitor");
                        }
                } else {
                    promise.fail(res.cause());
                }
            });
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }

    @Override
    public void getExhibitorById(int id, Handler<AsyncResult<Exhibitor>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                String sql = "SELECT exhibitor_id, name, contact_person, email, booth_number, product_ids, logo_url, floor_number, password_changed FROM exhibitor WHERE exhibitor_id = ?";
                
                jdbc.queryWithParams(sql, new JsonArray().add(id), res -> {
                if (res.succeeded()) {
                        ResultSet rs = res.result();
                        if (rs.getNumRows() > 0) {
                            JsonObject row = rs.getRows().get(0);
                            Exhibitor exhibitor = mapRowToExhibitor(row);
                            promise.complete(exhibitor);
                        } else {
                            promise.fail("Exhibitor not found");
                        }
                } else {
                    promise.fail(res.cause());
                }
            });
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }

    @Override
    public void getAllExhibitors(Handler<AsyncResult<List<Exhibitor>>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                String sql = "SELECT exhibitor_id, name, contact_person, email, booth_number, product_ids, logo_url, floor_number, password_changed FROM exhibitor ORDER BY name";
                
                jdbc.query(sql, res -> {
                if (res.succeeded()) {
                        ResultSet rs = res.result();
                            List<Exhibitor> exhibitors = new ArrayList<>();
                        
                        for (JsonObject row : rs.getRows()) {
                            exhibitors.add(mapRowToExhibitor(row));
                        }
                        
                            promise.complete(exhibitors);
                } else {
                    promise.fail(res.cause());
                }
            });
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }

    @Override
    public void updateExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Void>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                String sql = "UPDATE exhibitor SET name = ?, contact_person = ?, email = ?, booth_number = ?, product_ids = ?, logo_url = ?, floor_number = ? WHERE exhibitor_id = ?";
                
        JsonArray params = new JsonArray()
                .add(exhibitor.getName())
                .add(exhibitor.getContactPerson())
                .add(exhibitor.getEmail())
                .add(exhibitor.getBoothNumber())
                .add(exhibitor.getProductIds())
                .add(exhibitor.getLogoUrl())
                    .add(exhibitor.getFloorNumber())
                .add(exhibitor.getExhibitorId());

                jdbc.updateWithParams(sql, params, res -> {
                if (res.succeeded()) {
                            promise.complete();
                } else {
                    promise.fail(res.cause());
                }
            });
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }

    @Override
    public void deleteExhibitor(int id, Handler<AsyncResult<Void>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
        String sql = "DELETE FROM exhibitor WHERE exhibitor_id = ?";
                
                jdbc.updateWithParams(sql, new JsonArray().add(id), res -> {
                    if (res.succeeded()) {
                        promise.complete();
                    } else {
                        promise.fail(res.cause());
                    }
                });
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }
    
    public void updateExhibitorPassword(int exhibitorId, String password, Handler<AsyncResult<Void>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                String sql = "UPDATE exhibitor SET password = ?, password_changed = true WHERE exhibitor_id = ?";
                
                JsonArray params = new JsonArray()
                    .add(password)
                    .add(exhibitorId);
                
                jdbc.updateWithParams(sql, params, res -> {
                if (res.succeeded()) {
                            promise.complete();
                    } else {
                        promise.fail(res.cause());
                    }
                });
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }
    
    public void getExhibitorByEmail(String email, Handler<AsyncResult<Exhibitor>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                String sql = "SELECT exhibitor_id, name, contact_person, email, booth_number, product_ids, password, logo_url, floor_number, password_changed FROM exhibitor WHERE email = ?";
                
                jdbc.queryWithParams(sql, new JsonArray().add(email), res -> {
                    if (res.succeeded()) {
                        ResultSet rs = res.result();
                        if (rs.getNumRows() > 0) {
                            JsonObject row = rs.getRows().get(0);
                            Exhibitor exhibitor = mapRowToExhibitorWithPassword(row);
                            promise.complete(exhibitor);
                        } else {
                            promise.fail("Exhibitor not found");
                        }
                } else {
                    promise.fail(res.cause());
                }
            });
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }
    
    private Exhibitor mapRowToExhibitor(JsonObject row) {
        Exhibitor exhibitor = new Exhibitor();
        exhibitor.setExhibitorId(row.getInteger("exhibitor_id"));
        exhibitor.setName(row.getString("name"));
        exhibitor.setContactPerson(row.getString("contact_person"));
        exhibitor.setEmail(row.getString("email"));
        exhibitor.setBoothNumber(row.getString("booth_number"));
        exhibitor.setProductIds(row.getString("product_ids"));
        exhibitor.setLogoUrl(row.getString("logo_url"));
        exhibitor.setFloorNumber(row.getString("floor_number"));
        exhibitor.setPasswordChanged(row.getBoolean("password_changed", false));
        
        // Set registration date to current time since it's not stored in database
        exhibitor.setRegistrationDate(LocalDateTime.now());
        
        return exhibitor;
    }
    
    private Exhibitor mapRowToExhibitorWithPassword(JsonObject row) {
        Exhibitor exhibitor = new Exhibitor();
        exhibitor.setExhibitorId(row.getInteger("exhibitor_id"));
        exhibitor.setName(row.getString("name"));
        exhibitor.setContactPerson(row.getString("contact_person"));
        exhibitor.setEmail(row.getString("email"));
        exhibitor.setBoothNumber(row.getString("booth_number"));
        exhibitor.setProductIds(row.getString("product_ids"));
        exhibitor.setPassword(row.getString("password"));
        exhibitor.setLogoUrl(row.getString("logo_url"));
        exhibitor.setFloorNumber(row.getString("floor_number"));
        exhibitor.setPasswordChanged(row.getBoolean("password_changed", false));
        
        // Set registration date to current time since it's not stored in database
        exhibitor.setRegistrationDate(LocalDateTime.now());
        
        return exhibitor;
    }
}