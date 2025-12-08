package vn.codegym.lunchbot_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Tự động trả về mã lỗi HTTP 404 cho client
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Constructor nhận thông báo lỗi
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor nhận thông báo lỗi và nguyên nhân (cause)
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}