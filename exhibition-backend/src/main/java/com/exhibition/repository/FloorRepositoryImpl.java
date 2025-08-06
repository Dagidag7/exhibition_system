package com.exhibition.repository;

import com.exhibition.model.Floor;
import com.exhibition.MainVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FloorRepositoryImpl implements FloorRepository {

    private final SQLClient jdbc = MainVerticle.jdbc;

    @Override
    public void addFloor(Floor floor, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "INSERT INTO floor (floor_number, layout_image, exhibitor_ids, conference_ids) VALUES (?, ?, ?, ?)";
        JsonArray params = new JsonArray()
                .add(floor.getFloorNumber())
                .add(floor.getLayoutImage())
                .add(floor.getExhibitorIds())
                .add(floor.getConferenceIds());

        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.updateWithParams(sql, params, ar -> {
                    connection.close();
                    if (ar.succeeded()) {
                        resultHandler.handle(io.vertx.core.Future.succeededFuture());
                    } else {
                        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
                    }
                });
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void getFloorById(int id, Handler<AsyncResult<Floor>> resultHandler) {
        String sql = "SELECT * FROM floor WHERE floor_id = ?";
        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.queryWithParams(sql, new JsonArray().add(id), ar -> {
                    connection.close();
                    if (ar.succeeded() && !ar.result().getRows().isEmpty()) {
                        JsonObject row = ar.result().getRows().get(0);
                        Floor floor = new Floor();
                        floor.setFloorId(row.getInteger("floor_id"));
                        floor.setFloorNumber(row.getInteger("floor_number"));
                        floor.setLayoutImage(row.getString("layout_image"));
                        floor.setExhibitorIds(row.getString("exhibitor_ids"));
                        floor.setConferenceIds(row.getString("conference_ids"));
                        resultHandler.handle(io.vertx.core.Future.succeededFuture(floor));
                    } else {
                        resultHandler.handle(io.vertx.core.Future.failedFuture("Floor not found"));
                    }
                });
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void getAllFloors(Handler<AsyncResult<List<Floor>>> resultHandler) {
        String sql = "SELECT * FROM floor ORDER BY floor_number";
        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.query(sql, ar -> {
                    connection.close();
                    if (ar.succeeded()) {
                        List<Floor> floors = new ArrayList<>();
                        for (JsonObject row : ar.result().getRows()) {
                            Floor floor = new Floor();
                            floor.setFloorId(row.getInteger("floor_id"));
                            floor.setFloorNumber(row.getInteger("floor_number"));
                            floor.setLayoutImage(row.getString("layout_image"));
                            floor.setExhibitorIds(row.getString("exhibitor_ids"));
                            floor.setConferenceIds(row.getString("conference_ids"));
                            floors.add(floor);
                        }
                        resultHandler.handle(io.vertx.core.Future.succeededFuture(floors));
                    } else {
                        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
                    }
                });
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void updateFloor(Floor floor, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "UPDATE floor SET floor_number=?, layout_image=?, exhibitor_ids=?, conference_ids=? WHERE floor_id=?";
        JsonArray params = new JsonArray()
                .add(floor.getFloorNumber())
                .add(floor.getLayoutImage())
                .add(floor.getExhibitorIds())
                .add(floor.getConferenceIds())
                .add(floor.getFloorId());

        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.updateWithParams(sql, params, ar -> {
                    connection.close();
                    if (ar.succeeded()) {
                        resultHandler.handle(io.vertx.core.Future.succeededFuture());
                    } else {
                        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
                    }
                });
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void deleteFloor(int id, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "DELETE FROM floor WHERE floor_id = ?";
        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.updateWithParams(sql, new JsonArray().add(id), ar -> {
                    connection.close();
                    if (ar.succeeded()) {
                        resultHandler.handle(io.vertx.core.Future.succeededFuture());
                    } else {
                        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
                    }
                });
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(res.cause()));
            }
        });
    }
}