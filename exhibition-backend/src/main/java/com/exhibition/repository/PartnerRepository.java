package com.exhibition.repository;

import com.exhibition.model.Partner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface PartnerRepository {
    void addPartner(Partner partner, Handler<AsyncResult<Void>> resultHandler);
    void getPartnerById(int id, Handler<AsyncResult<Partner>> resultHandler);
    void getAllPartners(Handler<AsyncResult<List<Partner>>> resultHandler);
    void updatePartner(Partner partner, Handler<AsyncResult<Void>> resultHandler);
    void deletePartner(int id, Handler<AsyncResult<Void>> resultHandler);
}