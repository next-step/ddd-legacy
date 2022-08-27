package stringcalculator.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stringcalculator.factory.splitter.CustomSplitter;
import stringcalculator.factory.splitter.DefaultSplitter;

import static org.assertj.core.api.Assertions.assertThat;

class SplitterFactoryTest {

    @DisplayName("type 이 false 이면 DefaultSplitter 를 반환한다.")
    @Test
    void custom_splitter() {
        assertThat(SplitterFactory.findSplitter(false)).isInstanceOf(DefaultSplitter.class);
    }

    @DisplayName("type 이 true 이면 CustomSplitter 를 반환한다.")
    @Test
    void default_splitter() {
        assertThat(SplitterFactory.findSplitter(true)).isInstanceOf(CustomSplitter.class);
    }
}
