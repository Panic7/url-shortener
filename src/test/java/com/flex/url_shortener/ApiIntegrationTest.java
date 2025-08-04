package com.flex.url_shortener;

import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.LOGIN;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.LOGOUT;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.MY_SHORT_LINKS;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.SHORTEN;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.SIGN_UP;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.TOKEN_REFRESH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.flex.url_shortener.dto.AuthRequest;
import com.flex.url_shortener.dto.AuthResponse;
import com.flex.url_shortener.dto.PageResponse;
import com.flex.url_shortener.dto.ShortLinkRequest;
import com.flex.url_shortener.dto.ShortLinkResponse;
import com.flex.url_shortener.dto.ShortUrlDto;
import com.flex.url_shortener.dto.UserResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${security.jwt.access-cookie-name}")
    private String accessTokenCookieName;

    @Value("${security.jwt.refresh-cookie-name}")
    private String refreshTokenCookieName;

    private static final String URL_TO_SHORTEN = "https://google.com";
    private static final String EXPECTED_SHORT_CODE = "2XNGAK";
    private static final AuthRequest AUTH_REQUEST = new AuthRequest("asd@asd.com", "qweqwe");
    private static String accessTokenCookie;
    private static String refreshTokenCookie;

    @Test
    @Order(1)
    void whenSignupWithValidData_thenReturn200() {
        var response = restTemplate.postForEntity(SIGN_UP, AUTH_REQUEST, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    void whenLoginWithValidData_thenReturn200WithNewAccessAndRefreshCookies() {
        var response = restTemplate.postForEntity(LOGIN, AUTH_REQUEST, AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        validateCookies(response.getHeaders().get(HttpHeaders.SET_COOKIE));
    }

    @Test
    @Order(3)
    void whenRefreshTokenWithValidRefreshToken_thenReturn200WithNewAccessAndRefreshCookies() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, refreshTokenCookie);

        var response = restTemplate.postForEntity(TOKEN_REFRESH, new HttpEntity<>(headers), UserResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        validateCookies(response.getHeaders().get(HttpHeaders.SET_COOKIE));
    }

    @Test
    @Order(4)
    void whenShortenWithValidUrlAndWithoutAuthorization_thenReturn200AndShortUrl() throws URISyntaxException {
        var request = ShortLinkRequest.builder().url(new URI(URL_TO_SHORTEN).toString()).build();

        var response = restTemplate.postForEntity(SHORTEN, new HttpEntity<>(request), ShortUrlDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().shortenedUrl()).isNotBlank();
    }

    @Test
    @Order(5)
    void whenShortenWithValidUrlAndWithAuthorization_thenReturn200AndShortUrl() throws URISyntaxException {
        var request = ShortLinkRequest.builder().url(new URI(URL_TO_SHORTEN).toString()).build();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, accessTokenCookie);

        var response = restTemplate.postForEntity(SHORTEN, new HttpEntity<>(request, headers),
                ShortUrlDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().shortenedUrl()).isNotBlank();
    }

    @Test
    @Order(6)
    void whenAccessShortUrl_shouldRedirectAndReturn200() {
        var response = restTemplate.getForEntity("/%s".formatted(EXPECTED_SHORT_CODE), Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(7)
    void whenGetMyShortLinksWithValidAuth_thenReturn200AndPagedShortLinks() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, accessTokenCookie);

        var response = restTemplate.exchange(
                MY_SHORT_LINKS,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<PageResponse<ShortLinkResponse>>() { }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        var shortLink = response.getBody().content().iterator().next();
        assertNotNull(shortLink);
        assertAll(
                () -> assertThat(shortLink.shortCode()).isNotBlank(),
                () -> assertThat(shortLink.originalUrl()).isNotBlank(),
                () -> assertThat(shortLink.shortUrl()).isNotBlank(),
                () -> assertThat(shortLink.clickCount()).isGreaterThanOrEqualTo(0)
        );
    }


    @Test
    @Order(8)
    void whenLogout_shouldInvalidateCookies() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, accessTokenCookie);
        headers.add(HttpHeaders.COOKIE, refreshTokenCookie);

        var logoutResponse = restTemplate.postForEntity(LOGOUT, new HttpEntity<>(headers), Void.class);
        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());

        var invalidAuthCookies = logoutResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(invalidAuthCookies);

        var getMyShortLinksResponse = restTemplate.getForEntity(MY_SHORT_LINKS, Void.class,
                new HttpEntity<>(invalidAuthCookies));

        assertEquals(HttpStatus.UNAUTHORIZED, getMyShortLinksResponse.getStatusCode());
    }

    private void validateCookies(List<String> cookies) {
        assertThat(cookies).isNotNull().isNotEmpty().hasSize(2);

        var accessTokenFound = false;
        var refreshTokenFound = false;

        for (String cookie : cookies) {
            if (cookie.startsWith(accessTokenCookieName)) {
                accessTokenCookie = cookie;
                accessTokenFound = true;
                assertThat(cookie).isNotBlank().contains("HttpOnly", "Secure", "SameSite=STRICT");
            }
            if (cookie.startsWith(refreshTokenCookieName)) {
                refreshTokenCookie = cookie;
                refreshTokenFound = true;
                assertThat(cookie).isNotBlank().contains("HttpOnly", "Secure", "SameSite=STRICT");
            }
        }

        assertAll(
                () -> assertThat(accessTokenCookie).isNotBlank(),
                () -> assertThat(refreshTokenCookie).isNotBlank()
        );
        assertThat(accessTokenFound).isTrue();
        assertThat(refreshTokenFound).isTrue();
    }

}