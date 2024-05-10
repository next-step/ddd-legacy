package utils

import kitchenpos.infra.PurgomalumClient
import org.mockito.Mock
import org.mockito.Mockito

abstract class BaseMockTest {

    @Mock
    protected lateinit var purgomalumClient: PurgomalumClient

    protected fun `비속어 혹은 욕설 없음`(name: String) {
        Mockito.`when`(purgomalumClient.containsProfanity(name))
            .thenReturn(false)
    }


     protected fun `비속어 혹은 욕설 포함됨`(name: String) {
        Mockito.`when`(purgomalumClient.containsProfanity(name))
            .thenReturn(true)
    }
}
