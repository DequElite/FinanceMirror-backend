package dequelite.financemirror_backend.app.common.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        LocalDateTime timestamp,
        Integer status,
        String error,
        Boolean success,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                null,
                true,
                message,
                data
        );
    }

    public static <T> ApiResponse<T> error(
            ErrorCode error
    ) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                error.getStatus(),
                error.name(),
                false,
                error.getMessage(),
                null
        );
    }
}
