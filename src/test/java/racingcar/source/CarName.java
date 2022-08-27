package racingcar.source;

public class CarName {
    public static final int MAX_LENGTH = 5;
    private final String name;

    public CarName(String name) {
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("자동차의 이름이 비어있습니다.");

        }
        if (name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다.");
        }
        this.name = name;
    }
}
