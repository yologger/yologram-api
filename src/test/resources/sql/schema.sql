create table if not exists user (
    id              int unsigned    auto_increment primary key,
    email           varchar(200)    not null unique ,
    name            varchar(200)    not null,
    nickname        varchar(200)    not null,
    password        varchar(200)    not null,
    access_token    varchar(256)    null,
    join_date       timestamp       not null default CURRENT_TIMESTAMP,
    modified_at     timestamp       not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
) charset = utf8mb4, collate = utf8mb4_general_ci;

create table if not exists `board` (
    id                int unsigned auto_increment primary key,
    uid               int unsigned                                      not null,
    title             varchar(256)                                      not null,
    body              TEXT                                              not null,
    create_date       timestamp default CURRENT_TIMESTAMP not null,
    modified_at       timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
) comment '게시글' charset = 'utf8mb4' collate = 'utf8mb4_general_ci';