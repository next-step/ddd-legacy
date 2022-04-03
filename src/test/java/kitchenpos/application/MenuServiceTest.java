
package kitchenpos.application;

import static kitchenpos.application.MenuGroupServiceFixture.menuGroup;
import static kitchenpos.application.MenuServiceFixture.menu;
import static kitchenpos.application.MenuServiceFixture.menus;
import static kitchenpos.application.ProductServiceFixture.product;
import static kitchenpos.application.ProductServiceFixture.products;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    MenuRepository menuRepository;

    @Mock
    MenuGroupRepository menuGroupRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    PurgomalumClient purgomalumClient;

    @InjectMocks
    MenuService menuService;

    private static final String FUCK = "fuck";

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {

        //given
        Menu menu = menu();
        Product product = product();
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup()));

        given(productRepository.findAllByIdIn(any())
        ).willReturn(products());

        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);

        //when
        menuService.create(menu);

        //then
        assertAll(
            () -> verify(menuGroupRepository).findById(any()),
            () -> verify(productRepository).findAllByIdIn(any()),
            () -> verify(productRepository).findById(any()),
            () -> verify(purgomalumClient).containsProfanity(any()),
            () -> verify(menuRepository).save(any())
        );
    }

    @DisplayName("메뉴 가격은 비어있을 수 없다.")
    @Test
    void can_not_be_empty_menuPrice() {

        //given
        Menu menu = menu();
        menu.setPrice(null);

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("상품 가격은 0원 이상 이어야 한다.")
    @Test
    void must_be_more_than_zero_productPrice() {

        //given
        Menu menu = menu();
        menu.setPrice(BigDecimal.valueOf(-1));

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("상품 이름은 비어 있을 수 없다.")
    @Test
    void can_not_be_empty_productName() {

        //given
        Menu menu = menu();
        menu.setName(null);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(products());
        given(productRepository.findById(any())).willReturn(Optional.of(product()));

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("상품 이름은 통상적인 비속어는 포함될 수 없다.")
    @Test
    void can_not_contain_profanity_productName() {

        //given
        Menu menu = menu();
        menu.setName(FUCK);
        given(purgomalumClient.containsProfanity(FUCK)).willReturn(true);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(products());
        given(productRepository.findById(any())).willReturn(Optional.of(product()));

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 가격을 변경할 수 있다.")
    @Test
    void changePrice() {

        //given
        Menu menu = menu();
        BigDecimal originMenuPrice = menu.getPrice();
        menu.setPrice(BigDecimal.valueOf(17_000));
        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        //when
        Menu result = menuService.changePrice(menu.getId(), menu);

        //then
        assertThat(result.getPrice()).isNotEqualTo(originMenuPrice);

    }

    @DisplayName("메뉴를 고객에게 노출할 수 있다.")
    @Test
    void display() {

        //given
        Menu menu = menu();
        menu.setDisplayed(false);
        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        //when
        Menu result = menuService.display(menu.getId());

        //then
        assertThat(result.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 고객에게 노출하지 않게할 수 있다.")
    @Test
    void hide() {

        //given
        Menu menu = menu();
        menu.setDisplayed(true);
        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        //when
        Menu result = menuService.hide(menu.getId());

        //then
        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 전체 목록을 조회할 수 있다.")
    @Test
    void findAll() {

        //given
        List<Menu> menus = menus();
        given(menuRepository.findAll()).willReturn(menus);

        //when
        List<Menu> result = menuService.findAll();

        //then
        assertThat(result).hasSize(menus.size());
    }

}
