package kitchenpos.product.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.support.RandomPriceUtil.랜덤한_1000원이상_3000원이하의_금액을_생성한다;
import static kitchenpos.support.RandomPriceUtil.랜덤한_마이너스_1000원이하_금액을_생성한다;

public class ProductFixture {

    public static final Product A_제품 = 제품을_생성한다("A", 랜덤한_1000원이상_3000원이하의_금액을_생성한다());
    public static final Product B_제품 = 제품을_생성한다("B", 랜덤한_1000원이상_3000원이하의_금액을_생성한다());
    public static final Product 이름미존재_제품 = 제품을_생성한다(null, 랜덤한_1000원이상_3000원이하의_금액을_생성한다());
    public static final Product 욕설이름_제품 = 제품을_생성한다("D", 랜덤한_1000원이상_3000원이하의_금액을_생성한다());
    public static final Product 가격미존재_제품 = 제품을_생성한다("F", null);
    public static final Product 가격이_마이너스인_제품 = 제품을_생성한다("G", 랜덤한_마이너스_1000원이하_금액을_생성한다());
    public static final Product H_제품 = uuid가_존재하는_제품을_생성한다("H", 랜덤한_1000원이상_3000원이하의_금액을_생성한다());
    public static final Product I_제품 = uuid가_존재하는_제품을_생성한다("I", 랜덤한_1000원이상_3000원이하의_금액을_생성한다());
    public static final Product uuid가_존재하는_가격이_마이너스인_제품 = uuid가_존재하는_제품을_생성한다("G", 랜덤한_마이너스_1000원이하_금액을_생성한다());

    private static Product 제품을_생성한다(String name, BigDecimal price) {
        var 제품 = new Product();
        제품.setName(name);
        제품.setPrice(price);

        return 제품;
    }

    private static Product uuid가_존재하는_제품을_생성한다(String name, BigDecimal price) {
        var 제품 = 제품을_생성한다(name, price);
        제품.setId(UUID.randomUUID());

        return 제품;
    }

}
