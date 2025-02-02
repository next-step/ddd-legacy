package spec

import io.kotest.core.spec.style.FunSpec
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
abstract class BaseUnitSpec(body: FunSpec.() -> Unit = {}) : FunSpec(body)
