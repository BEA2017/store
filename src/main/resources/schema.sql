drop table if exists ingredients;

create table ingredients(
    id int auto_increment primary key,
    name varchar(100),
    type varchar(100),
    created datetime
)