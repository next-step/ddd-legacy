package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("메뉴")
public class MenuAcceptanceTest extends AcceptanceTest {

  private UUID 추천메뉴;
  private UUID 강정치킨;

  private Menu 신메뉴;

  @BeforeEach
  void init() {
    추천메뉴 = MenuGroupSteps.createMenuGroup("추천메뉴").jsonPath().getUUID("id");

    강정치킨 = ProductSteps.createProduct("강정치킨", 17000).jsonPath().getUUID("id");

    신메뉴 = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, List.of(new MenuProduct(2, 강정치킨)), 추천메뉴);
  }

  @DisplayName("메뉴 등록")
  @Test
  void createMenu() {
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> result = MenuSteps.getMenus();

    assertThat(result.jsonPath().getList("name")).containsExactly("후라이드+후라이드");
    assertThat(result.jsonPath().getList("menuGroup.name")).containsExactly("추천메뉴");
  }

  @DisplayName("메뉴 가격 null 등록 에러")
  @Test
  void createMenuPriceNull() {
    신메뉴.setPrice(null);
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 가격 음수 등록 에러")
  @Test
  void createMenuPriceNegative() {
    신메뉴.setPrice(BigDecimal.valueOf(-1));
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 메뉴상품 null 등록 에러")
  @Test
  void createMenuMenuProductNull() {
    신메뉴.setMenuProducts(null);
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 메뉴상품 빈값 등록 에러")
  @Test
  void createMenuMenuProductEmpty() {
    신메뉴.setMenuProducts(List.of());
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴상품 중 수량이 음수이면 에러")
  @Test
  void menuProductQuantityNegative() {
    신메뉴.setMenuProducts(List.of(new MenuProduct(-1, 강정치킨)));
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 가격이 메뉴상품의 총합보다 크면 에러")
  @Test
  void menuPriceMenuProductTotalPriceCompare() {
    신메뉴.setPrice(BigDecimal.valueOf(50000));
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 이름이 null이면 에러")
  @Test
  void menuNameNull() {
    신메뉴.setName(null);
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 가격 변경")
  @Test
  void chageMenuPrice() {
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    MenuSteps.chagePrice(response.jsonPath().getUUID("id"), 20000);
    ExtractableResponse<Response> result = MenuSteps.getMenus();

    assertThat(result.jsonPath().getList("name")).containsExactly("후라이드+후라이드");
    assertThat(result.jsonPath().getList("price")).containsExactly(20000F);
  }

  @DisplayName("메뉴 가격 음수 수정일 경우 에러")
  @Test
  void chageMenuPriceNegative() {
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    ExtractableResponse<Response> result = MenuSteps.chagePrice(response.jsonPath().getUUID("id"), -1);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 가격 메뉴상품의 총합보다 크게 수정일 경우 에러")
  @Test
  void chageMenuPriceMenuProduceTotalCompare() {
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    ExtractableResponse<Response> result = MenuSteps.chagePrice(response.jsonPath().getUUID("id"), 50000);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 노출로 변경")
  @Test
  void chageDisplay() {
    신메뉴.setDisplayed(false);
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.jsonPath().getBoolean("displayed")).isEqualTo(false);

    MenuSteps.chageDisplay(response.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = MenuSteps.getMenus();

    assertThat(result.jsonPath().getList("name")).containsExactly("후라이드+후라이드");
    assertThat(result.jsonPath().getList("displayed")).containsExactly(true);
  }

  @DisplayName("메뉴 숨김으로 변경")
  @Test
  void chageDisplayHide() {
    ExtractableResponse<Response> response = MenuSteps.createMenu(신메뉴);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.jsonPath().getBoolean("displayed")).isEqualTo(true);

    MenuSteps.chageDisplayHide(response.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = MenuSteps.getMenus();

    assertThat(result.jsonPath().getList("name")).containsExactly("후라이드+후라이드");
    assertThat(result.jsonPath().getList("displayed")).containsExactly(false);
  }
}
