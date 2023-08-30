package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

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

    @TestFactory()
    List<DynamicTest> createMenu() throws Exception {
        var menuGroup = TestFixture.createMenuGroup("메뉴그룹");
        var products = List.of(
                TestFixture.createProduct("후라이드치킨", 10000),
                TestFixture.createProduct("양념치킨", 12000),
                TestFixture.createProduct("파닭", 15000),
                TestFixture.createProduct("치킨무", 500)
        );
        mockCreateMenu(menuGroup, products);

        return List.of(
                dynamicTest("메뉴는 가격, 이름, 메뉴그룹, 전시여부를 필수로 가진다.", () -> {
                    var request = TestFixture.createMenu("메뉴", 1000L, true, menuGroup, products);

                    var result = menuService.create(request);

                    assertSoftly(softly -> {
                        softly.assertThat(result.getId()).isNotNull();
                        softly.assertThat(result.getName()).isEqualTo(request.getName());
                        softly.assertThat(result.getPrice()).isEqualTo(request.getPrice());
                        softly.assertThat(result.getMenuGroup()).isNotNull();
                    });
                }),

                dynamicTest("메뉴의 가격은 반드시 0원 이상이여야한다.", () -> {
                    var request = TestFixture.createMenu("메뉴", -100L, true, menuGroup, products);

                    var throwable = catchThrowable(() -> menuService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("메뉴의 가격은 0원 일 수 있다", () -> {
                    var request = TestFixture.createMenu("메뉴", 0L, true, menuGroup, products);

                    var result = menuService.create(request);

                    assertThat(result.getPrice()).isZero();
                }),

                dynamicTest("메뉴의 이름은 빈 값일 수 있다.", () -> {
                    var request = TestFixture.createMenu("", 1000L, true, menuGroup, products);

                    var throwable = catchThrowable(() -> menuService.create(request));

                    assertThat(throwable).isNull();
                }),

                dynamicTest("메뉴의 이름은 욕설을 포함할 수 없다.", () -> {
                    var request = TestFixture.createMenu("미친메뉴", 1000L, true, menuGroup, products);

                    var throwable = catchThrowable(() -> menuService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("메뉴는 반드시 메뉴 그룹에 속해있어야한다.", () -> {
                    var request = TestFixture.createMenu("", 1000L, true, menuGroup, products);
                    request.setMenuGroup(null);
                    request.setMenuGroupId(null);

                    var throwable = catchThrowable(() -> menuService.create(request));

                    assertThat(throwable).isInstanceOf(NoSuchElementException.class);
                }),

                dynamicTest("등록할 때 메뉴 상품의 재고는 0을 초과해야한다. 즉 0일 수 없다.", () -> {
                    var request = TestFixture.createMenu("", 1000L, true, menuGroup, products);
                    request.getMenuProducts().forEach(
                            menuProduct -> menuProduct.setQuantity(0)
                    );

                    var throwable = catchThrowable(() -> menuService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("메뉴안에 포함된 모든 (상품의 가격 * 재고)의 합은 메뉴의 가격을 초과하지 않아야 한다.", () -> {
                    var request = TestFixture.createMenu("메뉴", 999999L, true, menuGroup, products);

                    var throwable = catchThrowable(() -> menuService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("메뉴상품은 상품정보와 재고 정보를 포함해야 한다.", () -> {
                    var request = TestFixture.createMenu("메뉴", 1000L, true, menuGroup, products);

                    var result = menuService.create(request);

                    result.getMenuProducts().forEach(
                            menuProduct -> {
                                assertThat(menuProduct.getProduct()).isNotNull();
                                assertThat(menuProduct.getProduct().getName()).isNotNull();
                                assertThat(menuProduct.getProduct().getPrice()).isNotNull();
                                assertThat(menuProduct.getQuantity()).isNotZero();
                            }
                    );
                })
        );

    }

    @TestFactory
    List<DynamicTest> changePrice() throws Exception {
        var products = List.of(TestFixture.createProduct("상품", 1000L));
        var menuGroup = TestFixture.createMenuGroup("메뉴그룹");
        var menu = TestFixture.createMenu("메뉴", 1000L, true, menuGroup, products);
        doAnswer(args -> Optional.of(menu)).when(menuRepository).findById(eq(menu.getId()));

        return List.of(
                dynamicTest("메뉴의 가격은 언제든지 변경할 수 있다.", () -> {
                    var request = TestFixture.copy(menu);
                    request.setPrice(BigDecimal.valueOf(100L));

                    var result = menuService.changePrice(menu.getId(), request);

                    assertThat(result.getPrice()).isEqualTo(request.getPrice());
                }),

                dynamicTest("가격 이외의 다른 값은 변경할 수 없다.", () -> {
                    var request = TestFixture.copy(menu);
                    request.setName("새로운이름");
                    request.setDisplayed(!menu.isDisplayed());

                    var result = menuService.changePrice(menu.getId(), request);

                    assertThat(result.getName()).isNotEqualTo(request.getName());
                    assertThat(result.isDisplayed()).isNotEqualTo(request.isDisplayed());
                }),

                dynamicTest("메뉴의 가격은 반드시 0원 이상이여야한다.", () -> {
                    var request = TestFixture.copy(menu);
                    request.setPrice(BigDecimal.valueOf(-1000));

                    var throwable = catchThrowable(() -> menuService.changePrice(menu.getId(), request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("메뉴의 가격은 0원 일 수 있다", () -> {
                    var request = TestFixture.copy(menu);
                    request.setPrice(BigDecimal.valueOf(0));

                    var throwable = catchThrowable(() -> menuService.changePrice(menu.getId(), request));

                    assertThat(throwable).isNull();
                }),

                dynamicTest("메뉴안에 포함된 모든 (상품의 가격 * 재고)의 합은 메뉴의 가격을 초과하지 않아야 한다.", () -> {
                    var request = TestFixture.copy(menu);
                    request.setPrice(BigDecimal.valueOf(9999999L));

                    var throwable = catchThrowable(() -> menuService.changePrice(menu.getId(), request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @TestFactory
    List<DynamicTest> display() throws Exception {
        var products = List.of(TestFixture.createProduct("상품", 1000L));
        var menuGroup = TestFixture.createMenuGroup("메뉴그룹");
        var menu = TestFixture.createMenu("메뉴", 1000L, false, menuGroup, products);

        return List.of(
                dynamicTest("메뉴의 전시여부는 언제든지 수정할 수 있다.", () -> {
                    doAnswer(args -> Optional.of(menu)).when(menuRepository).findById(eq(menu.getId()));

                    var result = menuService.display(menu.getId());

                    assertThat(result.isDisplayed()).isTrue();
                }),

                dynamicTest("메뉴를 전시하려면 (상품의 가격 * 재고)의 합이 메뉴의 가격을 초과하지 않아야 한다.", () -> {
                    doAnswer(args -> Optional.of(TestFixture.createMenu("메뉴", 999999L, true, menuGroup, products)))
                            .when(menuRepository).findById(eq(menu.getId()));

                    var throwable = catchThrowable(() -> menuService.display(menu.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                })
        );
    }

    @Test
    @DisplayName("메뉴를 숨길 수 있다.")
    void hide() throws Exception {
        var products = List.of(TestFixture.createProduct("상품", 1000L));
        var menuGroup = TestFixture.createMenuGroup("메뉴그룹");
        var menu = TestFixture.createMenu("메뉴", 1000L, true, menuGroup, products);
        doAnswer(args -> Optional.of(menu)).when(menuRepository).findById(eq(menu.getId()));

        var result = menuService.hide(menu.getId());

        assertThat(result.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("모든 메뉴를 리스트로 조회할 수 있다.")
    void findAll() throws Exception {
        doAnswer(args -> List.of(TestFixture.createMenu("메뉴", 1000L)))
                .when(menuRepository).findAll();

        var result = menuService.findAll();

        assertThat(result).isNotEmpty();
    }

    private void mockCreateMenu(
            MenuGroup menuGroup,
            List<Product> products
    ) {
        var productsId = products.stream().map(Product::getId).collect(Collectors.toList());

        doAnswer(args -> Optional.of(menuGroup)).when(menuGroupRepository).findById(eq(menuGroup.getId()));

        doAnswer(args -> products).when(productRepository).findAllByIdIn(eq(productsId));

        doAnswer(
                args -> products.stream()
                        .filter(product -> product.getId().equals(args.getArgument(0)))
                        .findFirst()
        ).when(productRepository).findById(any());

        doAnswer(args -> args.getArgument(0)).when(menuRepository).save(any());

        doAnswer(args -> {
            String text = args.getArgument(0);
            return text.contains("미친");
        }).when(purgomalumClient).containsProfanity(any());
    }
}
