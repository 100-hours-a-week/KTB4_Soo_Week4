# 게시글 조회수 정합성 테스트

세 benchmark 브랜치에서 같은 스크립트를 실행해 조회수 처리 방식만 비교한다.

## 공통 준비

1. MySQL과 애플리케이션을 실행한다.
2. `load-tests/sql/reset-post-view.sql`의 `@post_id`를 테스트 게시글 ID로 바꿔 실행한다.
3. Redis 브랜치에서는 아래 패턴에 해당하는 테스트 키도 삭제한다.

```bash
redis-cli --scan --pattern 'post:view:1:guest:*'
```

공유 Redis에서 `FLUSHALL`은 사용하지 않는다.

## 테스트 1: 동일 비회원 100회 동시 요청

모든 VU가 같은 `guest_id` 쿠키를 사용한다.

```bash
k6 run \
  -e TEST_CASE=same-viewer \
  -e POST_ID=1 \
  -e VUS=100 \
  load-tests/k6/post-view-correctness.js
```

기대 결과:

- 전체 요청: 100
- B와 C의 HTTP 실패: 0
- 조회수 증가: 1
- B의 `post_views` 이력: 1개
- C의 Redis 조회 키: 1개

## 테스트 2: 서로 다른 비회원 1,000명 동시 요청

VU마다 서로 다른 유효한 UUID 쿠키를 사용한다.

```bash
k6 run \
  -e TEST_CASE=different-viewers \
  -e POST_ID=1 \
  -e VUS=1000 \
  load-tests/k6/post-view-correctness.js
```

기대 결과:

- 전체 요청: 1,000
- B와 C의 HTTP 실패: 0
- B와 C의 조회수 증가: 1,000
- B의 `post_views` 이력: 1,000개
- C의 Redis 조회 키: 1,000개

## 결과 검증

요청이 끝나면 `load-tests/sql/verify-post-view.sql`을 실행한다. Redis 브랜치는 키 개수와 TTL도 확인한다.

```bash
redis-cli --scan --pattern 'post:view:1:guest:*' | wc -l
redis-cli TTL 'post:view:1:guest:11111111-1111-4111-8111-111111111111'
```

각 시나리오 실행 전에 반드시 DB 조회수와 이전 조회 이력/Redis 키를 다시 초기화한다.
