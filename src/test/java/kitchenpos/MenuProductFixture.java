package kitchenpos;

import helper.SequenceGenerator;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MenuProductFixture {

    private static final SequenceGenerator sequenceGenerator = () -> ThreadLocalRandom.current().nextLong();


    public static List<MenuProduct> 후라이드_치킨_메뉴_구성_상품(Menu menu, Product... product) {
        List<MenuProduct> menuProducts = new ArrayList<>();

        for(Product p : product) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setSeq(sequenceGenerator.random());
            menuProduct.setProduct(p);
            menuProduct.setQuantity(1L);

            menuProducts.add(menuProduct);
        }

        menu.setMenuProducts(menuProducts);

        return menuProducts;
    }
}
