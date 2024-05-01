package racingcar;

public class Car{

    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {

        if(name.length() > 5){
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.position = position; // 인스턴스 생성 시 위치는 0 으로 초기화
    }

    public int getPosition() {
        return position;
    }

    public void moving(int num){

        if(num >= 4){
             position++;
        }
    }

}