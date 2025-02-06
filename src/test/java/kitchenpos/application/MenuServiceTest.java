package kitchenpos.application;

import config.UnitTest;
import kitchenpos.MenuFixture;
import kitchenpos.MenuGroupFixture;
import kitchenpos.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@UnitTest
@DisplayName("메뉴 서비스 테스트")
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
    private MenuService sut;

    private final static BigDecimal 큰_가격 = BigDecimal.valueOf(1000000);
    private final static BigDecimal 음수_가격 = BigDecimal.valueOf(-1);
    private final static long 음수_수량 = -1L;
    private final static UUID 존재하지_않는_메뉴ID = UUID.randomUUID();

    @Nested
    @DisplayName("메뉴 생성")
    class CreateMenuCases {
        @Test
        @DisplayName("메뉴 생성 - 성공: 유효한 입력 값으로 메뉴가 생성된다.")
        void createMenuSuccess() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            Product 콜라_상품 = ProductFixture.콜라_상품_Request();
            Menu expected = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);

            // 메뉴 그룹 및 상품 존재 검증
            given(menuGroupRepository.findById(추천_메뉴그룹.getId())).willReturn(Optional.of(추천_메뉴그룹));
            given(productRepository.findAllByIdIn(List.of(후라이드_치킨_상품.getId(), 콜라_상품.getId())))
                    .willReturn(List.of(후라이드_치킨_상품, 콜라_상품));
            given(productRepository.findById(후라이드_치킨_상품.getId())).willReturn(Optional.of(후라이드_치킨_상품));
            given(productRepository.findById(콜라_상품.getId())).willReturn(Optional.of(콜라_상품));

            // 메뉴명 비속어 검증 (비속어 없음)
            given(purgomalumClient.containsProfanity(expected.getName())).willReturn(false);

            // 메뉴 저장 동작 정의
            given(menuRepository.save(any(Menu.class))).willReturn(expected);

            // when
            var result = sut.create(expected);

            // then
            assertThat(result)
                    .isNotNull()
                    .extracting(Menu::getName, Menu::getPrice, Menu::getMenuGroup)
                    .containsExactly(expected.getName(), expected.getPrice(), expected.getMenuGroup());

            verify(menuRepository).save(any(Menu.class));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 요청 메뉴 가격이 없으면 IllegalArgumentException 발생")
        void createMenuWithoutPriceThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            Product 콜라_상품 = ProductFixture.콜라_상품_Request();

            // 가격이 음수인 메뉴 생성
            Menu 가격이_없는_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            가격이_없는_메뉴.setPrice(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(가격이_없는_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 요청 메뉴 가격이 0원 미만이면 IllegalArgumentException 발생")
        void createMenuWithNegativePriceThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            Product 콜라_상품 = ProductFixture.콜라_상품_Request();

            // 가격이 음수인 메뉴 생성
            Menu 음수_가격_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            음수_가격_메뉴.setPrice(음수_가격);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(음수_가격_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 요청 메뉴 그룹이 없으면 NoSuchElementException 발생")
        void createMenuWithoutMenuGroupThrowNoSuchElementException() {
            // given
            Menu 후라이드_치킨_메뉴 = MenuFixture.후라이드_치킨_메뉴();

            // 메뉴 그룹이 존재하지 않도록 설정
            given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.create(후라이드_치킨_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 요청 메뉴 구성 상품이 하나도 없으면 IllegalArgumentException 발생")
        void createMenuWithoutMenuProductsThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            // 구성 상품이 없는 메뉴 생성
            Menu 구성상품_없는_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹);

            given(menuGroupRepository.findById(추천_메뉴그룹.getId())).willReturn(Optional.of(추천_메뉴그룹));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(구성상품_없는_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 요청된 일부 메뉴 구성 상품 중, 실제 존재하지 않는 상품이 있으면 IllegalArgumentException 발생")
        void createMenuWithNonexistentMenuProductThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 존재하는_상품 = ProductFixture.후라이드_치킨_상품_Request(); // 존재하는 상품
            Product 존재하지_않는_상품 = ProductFixture.콜라_상품_Request(); // 존재하지 않는 상품

            Menu 존재하지_않는_상품_포함_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 존재하는_상품, 존재하지_않는_상품);

            // 메뉴 그룹 존재 검증
            given(menuGroupRepository.findById(추천_메뉴그룹.getId())).willReturn(Optional.of(추천_메뉴그룹));

            // 상품 조회: 존재하는 상품은 반환하지만, 존재하지 않는 상품은 반환하지 않음
            given(productRepository.findAllByIdIn(List.of(존재하는_상품.getId(), 존재하지_않는_상품.getId())))
                    .willReturn(List.of(존재하는_상품));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(존재하지_않는_상품_포함_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 메뉴 구성 상품의 수량이 음수이면 IllegalArgumentException 발생")
        void createMenuWithNegativeMenuProductQuantityThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            MenuProduct 음수_구성상품 = new MenuProduct();
            음수_구성상품.setProduct(후라이드_치킨_상품);
            음수_구성상품.setProductId(후라이드_치킨_상품.getId());
            음수_구성상품.setQuantity(음수_수량);
            // 음수 수량을 가진 메뉴 구성 상품이 포함된 메뉴 생성
            Menu 구성상품_수량_음수_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품);
            구성상품_수량_음수_메뉴.setMenuProducts(List.of(음수_구성상품));

            // 메뉴 그룹 존재 검증
            given(menuGroupRepository.findById(추천_메뉴그룹.getId()))
                    .willReturn(Optional.of(추천_메뉴그룹));

            // 상품 존재 검증
            given(productRepository.findAllByIdIn(List.of(후라이드_치킨_상품.getId())))
                    .willReturn(List.of(후라이드_치킨_상품));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(구성상품_수량_음수_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 메뉴의 가격이 구성 상품 가격 총합보다 높으면 IllegalArgumentException 발생")
        void createMenuWithPriceHigherThanSumOfMenuProductPricesThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request(); // 16,000원
            Product 콜라_상품 = ProductFixture.콜라_상품_Request(); // 1,000원

            // 총 상품 가격: 16,000 + 1,000 = 17,000원
            // 메뉴 가격을 19,000원으로 설정하여 예외를 발생시킨다.
            Menu 비싼_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            비싼_메뉴.setPrice(큰_가격); //총 상품 가격보다 높음

            // 메뉴 그룹 존재 검증
            given(menuGroupRepository.findById(추천_메뉴그룹.getId()))
                    .willReturn(Optional.of(추천_메뉴그룹));

            // 상품 존재 검증
            given(productRepository.findAllByIdIn(List.of(후라이드_치킨_상품.getId(), 콜라_상품.getId())))
                    .willReturn(List.of(후라이드_치킨_상품, 콜라_상품));
            given(productRepository.findById(후라이드_치킨_상품.getId())).willReturn(Optional.of(후라이드_치킨_상품));
            given(productRepository.findById(콜라_상품.getId())).willReturn(Optional.of(콜라_상품));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(비싼_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 메뉴명이 없으면 IllegalArgumentException 발생")
        void createMenuWithoutNameThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();

            Menu 이름_없는_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품);
            이름_없는_메뉴.setName(null);

            // 메뉴 그룹 존재 검증
            given(menuGroupRepository.findById(추천_메뉴그룹.getId())).willReturn(Optional.of(추천_메뉴그룹));

            // 상품 존재 검증
            given(productRepository.findAllByIdIn(List.of(후라이드_치킨_상품.getId())))
                    .willReturn(List.of(후라이드_치킨_상품));
            given(productRepository.findById(후라이드_치킨_상품.getId())).willReturn(Optional.of(후라이드_치킨_상품));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(이름_없는_메뉴));
        }

        @Test
        @DisplayName("메뉴 생성 - 실패: 메뉴에 비속어가 포함되면 IllegalArgumentException 발생")
        void createMenuWithEmptyOrProfanityNameThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();

            Menu 비속어_포함_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품);
            비속어_포함_메뉴.setName("비속어");

            // 메뉴 그룹 존재 검증
            given(menuGroupRepository.findById(추천_메뉴그룹.getId())).willReturn(Optional.of(추천_메뉴그룹));

            // 상품 존재 검증
            given(productRepository.findAllByIdIn(List.of(후라이드_치킨_상품.getId())))
                    .willReturn(List.of(후라이드_치킨_상품));
            given(productRepository.findById(후라이드_치킨_상품.getId())).willReturn(Optional.of(후라이드_치킨_상품));

            // 메뉴명 비속어 검증 (비속어 포함)
            given(purgomalumClient.containsProfanity(비속어_포함_메뉴.getName())).willReturn(true);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(비속어_포함_메뉴));
        }
    }


    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangeMenuPriceCases {

        @Test
        @DisplayName("메뉴 가격 변경 - 성공: 기존 메뉴 가격이 변경된다.")
        void changeMenuPriceSuccess() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            Product 콜라_상품 = ProductFixture.콜라_상품_Request();

            Menu 기존_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            UUID 기존_메뉴Id = 기존_메뉴.getId();
            given(menuRepository.findById(기존_메뉴Id)).willReturn(Optional.of(기존_메뉴));

            // when
            BigDecimal 새_가격 = BigDecimal.valueOf(15000); // 기존 구성 상품 가격보다 낮거나 같아야 함
            Menu 가격만_변경된_메뉴 = MenuFixture.가격만_변경된_메뉴(기존_메뉴, 새_가격);
            Menu 변경된_메뉴 = sut.changePrice(기존_메뉴Id, 가격만_변경된_메뉴);

            // then
            assertThat(변경된_메뉴.getPrice()).isEqualTo(새_가격);
            verify(menuRepository).findById(기존_메뉴Id);
        }

        @Test
        @DisplayName("메뉴 가격 변경 - 실패: 변경할 가격이 없으면 IllegalArgumentException 발생")
        void changeMenuPriceWithoutPriceThrowIllegalArgumentException() {
            // given
            Menu 후라이드_치킨_메뉴 = MenuFixture.후라이드_치킨_메뉴();
            Menu 음수_가격_메뉴 = MenuFixture.가격만_변경된_메뉴(후라이드_치킨_메뉴, null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(후라이드_치킨_메뉴.getId(), 음수_가격_메뉴));
        }

        @Test
        @DisplayName("메뉴 가격 변경 - 실패: 변경할 메뉴가격이 음수이면 IllegalArgumentException 발생")
        void changeMenuPriceNegativePriceThrowIllegalArgumentException() {
            // given
            Menu 후라이드_치킨_메뉴 = MenuFixture.후라이드_치킨_메뉴();
            Menu 음수_가격_메뉴 = MenuFixture.가격만_변경된_메뉴(후라이드_치킨_메뉴, 음수_가격);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(후라이드_치킨_메뉴.getId(), 음수_가격_메뉴));
        }

        @Test
        @DisplayName("메뉴 가격 변경 - 실패: 변경할 메뉴가 존재하지 않으면 NoSuchElementException 발생")
        void changeMenuPriceWithNonexistentMenuThrowNoSuchElementException() {
            // given
            Menu 존재하지_않는_메뉴 = MenuFixture.후라이드_치킨_메뉴();
            Menu 가격만_변경된_존재하지_않는_메뉴 = MenuFixture.가격만_변경된_메뉴(존재하지_않는_메뉴, BigDecimal.valueOf(15000));
            given(menuRepository.findById(존재하지_않는_메뉴.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.changePrice(존재하지_않는_메뉴.getId(), 가격만_변경된_존재하지_않는_메뉴))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("메뉴 가격 변경 - 실패: 변경할 메뉴 가격이 구성 상품 가격 총합보다 높으면 IllegalArgumentException 발생")
        void changeMenuPriceHigherThanMenuProductTotalThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            Product 콜라_상품 = ProductFixture.콜라_상품_Request();

            Menu 기존_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            UUID 기존_메뉴Id = 기존_메뉴.getId();
            given(menuRepository.findById(기존_메뉴Id)).willReturn(Optional.of(기존_메뉴));

            // when
            BigDecimal 변경할_가격 = 큰_가격; // 기존 구성 상품 가격보다 높음
            Menu 변경된_메뉴 = MenuFixture.가격만_변경된_메뉴(기존_메뉴, 변경할_가격);

            // then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(기존_메뉴Id, 변경된_메뉴));
        }
    }

    @Nested
    @DisplayName("메뉴 표시")
    class DisplayMenuCases {

        @Test
        @DisplayName("메뉴 표시 - 성공: 메뉴 가격이 구성 상품 총합과 같으면 표시 가능")
        void displayMenuSuccess() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            Product 콜라_상품 = ProductFixture.콜라_상품_Request();
            Menu 구성_상품_가격_총합과_동일한_메뉴 = MenuFixture.구성_상품_가격_총합과_동일한_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);

            given(menuRepository.findById(구성_상품_가격_총합과_동일한_메뉴.getId())).willReturn(Optional.of(구성_상품_가격_총합과_동일한_메뉴));

            // when
            Menu 결과 = sut.display(구성_상품_가격_총합과_동일한_메뉴.getId());

            // then
            assertThat(결과.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("메뉴 표시 - 실패: 메뉴 가격이 구성 상품 가격 총합보다 크면 IllegalStateException 발생")
        void displayMenuFailWithHigherPrice() {
            // given
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품_Request();
            Product 콜라_상품 = ProductFixture.콜라_상품_Request();
            Menu 구성_상품_가격_총합을_초과한_메뉴 = MenuFixture.구성_상품_가격_총합을_초과한_메뉴(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);

            given(menuRepository.findById(구성_상품_가격_총합을_초과한_메뉴.getId())).willReturn(Optional.of(구성_상품_가격_총합을_초과한_메뉴));

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.display(구성_상품_가격_총합을_초과한_메뉴.getId()));
        }

        @Test
        @DisplayName("메뉴 표시 - 실패: 존재하지 않는 메뉴 ID로 요청 시 NoSuchElementException 발생")
        void displayMenuFailWithNonexistentMenu() {
            // given
            given(menuRepository.findById(존재하지_않는_메뉴ID)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.display(존재하지_않는_메뉴ID));
        }
    }

    @Nested
    @DisplayName("메뉴 숨김")
    class HideMenuCases {

        @Test
        @DisplayName("메뉴 숨김 - 성공: 메뉴 숨김 처리 성공")
        void hideMenuSuccess() {
            // given
            Menu 메뉴 = MenuFixture.후라이드_치킨_메뉴();
            메뉴.setDisplayed(true); // 기존에 표시된 상태

            given(menuRepository.findById(메뉴.getId())).willReturn(Optional.of(메뉴));

            // when
            Menu 결과 = sut.hide(메뉴.getId());

            // then
            assertThat(결과.isDisplayed()).isFalse();
        }

        @Test
        @DisplayName("메뉴 숨김 - 실패: 존재하지 않는 메뉴 ID로 요청 시 NoSuchElementException 발생")
        void hideMenuFailWithNonexistentMenu() {
            // given
            given(menuRepository.findById(존재하지_않는_메뉴ID)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.hide(존재하지_않는_메뉴ID));
        }
    }

    @Nested
    @DisplayName("메뉴 목록 조회")
    class ListMenusCases {

        @Test
        @DisplayName("메뉴 목록 조회 - 성공: 메뉴 목록이 조회된다.")
        void listMenusSuccess() {
            // given
            Menu 후라이드_치킨_메뉴 = MenuFixture.후라이드_치킨_메뉴();
            Menu 양념_치킨_메뉴 = MenuFixture.양념_치킨_메뉴();

            given(menuRepository.findAll()).willReturn(List.of(후라이드_치킨_메뉴, 양념_치킨_메뉴));

            // when
            List<Menu> 결과 = sut.findAll();

            // then
            assertThat(결과).hasSize(2);
        }
    }
}
