package calculator;

class PositiveNumberValidator implements NumberValidator {

  @Override
  public void validate(final Number number) {
    if (number.isNegative()) {
      throw new NumberFormatException("음수는 지원하지 않습니다.");
    }
  }
}
