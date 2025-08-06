package com.exhibition.service;

import com.exhibition.model.Sponsor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface SponsorService {
    void registerSponsor(Sponsor sponsor, Handler<AsyncResult<Void>> resultHandler);
    void getSponsor(int id, Handler<AsyncResult<Sponsor>> resultHandler);
    void listSponsors(Handler<AsyncResult<List<Sponsor>>> resultHandler);
    void updateSponsor(Sponsor sponsor, Handler<AsyncResult<Void>> resultHandler);
    void removeSponsor(int id, Handler<AsyncResult<Void>> resultHandler);
}