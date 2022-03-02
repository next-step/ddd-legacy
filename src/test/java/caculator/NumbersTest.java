package caculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import caculator.domain.Numbers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumbersTest {

    @DisplayName("객체 생성")
    @Test
    void construct() {
        //given
        String[] stringNumbers = new String[]{"1", "2", "3"};

        //when
        Numbers actual = Numbers.from(stringNumbers);

        //then
        assertThat(actual).isEqualTo(Numbers.from(stringNumbers));
    }
}
