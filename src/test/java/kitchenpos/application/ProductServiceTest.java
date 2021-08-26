package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @Mock
    private MenuRepository menuRepository;

    private MenuGroup menuGroup;
    private Product mockProduct;
    private MenuProduct mockMenuProduct;
    private List<MenuProduct> mockMenuProducts;
    private Menu mockMenu;

    @Test
    @DisplayName("제품 추가 - 성공")
    void addMenuGroup() {
        // given
        Product mockProduct = generateProduct(UUID.randomUUID());

        // mocking
        given(productRepository.save(any())).willReturn(mockProduct);
        given(purgomalumClient.containsProfanity(any())).willReturn(false);

        // when
        Product newProduct = productService.create(mockProduct);

        // then
        assertThat(newProduct.getId()).isNotNull();
        assertThat(newProduct.getName()).isEqualTo(mockProduct.getName());
    }

    private Product generateProduct(UUID id) {
        Product product = new Product();
        product.setId(id);
        product.setName("product 1");
        product.setPrice(BigDecimal.valueOf(1000));
        return product;
    }

    @Test
    @DisplayName("제품 추가 - 실패: 빈 입력 값")
    void addMenuGroup_BadRequest_Empty_Input() {
        // given
        Product product = generateProduct(UUID.randomUUID());

        // when
        product.setName(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(product));
    }

    @Test
    @DisplayName("제품 추가 - 실패: 잘못된 가격")
    void addMenuGroup_BadRequest_Invalid_price() {
        // given
        Product product = generateProduct(UUID.randomUUID());

        // when
        product.setPrice(BigDecimal.valueOf(-1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(product));
    }

    @Test
    @DisplayName("제품 추가 - 실패: 제품명에 욕설이 포함")
    void addMenuGroup_BadRequest_Invalid_name() {
        // given
        Product product = generateProduct(UUID.randomUUID());

        // mocking
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when
        product.setName("입에담지 못할 욕");

        // then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(product));
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 성공")
    void changePrice() {
        // given
        generateMenuRequest();
        mockMenu.setPrice(BigDecimal.valueOf(1000));
        Product request = generateProduct(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(5000));

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(mockProduct));
        given(menuRepository.findAllByProductId(any())).willReturn(Collections.singletonList(mockMenu));

        // when
        Product product = productService.changePrice(mockMenu.getId(), request);

        // then
        assertThat(product.getPrice()).isEqualTo(request.getPrice());
    }

    private void generateMenuRequest() {
        menuGroup = generateMenuGroup(UUID.randomUUID());

        mockProduct = generateProduct(UUID.randomUUID());

        mockMenuProduct = generateMenuProduct();
        mockMenuProduct.setProduct(mockProduct);
        mockMenuProduct.setProductId(mockProduct.getId());

        mockMenuProducts = new ArrayList<>();
        mockMenuProducts.add(mockMenuProduct);

        mockMenu = generateMenu(UUID.randomUUID());
        mockMenu.setMenuGroup(menuGroup);
        mockMenu.setMenuGroupId(menuGroup.getId());
        mockMenu.setMenuProducts(mockMenuProducts);
    }

    private MenuGroup generateMenuGroup(UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName("menu group");
        return menuGroup;
    }

    private MenuProduct generateMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(2);
        return menuProduct;
    }

    private Menu generateMenu(UUID id) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName("menu");
        menu.setPrice(BigDecimal.valueOf(2000));
        menu.setDisplayed(true);
        return menu;
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 실패: 변경할 가격이 비어있거나 0보다 작다")
    void changePrice_IllegalArgumentException_Invalid_price() {
        // given
        generateMenuRequest();
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(-1));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.changePrice(mockMenu.getId(), request));
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 실패: 잘못된 메뉴 id")
    void changePrice_NoSuchElementException_Invalid_MenuId() {
        // given
        generateMenuRequest();
        mockMenu.setPrice(BigDecimal.valueOf(1000));
        Product request = generateProduct(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(5000));

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> productService.changePrice(mockMenu.getId(), request));
    }

    @Test
    @DisplayName("모든 제품 조회")
    void findAllMenuGroup() {
        // given
        int size = 10;
        List<Product> mockMenuGroups = generateProduct(size);

        // mocking
        given(productRepository.findAll()).willReturn(mockMenuGroups);

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products.size()).isEqualTo(size);
    }

    private List<Product> generateProduct(int size) {
        return IntStream.range(0, size).mapToObj(i -> generateProduct(UUID.randomUUID())).collect(Collectors.toList());
    }

}