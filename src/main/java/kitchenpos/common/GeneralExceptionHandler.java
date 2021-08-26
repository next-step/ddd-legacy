package kitchenpos.common;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(value = {
        IllegalArgumentException.class,
        NoSuchElementException.class
    })
    public ResponseEntity<Void> notEnoughElementException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> stateConflictException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
