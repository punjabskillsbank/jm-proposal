package com.jobmatrix.jm_proposal.exception;

public class InvalidFileTypeException extends RuntimeException{
    public InvalidFileTypeException(String extension) {
        super("File type not allowed: " + extension);
    }
}
