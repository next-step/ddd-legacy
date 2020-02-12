package calculator;

public class PositiveInteger {

    private int value;

    public PositiveInteger(int value){
        if(value <= 0 || value > Integer.MAX_VALUE){
            throw new RuntimeException();
        }
        this.value = value;
    }

    public int valueOf(){
        return this.value;
    }

}
