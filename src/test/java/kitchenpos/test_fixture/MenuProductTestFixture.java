package kitchenpos.test_fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

public class MenuProductTestFixture {
    private MenuProduct menuProduct;
    
    private MenuProductTestFixture(MenuProduct menuProduct) {
        this.menuProduct = menuProduct;
    }
    
    public static MenuProductTestFixture create() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setProduct(ProductTestFixture.create().getProduct());
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(1);
        return new MenuProductTestFixture(menuProduct);
    }
    
    public MenuProductTestFixture changeSeq(Long seq) {
        MenuProduct newMenuProduct = new MenuProduct();
        newMenuProduct.setSeq(seq);
        newMenuProduct.setProduct(menuProduct.getProduct());
        newMenuProduct.setProductId(menuProduct.getProductId());
        newMenuProduct.setQuantity(menuProduct.getQuantity());
        this.menuProduct = newMenuProduct;
        return this;
    }
    
    public MenuProductTestFixture changeProduct(Product product) {
        MenuProduct newMenuProduct = new MenuProduct();
        newMenuProduct.setSeq(menuProduct.getSeq());
        newMenuProduct.setProduct(product);
        newMenuProduct.setProductId(product.getId());
        newMenuProduct.setQuantity(menuProduct.getQuantity());
        this.menuProduct = newMenuProduct;
        return this;
    }

    public MenuProductTestFixture changeQuantity(long quantity) {
        MenuProduct newMenuProduct = new MenuProduct();
        newMenuProduct.setSeq(menuProduct.getSeq());
        newMenuProduct.setProduct(menuProduct.getProduct());
        newMenuProduct.setProductId(menuProduct.getProductId());
        newMenuProduct.setQuantity(quantity);
        this.menuProduct = newMenuProduct;
        return this;
    }

    public MenuProduct getMenuProduct() {
        return this.menuProduct;
    }
}
