INSERT INTO board_comment (uid, bid, content) VALUES (1, 1, 'This is a comment on bid=1 from uid 1');
INSERT INTO board_comment (uid, bid, content) VALUES (2, 1, 'This is a comment on bid=1 from uid 2');
INSERT INTO board_comment (uid, bid, content) VALUES (3, 2, 'This is a comment on bid=2 from uid 3');
INSERT INTO board_comment (uid, bid, content) VALUES (4, 2, 'This is a comment on bid=2 from uid 4');
INSERT INTO board_comment (uid, bid, content) VALUES (1, 2, 'This is a comment on bid=2 from uid 1');
INSERT INTO board_comment (uid, bid, content) VALUES (2, 3, 'This is a comment on bid=3 from uid 2');
INSERT INTO board_comment (uid, bid, content) VALUES (3, 3, 'This is a comment on bid=3 from uid 3');
INSERT INTO board_comment (uid, bid, content) VALUES (4, 3, 'This is a comment on bid=3 from uid 4');
INSERT INTO board_comment (uid, bid, content) VALUES (1, 3, 'This is a comment on bid=3 from uid 1');

INSERT INTO `board_comment_count`(bid, count) VALUES(1, 2);
INSERT INTO `board_comment_count`(bid, count) VALUES(2, 3);
INSERT INTO `board_comment_count`(bid, count) VALUES(3, 4);