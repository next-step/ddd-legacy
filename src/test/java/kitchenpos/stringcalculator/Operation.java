package kitchenpos.stringcalculator;

import java.util.List;

@FunctionalInterface
public interface Operation {
    int operate(List<ParsedNumber> parsedNumbers);
}
