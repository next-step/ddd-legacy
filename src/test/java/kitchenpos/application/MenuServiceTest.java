package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;
import kitchenpos.domain.FakeMenuGroupRepository;
import kitchenpos.domain.FakeMenuRepository;
import kitchenpos.domain.FakeProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakeProfanityDetectClient;
import kitchenpos.infra.ProfanityDetectClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MenuServiceTest {

    private final MenuRepository menuRepository = new FakeMenuRepository();

    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();

    private final ProductRepository productRepository = new FakeProductRepository();

    private final ProfanityDetectClient profanityDetectClient = new FakeProfanityDetectClient();

    // SUT

    private final MenuService menuService = new MenuService(
            menuRepository,
            menuGroupRepository,
            productRepository,
            profanityDetectClient
    );

    @DisplayName("생성")
    @Nested
    class Xbsrsixe {

        @DisplayName("메뉴를 생성할 수 있다.")
        @ValueSource(strings = {
                "get", "dead", "drink", "preference", "purple",
                "account", "angle", "seem", "strike", "per",
        })
        @ParameterizedTest
        void oxxpvzbb(final String name) {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName(name);
            requestMenu.setPrice(BigDecimal.valueOf(10000));
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when
            final Menu menu = menuService.create(requestMenu);

            // then
            assertThat(menu.getName()).isEqualTo(name);
        }

        @DisplayName("메뉴 이름은 비어있을 수 없다.")
        @NullAndEmptySource
        @ParameterizedTest
        void mpcfgqtl(final String name) {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName(name);
            requestMenu.setPrice(BigDecimal.valueOf(10000));
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("메뉴 이름은 비속어를 포함할 수 없다.")
        @ValueSource(strings = {
                "holiday", "bed", "anxious", "everyday", "reach",
                "private holiday", "courage bed", "about anxious", "knee everyday", "number reach",
                "holiday roll", "bed avenue", "anxious needle", "everyday chairman", "reach cape",
                "interrupt holiday sauce", "explore bed radio", "return anxious motherly",
                "entertain everyday cause", "separate reach they",
        })
        @ParameterizedTest
        void tncdtxzy(final String name) {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName(name);
            requestMenu.setPrice(BigDecimal.valueOf(10000));
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("가격은 비어있을 수 없다.")
        @Test
        void geyqzwaz() {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName("deal");
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("가격은 음수일 수 없다.")
        @ValueSource(longs = {
                -1558837739, -1658360731, -2027227346, -2034904425, -964948430,
                -1795738562, -360013043, -1258878092, -1850806317, -669728729,
        })
        @ParameterizedTest
        void hwjpzyir(final Long price) {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName("sore");
            requestMenu.setPrice(BigDecimal.valueOf(price));
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("가격은 메뉴에 포함된 상품 가격의 합보다 클 수 없다.")
        @Test
        void jczxmdbp() {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName("rabbit");
            requestMenu.setPrice(BigDecimal.valueOf(15000));
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("메뉴는 하나의 메뉴 그룹에 포함되어야 한다.")
        @Test
        void czdetpdi() {
            // given
            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName("rabbit");
            requestMenu.setPrice(BigDecimal.valueOf(15000));
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                    menuService.create(requestMenu));
        }

        @DisplayName("메뉴에 포함된 상품이 없을 수 없다.")
        @NullAndEmptySource
        @ParameterizedTest
        void tkwhqfsg(final List<MenuProduct> menuProducts) {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Menu requestMenu = new Menu();
            requestMenu.setName("rabbit");
            requestMenu.setPrice(BigDecimal.valueOf(15000));
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("메뉴에 포함된 각 상품별 수량은 음수일 수 없다.")
        @ValueSource(longs = {
                -19, -24, -31, -27, -1,
                -10, -22, -12, -28, -13,
        })
        @ParameterizedTest
        void ncxhikqf(final long quantity) {
            // given
            final MenuGroup menuGroup = new MenuGroup();
            menuGroupRepository.save(menuGroup);

            final Product product = new Product();
            product.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product);

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(quantity);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu requestMenu = new Menu();
            requestMenu.setName("rabbit");
            requestMenu.setPrice(BigDecimal.valueOf(15000));
            requestMenu.setMenuGroupId(menuGroup.getId());
            requestMenu.setDisplayed(false);
            requestMenu.setMenuProducts(menuProducts);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(requestMenu));
        }
    }

    @DisplayName("가격 변경(changePrice)")
    @Nested
    class Feewfxpz {

        @DisplayName("가격을 변경할 수 있다.")
        @Test
        void ufrmqryu() {
            // given
            final Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setPrice(BigDecimal.valueOf(10000));

            final Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setPrice(BigDecimal.valueOf(10000));

            final MenuProduct menuProduct1 = new MenuProduct();
            menuProduct1.setProduct(product1);
            menuProduct1.setProductId(product1.getId());
            menuProduct1.setQuantity(1);
            menuProduct1.setSeq(1L);

            final MenuProduct menuProduct2 = new MenuProduct();
            menuProduct2.setProduct(product2);
            menuProduct2.setProductId(product2.getId());
            menuProduct2.setQuantity(1);
            menuProduct2.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct1);
            menuProducts.add(menuProduct2);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("preference");
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(10000));
            menuRepository.save(menu);

            BigDecimal price = BigDecimal.valueOf(15000);

            final Menu requestMenu = new Menu();
            requestMenu.setPrice(price);

            // when
            final Menu updatedMenu = menuService.changePrice(menu.getId(), requestMenu);

            // then
            assertThat(updatedMenu.getPrice()).isEqualTo(price);

            final Menu foundMenu = menuRepository.findById(menu.getId())
                    .orElse(null);
            assertThat(foundMenu).isNotNull();
            assertThat(foundMenu.getPrice()).isEqualTo(price);
        }

        @DisplayName("가격은 비어있을 수 없다.")
        @Test
        void zxjufrhq() {
            // given
            final Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(10000));

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1);
            menuProduct.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("president");
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(10000));
            menuRepository.save(menu);

            final Menu requestMenu = new Menu();

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                    menuService.changePrice(menu.getId(), requestMenu));
        }

        @DisplayName("가격은 비어있을 수 없다.")
        @Test
        void wacgnvsp() {
            // given
            final Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(10000));

            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1);
            menuProduct.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("also");
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(10000));
            menuRepository.save(menu);

            final Menu requestMenu = new Menu();
            requestMenu.setPrice(BigDecimal.valueOf(-5000));

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                    menuService.changePrice(menu.getId(), requestMenu));
        }

        @DisplayName("가격을 메뉴에 포함된 모든 상품 가격 합보다 큰 값으로 바꿀 수 없다.")
        @Test
        void jextcqal() {
            // given
            final Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setPrice(BigDecimal.valueOf(10000));

            final Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setPrice(BigDecimal.valueOf(10000));

            final MenuProduct menuProduct1 = new MenuProduct();
            menuProduct1.setProduct(product1);
            menuProduct1.setProductId(product1.getId());
            menuProduct1.setQuantity(1);
            menuProduct1.setSeq(1L);

            final MenuProduct menuProduct2 = new MenuProduct();
            menuProduct2.setProduct(product2);
            menuProduct2.setProductId(product2.getId());
            menuProduct2.setQuantity(1);
            menuProduct2.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct1);
            menuProducts.add(menuProduct2);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("preference");
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(10000));
            menuRepository.save(menu);

            final Menu requestMenu = new Menu();
            requestMenu.setPrice(BigDecimal.valueOf(25000));

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                    menuService.changePrice(menu.getId(), requestMenu));
        }
    }

    @DisplayName("보이기(display)")
    @Nested
    class Bkhijcbm {

        @DisplayName("숨기기 상태인 메뉴를 보일 수 있다.")
        @Test
        void vqmyboxd() {
            // given
            final Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setPrice(BigDecimal.valueOf(10000));

            final Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setPrice(BigDecimal.valueOf(10000));

            final MenuProduct menuProduct1 = new MenuProduct();
            menuProduct1.setProduct(product1);
            menuProduct1.setProductId(product1.getId());
            menuProduct1.setQuantity(1);
            menuProduct1.setSeq(1L);

            final MenuProduct menuProduct2 = new MenuProduct();
            menuProduct2.setProduct(product2);
            menuProduct2.setProductId(product2.getId());
            menuProduct2.setQuantity(1);
            menuProduct2.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct1);
            menuProducts.add(menuProduct2);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("tame");
            menu.setDisplayed(false);
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(15000));
            menuRepository.save(menu);

            // when
            Menu displayedMenu = menuService.display(menu.getId());

            // then
            assertThat(displayedMenu.isDisplayed()).isTrue();

            final Menu foundMenu = menuRepository.findById(menu.getId())
                    .orElse(null);
            assertThat(foundMenu).isNotNull();
            assertThat(foundMenu.isDisplayed()).isTrue();
        }

        @DisplayName("보이기 상태인 메뉴를 다시 보일 수 있다.")
        @Test
        void ubjkvcyt() {
            // given
            final Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setPrice(BigDecimal.valueOf(10000));

            final Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setPrice(BigDecimal.valueOf(10000));

            final MenuProduct menuProduct1 = new MenuProduct();
            menuProduct1.setProduct(product1);
            menuProduct1.setProductId(product1.getId());
            menuProduct1.setQuantity(1);
            menuProduct1.setSeq(1L);

            final MenuProduct menuProduct2 = new MenuProduct();
            menuProduct2.setProduct(product2);
            menuProduct2.setProductId(product2.getId());
            menuProduct2.setQuantity(1);
            menuProduct2.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct1);
            menuProducts.add(menuProduct2);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("local");
            menu.setDisplayed(true);
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(15000));
            menuRepository.save(menu);

            // when
            Menu displayedMenu = menuService.display(menu.getId());

            // then
            assertThat(displayedMenu.isDisplayed()).isTrue();

            final Menu foundMenu = menuRepository.findById(menu.getId())
                    .orElse(null);
            assertThat(foundMenu).isNotNull();
            assertThat(foundMenu.isDisplayed()).isTrue();
        }

        @DisplayName("메뉴의 가격이 메뉴에 포함된 모든 상품 가격 합보다 큰 경우 보일 수 없다.")
        @Test
        void xrncyvic() {
            // given
            final Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setPrice(BigDecimal.valueOf(10000));

            final Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setPrice(BigDecimal.valueOf(10000));

            final MenuProduct menuProduct1 = new MenuProduct();
            menuProduct1.setProduct(product1);
            menuProduct1.setProductId(product1.getId());
            menuProduct1.setQuantity(1);
            menuProduct1.setSeq(1L);

            final MenuProduct menuProduct2 = new MenuProduct();
            menuProduct2.setProduct(product2);
            menuProduct2.setProductId(product2.getId());
            menuProduct2.setQuantity(1);
            menuProduct2.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct1);
            menuProducts.add(menuProduct2);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("lodge");
            menu.setDisplayed(true);
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(30000));
            menuRepository.save(menu);

            // when / then
            assertThatIllegalStateException().isThrownBy(() -> menuService.display(menu.getId()));
        }
    }

    @DisplayName("숨기기(hide)")
    @Nested
    class Iysnkjzi {

        @DisplayName("보이기 상태인 메뉴를 숨길 수 있다.")
        @Test
        void srgksrln() {
            // given
            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("history");
            menu.setDisplayed(true);
            menuRepository.save(menu);

            // when
            Menu hidedMenu = menuService.hide(menu.getId());

            // then
            assertThat(hidedMenu.isDisplayed()).isFalse();

            final Menu foundMenu = menuRepository.findById(menu.getId())
                    .orElse(null);
            assertThat(foundMenu).isNotNull();
            assertThat(foundMenu.isDisplayed()).isFalse();
        }

        @DisplayName("숨기기 상태인 메뉴를 다시 숨길 수 있다.")
        @Test
        void gjcghnyp() {
            // given
            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("destructive");
            menu.setDisplayed(false);
            menuRepository.save(menu);

            // when
            Menu hidedMenu = menuService.hide(menu.getId());

            // then
            assertThat(hidedMenu.isDisplayed()).isFalse();

            final Menu foundMenu = menuRepository.findById(menu.getId())
                    .orElse(null);
            assertThat(foundMenu).isNotNull();
            assertThat(foundMenu.isDisplayed()).isFalse();
        }
    }

    @DisplayName("목록 조회")
    @Nested
    class Dorrlmxj {

        @DisplayName("존재하는 메뉴를 모두 조회할 수 있다.")
        @ValueSource(ints = {
                19, 13, 14, 27, 7,
                2, 18, 17, 26, 12,
        })
        @ParameterizedTest
        void uzcwotkw(final int size) {
            // given
            IntStream.range(0, size)
                    .forEach(n -> {
                        final Menu menu = new Menu();
                        menu.setId(UUID.randomUUID());
                        menu.setName("lot");
                        menuRepository.save(menu);
                    });

            // when
            final List<Menu> menus = menuService.findAll();

            // then
            assertThat(menus).hasSize(size);
        }

        @DisplayName("메뉴가 없는 상태에서 모두 조회시 빈 list가 반환되어야 한다.")
        @Test
        void ljursbre() {
            // when
            final List<Menu> menus = menuService.findAll();

            // then
            assertThat(menus).isEmpty();
        }
    }
}
