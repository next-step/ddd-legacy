package calculator;

public class PositiveInteger {

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
}
