package kitchenpos.domain;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NoSuchProductException extends IllegalArgumentException {
    private static final String MESSAGE = "해당 Product가 존재하지 않습니다. Product id 값: [%s]";

    public NoSuchProductException(List<UUID> productIds) {
        super(String.format(
                        MESSAGE, productIds == null ? null : productIds.stream()
                                .map(UUID::toString)
                                .collect(Collectors.joining(","))
                )
        );
    }

    public NoSuchProductException(UUID productId) {
        super(String.format(MESSAGE, productId));
    }
}
