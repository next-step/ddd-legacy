package support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringBootTest
@ActiveProfiles
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptanceTest {

    /**
     * <pre>
     * 테스트 수행 시 설정할 profile
     * </pre>
     *
     * @return profile list
     */
    @AliasFor(annotation = ActiveProfiles.class)
    String[] profiles() default {"test"};

    /**
     * <pre>
     * 설정할 웹 환경 유형
     * {@link SpringBootTest.WebEnvironment#RANDOM_PORT
     * </pre>
     *
     * @return the type of web environment
     */
    @AliasFor(annotation = SpringBootTest.class)
    SpringBootTest.WebEnvironment webEnvironment() default SpringBootTest.WebEnvironment.RANDOM_PORT;

}
