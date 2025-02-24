package helper;

import java.math.BigDecimal;

public class PriceGenerator {
    public static BigDecimal of(long value){
        return BigDecimal.valueOf(value);
    }
    //큰 금액
    public static BigDecimal biggerThan(long value){
        return BigDecimal.valueOf(value+1);
    }

    //작은 금액
    public static BigDecimal smallerThan(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("0 이하의 값은 유효하지 않습니다. value: " + value);
        }
        return BigDecimal.valueOf(value - 1);
    }
}
