package kitchenpos.ui.dto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuRequest {

    private UUID id;
    private String name;
    private BigDecimal price;
    private MenuGroup menuGroup;
    private boolean displayed;
    private List<MenuProductRequest> menuProducts;

    private UUID menuGroupId;

    public MenuRequest() {
    }

    public MenuRequest(final long price) {
        this.price = BigDecimal.valueOf(price);
    }

    public MenuRequest(final String name, final long price, final boolean displayed,
        final MenuGroup menuGroup, final MenuProductRequest... menuProducts) {
        this.name = name;
        this.price = BigDecimal.valueOf(price);
        this.displayed = displayed;
        this.menuGroup = menuGroup;
        this.menuProducts = Arrays.asList(menuProducts);

        this.menuGroupId = menuGroup.getId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long price() {
        return price.longValue();
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public MenuGroup getMenuGroup() {
        return menuGroup;
    }

    public void setMenuGroup(final MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(final boolean displayed) {
        this.displayed = displayed;
    }

    public List<MenuProductRequest> getMenuProducts() {
        return menuProducts;
    }

    public void setMenuProducts(final List<MenuProductRequest> menuProducts) {
        this.menuProducts = menuProducts;
    }

    public UUID getMenuGroupId() {
        return menuGroupId;
    }

    public void setMenuGroupId(final UUID menuGroupId) {
        this.menuGroupId = menuGroupId;
    }
}
