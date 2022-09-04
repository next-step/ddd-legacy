package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    private MenuService testService;

    @Nested
    @DisplayName("메뉴 등록")
    class Create {
        @Mock
        private MenuGroup existingMenuGroup;

        @DisplayName("가격은 음수가 아니어야 한다.")
        @Test
        void negativePrice() {
            // given
            final var request = new Menu();
            request.setPrice(new BigDecimal(-500));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록된 메뉴그룹을 선택해야 한다.")
        @Test
        void menuGroupNotFound() {
            // given
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("상품을 하나 이상 가지고 있어야 한다.")
        @ParameterizedTest(name = "상품은 비어 있지 않아야 한다. 상품목록={0}")
        @NullAndEmptySource
        void nullOrEmptyMenuProducts(List<MenuProduct> menuProducts) {
            // given
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            request.setMenuProducts(menuProducts);

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록하려는 메뉴의 상품라스트에 중복된 상품이 없어야 한다.")
        @Test
        void duplicatedProducts() {
            // given
            final var productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            final var duplicatedMenuProduct1 = new MenuProduct();
            final var duplicatedMenuProduct2 = new MenuProduct();
            duplicatedMenuProduct1.setProductId(productId);
            duplicatedMenuProduct2.setProductId(productId);
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            request.setMenuProducts(List.of(duplicatedMenuProduct1, duplicatedMenuProduct2));

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));
            final var productInRepo = new Product();
            productInRepo.setId(productId);
            productInRepo.setPrice(new BigDecimal(2000));
            when(productRepository.findAllByIdIn(List.of(duplicatedMenuProduct1.getProductId(), duplicatedMenuProduct2.getProductId())))
                    .thenReturn(List.of(productInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록하려는 메뉴의 상품 개수는 음수가 아니어야 한다.")
        @Test
        void negativeProductQuantity() {
            // given
            final var productQuantity = -1;
            final var productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            final var menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(productQuantity);
            request.setMenuProducts(List.of(menuProduct));

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));
            final var productInRepo = new Product();
            productInRepo.setId(productId);
            productInRepo.setPrice(new BigDecimal(2000));
            when(productRepository.findAllByIdIn(List.of(productId))).thenReturn(List.of(productInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록하려는 메뉴의 상품이 존재해야 한다.")
        @Test
        void productNotFound() {
            // given
            final var productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            final var menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(1);
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            request.setMenuProducts(List.of(menuProduct));

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));
            final var productInRepo = new Product();
            productInRepo.setId(productId);
            productInRepo.setPrice(new BigDecimal(2000));
            when(productRepository.findAllByIdIn(List.of(menuProduct.getProductId()))).thenReturn(List.of(productInRepo));
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴 가격은 메뉴에 속한 상품 가격의 합보다 작거나 같아야 한다.")
        @Test
        void menuPriceShouldLessThanOrEqualToProductSumPrice() {
            // given
            final var menuPrice = new BigDecimal(10000);
            final var productPrice = new BigDecimal(2000);

            final var productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            final var menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(1);
            final var request = new Menu();
            request.setPrice(menuPrice);
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            request.setMenuProducts(List.of(menuProduct));

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));
            final var productInRepo = new Product();
            productInRepo.setId(productId);
            productInRepo.setPrice(productPrice);
            when(productRepository.findAllByIdIn(List.of(menuProduct.getProductId()))).thenReturn(List.of(productInRepo));
            when(productRepository.findById(productId)).thenReturn(Optional.of(productInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 비어 있지 않아야 한다.")
        @ParameterizedTest(name = "메뉴 이름이 [{0}]이 아니어야 한다.")
        @NullSource
        void nullName(String name) {
            // given
            final var productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            final var menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(1);
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            request.setMenuProducts(List.of(menuProduct));
            request.setName(name);

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));
            final var productInRepo = new Product();
            productInRepo.setId(productId);
            productInRepo.setPrice(new BigDecimal(2000));
            when(productRepository.findAllByIdIn(List.of(menuProduct.getProductId()))).thenReturn(List.of(productInRepo));
            when(productRepository.findById(productId)).thenReturn(Optional.of(productInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 비속어가 포함되지 않아야 한다.")
        @Test
        void nameContainsProfanity() {
            // given
            final var productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            final var menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(1);
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            request.setMenuProducts(List.of(menuProduct));
            request.setName("나쁜말");

            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));
            final var productInRepo = new Product();
            productInRepo.setId(productId);
            productInRepo.setPrice(new BigDecimal(2000));
            when(productRepository.findAllByIdIn(List.of(menuProduct.getProductId()))).thenReturn(List.of(productInRepo));
            when(productRepository.findById(productId)).thenReturn(Optional.of(productInRepo));
            when(purgomalumClient.containsProfanity("나쁜말")).thenReturn(true);

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴를 등록할 수 있다.")
        @Test
        void create() {
            // given
            final var productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            final var menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(1);
            final var request = new Menu();
            request.setPrice(new BigDecimal(2000));
            request.setMenuGroupId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            request.setMenuProducts(List.of(menuProduct));
            request.setName("아메리카노");

            when(existingMenuGroup.getId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(existingMenuGroup));
            final var productInRepo = new Product();
            productInRepo.setId(productId);
            productInRepo.setPrice(new BigDecimal(2000));
            when(productRepository.findAllByIdIn(List.of(menuProduct.getProductId()))).thenReturn(List.of(productInRepo));
            when(productRepository.findById(productId)).thenReturn(Optional.of(productInRepo));
            when(purgomalumClient.containsProfanity(any())).thenReturn(false);
            when(menuRepository.save(any())).thenAnswer((invocation -> invocation.getArgument(0)));

            // when
            final var result = testService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).as("메뉴아이디").isNotNull(),
                    () -> assertThat(result.getPrice()).as("메뉴가격").isEqualTo(new BigDecimal(2000)),
                    () -> assertThat(result.getName()).as("메뉴이름").isEqualTo("아메리카노"),
                    () -> assertThat(result.getMenuGroup().getId()).as("메뉴그룹").isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111")),
                    () -> assertThat(result.getMenuProducts()).as("상품목록")
                            .extracting((menuProduct1 -> menuProduct1.getProduct().getId()))
                            .containsExactly(UUID.fromString("22222222-2222-2222-2222-222222222222"))
            );
        }
    }
}
