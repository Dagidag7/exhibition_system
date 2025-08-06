package com.exhibition.repository;

import com.exhibition.model.Attendee;
import com.exhibition.MainVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.ResultSet;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AttendeeRepositoryImpl implements AttendeeRepository {

    private final SQLClient jdbc = MainVerticle.jdbc;

@Override
public void addAttendee(Attendee attendee, Handler<AsyncResult<Void>> resultHandler) {
    String sql = "INSERT INTO attendee (name, email, phone, password, registration_date, session_ids) VALUES (?, ?, ?, ?, CURRENT_DATE, ?)";
    JsonArray params = new JsonArray()
            .add(attendee.getName())
            .add(attendee.getEmail())
            .add(attendee.getPhone())
            .add(attendee.getPassword())
            .add(attendee.getSessionIds()); // Use the session IDs from the object

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
    public void getAttendeeById(int id, Handler<AsyncResult<Attendee>> resultHandler) {
        String sql = "SELECT * FROM attendee WHERE attendee_id = ?";
        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.queryWithParams(sql, new JsonArray().add(id), ar -> {
                    connection.close();
                    if (ar.succeeded() && !ar.result().getRows().isEmpty()) {
                        for (JsonObject row : ar.result().getRows()) {
                        Attendee attendee = new Attendee();
                        attendee.setAttendeeId(row.getInteger("attendee_id"));
                        attendee.setName(row.getString("name"));
                        attendee.setEmail(row.getString("email"));
                        attendee.setPhone(row.getString("phone"));
                        attendee.setPassword(row.getString("password"));
                        attendee.setSessionIds(row.getString("session_ids"));
                        resultHandler.handle(io.vertx.core.Future.succeededFuture(attendee));
                        }
                    } else {
                        resultHandler.handle(io.vertx.core.Future.failedFuture("Not found"));
                    }
                });
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void getAllAttendees(Handler<AsyncResult<List<Attendee>>> resultHandler) {
        String sql = "SELECT * FROM attendee";
        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.query(sql, ar -> {
                    connection.close();
                    if (ar.succeeded()) {
                        List<Attendee> attendees = new ArrayList<>();
                        for (JsonObject row : ar.result().getRows()) {
                            Attendee attendee = new Attendee();
                            attendee.setAttendeeId(row.getInteger("attendee_id"));
                            attendee.setName(row.getString("name"));
                            attendee.setEmail(row.getString("email"));
                            attendee.setPhone(row.getString("phone"));
                            attendee.setPassword(row.getString("password"));
                            attendee.setSessionIds(row.getString("session_ids"));
                            attendees.add(attendee);
                        }
                        resultHandler.handle(io.vertx.core.Future.succeededFuture(attendees));
                    } else {
                        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
                    }
                });
            } else {
                resultHandler.handle(io.vertx.core.Future.failedFuture(res.cause()));
            }
        });
    }

  // ... existing code ...
    @Override
    public void updateAttendee(Attendee attendee, Handler<AsyncResult<Void>> resultHandler) {
            String sql = "UPDATE attendee SET name=?, email=?, phone=?, password=?, registration_date=?, session_ids=? WHERE attendee_id=?";
        JsonArray params = new JsonArray()
                .add(attendee.getName())
                .add(attendee.getEmail())
                .add(attendee.getPhone())
                .add(attendee.getPassword())
                .add(attendee.getRegistrationDate())
                .add(attendee.getSessionIds())
                .add(attendee.getAttendeeId());

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
    public void deleteAttendee(int id, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "DELETE FROM attendee WHERE attendee_id = ?";
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

// ... existing code ...
