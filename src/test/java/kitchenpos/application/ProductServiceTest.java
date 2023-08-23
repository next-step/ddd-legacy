package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.application.MenuServiceTest.createMenu;
import static kitchenpos.application.MenuServiceTest.createMenuProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService sut;

    private final static UUID uuid = UUID.randomUUID();

    @Test
    void 음식의_가격이_null이면_음식을_생성할_수_없다() {
        // given
        Product request = createProduct("햄버거", null);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 음식의_가격이_0미만이면_음식을_생성할_수_없다() {
        // given
        Product request = createProduct("햄버거", new BigDecimal("-1"));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 음식의_이름이_null이면_음식을_생성할_수_없다() {
        // given
        Product request = createProduct(null, new BigDecimal("1000"));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 음식의_이름에_비속어가_포함되어_있으면_음식을_생성할_수_없다() {
        // given
        Product request = createProduct("바보", new BigDecimal("1000"));

        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 음식을_생성할_수_있다() {
        // given
        Product request = createProduct("햄버거", new BigDecimal("1000"));

        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        given(productRepository.save(any())).willReturn(new Product());

        // when
        Product result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(Product.class);
    }

    @Test
    void 음식의_가격이_null이면_음식의_가격을_수정할_수_없다() {
        // given
        Product request = createProduct("햄버거", null);

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 음식의_가격이_0미만이면_음식의_가격을_수정할_수_없다() {
        // given
        Product request = createProduct("햄버거", new BigDecimal("-1"));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 음식이_생성되어_있는_상태가_아니라면_가격을_수정할_수_없다() {
        // given
        Product request = createProduct("햄버거", new BigDecimal("2000"));

        given(productRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 음식의_가격을_수정할_수_있다() {
        // given
        Product request = createProduct("햄버거", new BigDecimal("2000"));

        given(productRepository.findById(any())).willReturn(Optional.of(new Product()));

        // when
        Product result = sut.changePrice(uuid, request);

        // then
        assertThat(result).isExactlyInstanceOf(Product.class);
    }

    @Test
    void 음식의_가격을_수정했을_때_메뉴의_가격이_음식_가격의_합보다_커진다면_메뉴를_숨김_처리한다() {
        // given
        Product request = createProduct("햄버거", new BigDecimal("2000"));
        MenuProduct menuProduct = createMenuProduct(request, 1L);
        Menu menu = createMenu(new BigDecimal("3000"), "메뉴", uuid, List.of(menuProduct));

        given(productRepository.findById(any())).willReturn(Optional.of(new Product()));
        given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

        // when
        sut.changePrice(UUID.randomUUID(), request);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
