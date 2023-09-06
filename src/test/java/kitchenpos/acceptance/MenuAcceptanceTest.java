package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import kitchenpos.test_fixture.ProductTestFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.acceptance.acceptance_step.MenuGroupStep.메뉴_그룹_등록된_상태다;
import static kitchenpos.acceptance.acceptance_step.MenuStep.*;
import static kitchenpos.acceptance.acceptance_step.ProductStep.상품의_가격을_변경한다;
import static kitchenpos.acceptance.acceptance_step.ProductStep.상품이_등록된_상태다;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("메뉴 인수 테스트")
class MenuAcceptanceTest extends AcceptanceTestBase {

    @Nested
    class 새로운_메뉴_등록_인수_테스트 {
        @Test
        void 메뉴_등록에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴_등록에_성공했다(response);
        }

        @Test
        void 메뉴_등록_시_가격_정보를_입력하지_않으면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .changePrice(null)
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴_가격을_입력하지_않아서_메뉴_등록에_실패헀다(response);
        }

        @Test
        void 메뉴_등록_시_가격을_음수로_입력하면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .changePrice(BigDecimal.valueOf(-1))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴_가격을_음수로_입력하여_메뉴_등록에_실패헀다(response);
        }

        @Test
        void 메뉴가_포함될_메뉴_그룹이_등록된_상태가_아니면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록되지_않은_메뉴_그룹 = MenuGroupTestFixture.create()
                    .changeId(null)
                    .changeName("메뉴그룹1")
                    .getMenuGroup();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록되지_않은_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴가_포함될_메뉴_그룹이_등록된_상태가_아니라서_메뉴_등록에_실패했다(response);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 메뉴에_포함될_상품_정보가_비어있으면_메뉴_등록에_실패한다(List<MenuProduct> 비어있는_메뉴_상품) {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(비어있는_메뉴_상품)
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴에_포함될_상품_정보가_비어있어서_메뉴_등록에_실패헀다(response);
        }

        @Test
        void 메뉴에_포함될_상품의_개수가_음수면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(-1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .changePrice(BigDecimal.valueOf(100000))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴에_포함될_상품의_개수가_음수라서_메뉴_등록에_실패했다(response);
        }

        @Test
        void 메뉴에_포함될_상품이_등록된_상품이_아니라면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록되지_않은_상품 = ProductTestFixture.create().getProduct();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록되지_않은_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .changePrice(BigDecimal.valueOf(100000))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴에_포함될_상품이_등록된_상품이_아니라서_메뉴_등록에_실패했다(response);
        }

        @Test
        void 메뉴_가격이_메뉴에_포함된_모든_상품의_합보다_비싸면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .changePrice(등록된_상품.getPrice().add(BigDecimal.ONE))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴_가격이_메뉴에_포함된_모든_상품의_합보다_비싸서_메뉴_등록에_실패했다(response);
        }

        @Test
        void 메뉴_이름을_입력하지_않으면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeName(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴_이름을_입력하지_않아서_메뉴_등록에_실패했다(response);
        }

        @Test
        void 메뉴_이름을_비속어로_입력하면_메뉴_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                    .changeProduct(등록된_상품)
                    .changeQuantity(1)
                    .getMenuProduct();
            Menu 등록할_메뉴 = MenuTestFixture.create()
                    .changeId(null)
                    .changeName("bastard") // `새끼` 라는 나쁜말 ^^
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴를_등록한다(등록할_메뉴);

            // then
            메뉴_이름을_비속어로_입력해서_메뉴_등록에_실패했다(response);
        }
    }

    @Nested
    class 메뉴_가격_변경_인수_테스트 {
        @Test
        void 메뉴_가격_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Menu 가격_변경_요청_정보 = MenuTestFixture.create()
                    .changeId(등록된_메뉴.getId())
                    .changeName(등록된_메뉴.getName())
                    .changePrice(등록된_메뉴.getPrice().subtract(BigDecimal.ONE))
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(등록된_메뉴.getMenuProducts())
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴_가격을_변경한다(가격_변경_요청_정보);

            // then
            메뉴_가격_변경에_성공했다(response, 등록된_메뉴.getPrice().subtract(BigDecimal.ONE));
        }

        @Test
        void 변경할_가격을_입력하지_않으면_가격_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Menu 가격_변경_요청_정보 = MenuTestFixture.create()
                    .changeId(등록된_메뉴.getId())
                    .changeName(등록된_메뉴.getName())
                    .changePrice(null)
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(등록된_메뉴.getMenuProducts())
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴_가격을_변경한다(가격_변경_요청_정보);

            // then
            변경할_가격을_입력하지_않아서_가격_변경에_실패했다(response);
        }

        @Test
        void 변경할_가격을_음수로_입력하면_가격_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Menu 가격_변경_요청_정보 = MenuTestFixture.create()
                    .changeId(등록된_메뉴.getId())
                    .changeName(등록된_메뉴.getName())
                    .changePrice(BigDecimal.valueOf(-1))
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(등록된_메뉴.getMenuProducts())
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴_가격을_변경한다(가격_변경_요청_정보);

            // then
            변경할_가격을_음수로_입력해서_가격_변경에_실패했다(response);
        }

        @Test
        void 변경할_메뉴가_존재하지_않으면_가격_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록되지_않은_메뉴 = 메뉴가_등록되지_않은_상태다(등록된_상품, 등록된_메뉴_그룹);
            Menu 가격_변경_요청_정보 = MenuTestFixture.create()
                    .changeId(등록되지_않은_메뉴.getId())
                    .changeName(등록되지_않은_메뉴.getName())
                    .changePrice(등록되지_않은_메뉴.getPrice().subtract(BigDecimal.valueOf(-1)))
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(등록되지_않은_메뉴.getMenuProducts())
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴_가격을_변경한다(가격_변경_요청_정보);

            // then
            변경할_메뉴가_존재하지_않아서_가격_변경에_실패했다(response);
        }

        @Test
        void 변경할_가격이_메뉴에_포함된_모든_상품의_합보다_비싸면_가격_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Menu 가격_변경_요청_정보 = MenuTestFixture.create()
                    .changeId(등록된_메뉴.getId())
                    .changeName(등록된_메뉴.getName())
                    .changePrice(등록된_상품.getPrice().add(BigDecimal.ONE))
                    .changeMenuGroup(등록된_메뉴_그룹)
                    .changeMenuProducts(등록된_메뉴.getMenuProducts())
                    .getMenu();

            // when
            ExtractableResponse<Response> response = 메뉴_가격을_변경한다(가격_변경_요청_정보);

            // then
            메뉴_가격이_메뉴에_포함된_모든_상품의_합보다_비싸서_가격_변경에_실패했다(response);
        }
    }

    @Nested
    class 메뉴_전시_상태_변경_인수테스트 {
        @Test
        void 메뉴_전시_상태_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);

            // when
            ExtractableResponse<Response> response = 메뉴를_전시_상태로_변경한다(등록된_메뉴);

            // then
            메뉴_전시_상태_변경에_성공했다(response);
        }

        @Test
        void 존재하지_않는_메뉴를_전시_상태로_변경하면_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록되지_않은_메뉴 = 메뉴가_등록되지_않은_상태다(등록된_상품, 등록된_메뉴_그룹);

            // when
            ExtractableResponse<Response> response = 메뉴를_전시_상태로_변경한다(등록되지_않은_메뉴);

            // then
            존재하지_않는_메뉴라서_전시_상태_변경에_실패했다(response);
        }

        @Test
        void 메뉴_가격이_메뉴에_포함된_상품_가격의_총합보다_비싸면_전시_상태_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Product 상품_가격을_메뉴_가격보다_작게_변경 = ProductTestFixture.create()
                    .changeId(등록된_상품.getId())
                    .changeName(등록된_상품.getName())
                    .changePrice(등록된_상품.getPrice().subtract(BigDecimal.ONE))
                    .getProduct();
            상품의_가격을_변경한다(등록된_상품.getId(), 상품_가격을_메뉴_가격보다_작게_변경);

            // when
            ExtractableResponse<Response> response = 메뉴를_전시_상태로_변경한다(등록된_메뉴);

            // then
            메뉴_가격이_메뉴에_포함된_상품_가격의_총합보다_비싸서_전시_상태_변경에_실패했다(response);
        }
    }

    @Nested
    class 메뉴_숨김_상태_변경_인수테스트 {
        @Test
        void 메뉴_숨김_상태_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            ExtractableResponse<Response> 메뉴를_전시_상태를_변경_응답 = 메뉴를_전시_상태로_변경한다(등록된_메뉴);
            메뉴_전시_상태_변경에_성공했다(메뉴를_전시_상태를_변경_응답);


            // when
            ExtractableResponse<Response> response = 메뉴를_숨김_상태로_변경한다(등록된_메뉴);

            // then
            메뉴_숨김_상태_변경에_성공했다(response);
        }

        @Test
        void 존재하지_않는_메뉴를_숨김_상태로_변경하면_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록되지_않은_메뉴 = 메뉴가_등록되지_않은_상태다(등록된_상품, 등록된_메뉴_그룹);

            // when
            ExtractableResponse<Response> response = 메뉴를_숨김_상태로_변경한다(등록되지_않은_메뉴);

            // then
            존재하지_않는_메뉴라서_숨김_상태_변경에_실패했다(response);
        }
    }
}
