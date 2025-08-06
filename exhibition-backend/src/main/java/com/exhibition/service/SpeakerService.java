package com.exhibition.service;

import com.exhibition.model.Speaker;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface SpeakerService {
    void registerSpeaker(Speaker speaker, Handler<AsyncResult<Integer>> resultHandler);
    void listSpeakers(Handler<AsyncResult<List<Speaker>>> resultHandler);
    void getSpeaker(int id, Handler<AsyncResult<Speaker>> resultHandler);
    void updateSpeaker(Speaker speaker, Handler<AsyncResult<Void>> resultHandler);
    void removeSpeaker(int id, Handler<AsyncResult<Void>> resultHandler);
}