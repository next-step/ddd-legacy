package string_calculator;

import java.util.List;

public class ListCalculator {

    private final List<NonNegativeLong> list;

    public ListCalculator(final List<NonNegativeLong> list) {
        if (list == null) {
            throw new IllegalArgumentException("list는 null일 수 없습니다");
        }
        this.list = list;
    }

    public Long sum() {
        return this.list.stream()
                .mapToLong(NonNegativeLong::value)
                .sum();
    }
}
