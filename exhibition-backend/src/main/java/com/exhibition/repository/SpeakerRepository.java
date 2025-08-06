package com.exhibition.repository;

import com.exhibition.model.Speaker;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface SpeakerRepository {
    void addSpeaker(Speaker speaker, Handler<AsyncResult<Integer>> resultHandler);
    void getAllSpeakers(Handler<AsyncResult<List<Speaker>>> resultHandler);
    void getSpeaker(int id, Handler<AsyncResult<Speaker>> resultHandler);
    void updateSpeaker(Speaker speaker, Handler<AsyncResult<Void>> resultHandler);
    void deleteSpeaker(int id, Handler<AsyncResult<Void>> resultHandler);
}