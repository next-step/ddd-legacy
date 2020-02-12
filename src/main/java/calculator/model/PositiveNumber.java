package calculator.model;

public class PositiveNumber {
    public int num;

    public PositiveNumber(int num) {
        this.num = num;
        this.validate(num);
    }

    public PositiveNumber(String str){
        this(Integer.parseInt(str));
    }

    public int getNum() {
        return this.num;
    }

    private void validate(int number) {
        if (number < 0) {
            throw new RuntimeException("Number must be positive");
        }
    }
}
