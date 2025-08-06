package com.exhibition.service;

import com.exhibition.model.Attendee;
import com.exhibition.repository.AttendeeRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;

public class AttendeeServiceImpl implements AttendeeService {

    private final AttendeeRepository attendeeRepository;

    public AttendeeServiceImpl(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    @Override
    public void registerAttendee(Attendee attendee, Handler<AsyncResult<Void>> resultHandler) {
        // Add business logic here if needed (e.g., validation)
        attendeeRepository.addAttendee(attendee, resultHandler);
    }

    @Override
    public void getAttendee(int id, Handler<AsyncResult<Attendee>> resultHandler) {
        attendeeRepository.getAttendeeById(id, resultHandler);
    }

    @Override
    public void listAttendees(Handler<AsyncResult<List<Attendee>>> resultHandler) {
        attendeeRepository.getAllAttendees(resultHandler);
    }

    @Override
    public void updateAttendee(Attendee attendee, Handler<AsyncResult<Void>> resultHandler) {
        attendeeRepository.updateAttendee(attendee, resultHandler);
    }

    @Override
    public void removeAttendee(int id, Handler<AsyncResult<Void>> resultHandler) {
        attendeeRepository.deleteAttendee(id, resultHandler);
    }

    @Override
    public void getAttendeeByEmail(String email, Handler<AsyncResult<Attendee>> resultHandler) {
        attendeeRepository.getAttendeeByEmail(email, resultHandler);
    }

    @Override
    public void checkEmailExists(String email, Handler<AsyncResult<Boolean>> resultHandler) {
        attendeeRepository.checkEmailExists(email, resultHandler);
    }
}