package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static kitchenpos.ui.step.MenuGroupStep.메뉴_그룹_생성_요청;
import static kitchenpos.ui.step.MenuStep.*;
import static kitchenpos.ui.step.ProductStep.제품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuRestControllerAcceptanceTest extends Acceptance {

    /**
     * 생성 -> 가격 변경 -> 전시 -> 전시 내리기 -> 조회
     */
    @DisplayName("메뉴를 관리 한다.")
    @Test
    void create() {
        // Arrange
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹 이름");
        ExtractableResponse<Response> createMenuGroupResponse = 메뉴_그룹_생성_요청(menuGroup);
        UUID menuGroupId = createMenuGroupResponse.jsonPath().getUUID("id");

        Product product = new Product();
        product.setName("제품 이름");
        product.setPrice(BigDecimal.TEN);

        UUID productId = 제품_생성_요청(product).jsonPath().getUUID("id");

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(3);
        menuProduct.setProductId(productId);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menu.setName("메뉴 이름");
        menu.setPrice(BigDecimal.TEN);
        menu.setDisplayed(false);

        // Act
        ExtractableResponse<Response> createMenuResponse = 메뉴_생성_요청(menu);
        UUID id = createMenuResponse.jsonPath().getUUID("id");
        // Assert
        메뉴_생성_완료(createMenuResponse, id);

        // Arrange
        Menu changePrice = new Menu();
        changePrice.setPrice(BigDecimal.ONE);

        // Act
        ExtractableResponse<Response> changePriceResponse = 메뉴_가격_변경_요청(id, changePrice);

        // Assert
        메뉴_가격_변경_확인(changePriceResponse, changePrice);

        // Act
        ExtractableResponse<Response> displayResponse = 메뉴_전시하기_요청(id);

        // Assert
        메뉴_전시_확인(displayResponse);

        // Act
        ExtractableResponse<Response> hideResponse = 메뉴_숨기기_요청(id);

        // Assert
        메뉴_숨기기_확인(hideResponse);

        // Act
        ExtractableResponse<Response> findAllResponse = 모든_메뉴_조회_요청();

        // Assert
        모든_메뉴_조회_확인(findAllResponse);
    }

    private void 모든_메뉴_조회_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 메뉴_숨기기_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isFalse()
        );
    }

    private void 메뉴_전시_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue()
        );
    }

    private void 메뉴_가격_변경_확인(ExtractableResponse<Response> response, Menu expected) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("price")).isEqualTo(expected.getPrice().intValue())
        );
    }

    private void 메뉴_생성_완료(ExtractableResponse<Response> response, UUID id) {
        assertAll (
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isEqualTo("/api/menus/" + id),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isFalse(),
                () -> assertThat(response.jsonPath().getInt("price")).isEqualTo(BigDecimal.TEN.intValue())
        );
    }
}
