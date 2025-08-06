package com.exhibition.service;

import com.exhibition.model.Exhibitor;
import com.exhibition.repository.ExhibitorRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public class ExhibitorServiceImpl implements ExhibitorService {

    private final ExhibitorRepository exhibitorRepository;

    public ExhibitorServiceImpl(ExhibitorRepository exhibitorRepository) {
        this.exhibitorRepository = exhibitorRepository;
    }

    @Override
    public void registerExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Integer>> resultHandler) {
        // Add business logic here if needed (e.g., validation)
        exhibitorRepository.addExhibitor(exhibitor, resultHandler);
    }

    @Override
    public void getExhibitor(int id, Handler<AsyncResult<Exhibitor>> resultHandler) {
        exhibitorRepository.getExhibitorById(id, resultHandler);
    }

    @Override
    public void listExhibitors(Handler<AsyncResult<List<Exhibitor>>> resultHandler) {
        exhibitorRepository.getAllExhibitors(resultHandler);
    }

    @Override
    public void updateExhibitor(Exhibitor exhibitor, Handler<AsyncResult<Void>> resultHandler) {
        exhibitorRepository.updateExhibitor(exhibitor, resultHandler);
    }

    @Override
    public void removeExhibitor(int id, Handler<AsyncResult<Void>> resultHandler) {
        exhibitorRepository.deleteExhibitor(id, resultHandler);
    }
    
    @Override
    public void updateExhibitorPassword(int exhibitorId, String password, Handler<AsyncResult<Void>> resultHandler) {
        exhibitorRepository.updateExhibitorPassword(exhibitorId, password, resultHandler);
    }
    
    @Override
    public void getExhibitorByEmail(String email, Handler<AsyncResult<Exhibitor>> resultHandler) {
        exhibitorRepository.getExhibitorByEmail(email, resultHandler);
    }

    @Override
    public void checkEmailExists(String email, Handler<AsyncResult<Boolean>> resultHandler) {
        exhibitorRepository.checkEmailExists(email, resultHandler);
    }
}