create table if not exists grad
(
  id              integer primary key,
  naziv           text,
  broj_stanovnika integer,
  drzava          int
    constraint grad_drzava_id_fk
      references drzava
);