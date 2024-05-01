package racingcar.domain;

public record Name(String name) {
    private static final int MAX_NAME_LENGTH = 5;

    public Name {
        validateName(name);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 빈 문자열이 될 수 없습니다.");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 " + MAX_NAME_LENGTH + "자 이하여야 합니다.");
        }
    }
}
