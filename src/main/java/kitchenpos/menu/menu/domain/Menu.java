package kitchenpos.menu.menu.domain;

import kitchenpos.domain.MenuProduct;
import kitchenpos.menu.menugroup.domain.MenuGroup;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(name = "menu")
@Entity
public class Menu {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private Price price;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "menu_group_id",
            columnDefinition = "binary(16)",
            foreignKey = @ForeignKey(name = "fk_menu_to_menu_group")
    )
    private MenuGroup menuGroup;

    @Column(name = "displayed", nullable = false)
    private boolean displayed;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(
            name = "menu_id",
            nullable = false,
            columnDefinition = "binary(16)",
            foreignKey = @ForeignKey(name = "fk_menu_product_to_menu")
    )
    private List<MenuProduct> menuProducts;

    @Transient
    private UUID menuGroupId;

    protected Menu() {
    }

    public Menu(MenuGroup menuGroup, List<MenuProduct> menuProducts, Price price) {
        validateMenuProducts(menuProducts);
        validateMenuGroup(menuGroup);
        validateMenuPrice(price);
        this.price = price;
        this.menuProducts = menuProducts;
        this.menuGroup = menuGroup;
    }

    private void validateMenuPrice(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("메뉴 가격을 입력해주세요.");
        }
    }

    private void validateMenuProducts(List<MenuProduct> menuProducts) {
        if (Objects.isNull(menuProducts) || menuProducts.isEmpty()) {
            throw new IllegalArgumentException("메뉴 상품 목록은 비어 있을 수 없습니다.");
        }
        for (MenuProduct menuProduct : menuProducts) {
            if (BigDecimal.ZERO.compareTo(menuProduct.getQuantity().getQuantity()) > 0) {
                throw new IllegalArgumentException("수량은 0보다 커야합니다.");
            }
        }
    }

    private static void validateMenuGroup(MenuGroup menuGroup) {
        if (menuGroup == null) {
            throw new IllegalArgumentException("메뉴 그룹이 없습니다.");
        }
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
        return this.price.getPrice();
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

    public List<MenuProduct> getMenuProducts() {
        return menuProducts;
    }

    public BigDecimal sumMenuProducts() {
        BigDecimal menuProductsPrice = BigDecimal.ZERO;
        for (MenuProduct menuProduct : this.menuProducts) {
            menuProductsPrice = menuProductsPrice.add(menuProduct.menuProductPrice());
        }
        return menuProductsPrice;
    }

    public void setMenuProducts(final List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
    }

    public UUID getMenuGroupId() {
        return menuGroupId;
    }

    public void setMenuGroupId(final UUID menuGroupId) {
        this.menuGroupId = menuGroupId;
    }

    public void changePrice(Price price) {
        validateMenuPrice(price);
        hideMenu(price);
        if (price == null) {
            throw new IllegalArgumentException("가격을 입력해주세요");
        }
    }

    private void hideMenu(Price price) {
        if (price.getPrice().compareTo(sumMenuProducts()) > 0) {
            hide();
        }
    }

    public void hide() {
        this.displayed = false;
    }

    public void display() {
        if (this.price.getPrice().compareTo(sumMenuProducts()) > 0) {
            throw new IllegalArgumentException("메뉴의 가격이 메뉴 상품의 합보다 클 수 없다.");
        }
        this.displayed = true;
    }
}
