package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fake.FakeMenuGroupRepository;
import kitchenpos.fake.FakeMenuRepository;
import kitchenpos.fake.FakeProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.*;
import static kitchenpos.fixture.MenuProductFixture.*;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    PurgomalumClient purgomalumClient;

    MenuGroupService menuGroupService;
    ProductService productService;
    MenuService menuService;

    MenuGroup 한마리_그룹;
    Product 양념치킨;
    Menu 양념치킨_메뉴;

    @BeforeEach
    void setUp() {
        FakeMenuGroupRepository fakeMenuGroupRepository = new FakeMenuGroupRepository();
        FakeProductRepository fakeProductRepository = new FakeProductRepository();
        FakeMenuRepository fakeMenuRepository = new FakeMenuRepository();
        this.menuGroupService = new MenuGroupService(fakeMenuGroupRepository);
        this.productService = new ProductService(fakeProductRepository, fakeMenuRepository, purgomalumClient);
        this.menuService = new MenuService(fakeMenuRepository, fakeMenuGroupRepository, fakeProductRepository, purgomalumClient);
        this.한마리_그룹 = menuGroupService.create(한마리());
        this.양념치킨 = productService.create(양념치킨());
        this.양념치킨_메뉴 = 양념치킨_메뉴(한마리_그룹, 양념치킨);
    }

    @Nested
    class 메뉴_전체기능_정상_테스트 {

        @Test
        void 메뉴를_생성한다() {
            final Menu 결과 = menuService.create(양념치킨_메뉴);

            assertThat(결과.getId()).isNotNull();
        }

        @Test
        void 메뉴_목록을_조회한다() {
            menuService.create(양념치킨_메뉴);

            final List<Menu> 결과 = menuService.findAll();

            assertThat(결과).hasSize(1);
        }

        @Test
        void 메뉴의_가격을_변경한다() {
            Menu 등록된_양념치킨 = menuService.create(양념치킨_메뉴);

            등록된_양념치킨.setPrice(BigDecimal.valueOf(30000L));
            final Menu 가격변경된_메뉴 = menuService.changePrice(등록된_양념치킨.getId(), 등록된_양념치킨);

            assertThat(가격변경된_메뉴.getPrice()).isEqualTo(BigDecimal.valueOf(30000L));
        }

        @Test
        void 메뉴를_노출한다() {
            Menu 등록된_양념치킨 = menuService.create(양념치킨_메뉴);

            final Menu 메뉴판에_노출할_메뉴 = menuService.display(등록된_양념치킨.getId());

            assertThat(메뉴판에_노출할_메뉴.isDisplayed()).isTrue();
        }

        @Test
        void 메뉴를_숨긴다() {
            Menu 등록된_양념치킨 = menuService.create(양념치킨_메뉴);

            final Menu 메뉴판에_노출할_메뉴 = menuService.hide(등록된_양념치킨.getId());

            assertThat(메뉴판에_노출할_메뉴.isDisplayed()).isFalse();
        }
    }

    @Nested
    class 메뉴_등록_예외_테스트 {
        @Test
        void 메뉴의_가격이_없으면_등록을_실패한다() {
            Menu 무료_메뉴 = 비어있는_메뉴();

            assertThatThrownBy(() -> menuService.create(무료_메뉴))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_메뉴그룹의_메뉴는_등록할_수_없다() {
            Menu 메뉴그룹이_없는_메뉴 = 간장치킨_메뉴();

            assertThatThrownBy(() -> menuService.create(메뉴그룹이_없는_메뉴))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 메뉴는_0개_이상의_메뉴상품이_포함되어야_한다() {
            MenuGroup 등록된_메뉴_그룹 = menuGroupService.create(반반());
            Menu 메뉴상품_없는_메뉴 = 메뉴상품_없는_메뉴(등록된_메뉴_그룹);

            assertThatThrownBy(() -> menuService.create(메뉴상품_없는_메뉴))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 메뉴상품의_수와_등록된_상품의_수가_일치하지_않으면_실패한다() {
            MenuGroup 등록된_메뉴_그룹 = menuGroupService.create(반반());
            Menu 상품없는_반반_메뉴 = 상품없는_메뉴(등록된_메뉴_그룹, 간장치킨_메뉴_상품());

            assertThatThrownBy(() -> menuService.create(상품없는_반반_메뉴))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 상품의_수량이_음수면_메뉴를_등록할_수_없다() {
            Product 등록된_상품 = productService.create(무료치킨());
            MenuGroup 등록된_메뉴_그룹 = menuGroupService.create(반반());
            Menu 상품_수량이_0개인_메뉴 = 상품없는_메뉴(등록된_메뉴_그룹, 수량이_음수인_메뉴상품(등록된_상품));

            assertThatThrownBy(() -> menuService.create(상품_수량이_0개인_메뉴))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 메뉴의_이름이_비어있으면_등록이_실패한다() {
            Menu 비어있는_메뉴 = 비어있는_메뉴();

            assertThatThrownBy(() -> menuService.create(비어있는_메뉴))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 메뉴_가격변경_예외_테스트 {

        @Test
        void 가격이_0원_미만이면_가격을_변경할_수_없다() {
            Menu 무료_메뉴 = 무료_메뉴();
            UUID 무료_메뉴_아이디 = 무료_메뉴.getId();

            assertThatThrownBy(() -> menuService.changePrice(무료_메뉴_아이디, 무료_메뉴))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 등록되지_않은_메뉴의_가격은_변경할_수_없다() {
            Menu 이름없는_메뉴 = 이름없는_메뉴();
            UUID 이름없는_메뉴_아이디 = 이름없는_메뉴.getId();

            assertThatThrownBy(() -> menuService.changePrice(이름없는_메뉴_아이디, 이름없는_메뉴))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }


    @Nested
    class 메뉴_노출여부_예외_테스트 {

        @Test
        void 등록되지_않은_메뉴는_노출할_수_없다() {
            Menu 비어있는_메뉴 = 비어있는_메뉴();
            UUID 비어있는_메뉴_아이디 = 비어있는_메뉴.getId();

            assertThatThrownBy(() -> menuService.display(비어있는_메뉴_아이디))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 메뉴의_총_가격이_0_미만이면_노출할_수_없다() {
            Menu 비어있는_메뉴 = 비어있는_메뉴();
            UUID 비어있는_메뉴_아이디 = 비어있는_메뉴.getId();

            assertThatThrownBy(() -> menuService.display(비어있는_메뉴_아이디))
                    .isInstanceOf(NoSuchElementException.class);
        }

    }


}