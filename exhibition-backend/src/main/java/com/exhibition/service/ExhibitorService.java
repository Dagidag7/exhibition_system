package com.exhibition.service;

import com.exhibition.model.Exhibitor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface ExhibitorService {
    void registerExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Integer>> resultHandler);
    void getExhibitor(int id, Handler<AsyncResult<Exhibitor>> resultHandler);
    void listExhibitors(Handler<AsyncResult<List<Exhibitor>>> resultHandler);
    void updateExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Void>> resultHandler);
    void removeExhibitor(int id, Handler<AsyncResult<Void>> resultHandler);
    void updateExhibitorPassword(int exhibitorId, String password, Handler<AsyncResult<Void>> resultHandler);
    void getExhibitorByEmail(String email, Handler<AsyncResult<Exhibitor>> resultHandler);
    void checkEmailExists(String email, Handler<AsyncResult<Boolean>> resultHandler);
}