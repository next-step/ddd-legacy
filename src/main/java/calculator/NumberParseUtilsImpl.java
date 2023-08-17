package calculator;

import java.util.stream.Stream;

public class NumberParseUtilsImpl implements NumberParseUtils {

    @Override
    public int[] parse(String[] src) {
        try {
            return Stream.of(src)
                .mapToInt(Integer::parseInt)
                .toArray();
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자 변환에 실패 하였습니다.");
        }
    }
}
