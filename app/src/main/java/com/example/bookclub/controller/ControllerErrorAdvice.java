package com.example.bookclub.controller;

import com.example.bookclub.common.CommonResponse;
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
import com.example.bookclub.errors.StudyAlreadyInOpenOrCloseException;
import com.example.bookclub.errors.StudyAlreadyStartedException;
import com.example.bookclub.errors.StudyCommentContentNotExistedException;
import com.example.bookclub.errors.StudyCommentDeleteBadRequest;
import com.example.bookclub.errors.StudyCommentLikeAlreadyExistedException;
import com.example.bookclub.errors.StudyCommentLikeNotFoundException;
import com.example.bookclub.errors.StudyCommentNotFoundException;
import com.example.bookclub.errors.StudyFavoriteAlreadyExistedException;
import com.example.bookclub.errors.StudyFavoriteNotExistedException;
import com.example.bookclub.errors.StudyLikeAlreadyExistedException;
import com.example.bookclub.errors.StudyLikeNotExistedException;
import com.example.bookclub.errors.StudyNotAppliedBefore;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudyNotInOpenStateException;
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
    public CommonResponse handleStudyStartDateInThePast(StudyStartDateInThePastException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyStartAndEndDateNotValidException.class)
    public CommonResponse handleStudyStartAndEndDateNotValidException(StudyStartAndEndDateNotValidException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountNewPasswordNotMatchedException.class)
    public CommonResponse handleAccountNewPasswordNotMatched(AccountNewPasswordNotMatchedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyAlreadyStartedException.class)
    public CommonResponse handleStudyAlreadyStarted(StudyAlreadyStartedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyAlreadyExistedException.class)
    public CommonResponse handleStudyAlreadyExisted(StudyAlreadyExistedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudySizeFullException.class)
    public CommonResponse handleStudySizeFull(StudySizeFullException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountEmailNotFoundException.class)
    public CommonResponse handleUserEmailNotFound(AccountEmailNotFoundException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }
  
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyStartAndEndTimeNotValidException.class)
    public CommonResponse handleStudyStartAndEndTimeNotValid(StudyStartAndEndTimeNotValidException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyAlreadyInOpenOrCloseException.class)
    public CommonResponse handleStudyAlreadyInOpenOrClose(StudyAlreadyInOpenOrCloseException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccountNotManagerOfStudyException.class)
    public CommonResponse handleAccountNotManagerException(AccountNotManagerOfStudyException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.FORBIDDEN.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenException.class)
    public CommonResponse handleInvalidToken(InvalidTokenException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthenticationBadRequestException.class)
    public CommonResponse handleAuthenticationBadRequest(AuthenticationBadRequestException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public CommonResponse handleUserNotFound(AccountNotFoundException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountEmailDuplicatedException.class)
    public CommonResponse handleUserEmailDuplicated(AccountEmailDuplicatedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountNicknameDuplicatedException.class)
    public CommonResponse UserNicknameDuplicated(AccountNicknameDuplicatedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailBadRequestException.class)
    public CommonResponse handleEmailBadRequest(EmailBadRequestException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailNotAuthenticatedException.class)
    public CommonResponse handleEmailNotAuthenticated(EmailNotAuthenticatedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountPasswordBadRequestException.class)
    public CommonResponse handlePasswordBadRequest(AccountPasswordBadRequestException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StudyNotFoundException.class)
    public CommonResponse handleStudyNotFound(StudyNotFoundException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse handleProductMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String field = Objects.requireNonNull(e.getBindingResult()
                .getFieldError())
                .getField();

        String message = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return CommonResponse.fail(field + ": " + message, HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileUploadBadRequestException.class)
    public CommonResponse handleFileUploadBadRequest(FileUploadBadRequestException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParseTimeException.class)
    public CommonResponse handleParseTimeException(ParseTimeException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MessageCreateBadRequestException.class)
    public CommonResponse handleMessageCreateBadRequestException(MessageCreateBadRequestException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudyNotAppliedBefore.class)
    public CommonResponse handleStudyNotAppliedBeforeException(StudyNotAppliedBefore e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StudyNotInOpenStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleStudyNotInOpenStateException(StudyNotInOpenStateException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StudyLikeAlreadyExistedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleStudyLikeAlreadyExistedException(StudyLikeAlreadyExistedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StudyLikeNotExistedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleStudyLikeNotExistedException(StudyLikeNotExistedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(StudyCommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleStudyCommentNotFoundException(StudyCommentNotFoundException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(StudyCommentLikeAlreadyExistedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleStudyCommentLikeAlreadyExistedException(StudyCommentLikeAlreadyExistedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StudyCommentLikeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleStudyCommentLikeNotFoundException(StudyCommentLikeNotFoundException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(StudyFavoriteAlreadyExistedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleStudyFavoriteAlreadyExistedException(StudyFavoriteAlreadyExistedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StudyFavoriteNotExistedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleStudyFavoriteNotExistedException(StudyFavoriteNotExistedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(StudyCommentDeleteBadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleStudyCommentDeleteBadRequest(StudyCommentDeleteBadRequest e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StudyCommentContentNotExistedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleStudyCommentContentNotExistedException(StudyCommentContentNotExistedException e) {
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }
}
