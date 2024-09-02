insert into category (id, description) values (1, 'Comic Books');
insert into category (id, description) values (2, 'Movies');
insert into category (id, description) values (3, 'Book');

insert into supplier (id, name) values (1, 'Panini Comics');
insert into supplier (id, name) values (2, 'Amazon');

insert into product (id, name, idsupplier, idcategory, quantity_available) values (1, 'Crise nas Infinitas Terras', 1, 1, 10);
insert into product (id, name, idsupplier, idcategory, quantity_available) values (2, 'Interstellar', 2, 2, 5);
insert into product (id, name, idsupplier, idcategory, quantity_available) values (3, 'Harry Potter e a Pedra Filosofal', 2, 3, 3);