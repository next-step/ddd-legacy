package calculator;

public class PosiviteInteger {

  private final int num;

  private PosiviteInteger(int num) {
    if (num < 0) {
      throw new RuntimeException("음수는 계산할 수 없습니다.");
    }
    this.num = num;
  }

  public static PosiviteInteger getInstance(String value) {
    return new PosiviteInteger(Integer.parseInt(value));
  }

  public int getNum() {
    return num;
  }
}
