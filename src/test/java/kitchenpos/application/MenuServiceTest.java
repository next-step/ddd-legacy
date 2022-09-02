package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuServiceTest extends InitTest {
    @Resource
    private MenuService target;

    @Test
    @DisplayName("정상생성")
    void create() {
        Menu request = buildValidMenu();

        target.create(request);
    }

    @Test
    @DisplayName("메뉴는 그룹에 속한다.")
    void noInvalidMenuGroup() {
        Menu request = buildValidMenu();
        request.setMenuGroupId(INVALID_ID);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴는 1개 이상의 상품으로 이루어진다.")
    void noEmptyProduct() {
        Menu request = buildValidMenu();
        request.setMenuProducts(new ArrayList<>());

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격은 음수일 수 없다.")
    void noMinusPriceMenu() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.valueOf(-1L));

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("디피된 메뉴의 가격은 구성 상품의 가격의 총합보다 클 수 없다.")
    void menuPriceNotGreaterThanProductPriceSum() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.TEN);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("메뉴의 이름에 비속어를 쓸 수 없다.")
    @ValueSource(strings = {"fuck", "asshole"})
    void noBadName(String badName) {
        Menu request = buildValidMenu();
        request.setName(badName);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격은 변경 가능하다.")
    void changePrice() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.ZERO);

        Menu result = target.changePrice(MENU_ID, request);

        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("디피된 메뉴의 가격은 구성 상품의 가격의 총합보다 클 수 없다.")
    void cannotChangeMenuPriceGreaterThanProductPriceSum() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.TEN);

        assertThatThrownBy(() -> {
            target.changePrice(MENU_ID, request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 디피 여부는 변경 가능하다.")
    void hideDisplay() {
        Menu result = target.hide(MENU_ID);

        assertThat(result.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("디피된 메뉴의 가격은 구성 상품의 가격의 총합보다 클 수 없다.")
    void cannotDisplayMenuPriceGreaterThanProductPriceSum() {
        assertThatThrownBy(() -> {
            target.display(UNDISPLAYED_MENU_ID);
        })
                .isInstanceOf(IllegalStateException.class);
    }
}