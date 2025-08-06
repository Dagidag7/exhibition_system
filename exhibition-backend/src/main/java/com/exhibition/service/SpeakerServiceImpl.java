package com.exhibition.service;

import com.exhibition.model.Speaker;
import com.exhibition.repository.SpeakerRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public class SpeakerServiceImpl implements SpeakerService {
    private final SpeakerRepository speakerRepository;

    public SpeakerServiceImpl(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    @Override
    public void registerSpeaker(Speaker speaker, Handler<AsyncResult<Integer>> resultHandler) {
        speakerRepository.addSpeaker(speaker, resultHandler);
    }

    @Override
    public void listSpeakers(Handler<AsyncResult<List<Speaker>>> resultHandler) {
        speakerRepository.getAllSpeakers(resultHandler);
    }

    @Override
    public void getSpeaker(int id, Handler<AsyncResult<Speaker>> resultHandler) {
        speakerRepository.getSpeaker(id, resultHandler);
    }

    @Override
    public void updateSpeaker(Speaker speaker, Handler<AsyncResult<Void>> resultHandler) {
        speakerRepository.updateSpeaker(speaker, resultHandler);
    }

    @Override
    public void removeSpeaker(int id, Handler<AsyncResult<Void>> resultHandler) {
        speakerRepository.deleteSpeaker(id, resultHandler);
    }
}