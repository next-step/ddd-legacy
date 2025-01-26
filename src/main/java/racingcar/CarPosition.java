package racingcar;

/**
 * 기본적으로는 입력 값 만큼 이동하지만, 추후 제한 값이 생길 수 있으므로 객체로 분리
 */
public record CarPosition(int position) {

    public int getCurrentPosition() {
        return position;
    }

    public CarPosition move(int input) {
        return new CarPosition(position + input);
    }
}
