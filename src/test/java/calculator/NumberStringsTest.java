package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class NumberStringsTest {

    @Test
    @DisplayName("널갑이 들어왔을 때, 빈 리스트를 갖는다")
    void nullCheck() {
        NumberStrings numberStrings = new NumberStrings(null);
        assertThat(numberStrings.getNumbers()).isNotNull();
    }

    @Test
    @DisplayName("isEmpty를 사용하여 내부 리스트가 비어있는지 체크한다")
    void emptyTest() {
        NumberStrings numberStrings = new NumberStrings(new ArrayList<>());
        assertThat(numberStrings.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("isEmpty를 사용하여 내부 리스트가 비어있는지 체크한다")
    void buildTest() {
        NumberStrings numberStrings = new NumberStrings(new ArrayList<>());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("15");
        numberStrings.addIfNotEmpty(stringBuilder);
        assertThat(numberStrings.isEmpty()).isFalse();
        assertThat(numberStrings.getNumbers()).containsExactly("15");
    }
}