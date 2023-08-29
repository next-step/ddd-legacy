package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Test
    @DisplayName("상품을 등록한다.")
    void create01() {
        Product product = createProduct("치킨", new BigDecimal("10000"));
        given(purgomalumClient.containsProfanity("치킨")).willReturn(false);

        Product savedProduct = productService.create(product);

        Product findProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(savedProduct.getId()).isEqualTo(findProduct.getId());
    }

    @Test
    @DisplayName("상품의 가격은 0보다 작을 수 없다.")
    void create02() {
        Product product = createProduct("치킨", new BigDecimal("-1"));
        given(purgomalumClient.containsProfanity("치킨")).willReturn(false);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 이름은 비속어가 들어갈 수 없다.")
    void create03() {
        Product product = createProduct("욕", new BigDecimal("1000"));
        given(purgomalumClient.containsProfanity("욕")).willReturn(true);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 이름은 비어있을 수 없다.")
    void create04() {
        Product product = createProduct(null, new BigDecimal("1000"));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격을 변경한다.")
    void changePrice01() {
        Product product = createProduct("치킨", new BigDecimal("10000"));
        Product savedProduct = productRepository.save(product);

        product.setPrice(new BigDecimal("20000"));
        productService.changePrice(savedProduct.getId(), product);

        Product findProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(findProduct.getPrice()).isEqualTo(new BigDecimal("20000"));
    }

    @Test
    @Disabled("테스트 실패로 로직 수정 필요.")
    @DisplayName("상품이 포함된 메뉴들의 가격이 메뉴의 가격보다 이하인 경우 해당 메뉴를 전시 하지 않는다.")
    void changePrice02() {
        // given
        Product product1 = createProduct("치킨1", new BigDecimal("10000"));
        Product product2 = createProduct("치킨2", new BigDecimal("10000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Menu menu = createMenu(savedMenuGroup, List.of(createMenuProduct(1L, savedProducts.get(0)),
                createMenuProduct(2L, savedProducts.get(1))), new BigDecimal("15000"));
        Menu savedMenu = menuRepository.save(menu);

        Product product = savedProducts.get(0);
        product.setPrice(new BigDecimal("15000"));

        // when
        productService.changePrice(savedProducts.get(0).getId(), product);

        // then
        Menu findMenu = menuRepository.findById(savedMenu.getId()).orElseThrow();
        assertThat(findMenu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("상품을 모두 조회한다.")
    void findAll01() {
        // given
        Product product1 = createProduct("치킨1", new BigDecimal("10000"));
        Product product2 = createProduct("치킨2", new BigDecimal("10000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Menu menu1 = createMenu(savedMenuGroup, List.of(createMenuProduct(1L, savedProducts.get(0))),
                new BigDecimal("10000"));
        Menu menu2 = createMenu(savedMenuGroup, List.of(createMenuProduct(1L, savedProducts.get(0)),
                createMenuProduct(2L, savedProducts.get(1))), new BigDecimal("20000"));

        // when
        List<Menu> savedMenus = menuRepository.saveAll(List.of(menu1, menu2));

        // then
        assertThat(savedMenus).hasSize(2);
        assertThat(savedMenus).extracting("id")
                              .containsExactly(savedMenus.get(0).getId(), savedMenus.get(1).getId());
    }
}
