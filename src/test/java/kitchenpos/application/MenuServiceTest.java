package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

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
    private MenuService menuService;
    private Product 돈가스;
    private Product 김밥;
    private UUID 돈가스id;

    @BeforeEach
    void setUp() {
        돈가스id = UUID.randomUUID();
        돈가스 = new Product(돈가스id, "돈가스", BigDecimal.valueOf(12000));
        김밥 = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(7000));

        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("메뉴 등록")
    class create {
        @DisplayName("가격이 null이면 오류")
        @ParameterizedTest
        @NullSource
        void priceIsNull(BigDecimal price) {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", price, new MenuGroup(), true);

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
        }

        @DisplayName("가격이 0보다 작으면 오류")
        @Test
        void priceIsUnderZero() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", new BigDecimal(-1), new MenuGroup(), true);

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
        }

        @DisplayName("메뉴 그룹이 등록이 안되어 있는 경우에 오류")
        @Test
        void notExistsMenuGroup() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", new BigDecimal(10), new MenuGroup(), true);
            given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

            //when
            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(0)).findAllByIdIn(any());
        }

        @DisplayName("입력 값에 메뉴상품이 없는경우에 오류")
        @Test
        void notExistsMenuProduct() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", new BigDecimal(10), new MenuGroup(), true);
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(0)).findAllByIdIn(any());
        }

        @DisplayName("요청한 상품정보들이, 기 등록 되어 있지 않으면 오류")
        @Test
        void wrongProductCount() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(120000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 10));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 10));
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(돈가스));

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(1)).findAllByIdIn(any());
        }

        @DisplayName("요청한 메뉴상품의 수량이 0보다 작으면 오류가 발생한다.")
        @Test
        void quantityUnderZero() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(120000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, -1));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 4));
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(돈가스, 김밥));

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(1)).findAllByIdIn(any());
        }

        @DisplayName("상품정보 전체 등록 검증에서는 통과 했으나, 개별 등록 조회에서 실패했을경우 오류가 발생한다.")
        @Test
        void notExistsProductId() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(120000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(돈가스, 김밥));
            given(productRepository.findById(돈가스.getId())).willReturn(Optional.empty());

            //when
            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(1)).findAllByIdIn(List.of(돈가스.getId(), 김밥.getId()));
            then(productRepository).should(times(1)).findById(돈가스.getId());
        }

        @DisplayName("등록할 메뉴가격 > 계산된 금액(상품금액*메뉴상품 갯수) 인경우 오류가 발생한다.")
        @Test
        void overPrice() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1200000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(돈가스, 김밥));
            given(productRepository.findById(돈가스.getId())).willReturn(Optional.of(돈가스));
            given(productRepository.findById(김밥.getId())).willReturn(Optional.of(김밥));

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(1)).findAllByIdIn(List.of(돈가스.getId(), 김밥.getId()));
            then(productRepository).should(times(1)).findById(돈가스.getId());
            then(productRepository).should(times(1)).findById(김밥.getId());
        }

        @DisplayName("메뉴이름 null 인 경우 오류가 발생한다.")
        @Test
        void menuNameIsEmpty() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), null, BigDecimal.valueOf(1000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(돈가스, 김밥));
            given(productRepository.findById(돈가스.getId())).willReturn(Optional.of(돈가스));
            given(productRepository.findById(김밥.getId())).willReturn(Optional.of(김밥));

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(1)).findAllByIdIn(List.of(돈가스.getId(), 김밥.getId()));
            then(productRepository).should(times(1)).findById(돈가스.getId());
            then(productRepository).should(times(1)).findById(김밥.getId());
        }

        @DisplayName("메뉴이름에 비속어가 포함되어 있으면 오류가 발생한다.")
        @Test
        void wrongMenuName() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(돈가스, 김밥));
            given(productRepository.findById(돈가스.getId())).willReturn(Optional.of(돈가스));
            given(productRepository.findById(김밥.getId())).willReturn(Optional.of(김밥));
            given(purgomalumClient.containsProfanity(any())).willReturn(true);

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(돈가스_세트));
            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(1)).findAllByIdIn(List.of(돈가스.getId(), 김밥.getId()));
            then(productRepository).should(times(1)).findById(돈가스.getId());
            then(productRepository).should(times(1)).findById(김밥.getId());
            then(purgomalumClient).should(times(1)).containsProfanity(any());
        }

        @DisplayName("정상 등록")
        @Test
        void normalCreate() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(돈가스, 김밥));
            given(productRepository.findById(돈가스.getId())).willReturn(Optional.of(돈가스));
            given(productRepository.findById(김밥.getId())).willReturn(Optional.of(김밥));
            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(menuRepository.save(any())).willReturn(돈가스_세트);

            //when
            //then
            Menu retunMenu = menuService.create(돈가스_세트);
            assertThat(retunMenu.getName()).isEqualTo(돈가스_세트.getName());
            assertThat(retunMenu.getPrice()).isEqualTo(돈가스_세트.getPrice());
            assertThat(retunMenu.isDisplayed()).isEqualTo(돈가스_세트.isDisplayed());

            then(menuGroupRepository).should(times(1)).findById(any());
            then(productRepository).should(times(1)).findAllByIdIn(List.of(돈가스.getId(), 김밥.getId()));
            then(productRepository).should(times(1)).findById(돈가스.getId());
            then(productRepository).should(times(1)).findById(김밥.getId());
            then(purgomalumClient).should(times(1)).containsProfanity(any());
            then(menuRepository).should(times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("메뉴 가격 변경")
    class changePrice {
        @DisplayName("가격이 null이면 오류")
        @ParameterizedTest
        @NullSource
        void priceIsNull(BigDecimal price) {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", price, new MenuGroup(), true);

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(돈가스id, 돈가스_세트));
        }

        @DisplayName("가격이 0보다 작으면 오류")
        @Test
        void priceIsUnderZero() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", new BigDecimal(-1), new MenuGroup(), true);

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(돈가스id, 돈가스_세트));
        }

        @DisplayName("기 등록된 메뉴가 아니라면 오류")
        @Test
        void notExistsMenu() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", new BigDecimal(10000), new MenuGroup(), true);
            given(menuRepository.findById(돈가스id)).willReturn(Optional.empty());

            //when
            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.changePrice(돈가스id, 돈가스_세트));
        }

        @DisplayName("기등록된 메뉴가격 > 계산된 금액(상품금액*메뉴상품 갯수) 인경우 오류")
        @Test
        void overPrice() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1200000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuRepository.findById(돈가스id)).willReturn(Optional.of(돈가스_세트));

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(돈가스id, 돈가스_세트));
        }

        @DisplayName("입력한 가격으로 정상 변경되었다.")
        @Test
        void normalChangePrice() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuRepository.findById(돈가스id)).willReturn(Optional.of(돈가스_세트));

            //when
            //then
            Menu returnMenu = menuService.changePrice(돈가스id, 돈가스_세트);
            assertThat(returnMenu.getPrice()).isSameAs(돈가스_세트.getPrice());
        }

    }

    @Nested
    @DisplayName("메뉴 (화면에) 표시")
    class display {
        @DisplayName("기등록 메뉴가 아니었을때 오류가 발생한다.")
        @Test
        void notExistsMenu() {
            ///given
            given(menuRepository.findById(돈가스id)).willReturn(Optional.empty());

            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.display(돈가스id));
        }

        @DisplayName("화면표시 처리할 메뉴의 메뉴가격 > 계산된 금액(상품금액*메뉴상품 갯수) 인경우 오류")
        @Test
        void overPrice() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1200000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuRepository.findById(돈가스id)).willReturn(Optional.of(돈가스_세트));

            //when
            //then
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> menuService.display(돈가스id));
        }

        @DisplayName("메뉴가 정상 (화면표시) 된다.")
        @Test
        void normalDisplay() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1000), new MenuGroup(), true);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuRepository.findById(돈가스id)).willReturn(Optional.of(돈가스_세트));

            //when
            //then
            Menu returnMenu = menuService.display(돈가스id);
            assertThat(returnMenu.isDisplayed()).isTrue();
        }
    }

    @Nested
    @DisplayName("메뉴 (화면에) 표시 하지 않는다.")
    class hide {
        @DisplayName("기등록 메뉴가 아니었을때 오류가 발생한다.")
        @Test
        void notExistsMenu() {
            ///given
            given(menuRepository.findById(돈가스id)).willReturn(Optional.empty());

            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.hide(돈가스id));
        }

        @DisplayName("메뉴가 정상 (화면표시) 된다.")
        @Test
        void normalDisplay() {
            //given
            Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1000), new MenuGroup(), false);
            돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
            돈가스_세트.addMenuProduct(new MenuProduct(김밥, 5));
            given(menuRepository.findById(돈가스id)).willReturn(Optional.of(돈가스_세트));

            //when
            //then
            Menu returnMenu = menuService.hide(돈가스id);
            assertThat(returnMenu.isDisplayed()).isFalse();
        }
    }

    @DisplayName("메뉴를 전체 조회하면, 등록된 메뉴가 조회된다")
    @Test
    void findAll() {
        //given
        Menu 돈가스_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1000), new MenuGroup(), false);
        돈가스_세트.addMenuProduct(new MenuProduct(돈가스, 5));
        Menu 김밥_세트 = new Menu(UUID.randomUUID(), "돈가스세트", BigDecimal.valueOf(1000), new MenuGroup(), false);
        김밥_세트.addMenuProduct(new MenuProduct(김밥, 5));
        Product 김밥 = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(12000));
        given(menuRepository.findAll()).willReturn(List.of(돈가스_세트, 김밥_세트));

        //when
        List<Menu> menus = menuService.findAll();

        //then
        assertThat(menus).containsOnly(돈가스_세트, 김밥_세트);
    }
}