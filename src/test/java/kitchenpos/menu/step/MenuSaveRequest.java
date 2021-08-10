package kitchenpos.menu.step;

import java.util.List;
import java.util.UUID;

public class MenuSaveRequest {

    private String name;
    private int price;
    private UUID menuGroupId;
    private boolean displayed;
    private List<MenuProductSaveRequest> menuProducts;

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public UUID getMenuGroupId() {
        return menuGroupId;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public List<MenuProductSaveRequest> getMenuProducts() {
        return menuProducts;
    }

    public static MenuSaveRequestBuilder builder() {
        return new MenuSaveRequestBuilder();
    }

    public static class MenuSaveRequestBuilder {
        private String name;
        private int price;
        private UUID menuGroupId;
        private boolean displayed;
        private List<MenuProductSaveRequest> menuProducts;

        public MenuSaveRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MenuSaveRequestBuilder price(int price) {
            this.price = price;
            return this;
        }

        public MenuSaveRequestBuilder menuGroupId(UUID menuGroupId) {
            this.menuGroupId = menuGroupId;
            return this;
        }

        public MenuSaveRequestBuilder displayed(boolean displayed) {
            this.displayed = displayed;
            return this;
        }

        public MenuSaveRequestBuilder menuProducts(List<MenuProductSaveRequest> menuProducts) {
            this.menuProducts = menuProducts;
            return this;
        }

        public MenuSaveRequest build() {
            MenuSaveRequest request = new MenuSaveRequest();
            request.name = this.name;
            request.price = this.price;
            request.menuGroupId = this.menuGroupId;
            request.displayed = this.displayed;
            request.menuProducts = this.menuProducts;
            return request;
        }
    }
}
