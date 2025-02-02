package racingcar;

/**
 * 자동차 이름 제한은 모든 차에 적용될 가능성이 높으므로,
 * 제한값을 입력이 아닌 클래스 내부에 정의
 */
public record CarName(String name) {

    private static final int MAX_CAR_NAME = 5;

    public CarName {
        if (name == null) {
            throw new IllegalArgumentException(
                    "자동차 이름은 null 일 수 없습니다."
            );
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                    "자동차 이름은 빈 문자열일 수 없습니다."
            );
        }
        if (name.length() > MAX_CAR_NAME) {
            throw new IllegalArgumentException(
                    "자동차 이름은 최대 " + MAX_CAR_NAME + "글자입니다" +
                            " (입력된 이름: " + name + ")"
            );
        }
    }
}
