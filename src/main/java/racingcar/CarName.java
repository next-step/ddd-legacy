package racingcar;

import org.springframework.util.StringUtils;

public class CarName {
    private final String name;

    public CarName(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException(RacingCarExceptionMessage.NAME_EMPTY.getMessage());
        }
        if (name.length() > 5) {
            throw new IllegalArgumentException(RacingCarExceptionMessage.NAME_BIGGER_THAN_FIVE.getMessage());
        }
    }

    public String getValue() {
        return name;
    }
}
