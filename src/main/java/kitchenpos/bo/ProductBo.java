package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
public class ProductBo {
    private final ProductDao productDao;

    public ProductBo(final ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * 제품 생성
     *
     * @param product
     * @return
     */
    @Transactional
    public Product create(final Product product) {
        final BigDecimal price = product.getPrice();

        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) { // 제품 가격은 0원 이상이다.
            throw new IllegalArgumentException();
        }

        return productDao.save(product);
    }

    /**
     * 전체 제품 리스트 조회
     *
     * @return
     */
    public List<Product> list() {
        return productDao.findAll(); // 전체 제품 리스트를 조회할 수 있다.
    }
}
