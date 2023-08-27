package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService sut;

    private final static UUID uuid = UUID.randomUUID();

    @DisplayName("음식의 가격이 null이면 음식을 생성할 수 없다")
    @Test
    void notCreateProductWithoutPrice() {
        // given
        Product request = createProduct("햄버거", null);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음식의 가격이 0미만이면 음식을 생성할 수 없다")
    @Test
    void notCreateProductWithPriceLessThanZero() {
        // given
        Product request = createProduct(new BigDecimal("-1"));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음식의 이름이 null이면 음식을 생성할 수 없다")
    @Test
    void notCreateProductWithoutName() {
        // given
        Product request = createProduct(null, new BigDecimal("1000"));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음식의 이름에 비속어가 포함되어 있으면 음식을 생성할 수 없다")
    @Test
    void notCreateProductWithNameContainingProfanity() {
        // given
        Product request = createProduct("바보", new BigDecimal("1000"));

        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음식을 생성할 수 있다")
    @Test
    void create() {
        // given
        Product request = createProduct();

        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        given(productRepository.save(any())).willReturn(new Product());

        // when
        Product result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(Product.class);
    }

    @DisplayName("음식의 가격이 null이면 음식의 가격을 수정할 수 없다")
    @Test
    void notChangePriceWithoutPrice() {
        // given
        Product request = createProduct("햄버거", null);

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음식의 가격이 0미만이면 음식의 가격을 수정할 수 없다")
    @Test
    void notChangePriceWithPriceLessThenZero() {
        // given
        Product request = createProduct(new BigDecimal("-1"));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음식의 가격을 수정할 수 있다")
    @Test
    void chanePrice() {
        // given
        Product request = createProduct(new BigDecimal("2000"));

        given(productRepository.findById(any())).willReturn(Optional.of(new Product()));

        // when
        Product result = sut.changePrice(uuid, request);

        // then
        assertThat(result).isExactlyInstanceOf(Product.class);
    }

    @DisplayName("음식의 가격을 수정했을 때 메뉴의 가격이 음식 가격의 합보다 커진다면 메뉴를 숨김 처리한다")
    @Test
    void hideMenuIfPriceOfMenuGreaterThanSumOfProducts() {
        // given
        Product request = createProduct(new BigDecimal("2000"));
        MenuProduct menuProduct = createMenuProduct(request, 1L);
        Menu menu = createMenu(new BigDecimal("3000"), "메뉴", List.of(menuProduct));

        given(productRepository.findById(any())).willReturn(Optional.of(new Product()));
        given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

        // when
        sut.changePrice(UUID.randomUUID(), request);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }
}
