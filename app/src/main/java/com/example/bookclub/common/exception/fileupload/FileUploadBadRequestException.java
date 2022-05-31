package com.example.bookclub.common.exception.fileupload;

/**
 * 파일 업로드 요청이 잘못된 경우 예외
 */
public class FileUploadBadRequestException extends RuntimeException {
    public FileUploadBadRequestException() {
        super("FileUpload bad request");
    }
}
