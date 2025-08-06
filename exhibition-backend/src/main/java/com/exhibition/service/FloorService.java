package com.exhibition.service;

import com.exhibition.model.Floor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class FloorService {
    private final SQLClient jdbcClient;

    public FloorService(SQLClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void registerFloor(Floor floor, Handler<AsyncResult<Void>> resultHandler) {
        jdbcClient.getConnection(conn -> {
            if (conn.failed()) {
                resultHandler.handle(Future.failedFuture(conn.cause()));
                return;
            }

            SQLConnection connection = conn.result();
            String sql = "INSERT INTO floor (floor_number, layout_image, exhibitor_ids, conference_ids) VALUES (?, ?, ?, ?)";
            
            JsonArray params = new JsonArray()
                .add(floor.getFloorNumber())
                .add(floor.getLayoutImage())
                .add(floor.getExhibitorIds())
                .add(floor.getConferenceIds());

            connection.updateWithParams(sql, params, res -> {
                connection.close();
                if (res.succeeded()) {
                    resultHandler.handle(Future.succeededFuture());
                } else {
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
            });
        });
    }

    public void listFloors(Handler<AsyncResult<List<Floor>>> resultHandler) {
        jdbcClient.getConnection(conn -> {
            if (conn.failed()) {
                resultHandler.handle(Future.failedFuture(conn.cause()));
                return;
            }

            SQLConnection connection = conn.result();
            String sql = "SELECT * FROM floor ORDER BY floor_number";

            connection.query(sql, res -> {
                connection.close();
                if (res.succeeded()) {
                    ResultSet resultSet = res.result();
                    List<Floor> floors = new ArrayList<>();
                    
                    for (JsonObject row : resultSet.getRows()) {
                        Floor floor = new Floor();
                        floor.setFloorId(row.getInteger("floor_id"));
                        floor.setFloorNumber(row.getInteger("floor_number"));
                        floor.setLayoutImage(row.getString("layout_image"));
                        floor.setExhibitorIds(row.getString("exhibitor_ids"));
                        floor.setConferenceIds(row.getString("conference_ids"));
                        floors.add(floor);
                    }
                    
                    resultHandler.handle(Future.succeededFuture(floors));
                } else {
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
            });
        });
    }

    public void getFloor(int floorId, Handler<AsyncResult<Floor>> resultHandler) {
        jdbcClient.getConnection(conn -> {
            if (conn.failed()) {
                resultHandler.handle(Future.failedFuture(conn.cause()));
                return;
            }

            SQLConnection connection = conn.result();
            String sql = "SELECT * FROM floor WHERE floor_id = ?";
            JsonArray params = new JsonArray().add(floorId);

            connection.queryWithParams(sql, params, res -> {
                connection.close();
                if (res.succeeded()) {
                    ResultSet resultSet = res.result();
                    if (resultSet.getNumRows() > 0) {
                        JsonObject row = resultSet.getRows().get(0);
                        Floor floor = new Floor();
                        floor.setFloorId(row.getInteger("floor_id"));
                        floor.setFloorNumber(row.getInteger("floor_number"));
                        floor.setLayoutImage(row.getString("layout_image"));
                        floor.setExhibitorIds(row.getString("exhibitor_ids"));
                        floor.setConferenceIds(row.getString("conference_ids"));
                        resultHandler.handle(Future.succeededFuture(floor));
                    } else {
                        resultHandler.handle(Future.failedFuture("Floor not found"));
                    }
                } else {
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
            });
        });
    }

    public void updateFloor(Floor floor, Handler<AsyncResult<Void>> resultHandler) {
        jdbcClient.getConnection(conn -> {
            if (conn.failed()) {
                resultHandler.handle(Future.failedFuture(conn.cause()));
                return;
            }

            SQLConnection connection = conn.result();
            String sql = "UPDATE floor SET floor_number = ?, layout_image = ?, exhibitor_ids = ?, conference_ids = ? WHERE floor_id = ?";
            
            JsonArray params = new JsonArray()
                .add(floor.getFloorNumber())
                .add(floor.getLayoutImage())
                .add(floor.getExhibitorIds())
                .add(floor.getConferenceIds())
                .add(floor.getFloorId());

            connection.updateWithParams(sql, params, res -> {
                connection.close();
                if (res.succeeded()) {
                    if (res.result().getUpdated() > 0) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        resultHandler.handle(Future.failedFuture("Floor not found"));
                    }
                } else {
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
            });
        });
    }

    public void removeFloor(int floorId, Handler<AsyncResult<Void>> resultHandler) {
        jdbcClient.getConnection(conn -> {
            if (conn.failed()) {
                resultHandler.handle(Future.failedFuture(conn.cause()));
                return;
            }

            SQLConnection connection = conn.result();
            String sql = "DELETE FROM floor WHERE floor_id = ?";
            JsonArray params = new JsonArray().add(floorId);

            connection.updateWithParams(sql, params, res -> {
                connection.close();
                if (res.succeeded()) {
                    if (res.result().getUpdated() > 0) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        resultHandler.handle(Future.failedFuture("Floor not found"));
                    }
                } else {
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
            });
        });
    }
}