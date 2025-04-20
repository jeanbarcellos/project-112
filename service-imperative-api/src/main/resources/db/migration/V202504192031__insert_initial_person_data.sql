SET client_encoding TO utf8;

INSERT INTO project112.person (name, birth_date) VALUES ('Alice Johnson', '1990-05-12');
INSERT INTO project112.person (name, birth_date) VALUES ('Bob Smith', '1985-10-20');

-- inserts gerados automaticamente
DO $$
BEGIN
  FOR i IN 1..98 LOOP
    INSERT INTO project112.person (name, birth_date)
    VALUES ('Person ' || i, date '1970-01-01' + (i * interval '365 days') / 100);
  END LOOP;
END $$;