package kitchenpos.application.fake.helper;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.fake.helper.ProductFixtureFactory.레몬에이드;
import static kitchenpos.application.fake.helper.ProductFixtureFactory.미트파이;


public final class MenuFixtureFactory {

    public static final String 미트파이_하나를_포함한_메뉴_이름 = "미트파이_하나를_포함한_메뉴";
    public static final BigDecimal 미트파이_하나를_포함한_메뉴_가격 = BigDecimal.valueOf(800L);
    public static final int 수량 = 1;

    public static final Menu 미트파이_하나를_포함한_메뉴 = new Builder()
            .id(UUID.randomUUID())
            .name(미트파이_하나를_포함한_메뉴_이름)
            .price(미트파이_하나를_포함한_메뉴_가격)
            .addProduct(미트파이, 수량)
            .displayed(true)
            .build();

    public static final Menu 미트파이_레몬에이드_세트_메뉴 = new Builder()
            .id(UUID.randomUUID())
            .name(미트파이_하나를_포함한_메뉴_이름)
            .price(BigDecimal.valueOf(1800L))
            .addProduct(미트파이, 수량)
            .addProduct(레몬에이드, 수량)
            .displayed(true)
            .build();


    public static final class Builder implements FixtureBuilder<Menu> {
        private UUID id;
        private String name;
        private BigDecimal price;
        private MenuGroup menuGroup = new MenuGroup();
        private boolean displayed;
        private List<MenuProduct> menuProducts = new ArrayList<>();

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder menuGroup(MenuGroup menuGroup) {
            this.menuGroup = menuGroup;
            return this;
        }

        public Builder displayed(boolean displayed) {
            this.displayed = displayed;
            return this;
        }

        public Builder addAllManuProducts(List<MenuProduct> menuProducts) {
            menuProducts.forEach(this::addManuProduct);
            return this;
        }

        public Builder addProduct(Product product, int quantity) {
            MenuProduct mp = new MenuProduct();
            mp.setProductId(product.getId());
            mp.setProduct(product);
            mp.setQuantity(quantity);
            return addManuProduct(mp);
        }

        public Builder addManuProduct(MenuProduct menuProduct) {
            menuProduct.setSeq(menuProducts.size() + 1L);
            this.menuProducts.add(menuProduct);
            return this;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public MenuGroup getMenuGroup() {
            return menuGroup;
        }

        public boolean isDisplayed() {
            return displayed;
        }

        public List<MenuProduct> getMenuProducts() {
            return menuProducts;
        }

        @Override
        public Menu build() {
            Menu menu = new Menu();
            menu.setId(this.id);
            menu.setName(this.name);
            menu.setPrice(this.price);
            menu.setMenuGroup(this.menuGroup);
            menu.setMenuGroupId(this.menuGroup.getId());
            menu.setDisplayed(this.displayed);
            menu.setMenuProducts(this.menuProducts);
            return menu;
        }


    }

}
