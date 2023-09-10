package studytest;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hibernate.type.DoubleType.ZERO;

public class BigDecimalTest {

    @Test
    void compareTo() {
        System.out.println(BigDecimal.valueOf(1L).compareTo(BigDecimal.ZERO)); // 1
        System.out.println(BigDecimal.valueOf(ZERO).compareTo(BigDecimal.ZERO)); // 0
        System.out.println(BigDecimal.valueOf(-1L).compareTo(BigDecimal.ZERO)); // -1
    }

}
