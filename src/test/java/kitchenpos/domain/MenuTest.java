package kitchenpos.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;

public class MenuTest {

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 이름이 없는 경우 Exception Throw")
    void nameFailTest(final String input) {
        menu.setName(input);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("메뉴 가격이 없는 경우 Exception Throw")
    void priceFailTest(final BigDecimal input) {
        menu.setPrice(input);
    }
}
