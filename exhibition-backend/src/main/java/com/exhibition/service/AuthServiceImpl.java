package com.exhibition.service;

import com.exhibition.MainVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

public class AuthServiceImpl implements AuthService {
    
    private final SQLClient jdbc;
    
    public AuthServiceImpl(SQLClient jdbc) {
        this.jdbc = jdbc;
    }
    
    @Override
    public void authenticateUser(String email, String password, Handler<AsyncResult<JsonObject>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                // First check in attendee table
                String attendeeQuery = "SELECT attendee_id as id, name, email, 'attendee' as role FROM attendee WHERE email = ? AND password = ?";
                
                jdbc.queryWithParams(attendeeQuery, 
                    new io.vertx.core.json.JsonArray().add(email).add(password),
                    attendeeResult -> {
                        if (attendeeResult.succeeded()) {
                            ResultSet rs = attendeeResult.result();
                            if (rs.getNumRows() > 0) {
                                JsonObject row = rs.getRows().get(0);
                                promise.complete(row);
                                return;
                            }
                        }
                        
                        // If not found in attendee, check in exhibitor table
                        String exhibitorQuery = "SELECT exhibitor_id as id, name, email, booth_number, floor_number, logo_url, status, 'exhibitor' as role FROM exhibitor WHERE email = ? AND password = ?";
                        
                        jdbc.queryWithParams(exhibitorQuery, 
                            new io.vertx.core.json.JsonArray().add(email).add(password),
                            exhibitorResult -> {
                                if (exhibitorResult.succeeded()) {
                                    ResultSet rs = exhibitorResult.result();
                                    if (rs.getNumRows() > 0) {
                                        JsonObject row = rs.getRows().get(0);
                                        promise.complete(row);
                                        return;
                                    }
                                }
                                
                                // Check for admin (hardcoded for now)
                                if ("admin@exhibition.com".equals(email) && "admin123".equals(password)) {
                                    JsonObject adminUser = new JsonObject()
                                        .put("id", 0)
                                        .put("name", "Admin User")
                                        .put("email", email)
                                        .put("role", "admin");
                                    promise.complete(adminUser);
                                    return;
                                }
                                
                                // User not found
                                promise.fail("Invalid credentials");
                            });
                    });
                    
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }
    
    @Override
    public void getUserById(int userId, Handler<AsyncResult<JsonObject>> resultHandler) {
        MainVerticle.vertx.executeBlocking(promise -> {
            try {
                // Check in attendee table first
                String attendeeQuery = "SELECT attendee_id as id, name, email, 'attendee' as role FROM attendee WHERE attendee_id = ?";
                
                jdbc.queryWithParams(attendeeQuery, 
                    new io.vertx.core.json.JsonArray().add(userId),
                    attendeeResult -> {
                        if (attendeeResult.succeeded()) {
                            ResultSet rs = attendeeResult.result();
                            if (rs.getNumRows() > 0) {
                                JsonObject row = rs.getRows().get(0);
                                promise.complete(row);
                                return;
                            }
                        }
                        
                        // If not found in attendee, check in exhibitor table
                        String exhibitorQuery = "SELECT exhibitor_id as id, name, email, booth_number, floor_number, logo_url, status, 'exhibitor' as role FROM exhibitor WHERE exhibitor_id = ?";
                        
                        jdbc.queryWithParams(exhibitorQuery, 
                            new io.vertx.core.json.JsonArray().add(userId),
                            exhibitorResult -> {
                                if (exhibitorResult.succeeded()) {
                                    ResultSet rs = exhibitorResult.result();
                                    if (rs.getNumRows() > 0) {
                                        JsonObject row = rs.getRows().get(0);
                                        promise.complete(row);
                                        return;
                                    }
                                }
                                
                                // User not found
                                promise.fail("User not found");
                            });
                    });
                    
            } catch (Exception e) {
                promise.fail(e);
            }
        }, resultHandler);
    }
    
    @Override
    public void updateUserRole(int userId, String role, Handler<AsyncResult<Void>> resultHandler) {
        // This would typically involve updating a user_roles table
        // For now, we'll just return success
        resultHandler.handle(io.vertx.core.Future.succeededFuture());
    }
} 