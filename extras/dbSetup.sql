use ebank;

create table user (
    id int not null auto_increment,
    name varchar(150) not null unique,
    password varchar(300) not null,
    balance float not null,

    primary key (id)
);

create table transaction (
    id int not null auto_increment,
    sender_id int not null,
    receiver_id int not null,
    ammount float not null,
    time timestamp not null,

    primary key (id),
    foreign key (sender_id) references user (id)
        on delete no action
        on update cascade,
    foreign key (receiver_id) references user (id)
        on delete no action
        on update cascade
);
