package kitchenpos.infra;

public class FakePurgomalumClient implements PurgomalumClient{
  @Override
  public boolean containsProfanity(String text) {
    return false;
  }
}
