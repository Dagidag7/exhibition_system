package com.exhibition.controller;

import com.exhibition.model.Exhibitor;
import com.exhibition.service.ExhibitorService;
import com.exhibition.util.EmailValidator;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ExhibitorController {

    public static void registerRoutes(Router router, ExhibitorService exhibitorService) {
        router.post("/exhibitors").handler(ctx -> createExhibitor(ctx, exhibitorService));
        router.get("/exhibitors").handler(ctx -> getAllExhibitors(ctx, exhibitorService));
        router.get("/exhibitors/:id").handler(ctx -> getExhibitorById(ctx, exhibitorService));
        router.put("/exhibitors/:id").handler(ctx -> updateExhibitor(ctx, exhibitorService));
        router.delete("/exhibitors/:id").handler(ctx -> deleteExhibitor(ctx, exhibitorService));
        router.put("/exhibitors/:id/password").handler(ctx -> changePassword(ctx, exhibitorService));
        router.get("/exhibitors/check-email/:email").handler(ctx -> checkEmailExists(ctx, exhibitorService));
    }

    private static void createExhibitor(RoutingContext ctx, ExhibitorService exhibitorService) {
        String body = ctx.getBody() != null ? ctx.getBody().toString() : null;
        
        if (body == null || body.trim().isEmpty()) {
            ctx.response()
            .setStatusCode(400)
            .putHeader("content-type", "application/json")
            .end("{\"error\": \"Request body is required\"}");
            return;
        }
        
        try {
            Exhibitor exhibitor = Json.decodeValue(body, Exhibitor.class);
            
            // Validate required fields
            if (exhibitor.getName() == null || exhibitor.getName().trim().isEmpty()) {
                ctx.response()
                   .setStatusCode(400)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"Company name is required\"}");
                return;
            }
            
            if (exhibitor.getEmail() == null || exhibitor.getEmail().trim().isEmpty()) {
                ctx.response()
                   .setStatusCode(400)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"Email is required\"}");
                return;
            }
            
            // Validate email format
            String normalizedEmail = EmailValidator.validateAndNormalize(exhibitor.getEmail());
            if (normalizedEmail == null) {
                ctx.response()
                   .setStatusCode(400)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"Invalid email format\"}");
                return;
            }
            
            // Set normalized email
            exhibitor.setEmail(normalizedEmail);
            
            // Check if email already exists
            exhibitorService.checkEmailExists(normalizedEmail, emailCheckRes -> {
                if (emailCheckRes.succeeded()) {
                    if (emailCheckRes.result()) {
                        // Email already exists
                        ctx.response()
                           .setStatusCode(409)
                           .putHeader("content-type", "application/json")
                           .end("{\"error\": \"Email already registered\"}");
                    } else {
                        // Email is unique, proceed with registration
                        exhibitorService.registerExhibitor(exhibitor, res -> {
                            if (res.succeeded()) {
                                ctx.response()
                                .setStatusCode(201)
                                .putHeader("content-type", "application/json")
                                .end("{\"message\": \"Exhibitor created successfully\"}");
                            } else {
                                // Add detailed error logging
                                System.err.println("Error creating exhibitor: " + res.cause().getMessage());
                                res.cause().printStackTrace();
                                
                                ctx.response()
                                .setStatusCode(500)
                                .putHeader("content-type", "application/json")
                                .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
                            }
                        });
                    }
                } else {
                    ctx.response()
                       .setStatusCode(500)
                       .putHeader("content-type", "application/json")
                       .end("{\"error\": \"Failed to validate email uniqueness\"}");
                }
            });
        } catch (Exception e) {
            // Add detailed error logging for JSON parsing errors
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            
            ctx.response()
            .setStatusCode(400)
            .putHeader("content-type", "application/json")
            .end("{\"error\": \"Invalid JSON format: " + e.getMessage() + "\"}");
        }
    }

    private static void getAllExhibitors(RoutingContext ctx, ExhibitorService exhibitorService) {
        exhibitorService.listExhibitors(res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(500).end(res.cause().getMessage());
            }
        });
    }

    private static void getExhibitorById(RoutingContext ctx, ExhibitorService exhibitorService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        exhibitorService.getExhibitor(id, res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(404).end("Exhibitor not found");
            }
        });
    }

    private static void updateExhibitor(RoutingContext ctx, ExhibitorService exhibitorService) {
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
            Exhibitor exhibitor = Json.decodeValue(body, Exhibitor.class);
            exhibitor.setExhibitorId(id);
            exhibitorService.updateExhibitor(exhibitor, res -> {
                if (res.succeeded()) {
                    ctx.response()
                    .putHeader("content-type", "application/json")
                    .end("{\"message\": \"Exhibitor updated successfully\"}");
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

    private static void deleteExhibitor(RoutingContext ctx, ExhibitorService exhibitorService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        exhibitorService.removeExhibitor(id, res -> {
            if (res.succeeded()) {
                ctx.response()
                .putHeader("content-type", "application/json")
                .end("{\"message\": \"Exhibitor deleted successfully\"}");
            } else {
                ctx.response()
                .setStatusCode(500)
                .putHeader("content-type", "application/json")
                .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
            }
        });
    }

    private static void changePassword(RoutingContext ctx, ExhibitorService exhibitorService) {
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
            io.vertx.core.json.JsonObject jsonBody = new io.vertx.core.json.JsonObject(body);
            String newPassword = jsonBody.getString("password");
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                ctx.response()
                .setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end("{\"error\": \"Password is required\"}");
                return;
            }
            
            exhibitorService.updateExhibitorPassword(id, newPassword, res -> {
                if (res.succeeded()) {
                    ctx.response()
                    .putHeader("content-type", "application/json")
                    .end("{\"message\": \"Password changed successfully\"}");
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

    private static void checkEmailExists(RoutingContext ctx, ExhibitorService exhibitorService) {
        String email = ctx.pathParam("email");
        
        if (email == null || email.trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Email parameter is required\"}");
            return;
        }
        
        // Validate email format
        String normalizedEmail = EmailValidator.validateAndNormalize(email);
        if (normalizedEmail == null) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Invalid email format\"}");
            return;
        }
        
        exhibitorService.checkEmailExists(normalizedEmail, res -> {
            if (res.succeeded()) {
                JsonObject response = new JsonObject()
                    .put("email", normalizedEmail)
                    .put("exists", res.result());
                ctx.response()
                   .putHeader("content-type", "application/json")
                   .end(response.encode());
            } else {
                ctx.response()
                   .setStatusCode(500)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
            }
        });
    }
}