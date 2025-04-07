package org.storkforge.barkr.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.storkforge.barkr.exceptions.AccountNotFound;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ErrorResponseUtilTest {

    @Test
    @DisplayName("null passed as parameter throws error")
    void nullPassedAsParameterThrowsError() {
        assertThatThrownBy(() -> ErrorResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, null))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Parameters 'status' and 'e' cannot be null");
        assertThatThrownBy(() -> ErrorResponseUtil.createErrorResponse(null, new RuntimeException()))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Parameters 'status' and 'e' cannot be null");

    }

    @Test
    @DisplayName("Valid input returns predicted error meassage")
    void validInputReturnsPredictedErrorMeassage() {

        var constantError = new AccountNotFound("Error!");



        var expected = Map.of(
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                "message", constantError.getMessage()
        );

        var result = ErrorResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, constantError);
        assertThat(result.get("status")).isEqualTo(expected.get("status"));
        assertThat(result.get("error")).isEqualTo(expected.get("error"));
        assertThat(result.get("message")).isEqualTo(expected.get("message"));
        assertThat(result.get("timestamp")).isExactlyInstanceOf(expected.get("timestamp").getClass());

    }







}
