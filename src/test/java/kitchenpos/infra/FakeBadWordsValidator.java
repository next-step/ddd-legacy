package kitchenpos.infra;

public class FakeBadWordsValidator implements BadWordsValidator {
  @Override
  public boolean containsProfanity(String text) {
    return text.equals("badwords");
  }
}
