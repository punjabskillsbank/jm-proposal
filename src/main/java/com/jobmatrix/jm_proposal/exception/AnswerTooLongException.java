package com.jobmatrix.jm_proposal.exception;

public class AnswerTooLongException extends RuntimeException {
    public AnswerTooLongException(int maxCharLimit) {
        super("Answer exceeds " + maxCharLimit + " char limit.");
    }
}
