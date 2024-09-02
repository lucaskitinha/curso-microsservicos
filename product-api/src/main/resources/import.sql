insert into category (id, description) values (1000, 'Comic Books');
insert into category (id, description) values (1001, 'Movies');
insert into category (id, description) values (1002, 'Book');

insert into supplier (id, name) values (1000, 'Panini Comics');
insert into supplier (id, name) values (1001, 'Amazon');

insert into product (id, name, idsupplier, idcategory, quantity_available) values (1000, 'Crise nas Infinitas Terras', 1000, 1000, 10);
insert into product (id, name, idsupplier, idcategory, quantity_available) values (1001, 'Interstellar', 1001, 1001, 5);
insert into product (id, name, idsupplier, idcategory, quantity_available) values (1002, 'Harry Potter e a Pedra Filosofal', 1001, 1002, 3);