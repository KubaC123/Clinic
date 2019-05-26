package cl.dbapp.spring;

import cl.dbapp.spring.frontend.view.Menu;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableVaadin
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String HOME_PATH = "/" + Menu.HOME_ROUTE;
    private static final String REGISTERED_VISIT_PATH = "/" + Menu.REGISTERED_VISIT_ROUTE;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HOME_PATH, REGISTERED_VISIT_PATH)
                .authenticated()
                .and()
                .oauth2Client()
                .and()
                .oauth2Login();
        http.csrf().disable();
    }
}