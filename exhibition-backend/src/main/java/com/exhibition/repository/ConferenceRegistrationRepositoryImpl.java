package com.exhibition.repository;

import com.exhibition.model.ConferenceRegistration;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import java.util.ArrayList;
import java.util.List;

public class ConferenceRegistrationRepositoryImpl implements ConferenceRegistrationRepository {
    private final SQLClient sqlClient;

    public ConferenceRegistrationRepositoryImpl(SQLClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    @Override
    public Future<ConferenceRegistration> addRegistration(ConferenceRegistration registration) {
        Promise<ConferenceRegistration> promise = Promise.promise();
        
        String sql = "INSERT INTO conference_registrations (conference_id, attendee_id, first_name, last_name, " +
                    "email, phone, company, job_title, dietary_restrictions, special_requirements, " +
                    "registration_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        sqlClient.updateWithParams(sql, 
            new io.vertx.core.json.JsonArray()
                .add(registration.getConferenceId())
                .add(registration.getAttendeeId())
                .add(registration.getFirstName())
                .add(registration.getLastName())
                .add(registration.getEmail())
                .add(registration.getPhone())
                .add(registration.getCompany())
                .add(registration.getJobTitle())
                .add(registration.getDietaryRestrictions())
                .add(registration.getSpecialRequirements())
                .add(registration.getRegistrationDate())
                .add(registration.getStatus()),
            ar -> {
                if (ar.succeeded()) {
                    registration.setRegistrationId(ar.result().getKeys().getInteger(0));
                    promise.complete(registration);
                } else {
                    promise.fail(ar.cause());
                }
            });
        
        return promise.future();
    }

    @Override
    public Future<List<ConferenceRegistration>> getRegistrationsByConferenceId(Integer conferenceId) {
        Promise<List<ConferenceRegistration>> promise = Promise.promise();
        
        String sql = "SELECT * FROM conference_registrations WHERE conference_id = ?";
        
        sqlClient.queryWithParams(sql, new io.vertx.core.json.JsonArray().add(conferenceId), ar -> {
            if (ar.succeeded()) {
                List<ConferenceRegistration> registrations = new ArrayList<>();
                for (io.vertx.core.json.JsonObject row : ar.result().getRows()) {
                    registrations.add(mapRowToRegistration(row));
                }
                promise.complete(registrations);
            } else {
                promise.fail(ar.cause());
            }
        });
        
        return promise.future();
    }

    @Override
    public Future<List<ConferenceRegistration>> getRegistrationsByEmail(String email) {
        Promise<List<ConferenceRegistration>> promise = Promise.promise();
        
        String sql = "SELECT * FROM conference_registrations WHERE email = ?";
        
        sqlClient.queryWithParams(sql, new io.vertx.core.json.JsonArray().add(email), ar -> {
            if (ar.succeeded()) {
                List<ConferenceRegistration> registrations = new ArrayList<>();
                for (io.vertx.core.json.JsonObject row : ar.result().getRows()) {
                    registrations.add(mapRowToRegistration(row));
                }
                promise.complete(registrations);
            } else {
                promise.fail(ar.cause());
            }
        });
        
        return promise.future();
    }

    @Override
    public Future<ConferenceRegistration> getRegistrationById(Integer registrationId) {
        Promise<ConferenceRegistration> promise = Promise.promise();
        
        String sql = "SELECT * FROM conference_registrations WHERE registration_id = ?";
        
        sqlClient.queryWithParams(sql, new io.vertx.core.json.JsonArray().add(registrationId), ar -> {
            if (ar.succeeded() && ar.result().getNumRows() > 0) {
                promise.complete(mapRowToRegistration(ar.result().getRows().get(0)));
            } else {
                promise.fail("Registration not found");
            }
        });
        
        return promise.future();
    }

    @Override
    public Future<Boolean> deleteRegistration(Integer registrationId) {
        Promise<Boolean> promise = Promise.promise();
        
        String sql = "DELETE FROM conference_registrations WHERE registration_id = ?";
        
        sqlClient.updateWithParams(sql, new io.vertx.core.json.JsonArray().add(registrationId), ar -> {
            if (ar.succeeded()) {
                promise.complete(ar.result().getUpdated() > 0);
            } else {
                promise.fail(ar.cause());
            }
        });
        
        return promise.future();
    }

    @Override
    public Future<List<ConferenceRegistration>> getAllRegistrations() {
        Promise<List<ConferenceRegistration>> promise = Promise.promise();
        
        String sql = "SELECT * FROM conference_registrations ORDER BY registration_date DESC";
        
        sqlClient.query(sql, ar -> {
            if (ar.succeeded()) {
                List<ConferenceRegistration> registrations = new ArrayList<>();
                for (io.vertx.core.json.JsonObject row : ar.result().getRows()) {
                    registrations.add(mapRowToRegistration(row));
                }
                promise.complete(registrations);
            } else {
                promise.fail(ar.cause());
            }
        });
        
        return promise.future();
    }

    private ConferenceRegistration mapRowToRegistration(io.vertx.core.json.JsonObject row) {
        ConferenceRegistration registration = new ConferenceRegistration();
        registration.setRegistrationId(row.getInteger("registration_id"));
        registration.setConferenceId(row.getInteger("conference_id"));
        registration.setAttendeeId(row.getInteger("attendee_id"));
        registration.setFirstName(row.getString("first_name"));
        registration.setLastName(row.getString("last_name"));
        registration.setEmail(row.getString("email"));
        registration.setPhone(row.getString("phone"));
        registration.setCompany(row.getString("company"));
        registration.setJobTitle(row.getString("job_title"));
        registration.setDietaryRestrictions(row.getString("dietary_restrictions"));
        registration.setSpecialRequirements(row.getString("special_requirements"));
        registration.setRegistrationDate(row.getString("registration_date"));
        registration.setStatus(row.getString("status"));
        return registration;
    }
} 