create table ambassadors
(
    id                 varchar(255) not null
        primary key,
    image_key          varchar(255) not null,
    instagram_username varchar(255),
    name               varchar(255) not null,
    tiktok_username    varchar(255),
    twitch_username    varchar(255),
    twitter_x_username varchar(255),
    youtube_username   varchar(255)
);

alter table ambassadors
    owner to teampeps;

create table ambassador_translations
(
    id            varchar(255) not null
        primary key,
    description   text         not null,
    lang          varchar(255) not null,
    ambassador_id varchar(255) not null
        constraint fk43j0ls3v8gh8p91m79oi3gj16
            references ambassadors
);

alter table ambassador_translations
    owner to teampeps;

create table articles
(
    id                  varchar(255) not null
        primary key,
    article_type        varchar(255) not null,
    created_at          date         not null,
    image_key           varchar(255),
    thumbnail_image_key varchar(255)
);

alter table articles
    owner to teampeps;

create table article_translations
(
    id         varchar(255) not null
        primary key,
    content    text,
    lang       varchar(255) not null,
    title      varchar(255) not null,
    article_id varchar(255) not null
        constraint fklb0kl16sn4gu5f1ksy1rj77ci
            references articles
);

alter table article_translations
    owner to teampeps;

create index idx_article_created_at
    on articles (created_at);

create index idx_article_type
    on articles (article_type);

create table authors
(
    id   varchar(255) not null
        primary key,
    name varchar(255) not null
);

alter table authors
    owner to teampeps;

create table galleries
(
    id                  varchar(255) not null
        primary key,
    date                date         not null,
    thumbnail_image_key varchar(255) not null
);

alter table galleries
    owner to teampeps;

create table gallery_photos
(
    id         varchar(255) not null
        primary key,
    image_key  varchar(255) not null,
    author_id  varchar(255) not null
        constraint fkhqx7w9hnhotjvm6hm9ubbmq4k
            references authors,
    gallery_id varchar(255) not null
        constraint fk1lrhgj1l77rnk0945u0ln8u4t
            references galleries
);

alter table gallery_photos
    owner to teampeps;

create table gallery_translations
(
    id          varchar(255) not null
        primary key,
    description text         not null,
    event_name  varchar(255) not null,
    lang        varchar(255) not null,
    gallery_id  varchar(255) not null
        constraint fkqlls1ey2y04ihcb6m3bhe0t6i
            references galleries
);

alter table gallery_translations
    owner to teampeps;

create table heroes
(
    id        varchar(255) not null
        primary key,
    game      varchar(255) not null,
    image_key varchar(255) not null,
    name      varchar(255) not null,
    role      varchar(255) not null
);

alter table heroes
    owner to teampeps;

create index idx_heroe_game
    on heroes (game);

create index idx_heroe_role
    on heroes (role);

create table legends
(
    id   varchar(255) not null
        primary key,
    name varchar(255) not null
);

alter table legends
    owner to teampeps;

create table matchs
(
    id                       varchar(255) not null
        primary key,
    competition_image_height integer,
    competition_image_key    varchar(255) not null,
    competition_image_width  integer,
    competition_name         varchar(255) not null,
    datetime                 timestamp(6) not null,
    game                     varchar(255) not null,
    opponent                 varchar(255) not null,
    opponent_image_height    integer,
    opponent_image_key       varchar(255) not null,
    opponent_image_width     integer,
    opponent_score           varchar(255),
    score                    varchar(255),
    stream_url               varchar(255),
    vod_url                  varchar(255)
);

alter table matchs
    owner to teampeps;

create index idx_match_game
    on matchs (game);

create index idx_match_score
    on matchs (score);

create table members
(
    id                 varchar(255) not null
        primary key,
    date_of_birth      date         not null,
    firstname          varchar(255) not null,
    game               varchar(255) not null,
    image_key          varchar(255),
    instagram_username varchar(255),
    is_active          boolean,
    is_substitute      boolean,
    lastname           varchar(255) not null,
    nationality        varchar(255) not null,
    pseudo             varchar(255) not null
        constraint uk_s7kn3y6ckr9k0n0b33rh0m2x6
            unique,
    role               varchar(255) not null,
    tiktok_username    varchar(255),
    twitch_username    varchar(255),
    twitter_username   varchar(255),
    youtube_username   varchar(255)
);

