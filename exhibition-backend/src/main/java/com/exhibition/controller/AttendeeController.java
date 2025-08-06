package com.exhibition.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.exhibition.model.Attendee;
import com.exhibition.service.AttendeeService;
import com.exhibition.util.EmailValidator;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class AttendeeController {

    public static void registerRoutes(Router router, AttendeeService attendeeService) {
        router.post("/attendees").handler(ctx -> createAttendee(ctx, attendeeService));
        router.get("/attendees").handler(ctx -> getAllAttendees(ctx, attendeeService));
        router.get("/attendees/:id").handler(ctx -> getAttendeeById(ctx, attendeeService));
        router.put("/attendees/:id").handler(ctx -> updateAttendee(ctx, attendeeService));
        router.delete("/attendees/:id").handler(ctx -> deleteAttendee(ctx, attendeeService));
        router.get("/attendees/check-email/:email").handler(ctx -> checkEmailExists(ctx, attendeeService));
    }

private static void createAttendee(RoutingContext ctx, AttendeeService attendeeService) {
    String body = ctx.getBody() != null ? ctx.getBody().toString() : null;
    
    if (body == null || body.trim().isEmpty()) {
        ctx.response()
           .setStatusCode(400)
           .putHeader("content-type", "application/json")
           .end("{\"error\": \"Request body is required\"}");
        return;
    }
    
    try {
        Attendee attendee = Json.decodeValue(body, Attendee.class);
        
        // Validate required fields
        if (attendee.getName() == null || attendee.getName().trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Name is required\"}");
            return;
        }
        
        if (attendee.getEmail() == null || attendee.getEmail().trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Email is required\"}");
            return;
        }
        
        // Validate email format
        String normalizedEmail = EmailValidator.validateAndNormalize(attendee.getEmail());
        if (normalizedEmail == null) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Invalid email format\"}");
            return;
        }
        
        // Set normalized email
        attendee.setEmail(normalizedEmail);
        
        // Check if email already exists
        attendeeService.checkEmailExists(normalizedEmail, emailCheckRes -> {
            if (emailCheckRes.succeeded()) {
                if (emailCheckRes.result()) {
                    // Email already exists
                    ctx.response()
                       .setStatusCode(409)
                       .putHeader("content-type", "application/json")
                       .end("{\"error\": \"Email already registered\"}");
                } else {
                    // Email is unique, proceed with registration
                    attendeeService.registerAttendee(attendee, res -> {
                        if (res.succeeded()) {
                            ctx.response()
                               .setStatusCode(201)
                               .putHeader("content-type", "application/json")
                               .end("{\"message\": \"Attendee created successfully\"}");
                        } else {
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
        ctx.response()
           .setStatusCode(400)
           .putHeader("content-type", "application/json")
           .end("{\"error\": \"Invalid JSON format: " + e.getMessage() + "\"}");
    }
}
    private static void getAllAttendees(RoutingContext ctx, AttendeeService attendeeService) {
        attendeeService.listAttendees(res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(500).end(res.cause().getMessage());
            }
        });
    }

    private static void getAttendeeById(RoutingContext ctx, AttendeeService attendeeService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        attendeeService.getAttendee(id, res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(404).end("Attendee not found");
            }
        });
    }

    private static void updateAttendee(RoutingContext ctx, AttendeeService attendeeService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String body = ctx.getBodyAsString();
        
        if (body == null || body.trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Request body is required\"}");
            return;
        }
        
        try {
            Attendee attendee = Json.decodeValue(body, Attendee.class);
            attendee.setAttendeeId(id);
            
            attendeeService.updateAttendee(attendee, res -> {
                if (res.succeeded()) {
                    ctx.response()
                       .putHeader("content-type", "application/json")
                       .end("{\"message\": \"Attendee updated successfully\"}");
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

    private static void deleteAttendee(RoutingContext ctx, AttendeeService attendeeService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        attendeeService.removeAttendee(id, res -> {
            if (res.succeeded()) {
                ctx.response()
                   .putHeader("content-type", "application/json")
                   .end("{\"message\": \"Attendee deleted successfully\"}");
            } else {
                ctx.response()
                   .setStatusCode(500)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
            }
        });
    }

    private static void checkEmailExists(RoutingContext ctx, AttendeeService attendeeService) {
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
        
        attendeeService.checkEmailExists(normalizedEmail, res -> {
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