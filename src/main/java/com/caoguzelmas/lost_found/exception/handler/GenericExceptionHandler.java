package com.caoguzelmas.lost_found.exception.handler;

import com.caoguzelmas.lost_found.exception.ClaimProcessingException;
import com.caoguzelmas.lost_found.exception.FileUploadException;
import com.caoguzelmas.lost_found.exception.UnsupportedFileTypeException;
import com.caoguzelmas.lost_found.model.dto.ErrorResponseDTO;
import com.caoguzelmas.lost_found.util.constants.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(UnsupportedFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleUnsupportedFileTypeException(UnsupportedFileTypeException ex) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleFileUploadException(FileUploadException ex) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ClaimProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleClaimProcessingException(ClaimProcessingException ex) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }

    // @Valid DTO validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }

    // BadCredentials
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleAuthenticationException(AuthenticationException ex) {
        String clientMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_AUTHENTICATION_FAILED, ex.getMessage());

        if (ex instanceof BadCredentialsException) {
            clientMessage = ErrorMessageConstants.ERROR_MESSAGE_JWT_INVALID_CREDENTIALS;
        }

        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(clientMessage)
                .build();
    }
}
