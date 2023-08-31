package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.test_fixture.ProductTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static kitchenpos.acceptance.acceptance_step.MenuGroupStep.메뉴_그룹_등록된_상태다;
import static kitchenpos.acceptance.acceptance_step.MenuStep.상품_가격보다_메뉴_가격이_높아져서_메뉴가_숨김_상태로_변경됐다;
import static kitchenpos.acceptance.acceptance_step.MenuStep.메뉴가_등록된_상태다;
import static kitchenpos.acceptance.acceptance_step.ProductStep.*;

@DisplayName("상품 인수 테스트")
class ProductAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품을 등록한다.")
    @Test
    void createProduct() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName("상품1")
                .changePrice(BigDecimal.valueOf(10000L))
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품을_등록한다(product);

        // then
        상품이_등록됐다(response, "상품1", BigDecimal.valueOf(10000L));
    }

    @DisplayName("상품의 가격을 0원 미만의 음수로 등록하는 경우 상품 등록에 실패한다.")
    @Test
    void createProductWithPriceUnderZero() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName("상품1")
                .changePrice(BigDecimal.valueOf(-1L))
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품을_등록한다(product);

        // then
        상품_등록에_실패한다(response);
    }

    @DisplayName("등록하는 상품의 가격 정보가 없는 경우 상품 생성에 실패한다.")
    @Test
    void createProductWithoutPrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName("상품1")
                .changePrice(null)
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품을_등록한다(product);

        // then
        상품_등록에_실패한다(response);
    }

    @DisplayName("등록하는 상품의 이름 정보가 없는 경우 상품 생성에 실패한다.")
    @Test
    void createProductWithoutName() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName(null)
                .changePrice(BigDecimal.valueOf(10000L))
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품을_등록한다(product);

        // then
        상품_등록에_실패한다(response);
    }

    @DisplayName("등록하는 상품의 이름에 비속어가 포함된 경우 상품 생성에 실패한다.")
    @Test
    void createProductWithProfanityName() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName("bastard")
                .changePrice(BigDecimal.valueOf(10000L))
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품을_등록한다(product);

        // then
        상품_등록에_실패한다(response);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changeProductPrice() {
        // given
        Product 등록된_상품 = 상품이_등록된_상태다();
        Product 가격_변경_정보 = ProductTestFixture.create()
                .changePrice(BigDecimal.valueOf(10000L))
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품의_가격을_변경한다(등록된_상품.getId(), 가격_변경_정보);

        // then
        상품_가격이_변경됐다(response, 등록된_상품.getId(), "상품1", BigDecimal.valueOf(10000L));
    }

    @DisplayName("상품의 가격을 0원 미만의 음수로 변경하려는 경우 상품 가격 변경에 실패한다.")
    @Test
    void changeProductPriceWithPriceUnderZero() {
        // given
        Product 등록된_상품 = 상품이_등록된_상태다();
        Product 가격_변경_정보 = ProductTestFixture.create()
                .changePrice(BigDecimal.valueOf(-1L))
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품의_가격을_변경한다(등록된_상품.getId(), 가격_변경_정보);

        // then
        상품_가격변경에_실패한다(response);
    }

    @DisplayName("변경하려는 가격 정보가 없는 경우 상품 가격 변경에 실패한다.")
    @Test
    void changeProductPriceWithoutPrice() {
        // given
        Product 등록된_상품 = 상품이_등록된_상태다();
        Product 가격_변경_정보 = ProductTestFixture.create()
                .changePrice(null)
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품의_가격을_변경한다(등록된_상품.getId(), 가격_변경_정보);

        // then
        상품_가격변경에_실패한다(response);
    }

    @DisplayName("상품의 가격을 변경할 때 변경된 가격보다 높은 가격의 메뉴가 존재하는 경우 해당 메뉴는 숭김 상태가 된다.")
    @Test
    void changeProductPriceWithMenuPriceHigherThanChangedPrice() {
        // given
        Product 등록된_상품 = 상품이_등록된_상태다();
        MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
        메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
        Product 가격_변경_정보 = ProductTestFixture.create()
                .changePrice(등록된_상품.getPrice().subtract(BigDecimal.valueOf(1)))
                .getProduct();

        // when
        ExtractableResponse<Response> response = 상품의_가격을_변경한다(등록된_상품.getId(), 가격_변경_정보);

        // then
        상품_가격이_변경됐다(response, 등록된_상품.getId(), "상품1", 등록된_상품.getPrice().subtract(BigDecimal.valueOf(1)));
        상품_가격보다_메뉴_가격이_높아져서_메뉴가_숨김_상태로_변경됐다();
    }

    @DisplayName("등록된 전체 상품을 조회한다.")
    @Test
    void getProducts() {
        // given
        상품이_등록된_상태다();
        상품이_등록된_상태다();
        상품이_등록된_상태다();

        // when
        ExtractableResponse<Response> response = 등록된_전체_상품을_조회한다();

        // then
        등록된_전체_상품_정보_조회됐다(response, 3);
    }
}
