package stringcalcurator;

public class StringNumber {
    private int number;

    public static StringNumber create(String numStr){
        int number = Integer.parseInt(numStr);
        return new StringNumber(number);
    }

    public static StringNumber create(int numStr){
        return new StringNumber(numStr);
    }

    public StringNumber(int number) {
        isPositive(number);
        this.number = number;
    }

    private void isPositive(int number){
        if(number < 0){
            throw new RuntimeException();
        }
    }

    public StringNumber add(StringNumber other){
        return create(this.getNumber() + other.getNumber());
    }

    public int getNumber() {
        return number;
    }
}
