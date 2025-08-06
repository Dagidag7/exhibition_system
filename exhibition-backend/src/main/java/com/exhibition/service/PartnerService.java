package com.exhibition.service;

import com.exhibition.model.Partner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface PartnerService {
    void registerPartner(Partner partner, Handler<AsyncResult<Void>> resultHandler);
    void getPartner(int id, Handler<AsyncResult<Partner>> resultHandler);
    void listPartners(Handler<AsyncResult<List<Partner>>> resultHandler);
    void updatePartner(Partner partner, Handler<AsyncResult<Void>> resultHandler);
    void removePartner(int id, Handler<AsyncResult<Void>> resultHandler);
}