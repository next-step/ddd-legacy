package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Numbers {
    private static final int ZERO = 0;
    private final List<Number> numbers;

    public Numbers(){
        numbers = new ArrayList<>();
    }

    public void checkIfMinus(Integer number){
        if (number < ZERO) throw new RuntimeException();
    }

    public void addNumbersFromToken(String[] token){
        Arrays.stream(token).map(Integer::parseInt)
                .map(Number::new)
                .forEach(number -> numbers.add(number));
    }

    public List<Number> getNumbers(){
        return this.numbers;
    }
}
