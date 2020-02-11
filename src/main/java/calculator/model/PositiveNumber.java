package calculator.model;

public class PositiveNumber {
    public int num;

    public PositiveNumber(int num){
        this.num = num;
    }

    public int getNum(){
        return this.num;
    }

    public static int validate(int number){
        if(number < 0) throw new RuntimeException("Number must be positive");
        return number;
    }
}
