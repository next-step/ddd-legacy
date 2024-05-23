package kitchenpos.support.util.random;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RandomPriceUtil {
    

    public static BigDecimal 랜덤한_1000원이상_3000원이하의_금액을_생성한다() {
        return 랜덤_가격을_생성한다(1000, 3000);
    }

    public static BigDecimal 랜덤한_마이너스_1000원이하_금액을_생성한다() {
        var price = 랜덤_가격을_생성한다(1, 1000);
        var minus = new BigDecimal(-1);

        return price.multiply(minus);
    }

    public static BigDecimal 랜덤_가격을_생성한다(int min, int max) {
        var random = Math.random();
        var value = (random * (max - min + 1)) + min;

        return new BigDecimal(value).setScale(0, RoundingMode.HALF_DOWN);
    }

}
