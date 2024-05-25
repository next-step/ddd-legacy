package kitchenpos.product.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductFixture {

    public static final Product 김치찜 = 제품을_생성한다("김치찜", new BigDecimal(24_000));
    public static final Product 공기밥 = 제품을_생성한다("공기밥", new BigDecimal(1_500));
    public static final Product 봉골레_파스타 = 제품을_생성한다("봉골레 파스타", new BigDecimal(12_000));
    public static final Product 수제_마늘빵 = 제품을_생성한다("수제 마늘빵", new BigDecimal(2_000));
    public static final Product 토마토_파스타 = 제품을_생성한다("토마토 파스타", new BigDecimal(11_000));
    public static final Product 피클 = 제품을_생성한다("피클", new BigDecimal(1_000));
    public static final Product 이름미존재_제품 = 제품을_생성한다(null, new BigDecimal(700));
    public static final Product 욕설이름_제품 = 제품을_생성한다("욕설", new BigDecimal(500));
    public static final Product 가격미존재_제품 = 제품을_생성한다("가격이 존재하지 않은 제품", null);
    public static final Product 가격이_마이너스인_제품 = 제품을_생성한다("가격이 마이너스인 제품", new BigDecimal(-500));

    private static Product 제품을_생성한다(String name, BigDecimal price) {
        var 제품 = new Product();
        제품.setName(name);
        제품.setPrice(price);

        return 제품;
    }

}
