package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.bo.ProductBo;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerTests {
    @InjectMocks
    private ProductRestController productRestController;

    @Mock
    private ProductBo productBo;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static Product mockCreated;
    private static List<Product> mockProducts = new ArrayList<>();

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(productRestController).alwaysDo(print()).build();
    }

    @BeforeAll
    public static void setup() {
        mockCreated = new Product();
        mockCreated.setId(1L);

        mockProducts.add(new Product());
    }

    @DisplayName("POST 상품 시도 성공(201)")
    @Test
    public void postProductSuccess() throws Exception {
        Product mockRequestProduct = new Product();
        given(productBo.create(any(Product.class))).willReturn(mockCreated);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mockRequestProduct)))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/products/1"))
                .andExpect(content().string(containsString("\"id\":1")))
        ;
    }

    @DisplayName("GET 상품 콜렉션 시도 성공(200)")
    @Test
    public void getProductsSuccess() throws Exception {
        given(productBo.list()).willReturn(mockProducts);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("price")))
        ;
    }
}
