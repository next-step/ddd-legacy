package kitchenpos.menu.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.product.fixture.ProductFixture;

public class MenuProductFixture {

    public static final MenuProduct 김치찜_1개 = 제품을_생성한다(ProductFixture.김치찜, 1L);

    public static final MenuProduct 공기밥_1개 = 제품을_생성한다(ProductFixture.공기밥, 1L);

    public static final MenuProduct 봉골레_파스타_1개 = 제품을_생성한다(ProductFixture.봉골레_파스타, 1L);

    public static final MenuProduct 수제_마늘빵_3개 = 제품을_생성한다(ProductFixture.수제_마늘빵, 3L);

    public static final MenuProduct 토마토_파스타_1개 = 제품을_생성한다(ProductFixture.토마토_파스타, 1L);

    public static final MenuProduct 피클_1개 = 제품을_생성한다(ProductFixture.피클, 1L);

    public static final MenuProduct 피클_3개 = 제품을_생성한다(ProductFixture.피클, 3L);

    public static final MenuProduct 마이너스수량_메뉴제품 = 제품을_생성한다(ProductFixture.피클, -1L);

    private static MenuProduct 제품을_생성한다(Product product, Long quantity) {
        var 제품 = new MenuProduct();
        제품.setProductId(product.getId());
        제품.setProduct(product);
        제품.setQuantity(quantity);

        return 제품;
    }
}
