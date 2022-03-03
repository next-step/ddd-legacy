package kitchenpos.stub;

import kitchenpos.domain.MenuProduct;

import java.util.ArrayList;
import java.util.List;

import static kitchenpos.stub.ProductStub.generateThousandPriceProduct;
import static kitchenpos.stub.ProductStub.generateTwoThousandPriceProduct;

public class MenuProductStub {

    private MenuProductStub() {
    }

    public static MenuProduct generateThousandPriceOneQuantityMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(generateThousandPriceProduct());
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static MenuProduct generateTwoThousandPriceTwoQuantityMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(generateTwoThousandPriceProduct());
        menuProduct.setQuantity(2L);
        return menuProduct;
    }

    public static List<MenuProduct> generateTestMenuProducts() {
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(generateThousandPriceOneQuantityMenuProduct());
        menuProducts.add(generateTwoThousandPriceTwoQuantityMenuProduct());
        return menuProducts;
    }
}
