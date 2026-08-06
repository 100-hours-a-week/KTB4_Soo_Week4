-- 사용 전 @post_id를 실제 테스트 게시글 ID로 변경한다.
SET @post_id = 1;

DELETE FROM post_views
WHERE post_id = @post_id;

UPDATE posts
SET view_count = 0
WHERE id = @post_id;

SELECT id, view_count
FROM posts
WHERE id = @post_id;
