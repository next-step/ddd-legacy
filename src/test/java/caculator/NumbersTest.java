package caculator;

import static org.assertj.core.api.Assertions.assertThat;

import caculator.domain.Numbers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumbersTest {

    private final String[] stringNumbers = new String[]{"1", "2", "3"};

    @DisplayName("객체 생성")
    @Test
    void construct() {
        //when
        Numbers actual = Numbers.from(stringNumbers);

        //then
        assertThat(actual).isEqualTo(Numbers.from(stringNumbers));
    }

    @Test
    @DisplayName("숫자 합산")
    void sum() {
        //given
        Numbers numbers = Numbers.from(stringNumbers);

        //when
        int actual = numbers.sum();

        //then
        assertThat(actual).isEqualTo(6);

    }

    @Test
    @DisplayName("빈 숫자 집합의 합산")
    void emptySum() {
        //given
        Numbers numbers = Numbers.EMPTY;

        //when
        int actual = numbers.sum();

        //then
        assertThat(actual).isZero();

    }


}
