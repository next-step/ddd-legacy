package kitchenpos.menu.menu.domain;

import kitchenpos.common.vo.Name;

import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.MenuFixture;
import kitchenpos.menu.menu.MenuRequestFixture;
import kitchenpos.menu.menugroup.MenuGroupFixture;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.menu.MenuFixture.*;
import static kitchenpos.menu.menugroup.MenuGroupFixture.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("메뉴")
class MenuTest {

    @DisplayName("메뉴 그룹에 속해 있다.")
    @Test
    void 메뉴그룹필수() {
        assertThatThrownBy(() -> menu(null, menuProducts(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 그룹이 없습니다.");
    }

    @DisplayName("메뉴 상품의 수량은 0개보다 작을 수 없다.")
    @Test
    void quantityCount() {
        MenuGroup menuGroup = menuGroup(UUID.randomUUID());
        assertThatThrownBy(() -> menu(menuGroup, 상품수량_음수(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수량은 0보다 커야합니다.");
    }

    @DisplayName("메뉴 가격이 0원보다 작을 수 없다.")
    @Test
    void minimumPrice() {
        assertThatThrownBy(() -> 메뉴가격_음수(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("메뉴 상품 목록은 비어 있을 수 없다.")
    @Test
    void notEmpty() {
        assertThatThrownBy(() -> 메뉴상품_NULL(menuGroup(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 상품 목록은 비어 있을 수 없습니다.");
    }

    @DisplayName("메뉴 생성 시 메뉴 가격을 필수로 입력받는다.")
    @Test
    void menuPrice() {
        assertThatThrownBy(() -> 메뉴가격_NULL(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 가격을 입력해주세요.");
    }

    @DisplayName("메뉴 가격 변경 시 메뉴 가격은 필수로 입력받는다.")
    @Test
    void 메뉴가격필수() {
        Menu menu = menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID()));
        assertThatThrownBy(() -> menu.changePrice(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격을 입력해주세요");
    }

    @DisplayName("메뉴 가격을 변경할 수 있다.")
    @Test
    void 메뉴가격변경() {
        Menu menu = menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID()));
        assertThat(menu.getPrice()).isEqualTo(BigDecimal.ONE);
        menu.changePrice(new Price(BigDecimal.valueOf(20)));
        assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(20));
    }

    @DisplayName("메뉴 가격은 0원보다 크다.")
    @Test
    void 메뉴최소가격() {
        Menu menu = menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID()));
        assertThatThrownBy(() -> menu.changePrice(new Price(BigDecimal.valueOf(-1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void 메뉴숨김() {
        Menu menu = menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID()));
        menu.display();
        assertThat(menu.isDisplayed()).isTrue();
        menu.hide();
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 보여줄 수 있다.")
    @Test
    void 메뉴보임() {
        Menu menu = menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID()));
        menu.hide();
        assertThat(menu.isDisplayed()).isFalse();
        menu.display();
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 합보다 크면 메뉴를 숨긴다.")
    @Test
    void 메뉴상품합보다_메뉴가격이크면_숨김() {
        Menu menu = menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID()));
        menu.display();
        assertThat(menu.isDisplayed()).isTrue();
        assertThat(menu.sumMenuProducts()).isEqualTo(BigDecimal.ONE);
        menu.changePrice(new Price(BigDecimal.valueOf(11)));
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 합보다 클 수 없다.")
    @Test
    void 메뉴가격이_메뉴상품의_합보다_클수없다() {
        Menu menu = 메뉴가격_메뉴상품합보다큼(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID()));
        assertThatThrownBy(() -> menu.display())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격이 메뉴 상품의 합보다 클 수 없다.");
    }

    @DisplayName("상품 가격의 총합은 0원보다 크다.")
    @Test
    void 상품가격총합_음수() {
        assertThatThrownBy(() -> menu(menuGroup(UUID.randomUUID()), 상품가격_총합_0(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 가격의 총합은 0원보다 크다.");
    }

    @DisplayName("주문 생성 시 주문 타입 / 주문 테이블 아이디 / 주문 항목 목록을 입력 받는다.")
    @Test
    void createMenu() {
        assertThatNoException().isThrownBy(() -> menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID())));
    }
}
