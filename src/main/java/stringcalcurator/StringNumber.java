package stringcalcurator;

public class StringNumber {
    private final int number;

    public static StringNumber create(String numStr){
        int number = Integer.parseInt(numStr);
        return new StringNumber(number);
    }

    public static StringNumber create(int numStr){
        return new StringNumber(numStr);
    }

    private StringNumber(int number) {
        isPositive(number);
        this.number = number;
    }

    private void isPositive(int number){
        if(number < 0){
            throw new RuntimeException();
        }
    }

    public int getNumber() {
        return number;
    }
}
