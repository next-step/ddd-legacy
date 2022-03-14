package kitchenpos.configuration;

import static kitchenpos.constant.Profile.LOCAL;
import static kitchenpos.constant.Profile.TEST;

import java.sql.SQLException;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile(value = {LOCAL, TEST})
@Configuration
public class H2ServerConfig {

    @Bean
    public Server h2Server() throws SQLException {
        return Server.createTcpServer().start();
    }
}
