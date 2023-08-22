package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.test_fixture.ProductTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductService 클래스")
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService sut;

    @DisplayName("새로운 상품을 등록할 수 있다.")
    @Test
    void create() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .getProduct();

        // when
        Product result = sut.create(product);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 상품");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @DisplayName("새로운 상품을 등록할 때 가격이 null이면 예외가 발생한다.")
    @Test
    void createWithNullPrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changePrice(null)
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 상품을 등록할 때 가격이 0보다 작으면 예외가 발생한다.")
    @Test
    void createWithNegativePrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changePrice(BigDecimal.valueOf(-1))
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 상품을 등록할 때 이름이 null이면 예외가 발생한다.")
    @Test
    void createWithNullName() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName(null)
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }
}