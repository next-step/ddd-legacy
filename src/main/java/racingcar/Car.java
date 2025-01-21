package racingcar;

public class Car {
    private final String name;
    private int position;
    public Car(String name) {
        if(name.length() > 5){
            throw new IllegalArgumentException("name is invalid");
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
