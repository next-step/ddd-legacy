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
            throw new IllegalArgumentException();
        }
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return name;
    }
}
