package racingcar;

class Car {
    private String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5자 이하여야 합니다.");
        }
        this.name = name;
        this.position = 0;
    }

    public void move(int randomValue) {
        if (randomValue >= 4) {
            this.position++;
        }
    }

    public void stop(int randomValue) {
        if (randomValue <= 4) {
            this.position--;
        }
    }


    public int position() {
        return position;
    }

    public String name() {
        return name;
    }
}