package kitchenpos.application.fake;

import kitchenpos.application.ProductService;
import kitchenpos.application.fake.helper.*;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.application.fake.helper.ProductFixtureFactory.미트파이;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


public class FakeProductServiceTest {

    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private ProductService productService;

    @BeforeEach
    void setup() {
        productService = new ProductService(productRepository, menuRepository, profanityClient);
    }


    private static Stream<BigDecimal> providePriceForNullAndNegative() {
        return Stream.of(
                null,
                BigDecimal.valueOf(-1500L)
        );
    }

    @DisplayName("상품등록 - 상품의 가격은 반드시 0보다 큰 값을 가져야 한다.")
    @MethodSource("providePriceForNullAndNegative")
    @ParameterizedTest
    void create01(BigDecimal 등록할_상품_가격) {
        //given
        Product 등록할_상품 = new ProductFixtureFactory.Builder()
                .name("미트파이")
                .price(등록할_상품_가격)
                .build();

        //when & then
        assertThatThrownBy(() -> productService.create(등록할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품은 반드시 이름을 가진다.")
    @Test
    void create02() {
        //given
        Product 등록할_상품_요청 = new ProductFixtureFactory.Builder()
                .name(null)
                .build();
        //when & then
        assertThatThrownBy(() -> productService.create(등록할_상품_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품의 이름은 비속어를 포함할 수 없다.")
    @Test
    void create03() {
        //given
        Product 등록할_상품 = new ProductFixtureFactory.Builder()
                .price(BigDecimal.valueOf(1000L))
                .name("존X 맛없는 미트파이")
                .build();
        //when & then
        assertThatThrownBy(() -> productService.create(등록할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    //
    @DisplayName("상품등록 - 상품을 등록할 수 있다.")
    @Test
    void create04() {
        //given &when
        Product saved = productService.create(ProductFixtureFactory.미트파이_생성_요청);

        //then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(ProductFixtureFactory.미트파이_상품_이름),
                () -> assertThat(saved.getPrice()).isEqualTo(ProductFixtureFactory.미트파이_상품_가격)
        );
    }

    @DisplayName("상품 가격 수정 - 상품의 가격은 반드시 0보다 큰 값을 가져야 한다.")
    @MethodSource("providePriceForNullAndNegative")
    @ParameterizedTest
    void changePrice01(BigDecimal 변경할_상품_가격) {
        //given
        Product 상품_변경_요청 = new ProductFixtureFactory.Builder()
                .price(변경할_상품_가격)
                .build();
        //when & then
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), 상품_변경_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }


    // 메뉴는 여러 상품을 가진다.
    // 메뉴에 속한 상품의 가격의 합은 메뉴의 가격보다 높아야 한다.
    // 예) 항상 할인된 가격으로 메뉴를 제공해야한다.
    // 런치세트(800원) - 미트파이(500원), 레몬에이드(500원)
    // 500 + 500 > 800
    @DisplayName("상품 가격 수정 - 가격을 변경하는 상품을 포함하는 메뉴의 가격보다 메뉴에 포함한 상품의 가격이 커지는 경우 메뉴를 진열하지 않는다.")
    @Test
    void changePrice02() {
        //given
        productRepository.save(ProductFixtureFactory.미트파이);
        menuRepository.save(MenuFixtureFactory.미트파이_하나를_포함한_메뉴);

        BigDecimal 변경할_상품_가격 = MenuFixtureFactory.미트파이_하나를_포함한_메뉴_가격.subtract(BigDecimal.valueOf(200L));
        Product 상품_변경_요청 = new ProductFixtureFactory.Builder()
                .price(변경할_상품_가격)
                .build();

        //when
        productService.changePrice(미트파이.getId(), 상품_변경_요청);

        //then
        assertThat(MenuFixtureFactory.미트파이_하나를_포함한_메뉴.isDisplayed()).isFalse();
    }

    @DisplayName("상품 가격 수정 - 가격을 변경하는 상품을 포함하는 메뉴의 가격보다 메뉴에 포함한 상품의 가격이 커지는 경우 메뉴를 진열하지 않는다.")
    @Test
    void changePrice02_1() {
        // given
        BigDecimal 현재_등록된_상품_가격 = BigDecimal.valueOf(1000L);
        BigDecimal 현재_등록된_메뉴_가격 = BigDecimal.valueOf(800L);
        BigDecimal 변경할_상품_가격 = BigDecimal.valueOf(600L);

        Product 등록된_상품 = new ProductFixtureFactory.Builder()
                .id(UUID.randomUUID())
                .name("미트파이")
                .price(현재_등록된_상품_가격)
                .build();

        Menu 등록된_메뉴 = new MenuFixtureFactory.Builder().id(UUID.randomUUID())
                .name("미트파이 런치 세트")
                .price(현재_등록된_메뉴_가격)
                .addProduct(등록된_상품, 1)
                .displayed(true)
                .build();

        productRepository.save(등록된_상품);
        menuRepository.save(등록된_메뉴);

        Product 상품_변경_요청 = new ProductFixtureFactory.Builder()
                .price(변경할_상품_가격)
                .build();

        //when
        productService.changePrice(등록된_상품.getId(), 상품_변경_요청);

        //then
        assertThat(MenuFixtureFactory.미트파이_하나를_포함한_메뉴.isDisplayed()).isFalse();
    }

    @DisplayName("상품 가격 수정 - 상품의 가격을 수정할 수 있다.")
    @Test
    void changePrice03() {
        //given
        productRepository.save(ProductFixtureFactory.미트파이);
        menuRepository.save(MenuFixtureFactory.미트파이_하나를_포함한_메뉴);
        BigDecimal 변경할_가격 = BigDecimal.valueOf(1500L);


        Product 상품_변경_요청 = new ProductFixtureFactory.Builder()
                .price(변경할_가격)
                .build();
        //when
        Product updated = productService.changePrice(미트파이.getId(), 상품_변경_요청);

        //then
        assertThat(updated.getPrice()).isEqualTo(변경할_가격);
    }


    @DisplayName("상품 가격 수정 - 가격을 변경하려는 상품은 반드시 존재해야 한다.")
    @Test
    void changePrice04() {
        //given
        UUID 존재하지_않는_상품_아이디 = UUID.randomUUID();
        BigDecimal 변경할_가격 = BigDecimal.valueOf(1500L);
        Product 상품_변경_요청 = new ProductFixtureFactory.Builder()
                .price(변경할_가격)
                .build();
        //when & then
        assertThatThrownBy(() -> productService.changePrice(존재하지_않는_상품_아이디, 상품_변경_요청))
                .isInstanceOf(NoSuchElementException.class);
    }


    @DisplayName("상품 조회 - 등록된 모든 상품을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        productRepository.save(ProductFixtureFactory.미트파이);
        // when
        List<Product> products = productService.findAll();
        //then
        assertThat(products).hasSize(1);
        assertThat(products).contains(ProductFixtureFactory.미트파이);

    }

}
