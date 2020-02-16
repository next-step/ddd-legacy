package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("사용자는 음식을 등록할 수 있고, 등록이 완료되면 등록된 음식 정보를 반환받아 확인할 수 있다")
    @Test
    void create() {
        //given
        Product item = new Product();
        item.setId(1L);
        item.setName("후라이드치킨");
        item.setPrice(BigDecimal.valueOf(13000));

        Mockito.when(productDao.save(item)).thenReturn(item);

        //when
        Product actual = productBo.create(item);

        //then
        assertThat(actual).isEqualTo(item);
    }

    @DisplayName("모든 음식은 가격이 있어야 하고, 가격은 0원 이상이어야 한다")
    @Test
    void product_should_have_positive_number_price() {
        //given
        Product item = new Product();
        item.setId(1L);
        item.setName("후라이드치킨");
        item.setPrice(BigDecimal.valueOf(-1));

        //when & then
        assertThatThrownBy(() -> {
            productBo.create(item);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("사용자는 등록된 모든 음식의 목록을 조회할 수 있다")
    @Test
    void list() {
        //given
        Product item1 = new Product();
        item1.setId(1L);
        item1.setName("후라이드치킨");
        item1.setPrice(BigDecimal.valueOf(13000));

        Product item2 = new Product();
        item2.setId(1L);
        item2.setName("양념치킨");
        item2.setPrice(BigDecimal.valueOf(11000));

        Product item3 = new Product();
        item3.setId(1L);
        item3.setName("간장치킨");
        item3.setPrice(BigDecimal.valueOf(10000));

        List<Product> products = Arrays.asList(item1, item2, item3);
        Mockito.when(productDao.findAll()).thenReturn(products);

        //when
        List<Product> actual = productBo.list();

        //then
        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual.get(0)).isEqualTo(item1);
        assertThat(actual.get(1)).isEqualTo(item2);
        assertThat(actual.get(2)).isEqualTo(item3);
    }
}