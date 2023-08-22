package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.acceptance.steps.ProductSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("상품")
public class ProductAcceptanceTest extends AcceptanceTest {

    private final static String NAME = "상품";
    private final static BigDecimal PRICE = BigDecimal.valueOf(1000);

    @DisplayName("[성공] 상품 등록")
    @Test
    void createTest1() {
        //when
        ExtractableResponse<Response> response
                = ProductSteps.상품을_생성한다(NAME, PRICE);
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath()
                        .getString("name")).isEqualTo(NAME)
                , () -> assertThat(response.jsonPath()
                        .getObject("price", BigDecimal.class))
                        .isEqualTo(PRICE)
        );

    }

    @DisplayName("[성공] 상품 가격 수정")
    @Test
    void priceChangeTest1() {
        //given
        UUID productId = 상품을_생성한다();

        //when
        BigDecimal changePrice = BigDecimal.valueOf(500);
        ExtractableResponse<Response> response
                = ProductSteps.상품_가격을_바꾼다(productId, BigDecimal.valueOf(500));
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath()
                        .getString("name")).isEqualTo(NAME)
                , () -> assertThat(response.jsonPath()
                        .getObject("price", BigDecimal.class))
                        .isEqualTo(changePrice)
        );

    }


    @DisplayName("[성공] 상품 전체 조회")
    @Test
    void findAllTest1() {
        //given
        UUID productOneId = 상품을_생성한다();
        UUID productTwoId = 상품을_생성한다();

        //when
        ExtractableResponse<Response> response = ProductSteps.상품_전체를_조회한다();
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .hasSize(2)
                        .contains(productOneId, productTwoId)
        );

    }

    private static UUID 상품을_생성한다() {
        ExtractableResponse<Response> created
                = ProductSteps.상품을_생성한다(NAME, PRICE);
        UUID productId = created.response().jsonPath().getObject("id", UUID.class);
        return productId;
    }
}
