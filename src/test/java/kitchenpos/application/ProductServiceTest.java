package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @TestFactory
    List<DynamicTest> create() {

        doAnswer(args -> args.getArgument(0)).when(productRepository).save(any());
        doAnswer(args -> {
            String text = args.getArgument(0);
            return text.contains("미친");
        }).when(purgomalumClient).containsProfanity(any());

        return List.of(
                dynamicTest("상품을 생성할 수 있다.", () -> {
                    var request = TestFixture.createProduct("상품", 1000L);

                    var result = productService.create(request);

                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getName()).isEqualTo(request.getName());
                    assertThat(result.getPrice()).isEqualTo(request.getPrice());
                }),

                dynamicTest("상품의 가격은 반드시 0 이상이어야 한다.", () -> {
                    var request = TestFixture.createProduct("상품", -100L);

                    var throwable = catchThrowable(() -> productService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("상품의 가격은 0일 수 있다.", () -> {
                    var request = TestFixture.createProduct("상품", 0L);

                    var result = productService.create(request);

                    assertThat(result.getPrice()).isZero();
                }),

                dynamicTest("상품 이름에는 욕설이 들어갈 수 없다.", () -> {
                    var request = TestFixture.createProduct("미친상품", 0L);

                    var throwable = catchThrowable(() -> productService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @TestFactory
    List<DynamicTest> changePrice() {
        var menuGroup = TestFixture.createMenuGroup("메뉴그룹");
        var products = List.of(
                TestFixture.createProduct("후라이드치킨", 100),
                TestFixture.createProduct("양념치킨", 120),
                TestFixture.createProduct("파닭", 150),
                TestFixture.createProduct("치킨무", 500)
        );
        var menus = List.of(
                TestFixture.createMenu("메뉴1", 1000L, true, menuGroup, products),
                TestFixture.createMenu("메뉴2", 1000L, true, menuGroup, products)
        );

        doAnswer(args -> Optional.of(TestFixture.createProduct(args.getArgument(0), "상품", 1000L)))
                .when(productRepository).findById(any());

        return List.of(
                dynamicTest("상품의 가격을 변경할 수 있다", () -> {
                    var request = TestFixture.createProduct("상품", 5000L);

                    var result = productService.changePrice(request.getId(), request);

                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getPrice()).isEqualTo(request.getPrice());
                }),

                dynamicTest("가격 변경된 상품이 포함된 메뉴들에서 (상품 가격*재고)가 메뉴가격을 초과한다면, 해당 메뉴 전시를 비활성화 한다.", () -> {
                    var request = TestFixture.createProduct("상품", 999999);
                    doAnswer(args -> menus).when(menuRepository).findAllByProductId(any());

                    var result = productService.changePrice(request.getId(), request);

                    menus.forEach(it -> {
                        assertThat(it.isDisplayed()).isFalse();
                    });
                })
        );
    }

    @Test
    @DisplayName("모든 상품을 리스트로 조회 할 수 있다.")
    void findAll() {
        doAnswer(args -> List.of(TestFixture.createProduct("메뉴", 1000L)))
                .when(productRepository).findAll();

        var result = productService.findAll();

        assertThat(result).isNotEmpty();
    }
}
