package stringcalcurator;

import java.util.ArrayList;
import java.util.List;

public class StringNumberList {
    private List<StringNumber> numbers;

    public static StringNumberList create(String[] numbers) {
        List<StringNumber> numberList = new ArrayList<>(numbers.length);
        for(String number : numbers) {
            numberList.add(StringNumber.create(number));
        }
        return new StringNumberList(numberList);
    }

    private StringNumberList(List<StringNumber> numbers) {
        this.numbers = numbers;
    }

    public List<StringNumber> getIntNumbers(){
        return numbers;
    }
}
