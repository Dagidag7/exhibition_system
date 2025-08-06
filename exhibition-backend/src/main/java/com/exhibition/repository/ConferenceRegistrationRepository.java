package com.exhibition.repository;

import com.exhibition.model.ConferenceRegistration;
import io.vertx.core.Future;
import java.util.List;

public interface ConferenceRegistrationRepository {
    Future<ConferenceRegistration> addRegistration(ConferenceRegistration registration);
    Future<List<ConferenceRegistration>> getRegistrationsByConferenceId(Integer conferenceId);
    Future<List<ConferenceRegistration>> getRegistrationsByEmail(String email);
    Future<ConferenceRegistration> getRegistrationById(Integer registrationId);
    Future<Boolean> deleteRegistration(Integer registrationId);
    Future<List<ConferenceRegistration>> getAllRegistrations();
} 