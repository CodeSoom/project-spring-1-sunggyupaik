package com.example.bookclub.controller;

import com.example.bookclub.dto.ErrorResponse;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import com.example.bookclub.errors.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.AccountNotManagerOfStudyException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import com.example.bookclub.errors.EmailBadRequestException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.errors.FileUploadBadRequestException;
import com.example.bookclub.errors.InvalidTokenException;
import com.example.bookclub.errors.MessageCreateBadRequestException;
import com.example.bookclub.errors.ParseTimeException;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyAlreadyInOpenException;
import com.example.bookclub.errors.StudyAlreadyInOpenOrClose;
import com.example.bookclub.errors.StudyAlreadyStartedException;
import com.example.bookclub.errors.StudyNotAppliedBefore;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudySizeFullException;
import com.example.bookclub.errors.StudyStartAndEndDateNotValidException;
import com.example.bookclub.errors.StudyStartAndEndTimeNotValidException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ControllerErrorAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyStartDateInThePastException.class)
    public ErrorResponse handleStudyStartDateInThePast(StudyStartDateInThePastException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyStartAndEndDateNotValidException.class)
    public ErrorResponse handleStudyStartAndEndDateNotValidException(StudyStartAndEndDateNotValidException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountNewPasswordNotMatchedException.class)
    public ErrorResponse handleAccountNewPasswordNotMatched(AccountNewPasswordNotMatchedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyAlreadyStartedException.class)
    public ErrorResponse handleStudyAlreadyStarted(StudyAlreadyStartedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyAlreadyExistedException.class)
    public ErrorResponse handleStudyAlreadyExisted(StudyAlreadyExistedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudySizeFullException.class)
    public ErrorResponse handleStudySizeFull(StudySizeFullException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountEmailNotFoundException.class)
    public ErrorResponse handleUserEmailNotFound(AccountEmailNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
  
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyStartAndEndTimeNotValidException.class)
    public ErrorResponse handleStudyStartAndEndTimeNotValid(StudyStartAndEndTimeNotValidException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyAlreadyInOpenOrClose.class)
    public ErrorResponse handleStudyAlreadyInOpenOrClose(StudyAlreadyInOpenOrClose e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccountNotManagerOfStudyException.class)
    public ErrorResponse handleAccountNotManagerException(AccountNotManagerOfStudyException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenException.class)
    public ErrorResponse handleInvalidToken(InvalidTokenException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthenticationBadRequestException.class)
    public ErrorResponse handleAuthenticationBadRequest(AuthenticationBadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public ErrorResponse handleUserNotFound(AccountNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountEmailDuplicatedException.class)
    public ErrorResponse handleUserEmailDuplicated(AccountEmailDuplicatedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountNicknameDuplicatedException.class)
    public ErrorResponse UserNicknameDuplicated(AccountNicknameDuplicatedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailBadRequestException.class)
    public ErrorResponse handleEmailBadRequest(EmailBadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailNotAuthenticatedException.class)
    public ErrorResponse handleEmailNotAuthenticated(EmailNotAuthenticatedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountPasswordBadRequestException.class)
    public ErrorResponse handlePasswordBadRequest(AccountPasswordBadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StudyNotFoundException.class)
    public ErrorResponse handleStudyNotFound(StudyNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleProductMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String field = Objects.requireNonNull(e.getBindingResult()
                .getFieldError())
                .getField();

        String message = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return new ErrorResponse(field + ": " + message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileUploadBadRequestException.class)
    public ErrorResponse handleFileUploadBadRequest(FileUploadBadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParseTimeException.class)
    public ErrorResponse handleParseTimeException(ParseTimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MessageCreateBadRequestException.class)
    public ErrorResponse handleMessageCreateBadRequestException(MessageCreateBadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyNotAppliedBefore.class)
    public ErrorResponse handleStudyNotAppliedBeforeException(StudyNotAppliedBefore e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(StudyAlreadyInOpenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStudyAlreadyInOpenException(StudyAlreadyInOpenException e) {
        return new ErrorResponse(e.getMessage());
    }
}
