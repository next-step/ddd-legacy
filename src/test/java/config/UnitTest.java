package config;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.*;

@Tag("unitTest")
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface UnitTest {
}
