package kitchenpos.bo;

import kitchenpos.builder.MenuBuilder;
import kitchenpos.builder.MenuProductBuilder;
import kitchenpos.builder.ProductBuilder;
import kitchenpos.dao.*;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

public class MenuBoTestWithBuilder extends MockTest {
    @InjectMocks private MenuBo menuBo;
    @Mock private MenuDao menuDao;
    @Mock private MenuProductDao menuProductDao;
    @Mock private MenuGroupDao menuGroupDao;
    @Mock private ProductDao productDao;

    @DisplayName("메뉴를 생성할 수 있다.")
    @Test
    void create() {
        //given
        final Product givenProduct1 = ProductBuilder
                .create()
                .setId(1L)
                .setName("후라이드")
                .setPrice(new BigDecimal(16000))
                .build();
        final Product givenProduct2 = ProductBuilder
                .create()
                .setId(2L)
                .setName("양념치킨")
                .setPrice(new BigDecimal(16000))
                .build();
        final MenuProduct givenMenuProduct1 = MenuProductBuilder
                .create()
                .setMenuId(1L)
                .setProductId(1L)
                .setSeq(1L)
                .setQuantity(2L)
                .build();
        final MenuProduct givenMenuProduct2 = MenuProductBuilder
                .create()
                .setMenuId(2L)
                .setProductId(2L)
                .setSeq(2L)
                .setQuantity(2L)
                .build();
        final Menu givenMenu = MenuBuilder
                .create()
                .setId(1L)
                .setName("후라이드치킨")
                .setPrice(new BigDecimal(16000))
                .setMenuGroupId(1L)
                .setMenuProducts(new ArrayList(Arrays.asList(givenMenuProduct1, givenMenuProduct2)))
                .build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);
        given(productDao.findById(anyLong()))
                .willReturn(Optional.of(givenProduct1));
        given(productDao.findById(givenProduct2.getId()))
                .willReturn(Optional.of(givenProduct2));
        given(menuDao.save(givenMenu))
                .willReturn(givenMenu);
        given(menuProductDao.save(givenMenuProduct1))
                .willReturn(givenMenuProduct1);
        given(menuProductDao.save(givenMenuProduct2))
                .willReturn(givenMenuProduct2);

        //when
        final Menu actualMenu = menuBo.create(givenMenu);

        //then
        assertThat(actualMenu.getName()).isEqualTo(givenMenu.getName());
    }

    @DisplayName("메뉴를 생성할 때 메뉴의 가격을 반드시 입력해야 한다.")
    @ParameterizedTest
    @NullSource
    void createMenuWithoutPriceTest(BigDecimal price) {
        //given
        Menu givenMenu = MenuBuilder.create().setPrice(price).build();

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }
}
