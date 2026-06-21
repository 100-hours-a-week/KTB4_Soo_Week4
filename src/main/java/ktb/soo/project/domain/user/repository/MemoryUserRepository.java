package ktb.soo.project.domain.user.repository;

import jakarta.annotation.PostConstruct;
import ktb.soo.project.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryUserRepository implements UserRepository{
    private static final Map<Long, User> store = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

    // 회원가입과 정보 수정
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence.incrementAndGet()); // 신규 회원이면 ID 발급
        }
        store.put(user.getId(), user); // 가짜 DB에 저장
        return user;
    }

    // 회원 검색 및 중복 체크
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return store.values().stream()
                .anyMatch(user -> user.getNickname().equals(nickname));
    }

    // 서버 켜지자마자 테스트할 수 있는 더미 유저 미리 넣어두기
    @PostConstruct
    public void init() {
        User dummy1 = new User("test1@gmail.com", "password123!", "더미작성자1");
        save(dummy1);

        User dummy2 = new User("test@2gmail.com", "password123!", "더미작성자2");
        save(dummy2);
    }
}
