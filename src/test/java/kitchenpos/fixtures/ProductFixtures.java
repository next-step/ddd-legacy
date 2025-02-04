package kitchenpos.fixtures;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixtures {

    public static final UUID BURGER_ID = UUID.randomUUID();
    public static final UUID PIZZA_ID = UUID.randomUUID();

    public static final String BURGER_NAME = "Burger";
    public static final String PIZZA_NAME = "Pizza";
    public static final String PROFANITY = "비속어";

    public static final BigDecimal BURGER_PRICE = BigDecimal.valueOf(8000);
    public static final BigDecimal PIZZA_PRICE = BigDecimal.valueOf(16000);

    public static Product burger() {
        Product product = new Product();
        product.setId(BURGER_ID);
        product.setName(BURGER_NAME);
        product.setPrice(BURGER_PRICE);

        return product;
    }

    public static Product pizza() {
        Product product = new Product();
        product.setId(PIZZA_ID);
        product.setName(PIZZA_NAME);
        product.setPrice(PIZZA_PRICE);

        return product;
    }
}
