package com.jobmatrix.jm_proposal.exception;

import com.common.exceptionHandling.FreelancerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleValidationExceptions_shouldReturnFieldErrorMessages() {
        // Arrange
        BindingResult bindingResult = new BindException(new Object(), "proposalSubmissionRequest");
        bindingResult.addError(new FieldError("proposalSubmissionRequest", "coverLetter", "Cover letter is required"));
        bindingResult.addError(new FieldError("proposalSubmissionRequest", "proposedBidAmount", "Bid amount must be positive"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertEquals(400, response.getStatusCodeValue());

        Map<String, String> errorMap = response.getBody();
        assertNotNull(errorMap);
        assertEquals(2, errorMap.size());
        assertEquals("Cover letter is required", errorMap.get("coverLetter"));
        assertEquals("Bid amount must be positive", errorMap.get("proposedBidAmount"));
    }

    @Test
    void handleFreelancerNotFoundException_shouldReturnNotFoundMessage() {
        // Arrange
        UUID freelancerId = UUID.randomUUID();
        FreelancerNotFoundException exception = new FreelancerNotFoundException(freelancerId);
        String expectedMessage = "Freelancer not found with ID: " + freelancerId;

        // Act
        ResponseEntity<String> response = exceptionHandler.handleFreelancerNotFoundException(exception);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    void handleProposalNotFoundException_shouldReturnNotFoundResponse() {
        // Arrange
        String errorMessage = "Proposal not found for job posting ID: 123";
        ProposalNotFoundException exception = new ProposalNotFoundException(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleProposalNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Object responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> errorMap = (Map<String, Object>) responseBody;

        assertEquals(404, errorMap.get("status"));
        assertEquals("Not Found", errorMap.get("error"));
        assertEquals(errorMessage, errorMap.get("message"));
        assertNotNull(errorMap.get("timestamp")); // timestamp should be present
    }


}