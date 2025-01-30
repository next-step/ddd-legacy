package calculator;

record PositiveNumber(Integer value) {

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber {
        if (value < 0) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
    }

    public PositiveNumber add(PositiveNumber number) {
        return new PositiveNumber(value + number.value);
    }
}
