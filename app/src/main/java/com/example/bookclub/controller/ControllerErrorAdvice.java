package com.example.bookclub.controller;

import com.example.bookclub.common.CommonResponse;
import com.example.bookclub.common.interceptor.CommonHttpRequestInterceptor;
import com.example.bookclub.errors.account.AccountEmailDuplicatedException;
import com.example.bookclub.errors.account.AccountEmailNotFoundException;
import com.example.bookclub.errors.account.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.account.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.account.AccountNotFoundException;
import com.example.bookclub.errors.account.AccountNotManagerOfStudyException;
import com.example.bookclub.errors.account.AccountPasswordBadRequestException;
import com.example.bookclub.errors.account.AuthenticationBadRequestException;
import com.example.bookclub.errors.account.emailauthentication.EmailBadRequestException;
import com.example.bookclub.errors.account.emailauthentication.EmailNotAuthenticatedException;
import com.example.bookclub.errors.account.emailauthentication.MessageCreateBadRequestException;
import com.example.bookclub.errors.fileupload.FileUploadBadRequestException;
import com.example.bookclub.errors.study.ParseTimeException;
import com.example.bookclub.errors.study.StudyAlreadyExistedException;
import com.example.bookclub.errors.study.StudyAlreadyInOpenOrCloseException;
import com.example.bookclub.errors.study.StudyAlreadyStartedException;
import com.example.bookclub.errors.study.StudyNotAppliedBefore;
import com.example.bookclub.errors.study.StudyNotFoundException;
import com.example.bookclub.errors.study.StudyNotInOpenStateException;
import com.example.bookclub.errors.study.StudySizeFullException;
import com.example.bookclub.errors.study.StudyStartAndEndDateNotValidException;
import com.example.bookclub.errors.study.StudyStartAndEndTimeNotValidException;
import com.example.bookclub.errors.study.StudyStartDateInThePastException;
import com.example.bookclub.errors.study.favorite.StudyFavoriteAlreadyExistedException;
import com.example.bookclub.errors.study.favorite.StudyFavoriteNotExistedException;
import com.example.bookclub.errors.study.studycomment.StudyCommentContentNotExistedException;
import com.example.bookclub.errors.study.studycomment.StudyCommentDeleteBadRequest;
import com.example.bookclub.errors.study.studycomment.StudyCommentNotFoundException;
import com.example.bookclub.errors.study.studycommentlike.StudyCommentLikeAlreadyExistedException;
import com.example.bookclub.errors.study.studycommentlike.StudyCommentLikeNotFoundException;
import com.example.bookclub.errors.study.studylike.StudyLikeAlreadyExistedException;
import com.example.bookclub.errors.study.studylike.StudyLikeNotExistedException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerErrorAdvice {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public CommonResponse onException(Exception e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.error("eventId = {} ", eventId, e);
        return CommonResponse.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.warn("[BaseException] eventId = {}, errorMsg = {}",
                eventId, NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        BindingResult bindingResult = e.getBindingResult();
        FieldError fe = bindingResult.getFieldError();
        if (fe != null) {
            String message = "Request Error" + " " + fe.getField() + "="
                    + fe.getRejectedValue() + " (" + fe.getDefaultMessage() + ")";
            return CommonResponse.fail(message, HttpStatus.BAD_REQUEST.value());
        } else {
            return CommonResponse.fail("요청한 값이 올바르지 않습니다", HttpStatus.BAD_REQUEST.value());
        }
    }

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
