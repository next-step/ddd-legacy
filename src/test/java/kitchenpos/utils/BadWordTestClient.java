package kitchenpos.utils;

import kitchenpos.domain.BadWordClient;
import org.springframework.stereotype.Component;

@Component
public class BadWordTestClient implements BadWordClient {
    @Override
    public boolean containsProfanity(String text) {
        return false;
    }
}
