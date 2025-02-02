package config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.*;


@Target({ElementType.TYPE})
@Retention(RUNTIME)
@ExtendWith(MockitoExtension.class)
public @interface UnitTest {
}
