package kitchenpos.menu.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.menugroup.fixture.MenuGroupFixture.C_메뉴그룹;
import static kitchenpos.product.fixture.ProductFixture.H_제품;
import static kitchenpos.product.fixture.ProductFixture.I_제품;
import static kitchenpos.product.fixture.ProductFixture.uuid가_존재하는_가격이_마이너스인_제품;
import static kitchenpos.support.util.random.RandomPriceUtil.랜덤한_1000원이상_3000원이하의_금액을_생성한다;
import static kitchenpos.support.util.random.RandomQuantityUtil.랜덤한_5개_이하의_수량을_생성한다;
import static kitchenpos.support.util.random.RandomQuantityUtil.랜덤한_마이너스_5개_이하의_수량을_생성한다;


public class MenuFixture {

    private static final MenuProduct H_메뉴제품 = 제품을_생성한다(1L, H_제품, 랜덤한_5개_이하의_수량을_생성한다());
    private static final MenuProduct I_메뉴제품 = 제품을_생성한다(2L, I_제품, 랜덤한_5개_이하의_수량을_생성한다());
    private static final List<MenuProduct> 제품_목록 = List.of(H_메뉴제품, I_메뉴제품);
    private static final MenuProduct 가격이_마이너스인_제품 = 제품을_생성한다(1L, uuid가_존재하는_가격이_마이너스인_제품, 랜덤한_5개_이하의_수량을_생성한다());
    public static final List<MenuProduct> 가격이_마이너스인_제품_목록 = List.of(가격이_마이너스인_제품);
    private static final MenuProduct 마이너스수량_메뉴제품 = 제품을_생성한다(2L, H_제품, 랜덤한_마이너스_5개_이하의_수량을_생성한다());
    private static final List<MenuProduct> 마이너스_수량의_제품이_포함된_제품목록 = List.of(마이너스수량_메뉴제품, I_메뉴제품);

    public static final Menu A_메뉴 = 메뉴를_생성한다(
                        "A",
                        제품_가격합계보다_낮은_금액을_생성한다(제품_목록),
                        C_메뉴그룹,
                        true,
                        제품_목록
    );

    public static final Menu 가격미존재_메뉴 = 메뉴를_생성한다(
                        "B",
                        null,
                        C_메뉴그룹,
                        true,
                        제품_목록
    );

    public static final Menu 가격마이너스_메뉴 = 메뉴를_생성한다(
                        "C",
                        제품_가격합계보다_낮은_금액을_생성한다(가격이_마이너스인_제품_목록),
                        C_메뉴그룹,
                        true,
                        가격이_마이너스인_제품_목록
    );

    public static final Menu 메뉴가격이_제품목록의_가격합계보다_높은메뉴 = 메뉴를_생성한다(
                        "D",
                        제품_가격합계보다_높은_금액을_생성한다(제품_목록),
                        C_메뉴그룹,
                        false,
                        제품_목록
    );

    public static final Menu 제품목록미존재_메뉴 = 메뉴를_생성한다(
                        "E",
                        랜덤한_1000원이상_3000원이하의_금액을_생성한다(),
                        C_메뉴그룹,
                        true,
                        null
    );

    public static final Menu 빈_제품목록_메뉴 = 메뉴를_생성한다(
                        "F",
                        랜덤한_1000원이상_3000원이하의_금액을_생성한다(),
                        C_메뉴그룹,
                        true,
                        Collections.emptyList()
    );

    public static final Menu 마이너스_수량의_제품을_가진_메뉴 = 메뉴를_생성한다(
                        "G",
                        랜덤한_1000원이상_3000원이하의_금액을_생성한다(),
                        C_메뉴그룹,
                        true,
                        마이너스_수량의_제품이_포함된_제품목록
    );

    public static final Menu 이름미존재_메뉴 = 메뉴를_생성한다(
                        null,
                        제품_가격합계보다_낮은_금액을_생성한다(제품_목록),
                        C_메뉴그룹,
                        true,
                        제품_목록
    );

    public static final Menu 숨김처리_되어있는_메뉴 = 메뉴를_생성한다(
            "H",
            제품_가격합계보다_낮은_금액을_생성한다(제품_목록),
            C_메뉴그룹,
            false,
            제품_목록
    );

    public static final Menu 숨김해제처리_되어있는_메뉴 = 메뉴를_생성한다(
            "I",
            제품_가격합계보다_낮은_금액을_생성한다(제품_목록),
            C_메뉴그룹,
            true,
            제품_목록
    );

    public static Menu 메뉴를_생성한다(
            String name,
            BigDecimal price,
            MenuGroup menuGroup,
            boolean displayed,
            List<MenuProduct> products) {
        var 메뉴 = new Menu();
        메뉴.setName(name);
        메뉴.setPrice(price);
        메뉴.setMenuGroup(menuGroup);
        메뉴.setMenuGroupId(menuGroup.getId());
        메뉴.setDisplayed(displayed);
        메뉴.setMenuProducts(products);

        return 메뉴;
    }

    public static MenuProduct 제품을_생성한다(Long seq, Product product, Long quantity) {
        var 제품 = new MenuProduct();
        제품.setSeq(seq);
        제품.setProduct(product);
        제품.setProductId(product.getId());
        제품.setQuantity(quantity);

        return 제품;
    }

    private static BigDecimal 제품_가격합계보다_높은_금액을_생성한다(List<MenuProduct> products) {
        return 제품들의_합계를_구한다(products).add(new BigDecimal(200));
    }

    public static BigDecimal 제품_가격합계보다_낮은_금액을_생성한다(List<MenuProduct> products) {
        return 제품들의_합계를_구한다(products).subtract(new BigDecimal(100));
    }

    private static BigDecimal 제품들의_합계를_구한다(List<MenuProduct> products) {
        var sum = BigDecimal.ZERO;
        for (var product : products) {
            var quantity = product.getQuantity();
            var price = product.getProduct().getPrice();
            sum = sum.add(
                    price.multiply(BigDecimal.valueOf(quantity))
            );
        }

        return sum;
    }

}
