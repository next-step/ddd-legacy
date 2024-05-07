package calculator;

import java.util.Objects;

public class Number {
  public static Number ZERO = new Number(0);
  private final Integer value;

  private Number(Integer value) {
    this.value = value;
  }

  public static Number create(NumberValidator numberValidator, String value) {
      Number number = create(value);
      numberValidator.validate(number);
      return number;
  }

  public static Number create(String value) {
    try {
      int intValue = Integer.parseInt(value);
      return create(intValue);
    } catch (java.lang.NumberFormatException e) {
      throw new NumberFormatException("숫자 이외의 값은 변환할 수 없습니다.", e);
    }
  }

  public static Number create(Integer value) {
    return new Number(value);
  }

  public boolean isNegative() {
    return value.compareTo(0) < 0;
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
