package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FakeProductRepositoryTest {

    // SUT

    private final FakeProductRepository fakeProductRepository = new FakeProductRepository();

    @DisplayName("상품이 저장되어야 한다.")
    @ValueSource(strings = {
            "sweeten", "ambitious", "strict", "mind", "shut",
            "persuade", "explain", "introduction", "lipstick", "cork",
    })
    @ParameterizedTest
    void kcxwngjb(final String name) {
        // given
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);

        // when
        final Product savedProduct = this.fakeProductRepository.save(product);

        // then
        assertThat(savedProduct).isEqualTo(product);
    }

    @DisplayName("저장된 상품은 ID로 찾을 수 있어야 한다.")
    @Test
    void jzbvojbl() {
        // given
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        final Product savedProduct = this.fakeProductRepository.save(product);

        // when
        final Product foundProduct = this.fakeProductRepository.findById(savedProduct.getId())
                .orElse(null);

        // then
        assertThat(foundProduct).isEqualTo(product);
    }

    @DisplayName("저장되지 않은 상품은 ID로 찾을 수 없어야 한다.")
    @Test
    void onctmslm() {
        // given
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        this.fakeProductRepository.save(product);

        // when
        final Product foundProduct = this.fakeProductRepository.findById(UUID.randomUUID())
                .orElse(null);

        // then
        assertThat(foundProduct).isNull();
    }

    @DisplayName("빈 상태에서 모두 조회시 빈 List가 반환되어야 한다.")
    @Test
    void rmytomfh() {
        // when
        final List<Product> products = this.fakeProductRepository.findAll();

        // then
        assertThat(products).isEmpty();
    }

    @DisplayName("모두 조회시 저장된 수 만큼 조회되어야 한다.")
    @ValueSource(ints = {
            11, 28, 8, 24, 22,
            24, 26, 4, 15, 31,
    })
    @ParameterizedTest
    void omtpiqil(final int size) {
        // given
        IntStream.range(0, size)
                .forEach(n -> {
                    final Product product = new Product();
                    product.setId(UUID.randomUUID());
                    this.fakeProductRepository.save(product);
                });

        // when
        final List<Product> products = this.fakeProductRepository.findAll();

        // then
        assertThat(products).hasSize(size);
    }

    @DisplayName("여러 ID로 찾을 수 있어야 한다.")
    @Test
    void rjphmfyb() {
        // given
        final List<Product> products = IntStream.range(0, 10)
                .mapToObj(n -> {
                    final Product product = new Product();
                    product.setId(UUID.randomUUID());
                    return this.fakeProductRepository.save(product);
                })
                .collect(Collectors.toUnmodifiableList());

        final int[] indices = {3, 6, 9};

        final List<Product> productsToBeFound = Arrays.stream(indices)
                .mapToObj(products::get)
                .collect(Collectors.toUnmodifiableList());

        final List<UUID> ids = productsToBeFound.stream()
                .map(Product::getId)
                .collect(Collectors.toUnmodifiableList());

        // when
        final List<Product> foundProducts = this.fakeProductRepository.findAllByIdIn(ids);

        // then
        assertThat(foundProducts).hasSize(3).containsAll(productsToBeFound);
    }
}
