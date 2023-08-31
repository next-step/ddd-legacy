package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    MenuService menuService;

    @Mock
    MenuRepository menuRepository;

    @Mock
    MenuGroupRepository menuGroupRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    PurgomalumClient purgomalumClient;

    Product product;
    MenuGroup menuGroup;
    MenuProduct menuProduct;
    Menu menu;

    @BeforeEach
    void setUp() {
        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴그룹1");

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("상품1");
        product.setPrice(BigDecimal.valueOf(100));

        menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1L);
        menuProduct.setSeq(1L);

        menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("메뉴1");
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(Arrays.asList(menuProduct));
        menu.setDisplayed(true);

    }

    @Test
    void 메뉴를_생성한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(100));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setDisplayed(true);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        given(menuRepository.save(any())).willReturn(request);

        Menu actual = menuService.create(request);

        assertThat(actual).isNotNull();
    }

    @Test
    void 메뉴_생성_시_금액이_0미만이면_생성에_실패한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(-1));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setDisplayed(true);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_시_메뉴_그룹이_존재하지_않으면_생성에_실패한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(1));
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setDisplayed(true);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 메뉴_생성_시_상품이_존재하지_않으면_생성에_실패한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(1));
        request.setMenuGroupId(menuGroup.getId());
        request.setDisplayed(true);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_시_이름이_비속어이면_생성에_실패한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(100));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setDisplayed(true);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_시_메뉴의_가격이_메뉴에_포함_된_상품_가격_x_상품_수량의_합보다_크면_생성에_실패한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(10000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setDisplayed(true);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_금액_수정_시_0미만이면_생성에_실패한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(-1));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setDisplayed(true);

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_금액_수정_시_메뉴에_포함_된_상품_가격_x_상품_수량의_합보다_크면_생성에_실패한다() {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("메뉴1");
        request.setPrice(BigDecimal.valueOf(10000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setDisplayed(true);
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_노출상태로_변경한다() {
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        menuService.display(menu.getId());

        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    void 메뉴를_숨김상태로_변경한다() {
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        menuService.hide(menu.getId());

        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    void 없는_메뉴를_숨김상태로_변경하면_실패한다() {
        given(menuRepository.findById(any())).willThrow(NoSuchElementException.class);

        assertThatThrownBy(() -> menuService.hide(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 메뉴_전체를_조회한다() {
        given(menuRepository.findAll()).willReturn(Arrays.asList(menu));

        List<Menu> actual = menuService.findAll();

        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(1);
    }
}
