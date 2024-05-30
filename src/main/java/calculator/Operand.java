package calculator;

public class Operand {
    private final Long value;
    private static final String NUMBER_MATCH_REGEX = "-?\\d+(\\.\\d+)?";

    public Operand(Long value){
        validatePositiveValue(value);
        this.value = value;
    }

    private void validatePositiveValue(Long value) {
        if(value < 0){
            throw new IllegalArgumentException("Negative number found: " + value);
        }
    }

    public Operand(String value){
        validateStringValue(value);
        validatePositiveValue(Long.parseLong(value));
        this.value = Long.parseLong(value);
    }

    private void validateStringValue(String str){
        if(str==null || str.isEmpty() || !str.matches(NUMBER_MATCH_REGEX)){
            throw new IllegalArgumentException("Invalid number found: " + str);
        }
    }

    public Long getValue(){
        return value;
    }
}
