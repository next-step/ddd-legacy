package kitchenpos.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public class MenuServiceTest {

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Autowired
    private MenuService menuService;

    private MenuGroup 추천메뉴;
    private Product 강정치킨;
    private Product 양념치킨;
    private Menu 오늘의치킨;

    @BeforeEach
    void init() {
        추천메뉴 = MenuGroupFixture.builder()
                .name("추천 메뉴")
                .build();
        menuGroupRepository.save(추천메뉴);

        강정치킨 = ProductFixture.Data.강정치킨();
        productRepository.save(강정치킨);

        양념치킨 = ProductFixture.Data.양념치킨();
        productRepository.save(양념치킨);

        오늘의치킨 = MenuFixture.builder()
                .name("오늘의 치킨")
                .price(new BigDecimal(1000))
                .menuGroup(추천메뉴)
                .menuProduct(
                        MenuProductFixture.create()
                                .product(강정치킨)
                                .quantity(1)
                                .build()
                ).build();
        menuRepository.save(오늘의치킨);
    }

    @Test
    void 메뉴_생성_실패__가격이_null() {
        Menu menu = MenuFixture.builder()
                .price(null)
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__가격이_음수() {
        Menu menu = MenuFixture.builder()
                .price(new BigDecimal(-1))
                .menuGroup(추천메뉴)
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴그룹이_존재하지_않음() {
        Menu menu = MenuFixture.builder()
                .price(new BigDecimal(1000))
                .menuGroup(MenuGroupFixture.builder()
                        .name("존재하지 않는 메뉴그룹")
                        .build())
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴상품이_null() {
        Menu menu = MenuFixture.builder()
                .price(new BigDecimal(1000))
                .menuGroup(추천메뉴)
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴상품이_0개() {
        Menu menu = MenuFixture.builder()
                .price(new BigDecimal(1000))
                .menuGroup(추천메뉴)
                .menuProducts(List.of())
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴_생성_요청의_메뉴상품이_존재하지_않음() {
        Menu menu = MenuFixture.builder()
                .price(new BigDecimal(1000))
                .menuGroup(추천메뉴)
                .menuProducts(
                        List.of(
                                MenuProductFixture.create()
                                        .build()
                        )
                ).build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴상품의_갯수가_음수() {
        Menu menu = MenuFixture.builder()
                .price(new BigDecimal(1000))
                .menuGroup(추천메뉴)
                .menuProducts(
                        List.of(
                                MenuProductFixture.create()
                                        .product(강정치킨)
                                        .quantity(-1)
                                        .build()
                        )
                ).build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__구성메뉴상품의_가격_총합이_메뉴_가격_보다_초과일_수_없다() {
        Menu menu = MenuFixture.builder()
                .price(new BigDecimal(52001))
                .menuGroup(추천메뉴)
                .menuProducts(
                        List.of(
                                MenuProductFixture.create()
                                        .product(강정치킨)
                                        .quantity(1)
                                        .build(),
                                MenuProductFixture.create()
                                        .product(양념치킨)
                                        .quantity(2)
                                        .build()
                        )
                ).build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__이름이_null() {
        Menu menu = MenuFixture.builder()
                .name(null)
                .price(new BigDecimal(10000))
                .menuGroup(추천메뉴)
                .menuProducts(
                        List.of(
                                MenuProductFixture.create()
                                        .product(강정치킨)
                                        .quantity(1)
                                        .build()
                        )
                ).build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__이름에_욕설_포함() {
        when(purgomalumClient.containsProfanity("abuse name")).thenReturn(true);
        Menu menu = MenuFixture.builder()
                .name("abuse name")
                .price(new BigDecimal(10000))
                .menuGroup(추천메뉴)
                .menuProducts(
                        List.of(
                                MenuProductFixture.create()
                                        .product(강정치킨)
                                        .quantity(1)
                                        .build()
                        )
                ).build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격_변경_실패__가격이_null() {
        UUID menuId = 오늘의치킨.getId();
        Menu request = new Menu();
        request.setPrice(null);

        assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격_변경_실패__가격이_음수() {
        UUID menuId = 오늘의치킨.getId();
        Menu request = new Menu();
        request.setPrice(new BigDecimal(-1));

        assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격_변경_실패__메뉴가_존재하지_않음() {
        UUID menuId = UUID.randomUUID();
        Menu request = new Menu();
        request.setPrice(new BigDecimal(20000));

        assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 메뉴_가격_변경_실패__메뉴_가격은_속한_메뉴상품_가격의_총합보다_클_수_없음() {
        UUID menuId = 오늘의치킨.getId();
        Menu request = new Menu();
        request.setPrice(new BigDecimal(20000));

        assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
