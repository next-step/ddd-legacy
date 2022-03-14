package calculator;

import static java.util.Objects.requireNonNull;

import java.util.List;
import org.springframework.lang.Nullable;

final class StringCalculator {

    private final TokenFactory tokenFactory;

    StringCalculator(final TokenFactory tokenFactory) {
        this.tokenFactory = requireNonNull(tokenFactory);
    }

    int add(@Nullable final String text) {
        final List<String> tokens = tokenFactory.createTokens(text);

        return Numbers.parse(tokens)
            .add();
    }
}
