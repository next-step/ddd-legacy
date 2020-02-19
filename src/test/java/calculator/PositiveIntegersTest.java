package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

public class PositiveIntegersTest {

    private static PositiveIntegers positiveIntegers = new PositiveIntegers(new ArrayList<>());

    @BeforeAll
    static void init() {
        for (int i = 1; i < 5; ++i) {
            positiveIntegers.add(new PositiveInteger(i));
        }
    }

    @DisplayName("입력한 숫자의 합계를 얻을 수 있다.")
    @Test
    void sumTest() {
        System.out.println(positiveIntegers.sum());
        Assertions.assertThat(positiveIntegers.sum()).isEqualTo(10);
    }

    @DisplayName("입력한 숫자의 곱을 얻을 수 있다.")
    @Test
    void multiplyTest() {
        System.out.println(positiveIntegers.multiply());
        Assertions.assertThat(positiveIntegers.multiply()).isEqualTo(1 * 2 * 3 * 4);
    }

    @DisplayName("입력한 값이 없으면, RuntimeException이 발생한다.")
    @Test
    void sumRuntimeException(){
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveIntegers(new ArrayList<>()).sum());
    }

    @DisplayName("입력한 값이 없으면, RuntimeException이 발생한다.")
    @Test
    void multiplyRuntimeException (){
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveIntegers(new ArrayList<>()).multiply());
    }

}
