package calculator;

import java.util.stream.Stream;

public class NumberParseUtilsImpl implements NumberParseUtils {

    private final static int ZERO = 0;

    @Override
    public int[] parse(String[] src) {
        return Stream.of(src)
            .mapToInt(this::parse)
            .toArray();
    }

    private int parse(String src) {
        try {
            int result = Integer.parseInt(src);
            validate(result);
            return result;
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자 변환에 실패 하였습니다.");
        }
    }

    private void validate(int result) {
        if (result < ZERO) {
            throw new RuntimeException("음수는 변환할수 없습니다.");
        }
    }
}
