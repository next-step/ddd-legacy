package kitchenpos.product.application;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import kitchenpos.product.dto.request.ChangePriceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ChangePriceServiceTest {

    @Autowired
    private ChangePriceService changePriceService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.ONE)));
        changePriceService = new ChangePriceService(productRepository, menuRepository);
    }

    @DisplayName("상품 가격을 0원보다 작은 가격으로 변경 할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    void negative(BigDecimal price) {
        ChangePriceRequest request = new ChangePriceRequest(price);
        assertThatThrownBy(() -> changePriceService.changePrice(product.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("상품가격 변경 시 상품 가격을 필수로 입력받는다.")
    @Test
    void changePrice() {
        ChangePriceRequest request = new ChangePriceRequest(null);
        assertThatThrownBy(() -> changePriceService.changePrice(product.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }
}
