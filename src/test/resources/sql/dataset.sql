INSERT INTO "user" (id, username, email, password_hash, roles, created_at) VALUES
  (1, 'alice', 'alice@example.com', 'pbkdf2_sha256$65536$tXKqsIEwcxR5HtwQu2qU4Q==$SKgwxMd7cwxJnoch/aC2d97SBr1wrkpmsrIQMjAToWg=', 'USER', CURRENT_TIMESTAMP),
  (2, 'bob', 'bob@example.com', 'pbkdf2_sha256$65536$NRpjFvrhD4DqnkYdrnb2LQ==$+KBkBM+xPjOHsZNr03Zf7NU8X+XReKQcA80mQlhreMI=', 'USER,ADMIN', CURRENT_TIMESTAMP);

INSERT INTO category (id, label) VALUES
  (1, 'Housing'),
  (2, 'Electronics');

INSERT INTO annonce (id, title, description, adress, mail, date, status, version, user_id, category_id) VALUES
  (1, 'Annonce 1', 'Desc 1', 'Paris 1', 'a1@example.com', TIMESTAMP '2025-01-01 10:00:00', 'DRAFT', 0, 1, 1),
  (2, 'Annonce 2', 'Desc 2', 'Paris 2', 'a2@example.com', TIMESTAMP '2025-01-02 10:00:00', 'PUBLISHED', 0, 1, 1),
  (3, 'Annonce 3', 'Desc 3', 'Paris 3', 'a3@example.com', TIMESTAMP '2025-01-03 10:00:00', 'ARCHIVED', 0, 2, 2),
  (4, 'Annonce 4', 'Desc 4', 'Paris 4', 'a4@example.com', TIMESTAMP '2025-01-04 10:00:00', 'DRAFT', 1, 2, 1),
  (5, 'Annonce 5', 'Desc 5', 'Paris 5', 'a5@example.com', TIMESTAMP '2025-01-05 10:00:00', 'PUBLISHED', 0, 1, 2),
  (6, 'Annonce 6', 'Desc 6', 'Paris 6', 'a6@example.com', TIMESTAMP '2025-01-06 10:00:00', 'DRAFT', 2, 1, 2);

ALTER TABLE "user" ALTER COLUMN id RESTART WITH 100;
ALTER TABLE category ALTER COLUMN id RESTART WITH 100;
ALTER TABLE annonce ALTER COLUMN id RESTART WITH 100;
