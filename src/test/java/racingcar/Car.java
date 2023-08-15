package racingcar;

import org.springframework.util.StringUtils;

public class Car {
    private String name;
    public Car(String name) {
        if(!StringUtils.hasText(name)){
            throw new IllegalArgumentException();
        }
        if (name.length() < 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }
}
