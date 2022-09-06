package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest extends InitTest {
    @Resource
    private ProductService target;

    @Test
    @DisplayName("상품은 가격과 이름을 가진다.")
    void create() {
        Product request = buildValidProduct();

        target.create(request);
    }

    @Test
    @DisplayName("가격은 음수일 수 없다.")
    void noMinusPrice() {
        Product request = buildValidProduct();
        request.setPrice(BigDecimal.valueOf(-1L));

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("상품의 이름에 비속어를 쓸 수 없다.")
    @ValueSource(strings = {"fuck", "asshole"})
    void noBadName(String badName) {
        Product request = buildValidProduct();
        request.setName(badName);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격은 변경 가능하다.")
    void changePrice() {
        Product request = buildValidProduct();
        request.setPrice(BigDecimal.TEN);

        Product result = target.changePrice(PRODUCT_ID, request);

        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("상품의 가격이 변경되어서 메뉴 가격보다 구성상품 가격의 총합이 작아지면, 해당 메뉴는 디피 해제된다.")
    void changePriceEffectDisplay() {
        Product request = buildValidProduct();
        request.setPrice(BigDecimal.ZERO);

        Menu before = menuRepository.findById(MENU_ID).get();
        assertThat(before.isDisplayed()).isTrue();

        target.changePrice(PRODUCT_ID, request);

        Menu after = menuRepository.findById(MENU_ID).get();
        assertThat(after.isDisplayed()).isFalse();
    }
}
