create table if not exists `user` (
    id            int unsigned auto_increment           primary key,
    email         varchar(200)                          not null,
    name          varchar(200)                          not null,
    nickname      varchar(200)                          not null,
    password      varchar(200)                          not null,
    status        varchar(15) default 'ACTIVE'          null,
    access_token  varchar(256)                          null,
    joined_date   timestamp   default CURRENT_TIMESTAMP not null,
    modified_date timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    deleted_date  timestamp                             null,
    constraint uidx__email unique (email)
);

create table if not exists `board` (
    id            int unsigned auto_increment           primary key,
    uid           int unsigned                          not null,
    title         varchar(256)                          not null,
    body          text                                  not null,
    status        varchar(15) default 'ACTIVE'          null,
    created_date  timestamp   default CURRENT_TIMESTAMP not null,
    modified_date timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    deleted_date  timestamp                             null
) comment '게시글';

create table if not exists `board_comment` (
    id            int unsigned auto_increment         primary key,
    uid           int unsigned                        not null,
    bid           int unsigned                        not null,
    content       text                                not null,
    created_date  timestamp default CURRENT_TIMESTAMP not null,
    modified_date timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);

create table if not exists `board_comment_count` (
    bid   int unsigned             not null primary key,
    count int unsigned default '0' not null
);

create table if not exists `board_like` (
    id  int unsigned auto_increment  primary key,
    uid int unsigned default '0' not null,
    bid int unsigned default '0' not null,
    constraint udx__uid__pid unique (uid, bid)
);

create table if not exists `board_like_count` (
    bid   int unsigned             not null primary key,
    count int unsigned default '0' not null
);

create table if not exists board_view_event (
    id        int unsigned auto_increment         primary key,
    bid       int unsigned                        not null,
    uid       varchar(255)                        null,
    ip        varchar(64)                         null,
    viewed_at timestamp default CURRENT_TIMESTAMP not null,
    constraint udx__bid___uid___ip unique (bid, uid, ip)
);

create table if not exists `board_view_count` (
    bid   int unsigned             not null primary key,
    count int unsigned default '0' not null
);