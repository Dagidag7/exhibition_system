package com.exhibition.service;

import com.exhibition.util.EmailValidator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class EmailValidationService {
    
    private final AttendeeService attendeeService;
    private final ExhibitorService exhibitorService;
    
    public EmailValidationService(AttendeeService attendeeService, ExhibitorService exhibitorService) {
        this.attendeeService = attendeeService;
        this.exhibitorService = exhibitorService;
    }
    
    /**
     * Validates email format and checks uniqueness across all user types
     * @param email Email to validate
     * @param userType Type of user (attendee, exhibitor) - to allow same email for updates
     * @param userId User ID for updates (null for new registrations)
     * @param resultHandler Handler for the result
     */
    public void validateEmailForRegistration(String email, String userType, Integer userId, Handler<AsyncResult<String>> resultHandler) {
        // First validate format
        String normalizedEmail = EmailValidator.validateAndNormalize(email);
        if (normalizedEmail == null) {
            resultHandler.handle(Future.failedFuture("Invalid email format"));
            return;
        }
        
        // Check if email exists in attendees
        attendeeService.checkEmailExists(normalizedEmail, attendeeRes -> {
            if (attendeeRes.failed()) {
                resultHandler.handle(Future.failedFuture("Failed to check attendee email: " + attendeeRes.cause().getMessage()));
                return;
            }
            
            boolean existsInAttendees = attendeeRes.result();
            
            // Check if email exists in exhibitors
            exhibitorService.checkEmailExists(normalizedEmail, exhibitorRes -> {
                if (exhibitorRes.failed()) {
                    resultHandler.handle(Future.failedFuture("Failed to check exhibitor email: " + exhibitorRes.cause().getMessage()));
                    return;
                }
                
                boolean existsInExhibitors = exhibitorRes.result();
                
                // Determine if email is available
                boolean emailTaken = false;
                String conflictType = "";
                
                if (existsInAttendees && !"attendee".equals(userType)) {
                    emailTaken = true;
                    conflictType = "attendee";
                } else if (existsInExhibitors && !"exhibitor".equals(userType)) {
                    emailTaken = true;
                    conflictType = "exhibitor";
                } else if ("attendee".equals(userType) && existsInAttendees) {
                    // For attendee updates, check if it's the same user
                    emailTaken = true; // Will be handled at controller level for updates
                } else if ("exhibitor".equals(userType) && existsInExhibitors) {
                    // For exhibitor updates, check if it's the same user
                    emailTaken = true; // Will be handled at controller level for updates
                }
                
                if (emailTaken && userId == null) {
                    // New registration with existing email
                    resultHandler.handle(Future.failedFuture("Email already registered as " + conflictType));
                } else {
                    // Email is valid and available
                    resultHandler.handle(Future.succeededFuture(normalizedEmail));
                }
            });
        });
    }
    
    /**
     * Simple format validation
     * @param email Email to validate
     * @param resultHandler Handler for the result
     */
    public void validateEmailFormat(String email, Handler<AsyncResult<String>> resultHandler) {
        String normalizedEmail = EmailValidator.validateAndNormalize(email);
        if (normalizedEmail == null) {
            resultHandler.handle(Future.failedFuture("Invalid email format"));
        } else {
            resultHandler.handle(Future.succeededFuture(normalizedEmail));
        }
    }
    
    /**
     * Check if email exists anywhere in the system
     * @param email Email to check
     * @param resultHandler Handler for the result
     */
    public void checkEmailExistsAnywhere(String email, Handler<AsyncResult<EmailExistenceResult>> resultHandler) {
        String normalizedEmail = EmailValidator.normalize(email);
        
        attendeeService.checkEmailExists(normalizedEmail, attendeeRes -> {
            if (attendeeRes.failed()) {
                resultHandler.handle(Future.failedFuture(attendeeRes.cause()));
                return;
            }
            
            boolean existsInAttendees = attendeeRes.result();
            
            exhibitorService.checkEmailExists(normalizedEmail, exhibitorRes -> {
                if (exhibitorRes.failed()) {
                    resultHandler.handle(Future.failedFuture(exhibitorRes.cause()));
                    return;
                }
                
                boolean existsInExhibitors = exhibitorRes.result();
                
                EmailExistenceResult result = new EmailExistenceResult();
                result.email = normalizedEmail;
                result.existsInAttendees = existsInAttendees;
                result.existsInExhibitors = existsInExhibitors;
                result.existsAnywhere = existsInAttendees || existsInExhibitors;
                
                if (existsInAttendees) {
                    result.existsAs = "attendee";
                } else if (existsInExhibitors) {
                    result.existsAs = "exhibitor";
                }
                
                resultHandler.handle(Future.succeededFuture(result));
            });
        });
    }
    
    public static class EmailExistenceResult {
        public String email;
        public boolean existsAnywhere;
        public boolean existsInAttendees;
        public boolean existsInExhibitors;
        public String existsAs;
    }
}