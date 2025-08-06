package com.exhibition.controller;

import com.exhibition.model.Floor;
import com.exhibition.service.FloorService;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class FloorController {

    public static void registerRoutes(Router router, FloorService floorService) {
        router.post("/floors").handler(ctx -> createFloor(ctx, floorService));
        router.get("/floors").handler(ctx -> getAllFloors(ctx, floorService));
        router.get("/floors/:id").handler(ctx -> getFloorById(ctx, floorService));
        router.put("/floors/:id").handler(ctx -> updateFloor(ctx, floorService));
        router.delete("/floors/:id").handler(ctx -> deleteFloor(ctx, floorService));
    }

    private static void createFloor(RoutingContext ctx, FloorService floorService) {
        String body = ctx.getBody() != null ? ctx.getBody().toString() : null;
        
        if (body == null || body.trim().isEmpty()) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Request body is required\"}");
            return;
        }
        
        try {
            Floor floor = Json.decodeValue(body, Floor.class);
            floorService.registerFloor(floor, res -> {
                if (res.succeeded()) {
                    ctx.response()
                       .setStatusCode(201)
                       .putHeader("content-type", "application/json")
                       .end("{\"message\": \"Floor created successfully\"}");
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

    private static void getAllFloors(RoutingContext ctx, FloorService floorService) {
        floorService.listFloors(res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(500).end(res.cause().getMessage());
            }
        });
    }

    private static void getFloorById(RoutingContext ctx, FloorService floorService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        floorService.getFloor(id, res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(res.result()));
            } else {
                ctx.response().setStatusCode(404).end("Floor not found");
            }
        });
    }

    private static void updateFloor(RoutingContext ctx, FloorService floorService) {
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
            Floor floor = Json.decodeValue(body, Floor.class);
            floor.setFloorId(id);
            floorService.updateFloor(floor, res -> {
                if (res.succeeded()) {
                    ctx.response()
                       .putHeader("content-type", "application/json")
                       .end("{\"message\": \"Floor updated successfully\"}");
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

    private static void deleteFloor(RoutingContext ctx, FloorService floorService) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        floorService.removeFloor(id, res -> {
            if (res.succeeded()) {
                ctx.response()
                   .putHeader("content-type", "application/json")
                   .end("{\"message\": \"Floor deleted successfully\"}");
            } else {
                ctx.response()
                   .setStatusCode(500)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"" + res.cause().getMessage() + "\"}");
            }
        });
    }
}