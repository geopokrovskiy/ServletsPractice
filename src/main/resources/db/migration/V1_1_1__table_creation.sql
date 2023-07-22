CREATE TABLE users
(
    id   bigint primary key auto_increment,
    name varchar(50) not null
);

CREATE TABLE files
(
    id       bigint primary key auto_increment,
    name     varchar(50) not null,
    filePath varchar(50) not null
);

CREATE TABLE events
(
    id      bigint primary key auto_increment,
    user_id bigint not null,
    file_id bigint not null,
    foreign key (user_id) references users (id)
        on delete cascade on update cascade,
    foreign key (file_id) references files (id)
        on delete cascade on update cascade
);