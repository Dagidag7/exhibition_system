package com.exhibition.repository;

import com.exhibition.model.Exhibitor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface ExhibitorRepository {
    void addExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Integer>> resultHandler);
    void getExhibitorById(int id, Handler<AsyncResult<Exhibitor>> resultHandler);
    void getAllExhibitors(Handler<AsyncResult<List<Exhibitor>>> resultHandler);
    void updateExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Void>> resultHandler);
    void deleteExhibitor(int id, Handler<AsyncResult<Void>> resultHandler);
    void updateExhibitorPassword(int exhibitorId, String password, Handler<AsyncResult<Void>> resultHandler);
    void getExhibitorByEmail(String email, Handler<AsyncResult<Exhibitor>> resultHandler);
    void checkEmailExists(String email, Handler<AsyncResult<Boolean>> resultHandler);
}