package com.exhibition.controller;

import com.exhibition.model.ConferenceRegistration;
import com.exhibition.repository.ConferenceRegistrationRepository;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ConferenceRegistrationController {
    private final ConferenceRegistrationRepository registrationRepository;

    public ConferenceRegistrationController(ConferenceRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public void registerRoutes(Router router) {
        router.post("/conference-registrations").handler(BodyHandler.create()).handler(this::registerForConference);
        router.get("/conference-registrations").handler(this::getAllRegistrations);
        router.get("/conference-registrations/:id").handler(this::getRegistrationById);
        router.get("/conference-registrations/conference/:conferenceId").handler(this::getRegistrationsByConference);
        router.get("/conference-registrations/email/:email").handler(this::getRegistrationsByEmail);
        router.delete("/conference-registrations/:id").handler(this::deleteRegistration);
    }

    private void registerForConference(RoutingContext context) {
        try {
            JsonObject body = context.getBodyAsJson();
            
            ConferenceRegistration registration = new ConferenceRegistration(
                body.getInteger("conferenceId"),
                body.getString("firstName"),
                body.getString("lastName"),
                body.getString("email"),
                body.getString("phone"),
                body.getString("company"),
                body.getString("jobTitle"),
                body.getString("dietaryRestrictions"),
                body.getString("specialRequirements")
            );

            registrationRepository.addRegistration(registration)
                .onSuccess(savedRegistration -> {
                    JsonObject response = new JsonObject()
                        .put("success", true)
                        .put("message", "Registration successful!")
                        .put("registrationId", savedRegistration.getRegistrationId())
                        .put("registration", JsonObject.mapFrom(savedRegistration));
                    context.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(201)
                        .end(response.encode());
                })
                .onFailure(error -> {
                    JsonObject response = new JsonObject()
                        .put("success", false)
                        .put("message", "Registration failed: " + error.getMessage());
                    context.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(500)
                        .end(response.encode());
                });
        } catch (Exception e) {
            JsonObject response = new JsonObject()
                .put("success", false)
                .put("message", "Invalid request data: " + e.getMessage());
            context.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(400)
                .end(response.encode());
        }
    }

    private void getAllRegistrations(RoutingContext context) {
        registrationRepository.getAllRegistrations()
            .onSuccess(registrations -> {
                JsonObject response = new JsonObject()
                    .put("success", true)
                    .put("registrations", registrations.stream()
                        .map(JsonObject::mapFrom)
                        .collect(java.util.stream.Collectors.toList()));
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .end(response.encode());
            })
            .onFailure(error -> {
                JsonObject response = new JsonObject()
                    .put("success", false)
                    .put("message", "Failed to fetch registrations: " + error.getMessage());
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(500)
                    .end(response.encode());
            });
    }

    private void getRegistrationById(RoutingContext context) {
        Integer registrationId = Integer.parseInt(context.pathParam("id"));
        
        registrationRepository.getRegistrationById(registrationId)
            .onSuccess(registration -> {
                JsonObject response = new JsonObject()
                    .put("success", true)
                    .put("registration", JsonObject.mapFrom(registration));
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .end(response.encode());
            })
            .onFailure(error -> {
                JsonObject response = new JsonObject()
                    .put("success", false)
                    .put("message", "Registration not found: " + error.getMessage());
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(404)
                    .end(response.encode());
            });
    }

    private void getRegistrationsByConference(RoutingContext context) {
        Integer conferenceId = Integer.parseInt(context.pathParam("conferenceId"));
        
        registrationRepository.getRegistrationsByConferenceId(conferenceId)
            .onSuccess(registrations -> {
                JsonObject response = new JsonObject()
                    .put("success", true)
                    .put("registrations", registrations.stream()
                        .map(JsonObject::mapFrom)
                        .collect(java.util.stream.Collectors.toList()));
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .end(response.encode());
            })
            .onFailure(error -> {
                JsonObject response = new JsonObject()
                    .put("success", false)
                    .put("message", "Failed to fetch registrations: " + error.getMessage());
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(500)
                    .end(response.encode());
            });
    }

    private void getRegistrationsByEmail(RoutingContext context) {
        String email = context.pathParam("email");
        
        registrationRepository.getRegistrationsByEmail(email)
            .onSuccess(registrations -> {
                JsonObject response = new JsonObject()
                    .put("success", true)
                    .put("registrations", registrations.stream()
                        .map(JsonObject::mapFrom)
                        .collect(java.util.stream.Collectors.toList()));
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .end(response.encode());
            })
            .onFailure(error -> {
                JsonObject response = new JsonObject()
                    .put("success", false)
                    .put("message", "Failed to fetch registrations: " + error.getMessage());
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(500)
                    .end(response.encode());
            });
    }

    private void deleteRegistration(RoutingContext context) {
        Integer registrationId = Integer.parseInt(context.pathParam("id"));
        
        registrationRepository.deleteRegistration(registrationId)
            .onSuccess(deleted -> {
                JsonObject response = new JsonObject()
                    .put("success", true)
                    .put("message", deleted ? "Registration deleted successfully" : "Registration not found");
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .end(response.encode());
            })
            .onFailure(error -> {
                JsonObject response = new JsonObject()
                    .put("success", false)
                    .put("message", "Failed to delete registration: " + error.getMessage());
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(500)
                    .end(response.encode());
            });
    }
} 