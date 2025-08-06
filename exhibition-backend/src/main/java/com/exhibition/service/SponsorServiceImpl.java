package com.exhibition.service;

import com.exhibition.model.Sponsor;
import com.exhibition.repository.SponsorRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public class SponsorServiceImpl implements SponsorService {

    private final SponsorRepository sponsorRepository;

    public SponsorServiceImpl(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
    }

    @Override
    public void registerSponsor(Sponsor sponsor, Handler<AsyncResult<Void>> resultHandler) {
        // Add business logic here if needed (e.g., validation)
        sponsorRepository.addSponsor(sponsor, resultHandler);
    }

    @Override
    public void getSponsor(int id, Handler<AsyncResult<Sponsor>> resultHandler) {
        sponsorRepository.getSponsorById(id, resultHandler);
    }

    @Override
    public void listSponsors(Handler<AsyncResult<List<Sponsor>>> resultHandler) {
        sponsorRepository.getAllSponsors(resultHandler);
    }

    @Override
    public void updateSponsor(Sponsor sponsor, Handler<AsyncResult<Void>> resultHandler) {
        sponsorRepository.updateSponsor(sponsor, resultHandler);
    }

    @Override
    public void removeSponsor(int id, Handler<AsyncResult<Void>> resultHandler) {
        sponsorRepository.deleteSponsor(id, resultHandler);
    }
}