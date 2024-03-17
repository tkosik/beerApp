drop table if exists beer cascade;

drop table if exists customer cascade;

create table beer
(
    beer_style       tinyint        not null check (beer_style between 0 and 9),
    price            numeric(38, 2) not null check (price >= 1),
    quantity_on_hand integer,
    version          integer,
    created_date     timestamp(6),
    update_date      timestamp(6),
    beer_name        varchar(30)    not null,
    id               varchar(36)    not null,
    upc              varchar(255)   not null,
    primary key (id)
);

create table customer
(
    version            integer,
    created_date       timestamp(6),
    last_modified_date timestamp(6),
    id                 varchar(36) not null,
    customer_last_name varchar(255),
    customer_name      varchar(255),
    primary key (id)
);
