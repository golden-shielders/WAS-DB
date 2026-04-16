INSERT INTO web_site_user (user_name, pw, role)
SELECT 'admin', 'admin1234', 'ADMIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM web_site_user WHERE user_name = 'admin');

INSERT INTO web_site_user (user_name, pw, role)
SELECT 'user1', 'user1234', 'USER' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM web_site_user WHERE user_name = 'user1');

INSERT INTO post (title, content, author_name)
SELECT '첫번째 게시글', '첫번째 내용입니다', 'admin' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '첫번째 게시글');

INSERT INTO post (title, content, author_name)
SELECT '두번째 게시글', '두번째 내용입니다', 'user1' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '두번째 게시글');

INSERT INTO post (title, content, author_name)
SELECT '세번째 게시글', '세번째 내용입니다', 'admin' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '세번째 게시글');