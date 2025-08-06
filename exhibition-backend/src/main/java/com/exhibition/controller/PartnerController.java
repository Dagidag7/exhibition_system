package com.exhibition.controller;

import com.exhibition.model.Partner;
import com.exhibition.service.PartnerService;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class PartnerController {

    public static void registerRoutes(Router router, PartnerService partnerService) {
        router.post("/partners").handler(ctx -> createPartner(ctx, partnerService));
        router.get("/partners").handler(ctx -> getAllPartners(ctx, partnerService));
        router.get("/partners/:id").handler(ctx -> getPartnerById(ctx, partnerService));
        router.put("/partners/:id").handler(ctx -> updatePartner(ctx, partnerService));
        router.delete("/partners/:id").handler(ctx -> deletePartner(ctx, partnerService));
    }

    private static void createPartner(RoutingContext ctx, PartnerService partnerService) {
        String body = ctx.getBody() != null ? ctx.getBody().toString() : null;
        
        if (body == null || body.trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Request body is required\"}");
            return;
        }
        
        try {
            Partner partner = Json.decodeValue(body, Partner.class);
            partnerService.registerPartner(partner, res -> {
                if (res.succeeded()) {
                    ctx.response()
                       .setStatusCode(201)
                       .putHeader("content-type", "application/json")
                       .end("{\"message\": \"Partner created successfully\"}");
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

    private static void getAllPartners(RoutingContext ctx, PartnerService partnerService) {
        partnerService.listPartners(res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(500).end(res.cause().getMessage());
            }
        });
    }

    private static void getPartnerById(RoutingContext ctx, PartnerService partnerService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        partnerService.getPartner(id, res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(404).end("Partner not found");
            }
        });
    }

    private static void updatePartner(RoutingContext ctx, PartnerService partnerService) {
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
            Partner partner = Json.decodeValue(body, Partner.class);
            partner.setPartnerId(id);
            partnerService.updatePartner(partner, res -> {
                if (res.succeeded()) {
                    ctx.response()
                       .putHeader("content-type", "application/json")
                       .end("{\"message\": \"Partner updated successfully\"}");
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

    private static void deletePartner(RoutingContext ctx, PartnerService partnerService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        partnerService.removePartner(id, res -> {
            if (res.succeeded()) {
                ctx.response()
                   .putHeader("content-type", "application/json")
                   .end("{\"message\": \"Partner deleted successfully\"}");
            } else {
                ctx.response()
                   .setStatusCode(500)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
            }
        });
    }
}