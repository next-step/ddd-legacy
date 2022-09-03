package string_calculator;

import java.util.List;

public class ListCalculator {

    private final List<Long> list;

    public ListCalculator(final List<Long> list) {
        if (list == null) {
            throw new IllegalArgumentException("list는 null일 수 없습니다");
        }
        this.list = list;
    }

    public Long sum() {
        long result = 0;
        for (final Long e : list) {
            result += e;
        }
        return result;
    }
}
