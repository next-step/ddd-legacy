package stringCalculator;

import java.util.List;

public class StringCalculator {
    private final SumProcess sumProcess = new SumProcess();

    public StringCalculator() {
    }

    public int add(String text) {
        if (this.isEmptyText(text)) {
            return 0;
        }

        List<PositiveNumber> numbers = NumberConvertor.getNumbers(text);
        return sumProcess.sum(numbers);
    }

    private boolean isEmptyText(String text) {
        if(text == null || text.isBlank()){
            return true;
        }

        return false;
    }

}
