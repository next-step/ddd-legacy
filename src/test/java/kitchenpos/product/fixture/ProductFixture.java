package kitchenpos.product.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class ProductFixture {

    public static final Product A_제품 = 제품을_생성하다("A", 랜덤한_0원이상_5만원이하의_금액을_생성하다());
    public static final Product B_제품 = 제품을_생성하다("B", 랜덤한_0원이상_5만원이하의_금액을_생성하다());
    public static final Product 이름미존재_제품 = 제품을_생성하다(null, 랜덤한_0원이상_5만원이하의_금액을_생성하다());
    public static final Product 욕설이름_제품 = 제품을_생성하다("D", 랜덤한_0원이상_5만원이하의_금액을_생성하다());
    public static final Product 가격_0원_제품 = 제품을_생성하다("E", BigDecimal.ZERO);
    public static final Product 가격미존재_제품 = 제품을_생성하다("F", null);
    public static final Product 가격이_마이너스인_제품 = 제품을_생성하다("G", 랜덤한_0원미만_금액을_생성하다());

    private static Product 제품을_생성하다(String name, BigDecimal price) {
        var 제품 = new Product();
        제품.setName(name);
        제품.setPrice(price);

        return 제품;
    }

    private static BigDecimal 랜덤한_0원이상_5만원이하의_금액을_생성하다() {
        var random = new Random();
        var price =  new BigDecimal(random.nextInt(50000));
        price.setScale(1, RoundingMode.HALF_UP);

        return price;
    }

    private static BigDecimal 랜덤한_0원미만_금액을_생성하다() {
        var random = new Random();
        var negative = "-" +  (random.nextInt(50000) + 1);
        var price =  new BigDecimal(negative);
        price.setScale(1, RoundingMode.HALF_UP);

        return price;
    }

}
