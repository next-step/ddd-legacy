package kitchenpos.menu.unit;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.support.AssertUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.menu.fixture.MenuFixture.A_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.가격마이너스_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.가격미존재_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.마이너스_수량의_제품을_가진_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.메뉴가격이_제품목록의_가격합계보다_높은메뉴;
import static kitchenpos.menu.fixture.MenuFixture.빈_제품목록_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.이름미존재_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.제품목록미존재_메뉴;
import static kitchenpos.support.RandomPriceUtil.랜덤한_1000원이상_3000원이하의_금액을_생성한다;
import static kitchenpos.support.RandomPriceUtil.랜덤한_마이너스_1000원이하_금액을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        this.menuService = new MenuService(
                menuRepository, menuGroupRepository, productRepository, purgomalumClient
        );
    }

    @Nested
    class 등록 {

        @Test
        @DisplayName("[성공] 메뉴를 등록한다.")
        void create() {
            // given
            var 메뉴그룹 = A_메뉴.getMenuGroup();
            given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

            var 제품목록 = A_메뉴.getMenuProducts().stream()
                    .map(MenuProduct::getProduct)
                    .toList();
            given(productRepository.findAllByIdIn(any())).willReturn(제품목록);
            for (var 제품 : 제품목록) {
                given(productRepository.findById(제품.getId())).willReturn(Optional.of(제품));
            }

            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(menuRepository.save(any())).willReturn(A_메뉴);

            // when
            var saved = menuService.create(A_메뉴);

            // then
            assertAll(
                    () -> then(menuRepository).should(times(1)).save(any()),
                    () -> assertThat(saved.getName()).isEqualTo(A_메뉴.getName())
            );
        }

        @Nested
        class 가격검증 {

            @Test
            @DisplayName("[실패] 메뉴의 가격을 입력하지 않으면 메뉴는 등록이 되지 않는다.")
            void 메뉴_가격_null() {
                // when & then
                assertThatThrownBy(() -> menuService.create(가격미존재_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 메뉴의 가격이 0원보다 낮으면 메뉴는 등록이 되지 않는다.")
            void 메뉴_가격_마이너스() {
                // when & then
                assertThatThrownBy(() -> menuService.create(가격마이너스_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 메뉴의 가격이 제품 목록의 가격 합계보다 높으면 메뉴는 등록이 되지 않는다.")
            void 메뉴_가격_제품_목록의_가격_합계보다_높음() {
                // given
                var 메뉴그룹 = 메뉴가격이_제품목록의_가격합계보다_높은메뉴.getMenuGroup();
                given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

                var 제품목록 = 메뉴가격이_제품목록의_가격합계보다_높은메뉴.getMenuProducts().stream()
                        .map(MenuProduct::getProduct)
                        .toList();
                given(productRepository.findAllByIdIn(any())).willReturn(제품목록);
                for (var 제품 : 제품목록) {
                    given(productRepository.findById(제품.getId())).willReturn(Optional.of(제품));
                }

                // when & then
                assertThatThrownBy(() -> menuService.create(메뉴가격이_제품목록의_가격합계보다_높은메뉴))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        class 메뉴그룹등록여부검증 {

            @Test
            @DisplayName("[실패] 등록하려는 메뉴가 등록되지 않은 메뉴그룹에 포함될 경우 메뉴는 등록이 되지 않는다.")
            void 메뉴그룹_미등록() {
                // given
                given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> menuService.create(A_메뉴))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

        @Nested
        class 제품목록검증 {

            @Test
            @DisplayName("[실패] 메뉴에 포함되어 있는 제품 목록이 존재하지 않으면 메뉴는 등록되지 않는다.")
            void 제품목록_null() {
                // given
                var 메뉴그룹 = 제품목록미존재_메뉴.getMenuGroup();
                given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

                // when & then
                assertThatThrownBy(() -> menuService.create(제품목록미존재_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 메뉴에 포함되어 있는 제품 목록이 비어있을 경우 메뉴는 등록되지 않는다.")
            void 제품목록_empty() {
                // given
                var 메뉴그룹 = 빈_제품목록_메뉴.getMenuGroup();
                given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

                // when & then
                assertThatThrownBy(() -> menuService.create(빈_제품목록_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 메뉴에 포함되어 있는 제품 목록 중 등록되지 않은 제품이 포함되어 있을 경우 메뉴는 등록되지 않는다.")
            void 제품목록_미등록_제품포함() {
                // given
                var 메뉴그룹 = A_메뉴.getMenuGroup();
                given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

                var 제품목록 = A_메뉴.getMenuProducts().stream()
                        .map(MenuProduct::getProduct)
                        .toList();
                given(productRepository.findAllByIdIn(any())).willReturn(제품목록);
                given(productRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> menuService.create(A_메뉴))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("[실패] 메뉴에 포함되어 있는 제품 목록 중 0개 미만의 수량을 가진 제품이 포함되어 있을 경우 메뉴는 등록되지 않는다.")
            void 제품목록_0개미만수량인_제품포함() {
                // given
                var 메뉴그룹 = 마이너스_수량의_제품을_가진_메뉴.getMenuGroup();
                given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

                var 제품목록 = 마이너스_수량의_제품을_가진_메뉴.getMenuProducts().stream()
                        .map(MenuProduct::getProduct)
                        .toList();
                given(productRepository.findAllByIdIn(any())).willReturn(제품목록);

                // when & then
                assertThatThrownBy(() -> menuService.create(마이너스_수량의_제품을_가진_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        class 이름검증 {

            @Test
            @DisplayName("[실패] 메뉴의 이름을 입력하지 않으면 등록이 되지 않는다.")
            void 메뉴_이름_null() {
                // given
                var 메뉴그룹 = 이름미존재_메뉴.getMenuGroup();
                given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

                var 제품목록 = 이름미존재_메뉴.getMenuProducts().stream()
                        .map(MenuProduct::getProduct)
                        .toList();
                given(productRepository.findAllByIdIn(any())).willReturn(제품목록);
                for (var 제품 : 제품목록) {
                    given(productRepository.findById(제품.getId())).willReturn(Optional.of(제품));
                }

                // when & then
                assertThatThrownBy(() -> menuService.create(이름미존재_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);

            }

            @Test
            @DisplayName("[실패] 메뉴의 이름에 부적절한 단어(욕설 등)가 포함되면 등록이 되지 않는다.")
            void 메뉴_이름_욕설() {
                // given
                var 욕설이름_메뉴 = A_메뉴;
                var 메뉴그룹 = 욕설이름_메뉴.getMenuGroup();
                given(menuGroupRepository.findById(any())).willReturn(Optional.ofNullable(메뉴그룹));

                var 제품목록 = 욕설이름_메뉴.getMenuProducts().stream()
                        .map(MenuProduct::getProduct)
                        .toList();
                given(productRepository.findAllByIdIn(any())).willReturn(제품목록);
                for (var 제품 : 제품목록) {
                    given(productRepository.findById(제품.getId())).willReturn(Optional.of(제품));
                }

                given(purgomalumClient.containsProfanity(any())).willReturn(true);

                // when & then
                assertThatThrownBy(() -> menuService.create(욕설이름_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);

            }

        }

    }

    @Nested
    class 가격수정 {

        @Test
        @DisplayName("[성공] 메뉴의 가격을 수정한다.")
        void changePrice() {
            // given
            given(menuRepository.findById(any())).willReturn(Optional.of(A_메뉴));

            // when
            var 수정할_내용 = new Menu();
            var price = A_메뉴.getPrice().subtract(new BigDecimal(100));
            수정할_내용.setPrice(price);
            var updated = menuService.changePrice(UUID.randomUUID(), 수정할_내용);

            // then
            AssertUtils.가격이_동등한가(updated.getPrice(), price);
        }

        @Nested
        class 가격검증 {


            @Test
            @DisplayName("[실패] 변경할 메뉴의 가격을 입력하지 않으면 메뉴 가격은 수정되지 않는다.")
            void 메뉴_가격_null() {
                // when & then
                var 수정할_내용 = new Menu();
                assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), 수정할_내용))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 변경할 메뉴의 가격이 0원보다 낮으면 메뉴 가격은 수정되지 않는다.")
            void 메뉴_가격_마이너스() {
                // when & then
                var 수정할_내용 = new Menu();
                수정할_내용.setPrice(랜덤한_마이너스_1000원이하_금액을_생성한다());

                assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), 수정할_내용))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 변경할 메뉴의 가격이 제품 목록의 가격 합계보다 높으면 메뉴는 등록이 되지 않는다.")
            void 메뉴_가격_제품_목록의_가격_합계보다_높음() {
                // given
                given(menuRepository.findById(any())).willReturn(Optional.of(A_메뉴));

                // when & then
                var 수정할_내용 = new Menu();
                var price = A_메뉴.getPrice().add(new BigDecimal(1000));
                수정할_내용.setPrice(price);
                assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), 수정할_내용))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class 메뉴등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 메뉴 아이디인 경우 메뉴 가격이 수정되지 않는다.")
            void 메뉴_미등록() {
                // given
                given(menuRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                var 수정할_내용 = new Menu();
                수정할_내용.setPrice(랜덤한_1000원이상_3000원이하의_금액을_생성한다());
                assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), 수정할_내용))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

    }

    @Nested
    class 숨김해제 {

        @Test
        @DisplayName("[성공] 메뉴를 숨김해제 처리를 한다.")
        void display() {

        }

        @Nested
        class 메뉴등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 메뉴 아이디인 경우 메뉴를 숨김해제 처리가 되지 않는다.")
            void 메뉴_미등록() {

            }

        }

        @Nested
        class 가격검증 {

            @Test
            @DisplayName("[실패] 메뉴의 가격이 제품 목록의 가격 합계보다 높으면 메뉴는 숨김해제 처리가 되지 않는다.")
            void 메뉴_가격_제품_목록의_가격_합계보다_높음() {

            }

        }

    }

    @Nested
    class 숨김 {

        @Test
        @DisplayName("[성공] 메뉴를 숨김처리를 한다.")
        void hide() {

        }

        @Nested
        class 메뉴등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 메뉴 아이디인 경우 메뉴를 숨김해제 처리가 되지 않는다.")
            void 메뉴_미등록() {

            }

        }

    }
}
