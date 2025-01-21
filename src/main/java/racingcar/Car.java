package racingcar;

public class Car {
    private final String name;
    public Car(String name) {
        if(name.length() > 5){
            throw new IllegalArgumentException("name is invalid");
        }
        this.name = name;
    }

}
