package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("가격이 0원 이하이면 IllegalArgumentException() 을 생성한다.")
    @Test
    void createPriceZero (){
        //given
        Product newProduct = new Product.Builder()
            .price(new BigDecimal(-1))
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(newProduct));
    }

    @DisplayName("가격을 설정하지 않으면 IllegalArgumentException() 을 생성한다.")
    @Test
    void createPriceNull (){
        Product newProduct = new Product.Builder().build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(newProduct));
    }

    @DisplayName("제품 등록을 성공하면 name과 price가 동일하다.")
    @Test
    void create(){

        String name = "뿌링클;";
        BigDecimal price = new BigDecimal(20000);

        Product newProduct = new Product.Builder()
            .name(name)
            .price(price)
            .build();

        Product expected = new Product.Builder()
            .name(name)
            .price(price)
            .build();

        given(productDao.save(newProduct)).willReturn(expected);

        assertThat(productBo.create(newProduct)).isEqualToIgnoringNullFields(expected);
    }


}
