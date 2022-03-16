package kitchenpos.application.fake;

import kitchenpos.application.MenuService;
import kitchenpos.application.fake.helper.*;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.application.fake.helper.ProductFixtureFactory.레몬에이드;
import static kitchenpos.application.fake.helper.ProductFixtureFactory.미트파이;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

public class FakeMenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private MenuService menuService;

    @BeforeEach
    void setup() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
    }


    private static Stream<BigDecimal> providePriceForNullAndNegative() {
        return Stream.of(
                null,
                BigDecimal.valueOf(-1000L)
        );
    }

    @DisplayName("메뉴 등록 - 메뉴의 가격은 반드시 0보다 큰 값을 가져야 한다.")
    @MethodSource("providePriceForNullAndNegative")
    @ParameterizedTest
    void create01(BigDecimal 등록할_메뉴_가격) {
        //given
        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .price(등록할_메뉴_가격)
                .build();
        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록 - 메뉴는 반드시 하나의 메뉴 그룹에 속해야 한다.")
    @Test
    void create02() {
        //given
        Menu 메뉴_생성_요청 = MenuFixtureFactory.미트파이_하나를_포함한_메뉴;

        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 등록 - 메뉴는 반드시 하나 이상의 상품(product)을 포함해야 한다.")
    @Test
    void create03() {
        //given
        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .name(any(String.class))
                .price(BigDecimal.valueOf(1000L))
                .addAllManuProducts(Collections.emptyList())
                .menuGroup(MenuGroupFixtureFactory.런체세트그룹)
                .build();
        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("메뉴 등록 - 메뉴에 포함할 상품은 반드시 존재해야 한다.")
    @Test
    void create04() {
        //given
        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .name(any(String.class))
                .price(BigDecimal.valueOf(1000L))
                .addProduct(ProductFixtureFactory.미트파이, 1)
                .menuGroup(MenuGroupFixtureFactory.런체세트그룹)
                .build();
        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // @TODO 품목 별 수량 0개 등록 가능 -> 1개 이상 등록 가능하도록 개선
    @DisplayName("메뉴 등록 - 메뉴는 반드시 하나 이상의 상품(product)을 포함해야 한다. - 품목별 수량은 -1보다 큰 값을 가져야 한다.")
    @Test
    void create05() {
        //given
        int 미트파이_수량 = -1;

        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        productRepository.save(ProductFixtureFactory.미트파이);
        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .name(any(String.class))
                .price(BigDecimal.valueOf(1000L))
                .addProduct(ProductFixtureFactory.미트파이, 미트파이_수량)
                .menuGroup(MenuGroupFixtureFactory.런체세트그룹)
                .build();
        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 등록 - 메뉴에 가격은 메뉴에 속한 모든 상품의 가격의 합보다 클 수 없다.")
    @Test
    void create06() {
        //given
        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        productRepository.save(ProductFixtureFactory.미트파이);

        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .name(any(String.class))
                .price(ProductFixtureFactory.미트파이.getPrice().add(BigDecimal.valueOf(1000L)))
                .addProduct(ProductFixtureFactory.미트파이, 1)
                .menuGroup(MenuGroupFixtureFactory.런체세트그룹)
                .build();

        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);

    }


    // @TODO 메뉴 이름 EmptyString 가능함 -> EmptyString 유효성 검증
    @DisplayName("메뉴 등록 -  메뉴는 반드시 이름을 가져야 한다.")
    @Test
    void create07() {
        //given

        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        productRepository.save(ProductFixtureFactory.미트파이);

        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .name(null) // 등록할 메뉴 이름
                .price(BigDecimal.valueOf(800L))
                .addProduct(ProductFixtureFactory.미트파이, 1)
                .menuGroup(MenuGroupFixtureFactory.런체세트그룹)
                .build();

        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록 -  메뉴의 이름은 비속어를 포함할 수 없다.")
    @Test
    void create08() {
        //given
        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        productRepository.save(ProductFixtureFactory.미트파이);

        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .name("존X 맛없는 미트파이 런치세트") // 등록할 메뉴 이름
                .price(BigDecimal.valueOf(800L))
                .addProduct(ProductFixtureFactory.미트파이, 1)
                .menuGroup(MenuGroupFixtureFactory.런체세트그룹)
                .build();

        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("메뉴 등록 -  메뉴를 등록 할 수 있다.")
    @Test
    void create09() {
        //given
        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        productRepository.save(ProductFixtureFactory.미트파이);

        String 미트파이_런치세트_메뉴_이름 = "미트파이 런치세트";
        BigDecimal 미트파이_런치세트_가격 = BigDecimal.valueOf(800L);

        Menu 메뉴_생성_요청 = new MenuFixtureFactory.Builder()
                .name(미트파이_런치세트_메뉴_이름) // 등록할 메뉴 이름
                .price(미트파이_런치세트_가격)
                .addProduct(ProductFixtureFactory.미트파이, 1)
                .menuGroup(MenuGroupFixtureFactory.런체세트그룹)
                .displayed(true)
                .build();

        //when & then
        Menu saved = menuService.create(메뉴_생성_요청);

        //then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(미트파이_런치세트_메뉴_이름),
                () -> assertThat(saved.getPrice()).isEqualTo(미트파이_런치세트_가격),
                () -> assertThat(saved.getMenuGroup()).isEqualTo(MenuGroupFixtureFactory.런체세트그룹),
                () -> assertThat(saved.getMenuProducts()).hasSize(1),
                () -> assertThat(saved.isDisplayed()).isTrue()
        );
    }


    @DisplayName("메뉴 가격 변경 - 메뉴의 가격은 반드시 0보다 큰 값을 가져야 한다.")
    @MethodSource("providePriceForNullAndNegative")
    @ParameterizedTest
    void changePrice01(BigDecimal 변경할_메뉴_가격) {
        //given
        Menu 메뉴_변경_요청 = new MenuFixtureFactory.Builder()
                .price(변경할_메뉴_가격)
                .build();
        //when & then
        assertThatThrownBy(() -> menuService.create(메뉴_변경_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격 변경 - 메뉴에 가격은 메뉴에 속한 모든 상품의 가격의 합보다 클 수 없다.")
    @Test
    void changePrice02() {

        //given
        BigDecimal 변경할_메뉴_가격 = ProductFixtureFactory.미트파이.getPrice().add(BigDecimal.valueOf(1000L));
        menuRepository.save(MenuFixtureFactory.미트파이_하나를_포함한_메뉴);

        Menu 메뉴_변경_요청 = new MenuFixtureFactory.Builder()
                .price(변경할_메뉴_가격)
                .build();

        //when & then
        assertThatThrownBy(() -> menuService.changePrice(MenuFixtureFactory.미트파이_하나를_포함한_메뉴.getId(), 메뉴_변경_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    //@TODO 버그 픽스
    @DisplayName("메뉴 가격 변경 - 개별 상품 가격과 메뉴 가격을 비교하고 있어 변경 가능한 상태임에도 불구하고 변경되지 않는다.")
    @Test
    void changePrice02_01() {
        //given
        BigDecimal 변경할_메뉴_가격 = BigDecimal.valueOf(1500L);
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);

        Menu 메뉴_변경_요청 = new MenuFixtureFactory.Builder()
                .price(변경할_메뉴_가격)
                .build();

        //when
        Menu updated = menuService.changePrice(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴.getId(), 메뉴_변경_요청);

        //then
        assertThat(updated.getPrice()).isEqualTo(변경할_메뉴_가격);
    }

    @DisplayName("메뉴 가격 변경 - 메뉴의 가격을 수정할 수 있다.")
    @Test
    void changePrice03() {
        //given
        BigDecimal 변경할_메뉴_가격 = BigDecimal.valueOf(600L);
        menuRepository.save(MenuFixtureFactory.미트파이_하나를_포함한_메뉴);
        Menu 메뉴_변경_요청 = new MenuFixtureFactory.Builder()
                .price(변경할_메뉴_가격)
                .build();

        //when
        Menu updated = menuService.changePrice(MenuFixtureFactory.미트파이_하나를_포함한_메뉴.getId(), 메뉴_변경_요청);

        //then
        assertThat(updated.getPrice()).isEqualTo(변경할_메뉴_가격);
    }

    @DisplayName("메뉴 가격 변경 - 가격을 변경하려는 메뉴는 반드시 존재해야 한다.")
    @Test
    void changePrice04() {
        //given

        BigDecimal 변경할_메뉴_가격 = BigDecimal.valueOf(600L);
        Menu 메뉴_변경_요청 = new MenuFixtureFactory.Builder()
                .price(변경할_메뉴_가격)
                .build();

        //when & then
        assertThatThrownBy(() -> menuService.changePrice(MenuFixtureFactory.미트파이_하나를_포함한_메뉴.getId(), 메뉴_변경_요청))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 노출 - 메뉴에 속한 상품의 가격의 합이 메뉴의 가격 보다 큰 경우 노출 할 수 없다.")
    @Test
    void display01() {
        //given
        BigDecimal 메뉴_가격 = ProductFixtureFactory.미트파이.getPrice().add(BigDecimal.valueOf(1000L));
        Menu 노출할_메뉴 = new MenuFixtureFactory.Builder()
                .id(UUID.randomUUID())
                .name(any(String.class))
                .price(메뉴_가격) // 메뉴 가격이 상품 가격의 합보다 크다.
                .addProduct(미트파이, 1)
                .displayed(false)
                .build();

        menuRepository.save(노출할_메뉴);

        //when & then
        assertThatThrownBy(() -> menuService.display(노출할_메뉴.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    // @TODO 리펙토링 필요
    @DisplayName("메뉴 노출 - 개별 상품 가격과 메뉴 가격을 비교하고 있어 노출 가능한 상태임에도 불구하고 노출되지 않는다.")
    @Test
    void display01_01() {
        //given
        BigDecimal 메뉴_가격 = BigDecimal.valueOf(1500L);
        Menu 노출할_메뉴 = new MenuFixtureFactory.Builder()
                .id(UUID.randomUUID())
                .name(any(String.class))
                .price(메뉴_가격) // 메뉴 가격이 상품 가격의 합보다 크다.
                .addProduct(미트파이, 1)
                .addProduct(레몬에이드, 1)
                .displayed(false)
                .build();
        menuRepository.save(노출할_메뉴);

        //when
        Menu updated = menuService.display(노출할_메뉴.getId());

        //then
        assertThat(updated.isDisplayed()).isTrue();

    }

    @DisplayName("메뉴 노출 - 메뉴를 노출 할 수 있다.")
    @Test
    void display02() {
        //given
        BigDecimal 메뉴_가격 = ProductFixtureFactory.미트파이.getPrice().subtract(BigDecimal.valueOf(200L));
        Menu 노출할_메뉴 = new MenuFixtureFactory.Builder()
                .id(UUID.randomUUID())
                .name(any(String.class))
                .price(메뉴_가격)  // 메뉴 가격이 상품 가격의 합보다 작다.
                .addProduct(미트파이, 1)
                .displayed(false)
                .build();
        menuRepository.save(노출할_메뉴);

        //when
        Menu updated = menuService.display(노출할_메뉴.getId());

        //then
        assertThat(updated.isDisplayed()).isTrue();
    }


    @DisplayName("메뉴 노출 - 노출하려는 메뉴는 반드시 존재해야 한다.")
    @Test
    void display03() {
        //when & then
        assertThatThrownBy(() -> menuService.display(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("메뉴 숨김 - 메뉴를 숨길 수 있다.")
    @Test
    void hide01() {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);

        //when
        Menu updated = menuService.hide(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴.getId());

        //then
        assertThat(updated.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 숨김 - 숨기려는 메뉴는 반드시 존재해야 한다.")
    @Test
    void hide02() {
        //when & then
        assertThatThrownBy(() -> menuService.hide(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 조회 - 등록된 모든 메뉴를 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        menuRepository.save(MenuFixtureFactory.미트파이_하나를_포함한_메뉴);
        //when
        List<Menu> menus = menuService.findAll();
        //then
        assertAll(
                () -> assertThat(menus).hasSize(2),
                () -> assertThat(menus).contains(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, MenuFixtureFactory.미트파이_하나를_포함한_메뉴)
        );
    }

}
