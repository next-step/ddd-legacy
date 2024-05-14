package stringCalculator;

import java.util.List;

public class SumProcess {

    public SumProcess() {
    }

    public int sum(List<PositiveNumber> numbers){
        return numbers.stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }
}
