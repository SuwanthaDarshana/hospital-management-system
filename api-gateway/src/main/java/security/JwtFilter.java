package security;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;



@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config>{

    private final String SECRET = "HOSPITAL_SECRET_KEY";

    public JwtFilter() {
        super(Config.class);
    }

    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

        };
    }
}
