package kitchenpos.application;

import kitchenpos.config.MenuTestContextConfiguration;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static kitchenpos.helper.MenuGroupTestHelper.메뉴카테고리_생성;
import static kitchenpos.helper.MenuProductTestHelper.음식메뉴_생성;
import static kitchenpos.helper.MenuTestHelper.메뉴_생성;
import static kitchenpos.helper.ProductTestHelper.음식_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@Import(MenuTestContextConfiguration.class)
class MenuServiceTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;
    private MenuGroup 추천메뉴;
    private MenuProduct 마라탕메뉴;
    private MenuProduct 미니꿔바로우메뉴;
    private Product 마라탕;
    private Product 미니꿔바로우;


    @BeforeEach
    void setUp() {
        추천메뉴 = 메뉴카테고리_생성("추천메뉴");
        마라탕 = 음식_생성("마라탕", BigDecimal.valueOf(10000));
        미니꿔바로우 = 음식_생성("미니꿔바로우", BigDecimal.valueOf(8000));

        마라탕메뉴 = 음식메뉴_생성(마라탕, 1);
        미니꿔바로우메뉴 = 음식메뉴_생성(미니꿔바로우, 1);

        Mockito.when(menuGroupRepository.findById(추천메뉴.getId()))
                .thenReturn(Optional.of(추천메뉴));

        Mockito.when(productRepository.findAllByIdIn(Arrays.asList(마라탕.getId(), 미니꿔바로우.getId())))
                .thenReturn(Arrays.asList(마라탕, 미니꿔바로우));

        Mockito.when(productRepository.findById(마라탕.getId()))
                .thenReturn(Optional.of(마라탕));

        Mockito.when(productRepository.findById(미니꿔바로우.getId()))
                .thenReturn(Optional.of(미니꿔바로우));
    }

    @DisplayName("특정 메뉴카테고리에 음식을 조합한 신규 메뉴를 추가한다.")
    @Test
    void createMenu(){
        //given
        Menu requestMenu = 메뉴_생성(추천메뉴, "마라세트", BigDecimal.valueOf(16000), Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴));

        Mockito.when(menuRepository.save(any()))
                .thenReturn(requestMenu);

        //when
        Menu createMenu = menuService.create(requestMenu);

        //then
        assertThat(createMenu.getName()).isSameAs(requestMenu.getName());
    }
}