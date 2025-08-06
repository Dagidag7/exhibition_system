package com.exhibition.repository;

import com.exhibition.model.Attendee;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import java.util.List;

public interface AttendeeRepository {
    void addAttendee(Attendee attendee, Handler<AsyncResult<Void>> resultHandler);
    void getAttendeeById(int id, Handler<AsyncResult<Attendee>> resultHandler);
    void getAllAttendees(Handler<AsyncResult<List<Attendee>>> resultHandler);
    void updateAttendee(Attendee attendee, Handler<AsyncResult<Void>> resultHandler);
    void deleteAttendee(int id, Handler<AsyncResult<Void>> resultHandler);
    void getAttendeeByEmail(String email, Handler<AsyncResult<Attendee>> resultHandler);
    void checkEmailExists(String email, Handler<AsyncResult<Boolean>> resultHandler);
}