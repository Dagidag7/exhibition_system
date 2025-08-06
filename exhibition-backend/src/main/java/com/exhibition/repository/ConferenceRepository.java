package com.exhibition.repository;

import com.exhibition.model.Conference;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface ConferenceRepository {
    void addConference(Conference conference, Handler<AsyncResult<Integer>> resultHandler);
    void getAllConferences(Handler<AsyncResult<List<Conference>>> resultHandler);
    void getConference(int id, Handler<AsyncResult<Conference>> resultHandler);
    void updateConference(Conference conference, Handler<AsyncResult<Void>> resultHandler);
    void deleteConference(int id, Handler<AsyncResult<Void>> resultHandler);
}