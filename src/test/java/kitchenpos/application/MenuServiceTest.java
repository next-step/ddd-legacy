package kitchenpos.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.OrderBy;
import javax.validation.constraints.Null;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.management.ThreadDumpEndpoint.ThreadDumpDescriptor;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    MenuService menuService;
    @Mock
    MenuGroupRepository menuGroupRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    private Menu menu;
    private MenuGroup menuGroup;
    private List<MenuProduct> menuProducts;

    private Product product_10000;
    private Product product_20000;

    @BeforeEach
    void setUp() {
        menuGroup = MenuGroupFixture.createDefault();
        product_10000 = ProductFixture.create(BigDecimal.valueOf(10000));
        product_20000 = ProductFixture.create(BigDecimal.valueOf(20000));
        menuProducts = MenuProductFixture.createDefaultsWithProduct(product_10000, product_20000);
    }


    @DisplayName("메뉴 생성 금액 체크")
    @Test
    public void 메뉴생성_금액체크() throws Exception {
        menu = MenuFixture.createDefaultWithNameAndPrice("가격체크메뉴", BigDecimal.valueOf(-10000));
        assertThrows(IllegalArgumentException.class, () -> {
            menuService.create(menu);
        });
    }

    @DisplayName("메뉴 상품에 총 가격보다 메뉴가격이 클 수 없다.")
    @Test
    public void 메뉴생성_상품가격_메뉴가격체크() throws Exception {
        Menu menu = MenuFixture.create(
            "메뉴"
            , BigDecimal.valueOf(31000)
            , true
            , menuGroup
            , menuProducts);

        setMock();

        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름에 욕설이 들어가면 안된다")
    @Test
    public void 메뉴생성_이름체크() {
        Menu menu = MenuFixture.create(
            "메뉴"
            , BigDecimal.valueOf(25000)
            , true
            , menuGroup
            , menuProducts);

        setMock();
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("메뉴 생성")
    @Test
    public void 메뉴생성() throws Exception {
        Menu menu = MenuFixture.create(
            "메뉴"
            , BigDecimal.valueOf(24000)
            , true
            , menuGroup
            , menuProducts);

        setMock();
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        given(menuRepository.save(any())).willReturn(menu);

        assertThat(menuService.create(menu).getName()).isEqualTo("메뉴");
    }

    @DisplayName("메뉴 가격 변경")
    @Test
    public void 메뉴가격변경() throws Exception {
        Menu menu = MenuFixture.create(
            "메뉴"
            , BigDecimal.valueOf(11000)
            , true
            , menuGroup
            , menuProducts);

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        BigDecimal changePrice = BigDecimal.valueOf(25000);
        menu.setPrice(changePrice);
        Menu changePriceMenu = menuService.changePrice(menu.getId(), menu);

        assertThat(changePriceMenu.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("메뉴 가격 변경 실패")
    @Test
    public void 메뉴가격변경_실패() throws Exception {
        Menu menu = MenuFixture.create(
            "메뉴"
            , BigDecimal.valueOf(25000)
            , true
            , menuGroup
            , menuProducts);
        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        BigDecimal changePrice = BigDecimal.valueOf(31000);
        menu.setPrice(changePrice);

        assertThatThrownBy(
            () -> menuService.changePrice(menu.getId(), menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 노출")
    @Test
    public void 메뉴노출() throws Exception {
        Menu menu = MenuFixture.create(
            "메뉴"
            , BigDecimal.valueOf(25000)
            , false
            , menuGroup
            , menuProducts);
        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        menuService.display(menu.getId());

        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴 노출 실패")
    @Test
    public void 메뉴노출_실패() throws Exception {
        Menu menu = MenuFixture.create(
            "메뉴"
            , BigDecimal.valueOf(31000)
            , false
            , menuGroup
            , menuProducts);
        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        assertThatThrownBy(
            () -> menuService.display(menu.getId())).isInstanceOf(IllegalStateException.class);
    }


    private void setMock() {
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any()))
            .willReturn(List.of(product_10000, product_20000));
        given(productRepository.findById(product_10000.getId()))
            .willReturn(Optional.of(product_10000));
        given(productRepository.findById(product_20000.getId()))
            .willReturn(Optional.of(product_20000));
    }

}