package racingcar;

class Car {
    private String name;
    private int position;

    public Car(String name) {
        this.name = name;
        this.position = 0;
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
    }

    public Car(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public void move(final MovingStrategy condition){
        if(condition.movable()){
            position++;
        }
    }

    public int position() {
        return this.position;
    }
}