alter table members
    owner to teampeps;

create table achievements
(
    id               varchar(255) not null
        primary key,
    competition_name varchar(255) not null,
    game             varchar(255),
    ranking          integer      not null,
    member_id        varchar(255)
        constraint fksrtx8btceg6ghddspnvhasgcs
            references members
);

alter table achievements
    owner to teampeps;

create index idx_achievement_game
    on achievements (game);

create table member_favorite_heroes
(
    member_id varchar(255) not null
        constraint fks2me9ym98hikvvsyvm6itjdqi
            references members,
    hero_id   varchar(255) not null
        constraint fksudal9y5qaj6q5x5wia4gh0pf
            references heroes
);

alter table member_favorite_heroes
    owner to teampeps;

create table member_translations
(
    id          varchar(255) not null
        primary key,
    description text         not null,
    lang        varchar(255) not null,
    member_id   varchar(255) not null
        constraint fk89b6cwc20ab2j1cfo14dlpvf0
            references members
);

alter table member_translations
    owner to teampeps;

create table partners
(
    id           varchar(255) not null
        primary key,
    image_key    varchar(255),
    is_active    boolean,
    link         varchar(255),
    name         varchar(255) not null,
    order_index  bigint       not null,
    partner_type varchar(255) not null
);

alter table partners
    owner to teampeps;

create table partner_codes
(
    id             varchar(255) not null
        primary key,
    code           varchar(255) not null,
    description_en text         not null,
    description_fr text         not null,
    partner_id     varchar(255) not null
        constraint fktrk7dqf37oj3xgja3pgy7alp1
            references partners
);

alter table partner_codes
    owner to teampeps;

create table partner_translations
(
    id          varchar(255) not null
        primary key,
    description text         not null,
    lang        varchar(255) not null,
    partner_id  varchar(255) not null
        constraint fk3s3qb0hyq3gw2le0vlc4j4bly
            references partners
);

alter table partner_translations
    owner to teampeps;

create table sliders
(
    id          varchar(255) not null
        primary key,
    cta_link    varchar(255) not null,
    is_active   boolean      not null,
    order_index bigint       not null
);

alter table sliders
    owner to teampeps;

create table slider_translations
(
    id               varchar(255) not null
        primary key,
    cta_label        varchar(255) not null,
    image_key        varchar(255) not null,
    lang             varchar(255) not null,
    mobile_image_key varchar(255) not null,
    slider_id        varchar(255) not null
        constraint fk44t2a0yabcd0boyfyn2l1115d
            references sliders
);

alter table slider_translations
    owner to teampeps;

create index idx_slider_order
    on sliders (order_index);

create table users
(
    id          varchar(255)   not null
        primary key,
    auth_type   varchar(255),
    authorities varchar(255)[] not null,
    avatar_url  varchar(255),
    created_at  date           not null,
    discord_id  varchar(255)
        constraint uk_8h3kehimxhos38stbts56bkba
            unique,
    email       varchar(255)   not null
        constraint uk_6dotkott2kjsp8vw4d0m25fb7
            unique,
    enable      boolean        not null,
    password    varchar(255),
    username    varchar(255)   not null
        constraint uk_r43af9ap4edm43mmtq01oddj6
            unique
);

alter table users
    owner to teampeps;

create table tokens
(
    id         integer      not null
        primary key,
    hex        varchar(255) not null,
    is_expired boolean      not null,
    is_revoked boolean      not null,
    type       varchar(255) not null,
    user_id    varchar(255)
        constraint fk2dylsfo39lgjyqml2tbe0b0ss
            references users
);

alter table tokens
    owner to teampeps;

create table videos
(
    id        varchar(255) not null
        primary key,
    image_key varchar(255),
    link      varchar(255) not null,
    title     varchar(30)  not null
);

alter table videos
    owner to teampeps;


