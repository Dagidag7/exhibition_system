package com.exhibition.service;

import com.exhibition.model.Conference;
import com.exhibition.repository.ConferenceRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public class ConferenceServiceImpl implements ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceServiceImpl(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    @Override
    public void registerConference(Conference conference, Handler<AsyncResult<Integer>> resultHandler) {
        conferenceRepository.addConference(conference, resultHandler);
    }

    @Override
    public void listConferences(Handler<AsyncResult<List<Conference>>> resultHandler) {
        conferenceRepository.getAllConferences(resultHandler);
    }

    @Override
    public void getConference(int id, Handler<AsyncResult<Conference>> resultHandler) {
        conferenceRepository.getConference(id, resultHandler);
    }

    @Override
    public void updateConference(Conference conference, Handler<AsyncResult<Void>> resultHandler) {
        conferenceRepository.updateConference(conference, resultHandler);
    }

    @Override
    public void removeConference(int id, Handler<AsyncResult<Void>> resultHandler) {
        conferenceRepository.deleteConference(id, resultHandler);
    }
}