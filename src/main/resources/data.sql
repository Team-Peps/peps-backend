INSERT INTO games (id, name)
VALUES ('game-ow', 'Overwatch');

INSERT INTO rosters (id, game_id, name)
VALUES ('roster-1', 'game-ow', 'Team Peps');

INSERT INTO members (id, date_of_birth, dpi, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2001-08-27', '1600', 'Brice','Montsçavoir', 'FR', 'FDGod', 'Support', 'roster-1');

INSERT INTO members (id, date_of_birth, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2005-03-26', 'Benjamin','Nambruide', 'FR', 'Xeriongdh', 'Support', 'roster-1');

INSERT INTO members (id, date_of_birth, dpi, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '1994-02-04', '800', 'Terance','Tarlier', 'FR', 'SoOn', 'DPS', 'roster-1');

INSERT INTO members (id, date_of_birth, dpi, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2006-12-29', '600', 'Daniel','Castro', 'ES', 'xzodyal', 'DPS', 'roster-1');

INSERT INTO members (id, date_of_birth, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2006-02-25', 'Jonas','Stratemeyer', 'DE', 'Eis', 'Tank', 'roster-1');

INSERT INTO members (id, date_of_birth, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2000-01-01', 'Manuel','Demir', 'BE', 'Ken', 'DPS', 'roster-1');

INSERT INTO members (id, date_of_birth, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2000-01-01', 'Clément','Ortega', 'FR', 'Tarteg', 'Coach', 'roster-1');

INSERT INTO members (id, date_of_birth, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2000-01-01', 'Julien','Garcia', 'FR', 'LeRenegat', 'Coach', 'roster-1');

INSERT INTO members (id, date_of_birth, firstname, lastname, nationality, pseudo, role, roster_id)
VALUES (gen_random_uuid(), '2003-05-24', 'Mélissa','Hu Yang', 'FR', 'Søeny', 'Manager', 'roster-1');
