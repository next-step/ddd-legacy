package stringcalculator;

import java.util.ArrayList;
import java.util.List;

public class PositiveStringNumbers {

    private List<PositiveStringNumber> positiveStringNumbers = new ArrayList<>();


    public PositiveStringNumbers(String[] stringNumbers) {
        for (String stringNumber : stringNumbers) {
            this.positiveStringNumbers.add(new PositiveStringNumber(stringNumber));
        }
    }

    public int addAllNumber() {
        return positiveStringNumbers.stream()
            .mapToInt(PositiveStringNumber::getNumber)
            .sum();
    }
}
