-- 사용 전 값을 실제 테스트 값으로 변경한다.
SET @post_id = 1;
SET @same_guest_id = '11111111-1111-4111-8111-111111111111';

SELECT id, view_count
FROM posts
WHERE id = @post_id;

SELECT COUNT(*) AS total_post_view_rows
FROM post_views
WHERE post_id = @post_id;

SELECT COUNT(*) AS same_guest_view_rows
FROM post_views
WHERE post_id = @post_id
  AND guest_id = @same_guest_id;
