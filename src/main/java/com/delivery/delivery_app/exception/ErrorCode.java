package com.delivery.delivery_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(5000, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Lỗi không xác định", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(1002, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1003, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    USER_EXISTED(1004, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1004, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1005, "OTP hết hạn hoặc không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIAL(1006, "Tài khoản hoặc mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1007, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(1008, "Token hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_DATA(1009, "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),
    TOO_MANY_REQUESTS(1010, "Quá nhiều yêu cầu", HttpStatus.TOO_MANY_REQUESTS),
    USER_NOT_FOUND(1011, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
