package vn.codegym.lunchbot_be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
    @Value("${vnp_TmnCode}")
    private String vnpTmnCode;

    @Value("${vnp_HashSecret}")
    private String vnpHashSecret;

    @Value("${vnp_Url}")
    private String vnpUrl;

    @Value("${vnp_ReturnUrl}")
    private String vnpReturnUrl;

    @Value("${vnp_IpnUrl}")
    private String vnpIpnUrl;

    public String getVnpTmnCode() { return vnpTmnCode; }
    public String getVnpHashSecret() { return vnpHashSecret; }
    public String getVnpUrl() { return vnpUrl; }
    public String getVnpReturnUrl() { return vnpReturnUrl; }
    public String getVnpIpnUrl() { return vnpIpnUrl; }
}
