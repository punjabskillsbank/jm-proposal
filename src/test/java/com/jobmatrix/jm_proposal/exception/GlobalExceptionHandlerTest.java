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
    void handleProposalNotFoundException_shouldReturnNotFoundErrorBody() {
        // Arrange
        Long jobPostingId = 123L;
        ProposalNotFoundException exception = new ProposalNotFoundException(jobPostingId);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleProposalNotFoundException(exception);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertEquals("Not Found", errorBody.get("error"));
        assertEquals("Proposal not found for job posting ID: " + jobPostingId, errorBody.get("message"));
    }

}

