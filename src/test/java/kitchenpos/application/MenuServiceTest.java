package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.constant.Fixtures;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        Fixtures.initialize();
    }

    @DisplayName("메뉴 등록")
    @Nested
    public class CreateTest {
        @DisplayName("정상 동작")
        @Test
        void create() {
            // given
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU_GROUP));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.PRODUCT));
            given(productRepository.findById(any())).willReturn(Optional.of(Fixtures.PRODUCT));
            given(purgomalumClient.containsProfanity(any())).willReturn(Boolean.FALSE);

            // when
            menuService.create(Fixtures.MENU);

            // then
            then(menuRepository).should().save(any());
        }

        @DisplayName("가격이 null 이거나 0보다 작을수 없음")
        @ParameterizedTest
        @NullSource
        @ValueSource(longs = -1)
        void createWithInvalidPrice(Long price) {
            // given
            Menu request = new Menu();
            if (price != null) {
                request.setPrice(BigDecimal.valueOf(price));
            }

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.create(request)
            );
        }

        @DisplayName("제품 1개 이상 등록 해야함")
        @Test
        void createWithEmptyProduct() {
            // given
            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(3000));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU_GROUP));

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.create(request)
            );
        }

        @DisplayName("반드시 이미 등록된 제품만 등록 해야함")
        @Test
        void createWithNotExistsProduct() {
            // given
            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(3000));
            request.setMenuProducts(List.of(Fixtures.MENU_PRODUCT));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU_GROUP));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of());

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.create(request)
            );
        }

        @DisplayName("등록하려는 제품 수량은 0개 이상 이어야함")
        @Test
        void createWithInvalidQuantity() {
            // given
            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProduct(Fixtures.PRODUCT);
            menuProductRequest.setQuantity(-1);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(3000));
            request.setMenuProducts(List.of(menuProductRequest));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU_GROUP));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.PRODUCT));

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.create(request)
            );
        }

        @DisplayName("메뉴 가격은 등록하려는 제품들의 제품 가격 * 수량의 총합보다 클 수 없음")
        @Test
        void createWithInvalidSumPrice() {
            // given
            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProduct(Fixtures.PRODUCT);
            menuProductRequest.setQuantity(1);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(100_000));
            request.setMenuProducts(List.of(menuProductRequest));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU_GROUP));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.PRODUCT));
            given(productRepository.findById(any())).willReturn(Optional.of(Fixtures.PRODUCT));

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.create(request)
            );
        }

        @DisplayName("메뉴 이름이 null 일 수 없음")
        @Test
        void createWithNullName() {
            // given
            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProduct(Fixtures.PRODUCT);
            menuProductRequest.setQuantity(1);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(5000));
            request.setMenuProducts(List.of(menuProductRequest));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU_GROUP));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.PRODUCT));
            given(productRepository.findById(any())).willReturn(Optional.of(Fixtures.PRODUCT));

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.create(request)
            );
        }

        @DisplayName("메뉴 이름에 욕설이 포함될수 없음")
        @Test
        void createWithProfanityName() {
            // given
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU_GROUP));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.PRODUCT));
            given(productRepository.findById(any())).willReturn(Optional.of(Fixtures.PRODUCT));
            given(purgomalumClient.containsProfanity(any())).willReturn(Boolean.TRUE);

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.create(Fixtures.MENU)
            );
        }
    }

    @DisplayName("메뉴 가격 수정")
    @Nested
    public class ChangePriceTest {
        @DisplayName("정상 동작")
        @Test
        void changePrice() {
            // given
            UUID menuId = Fixtures.MENU.getId();

            Menu request = new Menu();
            request.setId(menuId);
            request.setPrice(BigDecimal.valueOf(3000));
            request.setMenuGroup(Fixtures.MENU_GROUP);
            request.setDisplayed(true);
            request.setMenuProducts(List.of(Fixtures.MENU_PRODUCT));

            given(menuRepository.findById(menuId)).willReturn(Optional.of(Fixtures.MENU));

            // when
            Menu result = menuService.changePrice(menuId, request);

            // then
            assertThat(result.getPrice()).isEqualTo("3000");
        }

        @DisplayName("가격이 null 이거나 0보다 작을수 없음")
        @ParameterizedTest
        @NullSource
        @ValueSource(longs = -1)
        void changePriceWithInvalidPrice(Long price) {
            // given
            Menu request = new Menu();
            if (price != null) {
                request.setPrice(BigDecimal.valueOf(price));
            }

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.changePrice(UUID.randomUUID(), request)
            );
        }

        @DisplayName("변경하려는 가격이 하위 제품 가격의 총합보다 클 경우 가격을 변경할 수 없음")
        @Test
        void changePriceWithInvalidPrice2() {
            // given
            UUID menuId = Fixtures.MENU.getId();

            Menu request = new Menu();
            request.setId(menuId);
            request.setPrice(BigDecimal.valueOf(10000));
            request.setMenuGroup(Fixtures.MENU_GROUP);
            request.setDisplayed(true);
            request.setMenuProducts(List.of(Fixtures.MENU_PRODUCT));

            given(menuRepository.findById(menuId)).willReturn(Optional.of(Fixtures.MENU));

            // when
            assertThatIllegalArgumentException().isThrownBy(
                () -> menuService.changePrice(menuId, request)
            );
        }
    }

    @DisplayName("메뉴 표시")
    @Nested
    public class DisplayTest {
        @DisplayName("정상 동작")
        @Test
        void display() {
            // given
            UUID menuId = UUID.randomUUID();

            Menu menu = new Menu();
            menu.setId(menuId);
            menu.setName("SampleMenu");
            menu.setPrice(BigDecimal.valueOf(5000));
            menu.setMenuGroup(Fixtures.MENU_GROUP);
            menu.setDisplayed(false);
            menu.setMenuProducts(List.of(Fixtures.MENU_PRODUCT));

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            Menu result = menuService.display(menuId);

            // then
            assertThat(result.isDisplayed()).isTrue();
        }

        @DisplayName("메뉴 가격이 하위 제품 가격의 총합보다 클 경우 표시상태로 변경할 수 없음")
        @Test
        void displayWithInvalidPrice() {
            // given
            UUID menuId = UUID.randomUUID();

            Menu menu = new Menu();
            menu.setId(menuId);
            menu.setName("SampleMenu");
            menu.setPrice(BigDecimal.valueOf(100000));
            menu.setMenuGroup(Fixtures.MENU_GROUP);
            menu.setDisplayed(false);
            menu.setMenuProducts(List.of(Fixtures.MENU_PRODUCT));

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when then
            assertThatIllegalStateException().isThrownBy(() -> menuService.display(menuId));
        }
    }

    @DisplayName("메뉴 숨김")
    @Test
    void hide() {
        // given
        UUID menuId = UUID.randomUUID();
        given(menuRepository.findById(menuId)).willReturn(Optional.of(Fixtures.MENU));

        // when
        Menu result = menuService.hide(menuId);

        // then
        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("모든 메뉴 조회")
    @Test
    void findAll() {
        // given
        given(menuRepository.findAll()).willReturn(List.of(Fixtures.MENU));

        // when
        List<Menu> results = menuService.findAll();

        // then
        assertThat(results).containsExactly(Fixtures.MENU);
    }
}
