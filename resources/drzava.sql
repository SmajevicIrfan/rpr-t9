create table if not exists drzava
(
  id          int
    constraint drzava_pk
      primary key,
  naziv       text,
  glavni_grad int
    constraint drzava_grad_id_fk
      references grad
);
