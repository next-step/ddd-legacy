package kitchenpos.infra;

public interface ProfanityDetectClient {

    boolean containsProfanity(final String text);
}
