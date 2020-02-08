package calculator.exception;

public class NotPositiveNumberException extends RuntimeException{

    public NotPositiveNumberException(){
        this("not positive number");
    }

    public NotPositiveNumberException(String message){
        super(message);
    }

    public NotPositiveNumberException(String message, Throwable e){
        super(message, e);
    }
}
