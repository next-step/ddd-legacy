package racingcar;

import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.Random;

public class Car {
    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void move(int condition){
        if (condition >= 4) {
            position++;
        }
    }
}
