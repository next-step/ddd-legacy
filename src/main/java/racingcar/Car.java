package racingcar;

import org.springframework.util.StringUtils;

public class Car {
    private final CarName name;
    private int position;

    public Car(String name) {

        this.name = new CarName(name);
    }

    public void move(MoveStrategy moveStrategy) {
        if (moveStrategy.isMovable()) {
            this.position++;
        }
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name.getValue();
    }

    class CarName {
        private final String name;

        public CarName(String name) {
            validate(name);
            this.name = name;
        }

        private void validate(String name) {
            if (!StringUtils.hasText(name)) {
                throw new IllegalArgumentException();
            }
            if (name.length() < 5) {
                throw new IllegalArgumentException();
            }
        }

        public String getValue() {
            return name;
        }
    }
}
