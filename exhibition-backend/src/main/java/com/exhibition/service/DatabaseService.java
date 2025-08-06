package com.exhibition.service;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private final SQLClient sqlClient;

    public DatabaseService(SQLClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    // Get database health status
    public Future<JsonObject> getDatabaseHealth() {
        Promise<JsonObject> promise = Promise.promise();
        sqlClient.query("SELECT version() as db_version, current_database() as db_name", ar -> {
            if (ar.succeeded()) {
                ResultSet resultSet = ar.result();
                JsonObject health = new JsonObject();
                if (resultSet.getNumRows() > 0) {
                    JsonObject row = resultSet.getRows().get(0);
                    health.put("status", "healthy")
                           .put("database", row.getString("db_name"))
                           .put("version", row.getString("db_version"))
                           .put("timestamp", System.currentTimeMillis());
                }
                promise.complete(health);
            } else {
                JsonObject error = new JsonObject()
                        .put("status", "error")
                        .put("message", ar.cause().getMessage())
                        .put("timestamp", System.currentTimeMillis());
                promise.complete(error);
            }
        });
        return promise.future();
    }

    // Get table schema information
    public Future<JsonObject> getTableSchema(String tableName) {
        String sql = """
            SELECT 
                column_name,
                data_type,
                is_nullable,
                column_default,
                character_maximum_length
            FROM information_schema.columns 
            WHERE table_name = ? 
            ORDER BY ordinal_position
            """;
        
        Promise<JsonObject> promise = Promise.promise();
        sqlClient.queryWithParams(sql, new JsonArray().add(tableName), ar -> {
            if (ar.succeeded()) {
                ResultSet resultSet = ar.result();
                JsonObject schema = new JsonObject();
                schema.put("tableName", tableName);
                
                JsonArray columns = new JsonArray();
                for (JsonObject row : resultSet.getRows()) {
                    JsonObject column = new JsonObject()
                            .put("name", row.getString("column_name"))
                            .put("type", row.getString("data_type"))
                            .put("nullable", "YES".equals(row.getString("is_nullable")))
                            .put("default", row.getString("column_default"))
                            .put("maxLength", row.getInteger("character_maximum_length"));
                    columns.add(column);
                }
                schema.put("columns", columns);
                promise.complete(schema);
            } else {
                JsonObject error = new JsonObject()
                        .put("error", true)
                        .put("message", "Failed to get schema: " + ar.cause().getMessage());
                promise.complete(error);
            }
        });
        return promise.future();
    }

    // Get all tables in the database
    public Future<JsonArray> getAllTables() {
        String sql = """
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public' 
            ORDER BY table_name
            """;
        
        Promise<JsonArray> promise = Promise.promise();
        sqlClient.query(sql, ar -> {
            if (ar.succeeded()) {
                ResultSet resultSet = ar.result();
                JsonArray tables = new JsonArray();
                for (JsonObject row : resultSet.getRows()) {
                    tables.add(row.getString("table_name"));
                }
                promise.complete(tables);
            } else {
                JsonArray error = new JsonArray();
                error.add(new JsonObject()
                        .put("error", true)
                        .put("message", "Failed to get tables: " + ar.cause().getMessage()));
                promise.complete(error);
            }
        });
        return promise.future();
    }

    // Update conference table schema (add new columns)
    public Future<JsonObject> updateConferenceSchema(JsonObject updates) {
        List<String> operations = new ArrayList<>();
        JsonArray params = new JsonArray();
        
        // Check if we need to add location column
        if (updates.getBoolean("addLocation", false)) {
            operations.add("ADD COLUMN location VARCHAR(255)");
        }
        
        // Check if we need to add time column
        if (updates.getBoolean("addTime", false)) {
            operations.add("ADD COLUMN time VARCHAR(50)");
        }
        
        if (operations.isEmpty()) {
            return Future.succeededFuture(new JsonObject()
                    .put("success", false)
                    .put("message", "No schema updates specified"));
        }
        
        String sql = "ALTER TABLE conference " + String.join(", ", operations);
        
        Promise<JsonObject> promise = Promise.promise();
        sqlClient.update(sql, ar -> {
            if (ar.succeeded()) {
                JsonObject response = new JsonObject()
                        .put("success", true)
                        .put("message", "Conference table schema updated successfully")
                        .put("operations", operations)
                        .put("timestamp", System.currentTimeMillis());
                promise.complete(response);
            } else {
                JsonObject error = new JsonObject()
                        .put("success", false)
                        .put("message", "Failed to update schema: " + ar.cause().getMessage())
                        .put("timestamp", System.currentTimeMillis());
                promise.complete(error);
            }
        });
        return promise.future();
    }

    // Add sample conference data
    public Future<JsonObject> addSampleConferences() {
        String sql = """
            INSERT INTO conference (title, description, date, time, location, speaker) 
            VALUES 
                ('Future of Technology', 'Exploring emerging technologies and their impact', '2024-02-15', '09:00 AM', 'Main Hall A', 'Dr. Sarah Johnson'),
                ('Digital Marketing Trends', 'Latest trends in digital marketing and social media', '2024-02-15', '02:00 PM', 'Conference Room B', 'Mike Chen'),
                ('AI in Business', 'How artificial intelligence is transforming business operations', '2024-02-16', '10:30 AM', 'Main Hall A', 'Prof. David Wilson'),
                ('Sustainable Development', 'Green technologies and sustainable business practices', '2024-02-16', '03:30 PM', 'Conference Room C', 'Lisa Rodriguez'),
                ('Cybersecurity Best Practices', 'Protecting your business in the digital age', '2024-02-17', '11:00 AM', 'Main Hall B', 'Alex Thompson')
            RETURNING conference_id
            """;
        
        Promise<JsonObject> promise = Promise.promise();
        sqlClient.update(sql, ar -> {
            if (ar.succeeded()) {
                int count = ar.result().getUpdated();
                JsonObject response = new JsonObject()
                        .put("success", true)
                        .put("message", "Added " + count + " sample conferences successfully")
                        .put("count", count)
                        .put("timestamp", System.currentTimeMillis());
                promise.complete(response);
            } else {
                JsonObject error = new JsonObject()
                        .put("success", false)
                        .put("message", "Failed to add sample data: " + ar.cause().getMessage())
                        .put("timestamp", System.currentTimeMillis());
                promise.complete(error);
            }
        });
        return promise.future();
    }

    // Get database statistics
    public Future<JsonObject> getDatabaseStats() {
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM attendee) as attendee_count,
                (SELECT COUNT(*) FROM exhibitor) as exhibitor_count,
                (SELECT COUNT(*) FROM sponsor) as sponsor_count,
                (SELECT COUNT(*) FROM partner) as partner_count,
                (SELECT COUNT(*) FROM floor) as floor_count,
                (SELECT COUNT(*) FROM product) as product_count,
                (SELECT COUNT(*) FROM speaker) as speaker_count,
                (SELECT COUNT(*) FROM conference) as conference_count
            """;
        
        Promise<JsonObject> promise = Promise.promise();
        sqlClient.query(sql, ar -> {
            if (ar.succeeded()) {
                ResultSet resultSet = ar.result();
                JsonObject stats = new JsonObject();
                if (resultSet.getNumRows() > 0) {
                    JsonObject row = resultSet.getRows().get(0);
                    stats.put("attendeeCount", row.getInteger("attendee_count"))
                           .put("exhibitorCount", row.getInteger("exhibitor_count"))
                           .put("sponsorCount", row.getInteger("sponsor_count"))
                           .put("partnerCount", row.getInteger("partner_count"))
                           .put("floorCount", row.getInteger("floor_count"))
                           .put("productCount", row.getInteger("product_count"))
                           .put("speakerCount", row.getInteger("speaker_count"))
                           .put("conferenceCount", row.getInteger("conference_count"))
                           .put("timestamp", System.currentTimeMillis());
                }
                promise.complete(stats);
            } else {
                JsonObject error = new JsonObject()
                        .put("error", true)
                        .put("message", "Failed to get database stats: " + ar.cause().getMessage());
                promise.complete(error);
            }
        });
        return promise.future();
    }
} 