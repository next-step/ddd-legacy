package calculator;

import java.util.List;

class Calculator {

    public static int add(List<String> nums) {
        if (nums.isEmpty()) {
            return 0;
        }
        return nums.stream().mapToInt(Integer::parseInt).sum();
    }
}
