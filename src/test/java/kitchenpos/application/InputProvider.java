package kitchenpos.application;

import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputProvider {
    public static Stream<BigDecimal> provideValidPrice() {
        return Stream.of(
                BigDecimal.valueOf(100)
        );
    }

    public static Stream<BigDecimal> provideHighPrice() {
        return Stream.of(
                BigDecimal.valueOf(Long.MAX_VALUE)
        );
    }

    public static Stream<BigDecimal> provideInvalidPrice() {
        return Stream.of(
                BigDecimal.valueOf(-1),
                BigDecimal.valueOf(Long.MAX_VALUE)
        );
    }

    public static Stream<BigDecimal> provideNullOrMinusPrice() {
        return Stream.of(
                BigDecimal.valueOf(-1),
                null
        );
    }

    public static Stream<UUID> provideValidMenuGroupId() {
        return Stream.of(UUID.fromString("5e9879b7-6112-4791-a4ce-f22e94af8752"));
    }

    public static Stream<List<UUID>> provideValidProductIdList() {
        return Stream.of(List.of(UUID.fromString("0ac16db7-1b02-4a87-b9c1-e7d8f226c48d")));
    }

    public static Stream<List<UUID>> provideInvalidProductIdList() {
        return Stream.of(List.of(new UUID(1, 2)));
    }

    public static Stream<List<MenuProduct>> provideInvalidProductListWithInvalidQuantity() {
        return provideInvalidProductIdList()
                .map(notExistProductIdList -> notExistProductIdList.stream().map(
                        notExistProductId -> {
                            MenuProduct menuProduct = new MenuProduct();
                            menuProduct.setProductId(notExistProductId);
                            menuProduct.setQuantity(-1);
                            return menuProduct;
                        }
                ).collect(Collectors.toList()));
    }

    public static Stream<List<MenuProduct>> provideValidProductListWithInvalidQuantity() {
        return provideValidProductIdList()
                .map(existProductIdList -> existProductIdList.stream().map(
                        existProductId -> {
                            MenuProduct menuProduct = new MenuProduct();
                            menuProduct.setProductId(existProductId);
                            menuProduct.setQuantity(-1);
                            return menuProduct;
                        }
                ).collect(Collectors.toList()));
    }

    public static Stream<List<MenuProduct>> provideValidProductListWithValidQuantity() {
        return provideValidProductIdList()
                .map(existProductIdList -> existProductIdList.stream().map(
                        existProductId -> {
                            MenuProduct menuProduct = new MenuProduct();
                            menuProduct.setProductId(existProductId);
                            menuProduct.setQuantity(1);
                            return menuProduct;
                        }
                ).collect(Collectors.toList()));
    }

    public static Stream<UUID> provideValidMenuId() {
        return Stream.of(UUID.fromString("191fa247-b5f3-4b51-b175-e65db523f754"));
    }

    public static Stream<UUID> provideInvalidMenuId() {
        return Stream.of(new UUID(1, 2));
    }

    public static Stream<UUID> provideExistProductId() {
        return Stream.of(UUID.fromString("0ac16db7-1b02-4a87-b9c1-e7d8f226c48d"));
    }

    public static Stream<UUID> provideNonExistProductId() {
        return Stream.of(new UUID(1, 2));
    }

    public static Stream<UUID> provideNonExistOrderTableId() {
        return Stream.of(new UUID(1, 2));
    }

    public static Stream<UUID> provideExistOrderTableId() {
        return Stream.of(UUID.fromString("3faec3ab-5217-405d-aaa2-804f87697f84"));
    }
}
