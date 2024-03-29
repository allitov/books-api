CREATE SCHEMA IF NOT EXISTS books_api_schema;

CREATE TABLE IF NOT EXISTS books_api_schema.categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS books_api_schema.books (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    author VARCHAR(256) NOT NULL,
    category_id BIGINT NOT NULL,
    publication_date DATE NOT NULL,
    FOREIGN KEY (category_id) REFERENCES books_api_schema.categories(id) ON DELETE CASCADE
);

insert into categories (name) values ('Thriller');
insert into categories (name) values ('Horror');
insert into categories (name) values ('Comedy');
insert into categories (name) values ('Children');
insert into categories (name) values ('Western');

insert into books (name, author, category_id, publication_date) values ('Song of the Bloodred Flower (Laulu tulipunaisesta kukasta)', 'Nels Lamerton', 1, '2012-06-20');
insert into books (name, author, category_id, publication_date) values ('Pin...', 'Oralle Tarbin', 3, '2005-05-25');
insert into books (name, author, category_id, publication_date) values ('1612: Khroniki smutnogo vremeni', 'Adlai Leader', 5, '2008-10-07');
insert into books (name, author, category_id, publication_date) values ('Travelling Players, The (O thiasos)', 'Adelheid Goscomb', 4, '2007-04-28');
insert into books (name, author, category_id, publication_date) values ('Prisoners of the Lost Universe', 'Sisile Erricker', 4, '2009-12-06');
