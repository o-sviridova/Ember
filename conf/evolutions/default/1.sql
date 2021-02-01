# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table accounts (
  id                            serial not null,
  username                      varchar(255),
  password                      varchar(255),
  role                          varchar(8),
  constraint ck_accounts_role check ( role in ('HABITANT','MAYOR','CHIEF','POSTMAN')),
  constraint uq_accounts_username unique (username),
  constraint pk_accounts primary key (id)
);

create table habitants (
  id                            serial not null,
  name                          varchar(255),
  surname                       varchar(255),
  patronymic                    varchar(255),
  sex                           varchar(6),
  birthday                      timestamptz,
  working_capacity              boolean,
  account_id                    integer,
  career_objective_id           integer,
  constraint ck_habitants_sex check ( sex in ('MALE','FEMALE')),
  constraint uq_habitants_account_id unique (account_id),
  constraint pk_habitants primary key (id)
);

create table notification (
  id                            serial not null,
  message                       varchar(255),
  status                        varchar(8),
  constraint ck_notification_status check ( status in ('OPEN','ACCEPTED')),
  constraint pk_notification primary key (id)
);

create table organization (
  id                            serial not null,
  name                          varchar(255),
  is_closed                     boolean default false not null,
  chief_id                      integer,
  constraint uq_organization_chief_id unique (chief_id),
  constraint pk_organization primary key (id)
);

create table requests (
  id                            serial not null,
  json                          varchar(255),
  status                        varchar(8),
  request_type                  varchar(19),
  organization_id               integer,
  constraint ck_requests_status check ( status in ('OPEN','ACCEPTED','ABORTED','DENIED')),
  constraint ck_requests_request_type check ( request_type in ('CREATE_VACANCY_TYPE','CREATE_VACANCY','HIRE_CANDIDATE','FREE_VACANCY')),
  constraint pk_requests primary key (id)
);

create table vacancies (
  id                            serial not null,
  from_date                     timestamptz,
  to_date                       timestamptz,
  is_archived                   boolean default false not null,
  worker_id                     integer,
  vacancy_type_id               integer,
  constraint pk_vacancies primary key (id)
);

create table vacancy_types (
  id                            serial not null,
  name                          varchar(255),
  organization_id               integer,
  constraint uq_vacancy_types_name_organization_id unique (name,organization_id),
  constraint pk_vacancy_types primary key (id)
);

alter table habitants add constraint fk_habitants_account_id foreign key (account_id) references accounts (id) on delete restrict on update restrict;

alter table habitants add constraint fk_habitants_career_objective_id foreign key (career_objective_id) references vacancy_types (id) on delete restrict on update restrict;
create index ix_habitants_career_objective_id on habitants (career_objective_id);

alter table organization add constraint fk_organization_chief_id foreign key (chief_id) references habitants (id) on delete restrict on update restrict;

alter table requests add constraint fk_requests_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;
create index ix_requests_organization_id on requests (organization_id);

alter table vacancies add constraint fk_vacancies_worker_id foreign key (worker_id) references habitants (id) on delete restrict on update restrict;
create index ix_vacancies_worker_id on vacancies (worker_id);

alter table vacancies add constraint fk_vacancies_vacancy_type_id foreign key (vacancy_type_id) references vacancy_types (id) on delete restrict on update restrict;
create index ix_vacancies_vacancy_type_id on vacancies (vacancy_type_id);

alter table vacancy_types add constraint fk_vacancy_types_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;
create index ix_vacancy_types_organization_id on vacancy_types (organization_id);


# --- !Downs

alter table if exists habitants drop constraint if exists fk_habitants_account_id;

alter table if exists habitants drop constraint if exists fk_habitants_career_objective_id;
drop index if exists ix_habitants_career_objective_id;

alter table if exists organization drop constraint if exists fk_organization_chief_id;

alter table if exists requests drop constraint if exists fk_requests_organization_id;
drop index if exists ix_requests_organization_id;

alter table if exists vacancies drop constraint if exists fk_vacancies_worker_id;
drop index if exists ix_vacancies_worker_id;

alter table if exists vacancies drop constraint if exists fk_vacancies_vacancy_type_id;
drop index if exists ix_vacancies_vacancy_type_id;

alter table if exists vacancy_types drop constraint if exists fk_vacancy_types_organization_id;
drop index if exists ix_vacancy_types_organization_id;

drop table if exists accounts cascade;

drop table if exists habitants cascade;

drop table if exists notification cascade;

drop table if exists organization cascade;

drop table if exists requests cascade;

drop table if exists vacancies cascade;

drop table if exists vacancy_types cascade;

