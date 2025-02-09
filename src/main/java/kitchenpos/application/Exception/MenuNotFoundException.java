package kitchenpos.application.Exception;


public class MenuNotFoundException extends EntityNotFoundException {
    public MenuNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public MenuNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MenuNotFoundException(){
        super(ErrorCode.MENU_NOT_FOUND);
    }
}