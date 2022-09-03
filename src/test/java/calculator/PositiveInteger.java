package calculator;

public class PositiveInteger {

  static final PositiveInteger ZERO_POSITIVE = new PositiveInteger(0);

  private final int num;

  private PositiveInteger(int num) {
    if (num < 0) {
      throw new RuntimeException("음수는 계산할 수 없습니다.");
    }
    this.num = num;
  }

  public static PositiveInteger getInstance(String value) {
    try {
      return new PositiveInteger(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      throw new RuntimeException("숫자를 입력해주세요.");
    }
  }

  public int getNum() {
    return num;
  }

  public PositiveInteger add(PositiveInteger target) {
    return new PositiveInteger(this.num + target.getNum());
  }
}
