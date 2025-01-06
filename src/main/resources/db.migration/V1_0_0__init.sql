create table user_role (
    id              int4               primary key ,
    name            varchar(20)        not null ,
    description     varchar(30)        not null
);

create table app_user (
    sub             varchar(100)       primary key ,
    given_name      varchar(50) ,
    name            varchar(50) ,
    family_name     varchar(50) ,
    user_role_id    int4 ,
    foreign key     (user_role_id)     references user_role(id)
);

create table user_details (
    id              bigserial           primary key ,
    picture         text ,
    email           varchar(50) ,
    date_of_birth   timestamp ,
    about_yourself  text ,
    city            varchar(30) ,
    user_id         varchar(100) ,
    foreign key     (user_id)           references app_user(sub)
);