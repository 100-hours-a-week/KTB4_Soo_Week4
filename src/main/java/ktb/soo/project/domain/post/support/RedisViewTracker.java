package ktb.soo.project.domain.post.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisViewTracker {
    private static final String VIEW_KEY_PREFIX = "post:view:";
    private static final Duration VIEW_TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    public boolean acquireMember(Long postId, Long userId) {
        return acquire(postId, "member:" + userId);
    }

    public boolean acquireGuest(Long postId, String guestId) {
        return acquire(postId, "guest:" + guestId);
    }

    public void releaseMember(Long postId, Long userId) {
        release(postId, "member:" + userId);
    }

    public void releaseGuest(Long postId, String guestId) {
        release(postId, "guest:" + guestId);
    }

    private boolean acquire(Long postId, String viewerKey) {
        String redisKey = VIEW_KEY_PREFIX + postId + ":" + viewerKey;

        try {
            Boolean acquired = redisTemplate.opsForValue()
                    .setIfAbsent(redisKey, "1", VIEW_TTL);

            return Boolean.TRUE.equals(acquired);
        } catch (DataAccessException e) {
            log.warn("Redis 조회 중복 방지에 실패했습니다. redisKey={}", redisKey, e);
            return false;
        }
    }

    private void release(Long postId, String viewerKey) {
        String redisKey = VIEW_KEY_PREFIX + postId + ":" + viewerKey;

        try {
            redisTemplate.delete(redisKey);
        } catch (DataAccessException e) {
            log.warn("Redis 조회 중복 방지 키 삭제에 실패했습니다. redisKey={}", redisKey, e);
        }
    }
}
