package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품 아이디 목록으로 상품 목록을 조회한다.")
    @Test
    void findAllByIdIn() {
        // given
        var product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setName("후라이드 치킨");
        product1.setPrice(BigDecimal.valueOf(16000));

        var product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("양념 치킨");
        product2.setPrice(BigDecimal.valueOf(16000));

        var product3 = new Product();
        product3.setId(UUID.randomUUID());
        product3.setName("허니 콤보");
        product3.setPrice(BigDecimal.valueOf(19500));

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // when
        var products = productRepository.findAllByIdIn(
                        List.of(product1.getId(), product2.getId()))
                .stream()
                .toList();

        // then
        assertThat(products).containsExactlyInAnyOrder(product1, product2);
    }
}
