package example;

import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = "example")
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfig {

    /**
     * HttpClient for SSL.
     */
    private CloseableHttpClient sslTrustClient() throws GeneralSecurityException {
        TrustStrategy acceptingTrustStrategy = (notUsed1, notUsed2) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        return HttpClients.custom().setSSLSocketFactory(csf).build();
    }

    /**
     * HttpClient factory.
     */
    private HttpComponentsClientHttpRequestFactory httpRequestFactory() throws GeneralSecurityException {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(sslTrustClient());
        return requestFactory;
    }

    @Bean
    public RestTemplate restTemplate() throws GeneralSecurityException {
        return new RestTemplate(httpRequestFactory());
    }

}
