create table if not exists drzava
(
  id          integer primary key,
  naziv       text,
  glavni_grad integer
    constraint drzava_grad_id_fk
      references grad
);
