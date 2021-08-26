package kitchenpos.ui.dto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuRequest {

    private UUID id;
    private String name;
    private BigDecimal price;
    private MenuGroupRequest menuGroup;
    private boolean displayed;
    private List<MenuProductRequest> menuProducts;

    private UUID menuGroupId;

    public MenuRequest() {
    }

    public MenuRequest(final long price) {
        this.price = BigDecimal.valueOf(price);
    }

    public MenuRequest(final Menu menu) {
        this(menu.getName(), menu.getPrice(), menu.isDisplayed(), menu.getMenuGroup(),
            menu.getMenuProducts());
    }

    public MenuRequest(final String name, final long price, final boolean displayed,
        final MenuGroup menuGroup, final MenuProduct... menuProducts) {
        this(name, BigDecimal.valueOf(price), displayed, menuGroup, Arrays.asList(menuProducts));
    }

    public MenuRequest(final String name, final BigDecimal price, final boolean displayed,
        final MenuGroup menuGroup, final List<MenuProduct> menuProducts) {
        this.name = name;
        this.price = price;
        this.displayed = displayed;
        this.menuGroup = new MenuGroupRequest(menuGroup);
        this.menuProducts = menuProducts.stream()
            .map(MenuProductRequest::new)
            .collect(Collectors.toList());

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

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public long price() {
        return price.longValue();
    }

    public MenuGroupRequest getMenuGroup() {
        return menuGroup;
    }

    public void setMenuGroup(final MenuGroupRequest menuGroup) {
        this.menuGroup = menuGroup;
    }

    public void setMenuGroup(final MenuGroup menuGroup) {
        this.menuGroup = new MenuGroupRequest(menuGroup);
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

    public void setMenuProducts(final MenuProduct... menuProducts) {
        this.menuProducts = Arrays.stream(menuProducts)
            .map(MenuProductRequest::new)
            .collect(Collectors.toList());
    }

    public UUID getMenuGroupId() {
        return menuGroupId;
    }

    public void setMenuGroupId(final UUID menuGroupId) {
        this.menuGroupId = menuGroupId;
    }
}
