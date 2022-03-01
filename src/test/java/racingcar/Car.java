package racingcar;

import java.util.Random;

/**
 * <pre>
 * racingcar
 *      Car
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-01 오후 9:53
 */

public class Car {

    private String name;
    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    public Car(final String name, final int position) {
        validate(name);
        this.name = name;
        this.position = position;
    }

    private void validate(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘길 수 없습니다.");
        }
    }

    public void move(final int number) {
        if (number >= 4) {
            this.position++;
        }
    }


    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            this.position++;
        }
    }

    public int getPosition() {
        return this.position;
    }
}
