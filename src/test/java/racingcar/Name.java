package racingcar;

public record Name(String value) {

	private static final int MAX_LENGTH = 5;

	public Name {
		if (value.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("이름은 5글자를 넘을 수 없습니다");
		}
	}
}
