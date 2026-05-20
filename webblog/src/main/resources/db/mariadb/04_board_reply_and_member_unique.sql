-- =============================================================================
-- webblog - 기존 DB 업그레이드: 게시판(board), 게시글 board_id·parent_post_id
-- (신규 설치는 01_create_tables.sql 만으로 충분합니다.)
-- 실행: mysql -u ... pub_blog < 04_board_reply_and_member_unique.sql
-- =============================================================================

USE pub_blog;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS board (
  board_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  blog_id       BIGINT UNSIGNED NOT NULL,
  board_name    VARCHAR(200) NOT NULL,
  sort_order    INT NOT NULL DEFAULT 0,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (board_id),
  KEY idx_board_blog (blog_id),
  CONSTRAINT fk_board_blog
    FOREIGN KEY (blog_id) REFERENCES blog (blog_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO board (blog_id, board_name, sort_order)
SELECT b.blog_id, '기본 게시판', 0
FROM blog b
WHERE NOT EXISTS (SELECT 1 FROM board x WHERE x.blog_id = b.blog_id);

SET @col_board := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'post' AND COLUMN_NAME = 'board_id'
);
SET @sql_board := IF(@col_board = 0,
  'ALTER TABLE post ADD COLUMN board_id BIGINT UNSIGNED NULL AFTER blog_id',
  'SELECT 1');
PREPARE stmt FROM @sql_board;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_parent := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'post' AND COLUMN_NAME = 'parent_post_id'
);
SET @sql_parent := IF(@col_parent = 0,
  'ALTER TABLE post ADD COLUMN parent_post_id BIGINT UNSIGNED NULL AFTER board_id',
  'SELECT 1');
PREPARE stmt2 FROM @sql_parent;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

UPDATE post p
INNER JOIN (
  SELECT blog_id, MIN(board_id) AS board_id
  FROM board
  GROUP BY blog_id
) m ON m.blog_id = p.blog_id
SET p.board_id = m.board_id
WHERE p.board_id IS NULL;

ALTER TABLE post MODIFY COLUMN board_id BIGINT UNSIGNED NOT NULL;

SET @fk_board := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'post' AND CONSTRAINT_NAME = 'fk_post_board'
);
SET @sql_fkb := IF(@fk_board = 0,
  'ALTER TABLE post ADD CONSTRAINT fk_post_board FOREIGN KEY (board_id) REFERENCES board (board_id) ON DELETE RESTRICT ON UPDATE CASCADE',
  'SELECT 1');
PREPARE stmt3 FROM @sql_fkb;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

SET @fk_par := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'post' AND CONSTRAINT_NAME = 'fk_post_parent'
);
SET @sql_fkp := IF(@fk_par = 0,
  'ALTER TABLE post ADD CONSTRAINT fk_post_parent FOREIGN KEY (parent_post_id) REFERENCES post (post_id) ON DELETE CASCADE ON UPDATE CASCADE',
  'SELECT 1');
PREPARE stmt4 FROM @sql_fkp;
EXECUTE stmt4;
DEALLOCATE PREPARE stmt4;

-- 멤버 (blog_id,user_id) 유니크: 기존 중복 행이 있으면 실패합니다.
SET @uk := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'blog_member' AND INDEX_NAME = 'uk_blog_member_blog_user'
);
SET @sql_uk := IF(@uk = 0,
  'ALTER TABLE blog_member ADD UNIQUE KEY uk_blog_member_blog_user (blog_id, user_id)',
  'SELECT 1');
PREPARE stmt5 FROM @sql_uk;
EXECUTE stmt5;
DEALLOCATE PREPARE stmt5;
