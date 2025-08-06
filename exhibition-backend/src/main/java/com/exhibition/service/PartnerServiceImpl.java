package com.exhibition.service;

import com.exhibition.model.Partner;
import com.exhibition.repository.PartnerRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public void registerPartner(Partner partner, Handler<AsyncResult<Void>> resultHandler) {
        partnerRepository.addPartner(partner, resultHandler);
    }

    @Override
    public void getPartner(int id, Handler<AsyncResult<Partner>> resultHandler) {
        partnerRepository.getPartnerById(id, resultHandler);
    }

    @Override
    public void listPartners(Handler<AsyncResult<List<Partner>>> resultHandler) {
        partnerRepository.getAllPartners(resultHandler);
    }

    @Override
    public void updatePartner(Partner partner, Handler<AsyncResult<Void>> resultHandler) {
        partnerRepository.updatePartner(partner, resultHandler);
    }

    @Override
    public void removePartner(int id, Handler<AsyncResult<Void>> resultHandler) {
        partnerRepository.deletePartner(id, resultHandler);
    }
}