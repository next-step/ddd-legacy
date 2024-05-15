package kitchenpos.testfixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductTestFixture {

    public static MenuProduct createMenuProductRequest(){
        Product product =ProductTestFixture.createProductRequest();
        return createMenuProductRequest(1L, 1L, product);
    }

    public static MenuProduct createMenuProductRequest(Long seq, long quantity, Product product){
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }
}
