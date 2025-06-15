package com.jobmatrix.jm_proposal.exception;

public class AnswerTooLongException extends RuntimeException {
    public AnswerTooLongException(int maxWordLimit) {
        super("Answer exceeds " + maxWordLimit + " word limit.");
    }
}
