package kitchenpos.infra;

public class FakePurgomalumClient implements PurgomalumClient {
  @Override
  public boolean containsProfanity(String text) {
    if (text.equals("badwords"))
      return true;

    return false;
  }
}
