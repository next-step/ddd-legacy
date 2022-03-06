package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Numbers {
    private static final int ZERO = 0;
    private final List<Integer> numbers;

    public Numbers(){
        numbers = new ArrayList<>();
    }

    public void checkIfMinus(Integer number){
        if (number < ZERO) throw new RuntimeException();
    }

    public void addNumbersFromToken(String[] token){
        Arrays.stream(token).map(Integer::parseInt)
                .peek(number -> checkIfMinus(number))
                .forEach(number -> numbers.add(number));
    }

    public List<Integer> getNumbers(){
        return this.numbers;
    }
}
