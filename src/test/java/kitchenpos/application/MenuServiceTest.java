package kitchenpos.application;

import kitchenpos.IntegrationTestSupport;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;

@Service
class MenuServiceTest extends IntegrationTestSupport {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
        menuGroupRepository.deleteAllInBatch();
        menuRepository.deleteAllInBatch();
    }
}
