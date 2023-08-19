package calculator;

import java.util.stream.Stream;

public class NumberParseUtilsImpl implements NumberParseUtils {


    @Override
    public int[] parse(String[] src) {
        return Stream.of(src)
            .mapToInt(this::parse)
            .toArray();
    }

    private int parse(String src) {
        try {
            PositiveNumber positiveNumber = new PositiveNumber(Integer.parseInt(src));
            return positiveNumber.getValue();
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자 변환에 실패 하였습니다.");
        }
    }

    private static class PositiveNumber {

        private final static int ZERO = 0;
        private final int value;

        public PositiveNumber(int value) {
            validate(value);
            this.value = value;
        }

        private void validate(int result) {
            if (result < ZERO) {
                throw new RuntimeException("음수는 사용할수 없습니다..");
            }
        }

        public int getValue() {
            return value;
        }
    }
}
