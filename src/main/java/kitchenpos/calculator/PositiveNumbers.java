
package kitchenpos.calculator;

import java.util.Arrays;

class PositiveNumbers {

    private final int[] numberList;
    private int total;

    PositiveNumbers(String[] numberList) {
        this.numberList = Arrays.stream(numberList)
                .mapToInt(Integer::parseInt)
                .peek(this::checkNegativeNumber)
                .toArray();
    }

    private void checkNegativeNumber(int number) {
        if (number < 0) throw new RuntimeException("음수 불가");
    }

    public int sum() {
        for (int number : numberList) total += number;
        return total;
    }
}
