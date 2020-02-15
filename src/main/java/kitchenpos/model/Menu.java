package kitchenpos.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Menu {
    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final Long menuGroupId;
    private final List<MenuProduct> menuProducts;

    private Menu (Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.price = builder.price;
        this.menuGroupId = builder.menuGroupId;
        this.menuProducts = builder.menuProducts;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getMenuGroupId() {
        return menuGroupId;
    }

    public List<MenuProduct> getMenuProducts() {
        return menuProducts;
    }

    public void changeMenuProducts (List<MenuProduct> menuProducts){
        menuProducts.clear();

        for(MenuProduct menuProduct : menuProducts){
            menuProducts.add(menuProduct);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return Objects.equals(getId(), menu.getId()) &&
            Objects.equals(getName(), menu.getName()) &&
            Objects.equals(getPrice(), menu.getPrice()) &&
            Objects.equals(getMenuGroupId(), menu.getMenuGroupId()) &&
            Objects.equals(getMenuProducts(), menu.getMenuProducts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPrice(), getMenuGroupId(), getMenuProducts());
    }

    @Override
    public String toString() {
        return "Menu{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", menuGroupId=" + menuGroupId +
            ", menuProducts=" + menuProducts +
            '}';
    }

    public static class Builder{
        private Long id;
        private String name;
        private BigDecimal price;
        private Long menuGroupId;
        private List<MenuProduct> menuProducts;

        public Builder(){}

        public Builder id (Long id){
            this.id = id;
            return this;
        }

        public Builder name (String name){
            this.name = name;
            return this;
        }

        public Builder price (BigDecimal price){
            this.price = price;
            return this;
        }

        public Builder menuGroupId(Long menuGroupId){
            this.menuGroupId = menuGroupId;
            return this;
        }

        public Builder menuProducts (List<MenuProduct> menuProducts){
            this.menuProducts = menuProducts;
            return this;
        }

        public Menu build(){
            return new Menu(this);
        }
    }
}
