package kitchenpos.infra;

public class FakeBadWordsValidator implements BadWordsValidator {
  @Override
  public boolean containsProfanity(String text) {
    if (text.equals("badwords"))
      return true;

    return false;
  }
}
