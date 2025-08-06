package com.exhibition.repository;

import com.exhibition.model.Floor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public interface FloorRepository {
    void addFloor(Floor floor, Handler<AsyncResult<Void>> resultHandler);
    void getFloorById(int id, Handler<AsyncResult<Floor>> resultHandler);
    void getAllFloors(Handler<AsyncResult<List<Floor>>> resultHandler);
    void updateFloor(Floor floor, Handler<AsyncResult<Void>> resultHandler);
    void deleteFloor(int id, Handler<AsyncResult<Void>> resultHandler);
}