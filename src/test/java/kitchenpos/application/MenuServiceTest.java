package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

    @Test
    void create() {

        //given
        Menu request = new Menu();
        request.setName("menu-name");
        request.setPrice(BigDecimal.valueOf(40000));

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("menu-group-name");
        menuGroup.setId(UUID.randomUUID());

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(40000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(1L);
        menuProduct.setProduct(product);

        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Arrays.asList(menuProduct));

        given(menuGroupRepository.findById(menuGroup.getId()))
                .willReturn(Optional.of(menuGroup));

        given(productRepository.findAllByIdIn(any()))
                .willReturn(Arrays.asList(product));

        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        given(purgomalumClient.containsProfanity(any()))
                .willReturn(false);

        given(menuRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        //when
        Menu response = menuService.create(request);

        //then
        assertEquals(request.getName(), response.getName());
        assertEquals(request.isDisplayed(), response.isDisplayed());


    }

    @Test
    void changePrice() {
        //given
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(30000));

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(40000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(1L);
        menuProduct.setProduct(product);


        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(40000));
        menu.setMenuProducts(Arrays.asList(menuProduct));


        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));

        //when
        Menu response = menuService.changePrice(request.getId(), request);

        //then
        assertEquals(BigDecimal.valueOf(30000), response.getPrice());

    }

    @Test
    void display() {

        //given
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(20000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(1L);
        menuProduct.setProduct(product);

        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setMenuProducts(Arrays.asList(menuProduct));
        request.setPrice(BigDecimal.valueOf(20000));


        given(menuRepository.findById(any()))
                .willReturn(Optional.of(request));

        //when
        Menu response = menuService.display(request.getId());

        //then
        assertEquals(true, response.isDisplayed());

    }

    @Test
    void hide() {

        //given
        UUID menuId = UUID.randomUUID();
        Menu request = new Menu();
        request.setId(menuId);
        request.setDisplayed(true);

        given(menuRepository.findById(request.getId())).willReturn(Optional.of(request));

        // when
        Menu response = menuService.hide(request.getId());

        // then
        assertEquals(false, response.isDisplayed());

    }

    @Test
    void findAll() {

        //given
        Menu menu1 = new Menu();
        menu1.setId(UUID.randomUUID());
        menu1.setDisplayed(true);

        Menu menu2 = new Menu();
        menu2.setId(UUID.randomUUID());
        menu2.setDisplayed(true);

        given(menuRepository.findAll()).willReturn(Arrays.asList(menu1, menu2));

        //when
        List<Menu> response = menuService.findAll();

        //then
        assertEquals(2, response.size());
        assertEquals(Arrays.asList(menu1, menu2), response);
    }

}