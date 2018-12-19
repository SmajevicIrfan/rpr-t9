create table if not exists grad
(
  id              int primary key,
  naziv           text,
  broj_stanovnika int,
  drzava          int
    constraint grad_drzava_id_fk
      references drzava
);