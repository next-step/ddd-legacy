package calculator;

import java.util.Objects;

public class Number {

  public final static Number ZERO = new Number(0);
  private final int value;

  private Number(int value) {
    this.value = value;
  }

  public static Number createPositive(String value) {
    try {
      int intValue = Integer.parseInt(value);
      return createPositive(intValue);
    } catch (java.lang.NumberFormatException e) {
      throw new NumberFormatException("숫자 이외의 값은 변환할 수 없습니다.", e);
    }
  }

  public static Number createPositive(int value) {
    if (value < 0) {
      throw new NumberFormatException("음수는 지원하지 않습니다.");
    }
    return new Number(value);
  }

  public Number add(Number number) {
    int addedNumber = Math.addExact(value, number.value);
    return new Number(addedNumber);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Number number = (Number) o;
    return Objects.equals(value, number.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
