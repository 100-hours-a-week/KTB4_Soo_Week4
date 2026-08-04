package ktb.soo.project.domain.post.support;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class GuestCookieManager {
    public static final String COOKIE_NAME = "guest_id";

    public GuestCookieResolution resolve(String guestId, boolean secure) {
        // 올바른 쿠키일때
        if (isValidUuid(guestId)) {
            return new GuestCookieResolution(guestId, Optional.empty());
        }

        String newGuestId = UUID.randomUUID().toString();
        ResponseCookie newCookie = createCookie(newGuestId, secure);

        return new GuestCookieResolution(newGuestId, Optional.of(newCookie));
    }

    private ResponseCookie createCookie(String guestId, boolean secure) {
        return ResponseCookie.from(COOKIE_NAME, guestId)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(365))
                .build();
    }

    private boolean isValidUuid(String value) {
        if (value == null) {
            return false;
        }

        try {
            return UUID.fromString(value).toString().equals(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public record GuestCookieResolution(
            String guestId,
            Optional<ResponseCookie> responseCookie
    ) {
    }
}
