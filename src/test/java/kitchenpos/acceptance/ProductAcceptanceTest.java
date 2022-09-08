package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("상품")
public class ProductAcceptanceTest extends AcceptanceTest {

  @DisplayName("상품을 등록한다.")
  @Test
  void createProductTest() {
    ExtractableResponse<Response> response = ProductSteps.createProduct("강정치킨", 17000);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> result = ProductSteps.getProducts();

    assertThat(result.jsonPath().getList("name")).contains("강정치킨");
  }

  @DisplayName("상품 이름 null 등록 에러")
  @Test
  void createProductNameNull() {
    ExtractableResponse<Response> response = ProductSteps.createProduct(null, 17000);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("상품 가격 음수 등록 에러")
  @Test
  void createProductPriceNegative() {
    ExtractableResponse<Response> response = ProductSteps.createProduct("강정치킨", -1);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("상품 가격을 수정한다.")
  @Test
  void updatePrice() {
    UUID 강정치킨 = ProductSteps.createProduct("강정치킨", 17000).jsonPath().getUUID("id");
    ProductSteps.updateProductPrice(강정치킨, 18000);

    ExtractableResponse<Response> result = ProductSteps.getProducts();

    assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.jsonPath().getList("price")).contains(18000F);
  }

  @DisplayName("상품 가격 음수 변경 에러")
  @Test
  void updatePriceNegative() {
    UUID 강정치킨 = ProductSteps.createProduct("강정치킨", 17000).jsonPath().getUUID("id");
    ExtractableResponse<Response> result = ProductSteps.updateProductPrice(강정치킨, -1);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("상품 가격이 변경으로 메뉴상품 가격 총합이 메뉴 가격보다 작으면 노출 숨김 변경")
  @Test
  void productPriceMenuProduceTotalCompare() {
    UUID 강정치킨 = ProductSteps.createProduct("강정치킨", 17000).jsonPath().getUUID("id");
    UUID 추천메뉴 = MenuGroupSteps.createMenuGroup("추천메뉴").jsonPath().getUUID("id");

    Menu 신메뉴 = new Menu();
    신메뉴.setName("후라이드+후라이드");
    신메뉴.setPrice(BigDecimal.valueOf(19000));
    신메뉴.setDisplayed(true);
    신메뉴.setMenuProducts(List.of(new MenuProduct(2, 강정치킨)));
    신메뉴.setMenuGroupId(추천메뉴);

    MenuSteps.createMenu(신메뉴);

    ProductSteps.updateProductPrice(강정치킨, 1000);

    ExtractableResponse<Response> result = MenuSteps.getMenus();

    assertThat(result.jsonPath().getList("displayed")).containsExactly(false);
  }
}
