package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import jakarta.transaction.Transactional
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@Transactional
class ProductServiceTest : BehaviorSpec() {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var menuRepository: MenuRepository

    @Autowired
    private lateinit var createMenuHelper: CreateMenuHelper

    private val purgomalumClient = mockk<PurgomalumClient>(relaxed = true)  // relaxed 옵션 추가
    private lateinit var productService: ProductService

    init {
        beforeTest {
            productService = ProductService(
                productRepository,
                menuRepository,
                purgomalumClient
            )
            every { purgomalumClient.containsProfanity(any()) } returns false
        }

        Given("상품을 생성할 때") {
            When("올바른 상품 정보로 생성하는 경우") {
                val product = Product().apply {
                    name = "상품"
                    price = BigDecimal("1000")
                }

                Then("상품이 정상적으로 생성된다") {
                    val savedProduct = productService.create(product)

                    savedProduct.id shouldNotBe null
                    savedProduct.name shouldBe "상품"
                    savedProduct.price shouldBe BigDecimal("1000")
                }
            }

            When("상품 이름이 null인 경우") {
                val product = Product().apply {
                    name = null
                    price = BigDecimal("1000")
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        productService.create(product)
                    }
                }
            }

            When("상품 이름에 비속어가 포함된 경우") {
                val productName = "비속어가 포함된 상품"

                beforeTest {
                    clearMocks(purgomalumClient)
                    every { purgomalumClient.containsProfanity(productName) } returns true
                }

                val product = Product().apply {
                    name = productName
                    price = BigDecimal("1000")
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        productService.create(product)
                    }
                }
            }

            When("가격이 null인 경우") {
                val product = Product().apply {
                    name = "상품"
                    price = null
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        productService.create(product)
                    }
                }
            }

            When("가격이 음수인 경우") {
                val product = Product().apply {
                    name = "상품"
                    price = BigDecimal("-1000")
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        productService.create(product)
                    }
                }
            }
        }

        Given("상품 가격을 변경할 때") {
            val product = Product().apply {
                id = UUID.randomUUID()
                name = "상품"
                price = BigDecimal("1000")
            }
            productRepository.save(product)

            When("올바른 가격으로 변경하는 경우") {
                val request = Product().apply {
                    price = BigDecimal("2000")
                }

                Then("가격이 정상적으로 변경된다") {
                    val updatedProduct = productService.changePrice(product.id!!, request)
                    updatedProduct.price shouldBe BigDecimal("2000")
                }
            }

            When("가격이 null인 경우") {
                val request = Product().apply {
                    price = null
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        productService.changePrice(product.id!!, request)
                    }
                }
            }

            When("가격이 음수인 경우") {
                val request = Product().apply {
                    price = BigDecimal("-1000")
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        productService.changePrice(product.id!!, request)
                    }
                }
            }

            When("존재하지 않는 상품의 가격을 변경하는 경우") {
                val request = Product().apply {
                    price = BigDecimal("2000")
                }

                Then("예외가 발생한다") {
                    shouldThrow<NoSuchElementException> {
                        productService.changePrice(UUID.randomUUID(), request)
                    }
                }
            }

            When("메뉴 가격이 상품 원가 총합보다 작아지는 경우") {
                val menu = createMenuHelper.createTestMenu(
                    menuName = "메뉴",
                    menuPrice = BigDecimal("5000"),
                    menuGroupName = "메뉴 그룹",
                    products = listOf(
                        CreateMenuHelper.ProductDto(
                            id = product.id,
                            name = product.name,
                            price = product.price, // 1000
                            quantity = 1L
                        ),
                        CreateMenuHelper.ProductDto(
                            name = "상품2",
                            price = BigDecimal("1000"),
                            quantity = 1L
                        )
                    ),
                    isDisplayed = true
                )

                // 1000원 메뉴를 2000원으로 변경했을 때
                val request = Product().apply {
                    price = BigDecimal("2000")
                }

                Then("메뉴가 숨김 처리된다") {
                    productService.changePrice(product.id!!, request)

                    val updatedMenu = menuRepository.findById(menu.id!!).get()
                    updatedMenu.isDisplayed shouldBe false
                }
            }
        }
    }
}
