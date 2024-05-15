package kitchenpos.application

import domain.MenuFixtures.makeMenuOne
import domain.MenuGroupFixtures.makeMenuGroupOne
import io.kotest.core.spec.style.DescribeSpec
import kitchenpos.application.fake.FakeMenuGroupRepository
import kitchenpos.application.fake.FakeMenuRepository
import kitchenpos.application.fake.FakeProductRepository
import kitchenpos.application.fake.FakePurgomalumClient
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*
import kotlin.NoSuchElementException

class MenuServiceTest : DescribeSpec() {
    init {
        describe("MenuService 클래스의") {
            val menuRepository = FakeMenuRepository()
            val menuGroupRepository = FakeMenuGroupRepository()
            val productRepository = FakeProductRepository()
            val purgomalumClient = FakePurgomalumClient()

            val menuService =
                MenuService(
                    menuRepository,
                    menuGroupRepository,
                    productRepository,
                    purgomalumClient,
                )

            describe("create 메서드는") {
                context("요청에 가격이 존재하지 않을 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.fromString("df8d7e4f-4283-4c91-9e43-d9e2dcb6f182")
                        }
                    it("IllegalArgumentException을 던진다") {
                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴 그룹이 존재하지 않을 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.randomUUID()
                            this.price = BigDecimal("5000")
                        }
                    it("NoSuchElementException을 던진다") {
                        assertThrows<NoSuchElementException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴 상품이 존재하지 않을 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.fromString("df8d7e4f-4283-4c91-9e43-d9e2dcb6f182")
                            this.menuProducts =
                                listOf(
                                    MenuProduct().apply {
                                        id = UUID.randomUUID()
                                    },
                                )
                        }
                    it("IllegalArgumentException을 던진다") {
                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴 상품의 수량이 음수일 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.fromString("df8d7e4f-4283-4c91-9e43-d9e2dcb6f182")
                            this.menuProducts =
                                listOf(
                                    MenuProduct().apply {
                                        id = UUID.randomUUID()
                                    },
                                )
                        }
                    it("IllegalArgumentException을 던진다") {
                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴의 가격이 메뉴 상품의 총 가격보다 클 때") {
                    menuGroupRepository.save(makeMenuGroupOne())

                    val createMenuRequest =
                        makeMenuOne().apply {
                            this.price = 500000.toBigDecimal()
                        }
                    it("IllegalArgumentException을 던진다") {
                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴의 이름에 비속어가 포함되어 있을 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "비속어"
                            this.menuGroupId = UUID.fromString("df8d7e4f-4283-4c91-9e43-d9e2dcb6f182")
                        }
                    it("IllegalArgumentException을 던진다") {
                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }
            }
        }
    }
}
