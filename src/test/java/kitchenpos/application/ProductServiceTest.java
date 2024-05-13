package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @ParameterizedTest
    @DisplayName("상품의 가격은 null 이나 음수일 수 없다")
    @MethodSource("nullAndNegativePrice")
    void nullAndNegativePrice(BigDecimal price) {
        Product request = new Product();
        request.setPrice(price);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.create(request));
    }

    @ParameterizedTest
    @DisplayName("상품의 이름은 null 일 수 없다")
    @NullSource
    void nullName(String name) {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);
        request.setName(name);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.create(request));
    }

    @Test
    @DisplayName("상품의 이름은 비속어 일 수 없다")
    void profanityName() {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);
        request.setName("비속어");

        when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.create(request));
    }

    @Test
    @DisplayName("상품 등록 정상 케이스")
    void create() {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);
        request.setName("상품 이름");

        when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);
        when(productRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));
        Product savedProduct = service.create(request);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(savedProduct.getName()).isEqualTo("상품 이름");
    }

    @ParameterizedTest
    @DisplayName("상품 가격 수정시에 가격은 null 이나 음수 일 수 없다")
    @MethodSource("nullAndNegativePrice")
    void cannotChangeNullAndNegativePrice(BigDecimal price) {
        Product request = new Product();
        request.setPrice(price);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.changePrice(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("없는 상품")
    void notExistsProduct() {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);

        assertThatThrownBy(() -> service.changePrice(UUID.randomUUID(), request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("가격 정상 수정 메뉴 비노출")
    void succeedChangePrice() {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);

        Product product = new Product();
        product.setPrice(BigDecimal.TEN);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.TEN);
        menu.setMenuProducts(List.of(menuProduct));

        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

        Product changedProduct = service.changePrice(UUID.randomUUID(), request);

        assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("가격 정상 수정 메뉴 노출")
    void succeedChangePrice2() {
        Product request = new Product();
        request.setPrice(BigDecimal.TEN);

        Product product = new Product();
        product.setPrice(BigDecimal.TEN);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.TEN);
        menu.setMenuProducts(List.of(menuProduct));

        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

        Product changedProduct = service.changePrice(UUID.randomUUID(), request);

        assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    void findAll() {
        when(productRepository.findAll()).thenReturn(List.of(new Product()));

        assertThat(service.findAll()).hasSize(1);
    }

    static Stream<Arguments> nullAndNegativePrice() {
        return Stream.of(
                null,
                Arguments.of(BigDecimal.valueOf(-1))
        );
    }
}
