package kitchenpos.menu;

import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static kitchenpos.menu.MenuSteps.*;
import static kitchenpos.menu.MenuSteps.메뉴목록_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴")
class MenuAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    void init() {
        메뉴그룹_메뉴상품_생성();
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void create() {
        var 후라이드후라이드 = 메뉴_생성_요청(후라이드_후라이드_메뉴_생성());

        var 메뉴목록 = 메뉴목록_조회_요청();

        assertAll(
                () -> assertThat(후라이드후라이드.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(메뉴목록.jsonPath().getList("name")).containsExactly("후라이드+후라이드")
        );
    }

    @DisplayName("메뉴 상품이 없으면 메뉴를 생성할 수 없다.")
    @Test
    void createWithNullMenuProduct() {
        Menu 메뉴 = 상품이_없는_메뉴_성성();

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴 상품의 개수가 음수인 메뉴를 생성할 수 없다.")
    @Test
    void createWithNegativeQuantity() {
        Menu 메뉴 = 상품_개수가_음수인_메뉴_생성();

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 가격(메뉴 상품의 개수 * 상품의 가격)의 합보다 크면 안된다.")
    @Test
    void createWithHigherPriceThanMenuProduct() {
        Menu 메뉴 = 메뉴가격이_상품_가격_합보다_큰_메뉴_생성();

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("이름이 없는 메뉴는 생성할 수 없다.")
    @Test
    void createWithNullName() {
        Menu 메뉴 = 이름이_없는_메뉴_생성();

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴의 가격을 변경한다.")
    @Test
    void change() {
        var 후라이드후라이드 = 메뉴_생성_요청(후라이드_후라이드_메뉴_생성());

        var response = 메뉴가격_변경_요청(후라이드후라이드.header("Location"), 20000);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴목록_조회_요청().jsonPath().getList("price", BigDecimal.class))
                        .containsExactly(BigDecimal.valueOf(20000.0))
        );
    }

    @DisplayName("메뉴의 가격을 0원 미만으로 변경할 수 없다.")
    @Test
    void changeWithNegative() {
        var 후라이드후라이드 = 메뉴_생성_요청(후라이드_후라이드_메뉴_생성());

        var response = 메뉴가격_변경_요청(후라이드후라이드.header("Location"), -1);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴의 가격을 포함된 각 메뉴 상품의 가격보다 작도록 변경할 수 없다.")
    @Test
    void changeWithHigherPriceThanMenuProduct() {
        var 후라이드후라이드 = 메뉴_생성_요청(후라이드_후라이드_메뉴_생성());

        var response = 메뉴가격_변경_요청(후라이드후라이드.header("Location"), 34001);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴를 전시한다.")
    @Test
    void display() {
        Menu 메뉴 = 보이지_않는_메뉴_생성();
        var 후라이드후라이드 = 메뉴_생성_요청(메뉴);

        var response = 메뉴_전시_요청(후라이드후라이드.header("Location"));

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴목록_조회_요청().jsonPath().getList("displayed")).containsExactly(true)
        );
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hide() {
        var 후라이드후라이드 = 메뉴_생성_요청(후라이드_후라이드_메뉴_생성());

        var response = 메뉴_숨김_요청(후라이드후라이드.header("Location"));

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴목록_조회_요청().jsonPath().getList("displayed")).containsExactly(false)
        );
    }

    @DisplayName("메뉴 목록을 조회한다.")
    @Test
    void findAll() {
        메뉴_생성_요청(후라이드_후라이드_메뉴_생성());
        메뉴_생성_요청(양념_양념_메뉴_생성());
        메뉴_생성_요청(양념_후라이드_메뉴_생성());

        var 메뉴목록 = 메뉴목록_조회_요청();

        assertThat(메뉴목록.jsonPath().getList("name"))
                .containsExactly("후라이드+후라이드", "양념+양념", "양념+후라이드");
    }
}
