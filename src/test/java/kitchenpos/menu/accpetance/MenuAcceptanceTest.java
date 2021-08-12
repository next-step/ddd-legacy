package kitchenpos.menu.accpetance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.menu.fixture.MenuProductSaveRequest;
import kitchenpos.menu.fixture.MenuSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Arrays;

import static kitchenpos.menu.step.MenuStep.createMenuSaveRequest;
import static kitchenpos.menu_group.step.MenuGroupStep.completeCreateMenuGroup;
import static kitchenpos.menu_group.step.MenuGroupStep.createMenuGroupSaveRequest;
import static kitchenpos.product.fixture.ProductionFixture.createProductSaveRequest;
import static kitchenpos.product.step.ProductStep.completeCreateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Menu 인수 테스트")
public class MenuAcceptanceTest extends AcceptanceTest {

    @DisplayName("메뉴를 등록한다")
    @Test
    public void create() {
        // given
        MenuGroup menuGroup = completeCreateMenuGroup(createMenuGroupSaveRequest("두마리메뉴"));
        Product product = completeCreateProduct(createProductSaveRequest("후라이드", 16000));

        MenuProductSaveRequest menuProductSaveRequest = new MenuProductSaveRequest(product.getId(), 1);
        MenuSaveRequest menuSaveRequest = createMenuSaveRequest(
                "후라이드치킨", false, 15000, menuGroup.getId(),
                Arrays.asList(menuProductSaveRequest));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuSaveRequest)
                .when().post("/api/menus")
                .then().log().all().extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.as(Menu.class).getName()).isEqualTo("후라이드치킨"),
                () -> assertThat(response.as(Menu.class).getPrice()).isEqualTo(new BigDecimal(15000)));
    }
}
