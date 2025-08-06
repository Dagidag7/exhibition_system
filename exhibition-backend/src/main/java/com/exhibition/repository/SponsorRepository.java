package com.exhibition.repository;

import com.exhibition.model.Sponsor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface SponsorRepository {
    void addSponsor(Sponsor sponsor, Handler<AsyncResult<Void>> resultHandler);
    void getSponsorById(int id, Handler<AsyncResult<Sponsor>> resultHandler);
    void getAllSponsors(Handler<AsyncResult<List<Sponsor>>> resultHandler);
    void updateSponsor(Sponsor sponsor, Handler<AsyncResult<Void>> resultHandler);
    void deleteSponsor(int id, Handler<AsyncResult<Void>> resultHandler);
}