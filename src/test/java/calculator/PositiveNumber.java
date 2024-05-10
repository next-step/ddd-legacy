package calculator;

import java.util.Objects;

class PositiveNumber {
  private final Integer value;

  private PositiveNumber(Integer value) {
    this.value = value;
  }

  public static PositiveNumber from(String number) {
    Integer value = Integer.valueOf(number);

    if (value.compareTo(0) < 0)
      throw new RuntimeException(String.format("연산 대상의 숫자는 0 이상의 양수여야 합니다. 입력된 숫자: %d", value));

    return new PositiveNumber(value);
  }

  public Integer getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PositiveNumber that = (PositiveNumber) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
