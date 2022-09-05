package kitchenpos.infra.purgomalum;

@FunctionalInterface
public interface PurgomalumClient {
    boolean containsProfanity(final String text);
}
