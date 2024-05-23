package kitchenpos.support.util.assertion;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertUtils {

    public static void 가격이_동등한가(BigDecimal target, BigDecimal source) {
        assertThat(target.compareTo(source)).isEqualTo(0);
    }

}
