package kitchenpos.infra;

import org.springframework.stereotype.Component;

@Component
public interface PurgomalumClient {
    boolean containsProfanity(final String text);
}
