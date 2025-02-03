package stringcalculator;

import java.util.ArrayList;
import java.util.List;

public class Numbers {
    private final List<PositiveNumber> numbers;

    public Numbers(String[] numbers) {
        this.numbers = new ArrayList<>();
        for (String number : numbers) {
            this.numbers.add(new PositiveNumber(number));
        }
    }

    public int sum(){
        int result = 0;

        for(PositiveNumber number : numbers){
            result += number.getValue();
        }

        return result;
    }

    public List<PositiveNumber> getNumbers() {
        return numbers;
    }
}
