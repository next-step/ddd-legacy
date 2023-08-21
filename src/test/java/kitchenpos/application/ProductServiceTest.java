package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);

    }

    //이거나 0보다 작으면
    @DisplayName("상품 등록할때, 가격이 null이면 오류가 발생한다.")
    @ParameterizedTest
    @NullSource
    void priceIsNull(BigDecimal price) {
        Product product = new Product(UUID.randomUUID(), "김밥", price);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
        then(purgomalumClient).should(times(0)).containsProfanity(any());
        then(productRepository).should(times(0)).save(any());
    }

    @DisplayName("상품 등록할때, 가격이 0보다 작으면 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1, -99, -100000})
    void priceUnderZero(long price) {
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(price));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
        then(purgomalumClient).should(times(0)).containsProfanity(any());
        then(productRepository).should(times(0)).save(any());
    }

    @DisplayName("상품 등록할때, 이름이 null 이면 오류가 발생한다.")
    @Test
    void nameNull() {
        Product product = new Product(UUID.randomUUID(), null, BigDecimal.valueOf(12000));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
        then(purgomalumClient).should(times(0)).containsProfanity(any());
        then(productRepository).should(times(0)).save(any());
    }

    @DisplayName("상품 등록할때, 이름에 비속어가 포함되어 있다면 오류가 발생한다.")
    @Test
    void purgomalTrue() {
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);
        //when
        //then
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(12000));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
        then(purgomalumClient).should(times(1)).containsProfanity(any());
        then(productRepository).should(times(0)).save(any());
    }

    @DisplayName("상품 등록할때, 가격이 0보다 크고 이름에 비속어가 없으면 정상 등록")
    @Test
    void normal() {
        //given
        Product 돈가스 = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(12000));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(productRepository.save(any())).willReturn(돈가스);

        //when
        Product input = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(12000));
        Product output = productService.create(input);

        //then
        assertThat(output).isEqualTo(돈가스);
        then(purgomalumClient).should(times(1)).containsProfanity(any());
        then(productRepository).should(times(1)).save(any());
    }

    @DisplayName("상품 수정할떄 상품 가격이 null이면 오류가 발생한다.")
    @ParameterizedTest
    @NullSource
    void changePrice_priceIsNull(BigDecimal price) {
        Product product = new Product(UUID.randomUUID(), "김밥", price);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
        then(productRepository).should(times(0)).findById(any());
        then(menuRepository).should(times(0)).findAllByProductId(any());
    }

    @DisplayName("상품 수정할떄 상품 가격이 0보다 작으면 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1, -99, -100000})
    void updatePrice_priceUnderZero(long price) {
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(price));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
        then(productRepository).should(times(0)).findById(any());
        then(menuRepository).should(times(0)).findAllByProductId(any());
    }

    @DisplayName("등록되지 않은 상품의 가격을 수정할때, 오류가 발생한다.")
    @Test
    void notFoundProduct() {
        //given
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(null));

        //when
        //then
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(45000));
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
        then(productRepository).should(times(1)).findById(any());
        then(menuRepository).should(times(0)).findAllByProductId(any());
    }

    /**
     * 상품으로 등록된 메뉴가 없을경우
     * 등록된 메뉴의 가격이 > 변경된 상품가격*메뉴상품 인 경우
     * 등록된 메뉴의 가격 <= 변경된 상품가격*메뉴상품 인 경우
     */
    @DisplayName("메뉴가 등록된 상품을 수정할때, 해당 상품으로 등록된 메뉴가 없는경우 상품가격만 변경된다.")
    @Test
    void isNotExistsMenu() {
        //given
        UUID 돈가스_id = UUID.randomUUID();
        Product 돈가스 = new Product(돈가스_id, "돈가스", BigDecimal.valueOf(12000));
        Product 돈가스_가격변경 = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(10000));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(돈가스));
        given(menuRepository.findAllByProductId(any())).willReturn(new ArrayList<>());

        //when
        Product returnProduct = productService.changePrice(돈가스_id, 돈가스_가격변경);

        //then
        assertThat(returnProduct.getPrice()).isSameAs(돈가스_가격변경.getPrice());
        then(productRepository).should(times(1)).findById(돈가스_id);
        then(menuRepository).should(times(1)).findAllByProductId(돈가스_id);
    }

    @DisplayName("메뉴가 등록된 상품을 수정할때,"
        + " 해당 상품으로 등록된 메뉴가격이 > (변경되상품가격 * 메뉴상품갯수) 이면 "
        + " 메뉴는 (화면에) 표시 되지 않는다.")
    @Test
    void isExistsMenuCase1() {
        //given
        UUID 돈가스_id = UUID.randomUUID();
        Product 돈가스 = new Product(돈가스_id, "돈가스", BigDecimal.valueOf(12000));

        MenuProduct 돈가스_단품 = new MenuProduct(돈가스, 10);
        MenuProduct 돈가스_세트 = new MenuProduct(돈가스, 5);

        Menu 돈가스_단품_메뉴 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(120000), new MenuGroup(), true);
        돈가스_단품_메뉴.addMMenuProduct(돈가스_단품);

        Menu 돈가스_세트_메뉴 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(60000), new MenuGroup(), true);
        돈가스_세트_메뉴.addMMenuProduct(돈가스_세트);
        
        given(productRepository.findById(돈가스_id)).willReturn(Optional.ofNullable(돈가스));
        given(menuRepository.findAllByProductId(돈가스_id)).willReturn(List.of(돈가스_단품_메뉴, 돈가스_세트_메뉴));

        //when
        Product 돈가스_가격변경 = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(11999));
        productService.changePrice(돈가스_id, 돈가스_가격변경);

        //then
        assertThat(돈가스_단품_메뉴.isDisplayed()).isFalse();
        assertThat(돈가스_세트_메뉴.isDisplayed()).isFalse();
        then(productRepository).should(times(1)).findById(돈가스_id);
        then(menuRepository).should(times(1)).findAllByProductId(돈가스_id);
    }

    @DisplayName("메뉴가 등록된 상품을 수정할때,"
        + " 해당 상품으로 등록된 메뉴가격이 <= (변경되상품가격 * 메뉴상품갯수) 이면 "
        + " 메뉴는  (화면에) 표시된다.")
    @Test
    void isExistsMenuCase2() {
        //given
        UUID 돈가스_id = UUID.randomUUID();
        Product 돈가스 = new Product(돈가스_id, "돈가스", BigDecimal.valueOf(12000));

        MenuProduct 돈가스_단품 = new MenuProduct(돈가스, 10);
        MenuProduct 돈가스_세트 = new MenuProduct(돈가스, 5);

        Menu 돈가스_단품_메뉴 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(120000), new MenuGroup(), true);
        돈가스_단품_메뉴.addMMenuProduct(돈가스_단품);

        Menu 돈가스_세트_메뉴 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(60000), new MenuGroup(), true);
        돈가스_세트_메뉴.addMMenuProduct(돈가스_세트);

        given(productRepository.findById(돈가스_id)).willReturn(Optional.ofNullable(돈가스));
        given(menuRepository.findAllByProductId(돈가스_id)).willReturn(List.of(돈가스_단품_메뉴, 돈가스_세트_메뉴));

        //when
        Product 돈가스_가격변경 = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(12001));
        productService.changePrice(돈가스_id, 돈가스_가격변경);

        //then
        assertThat(돈가스_단품_메뉴.isDisplayed()).isTrue();
        assertThat(돈가스_세트_메뉴.isDisplayed()).isTrue();

        then(productRepository).should(times(1)).findById(돈가스_id);
        then(menuRepository).should(times(1)).findAllByProductId(any());
    }
}