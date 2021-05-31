package com.example.bookclub.errors;

public class FileUploadBadRequestException extends RuntimeException {
    public FileUploadBadRequestException() {
        super("FileUpload bad request");
    }
}
