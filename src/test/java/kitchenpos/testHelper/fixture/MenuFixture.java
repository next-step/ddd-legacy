package kitchenpos.testHelper.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {

    public static MenuCreateRequestBuilder createRequestBuilder() {
        return new MenuCreateRequestBuilder();
    }
    public static class MenuCreateRequestBuilder {

        private BigDecimal price = BigDecimal.valueOf(0);
        private String name;
        private List<MenuProduct> menuProducts = new ArrayList<>();
        private UUID menuGroupId;
        private boolean isDisplay;

        MenuCreateRequestBuilder() {
        }

        public MenuCreateRequestBuilder name(final String name) {
            this.name = name;

            return this;
        }

        public MenuCreateRequestBuilder menuProduct(final Product product, final Long quantity) {
            menuProducts.add(new MenuProduct(product.getId(), quantity));
            this.price = this.price.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

            return this;
        }

        public MenuCreateRequestBuilder menuGroupId(final UUID menuGroupId) {
            this.menuGroupId = menuGroupId;

            return this;
        }

        public MenuCreateRequestBuilder addPrice(final long price) {
            this.price = this.price.add(BigDecimal.valueOf(price));

            return this;
        }

        public MenuCreateRequestBuilder isDisplay(final boolean isDisplay) {
            this.isDisplay = isDisplay;

            return this;
        }

        public Menu build() {
            Menu menu = new Menu();
            menu.setMenuGroupId(this.menuGroupId);
            menu.setMenuProducts(this.menuProducts);
            menu.setName(this.name);
            menu.setPrice(this.price);
            menu.setDisplayed(this.isDisplay);

            return menu;
        }
    }

    public static MenuUpdateRequestBuilder updateRequestBuilder() {
        return new MenuUpdateRequestBuilder();
    }

    public static class MenuUpdateRequestBuilder {
        private BigDecimal price;

        public MenuUpdateRequestBuilder price(final long price) {
            this.price = BigDecimal.valueOf(price);

            return this;
        }

        public Menu build() {
            Menu menu = new Menu();
            menu.setPrice(this.price);

            return menu;
        }
    }
}
