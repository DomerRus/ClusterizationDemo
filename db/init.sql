CREATE TABLE roles ( id serial NOT NULL CONSTRAINT id_roles_pk PRIMARY KEY, NAME VARCHAR ( 20 ) NOT NULL );
ALTER TABLE roles OWNER TO postgres;
-- auto-generated definition
CREATE TABLE users (
	id serial NOT NULL CONSTRAINT id_users_pk PRIMARY KEY,
	LOGIN VARCHAR ( 50 ),
	PASSWORD VARCHAR ( 256 ),
	role_id INTEGER CONSTRAINT user_table_role_table_id_fk REFERENCES roles
);
CREATE TABLE stores (
  id serial NOT NULL CONSTRAINT id_stores_pk PRIMARY KEY,
  latitude float8,
  longitude float8,
  address varchar(255)
);
CREATE TABLE clusters (
  id serial NOT NULL CONSTRAINT id_clusters_pk PRIMARY KEY
);
CREATE TABLE cluster_store_maping (
	id serial NOT NULL CONSTRAINT id_clusterStore_pk PRIMARY KEY,
  store_id INTEGER,
	cluster_id INTEGER
);
ALTER TABLE cluster_store_maping ADD CONSTRAINT "fkgfa6x0ciyon7rhvhdx5tpf5vm" FOREIGN KEY ("store_id") REFERENCES stores("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE cluster_store_maping ADD CONSTRAINT "fkj122f72116i8i1hr8vjqb7cb2" FOREIGN KEY ("cluster_id") REFERENCES clusters("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE stores OWNER TO postgres;
ALTER TABLE clusters OWNER TO postgres;
ALTER TABLE users OWNER TO postgres;
ALTER TABLE cluster_store_maping OWNER TO postgres;
CREATE UNIQUE INDEX user_table_login_uindex ON users(LOGIN);
INSERT INTO roles(NAME)
VALUES
	('ROLE_ADMIN');
INSERT INTO roles(NAME)
VALUES
	('ROLE_USER');
INSERT INTO users(login, password, role_id)
VALUES
	('admin', '$2a$10$akvicOl6RFS8BPLorlrcb.HEivCBf.XWe4ZqS.hK9VoC91ma.wNA2', 1); --admin admin
INSERT INTO stores VALUES (76674, 45.0612754, 38.9276344, 'Краснодарский край, Краснодар г, Сочинская ул,27');
INSERT INTO stores VALUES (76689, 45.137543, 38.998302, 'Краснодарский край, Краснодар г, 9-я Тихая ул,11,а ');
INSERT INTO stores VALUES (76690, 45.009745, 38.963752, 'Краснодарский край, Краснодар г, Речная ул,3');
INSERT INTO stores VALUES (76916, 45.0362842, 38.99975359, 'Краснодарский край, Краснодар г, Северная ул,510');
INSERT INTO stores VALUES (76934, 45.051138, 39.001821, 'Краснодарский край, Краснодар г, им Островского ул,18');
INSERT INTO stores VALUES (77144, 45.0114877, 39.1225639, 'Краснодарский край, Краснодар г, Крылатая ул,2');
INSERT INTO stores VALUES (77175, 45.055888, 38.891516, 'Краснодарский край, Краснодар г, им Вавилова Н.И. ул,1');
INSERT INTO stores VALUES (77176, 45.05595135, 39.01114225, 'Краснодарский край, Краснодар г, Победы пл,35');
INSERT INTO stores VALUES (77406, 45.035776, 38.977774, 'Краснодарский край, Краснодар г, им Буденного ул,147');
INSERT INTO stores VALUES (77414, 45.067694, 38.982915, 'Краснодарский край, Краснодар г, Нефтяников ул,28');
INSERT INTO stores VALUES (77855, 45.034005, 39.05008, 'Краснодарский край, Краснодар г, Уральская ул,79');
INSERT INTO stores VALUES (77947, 45.080325, 38.893774, 'Краснодарский край, Краснодар г, Красных Партизан ул,1');
INSERT INTO stores VALUES (77966, 45.071011, 38.94768, 'Краснодарский край, Краснодар г, им Симиренко ул,14,,корп1 ');
INSERT INTO stores VALUES (77984, 45.093505, 39.003197, 'Краснодарский край, Краснодар г, Московская ул,162');
INSERT INTO stores VALUES (78093, 45.036818, 38.933609, 'Краснодарский край, Краснодар г, Минская ул,120,к.8 ');
INSERT INTO stores VALUES (78095, 45.031639, 39.047182, 'Краснодарский край, Краснодар г, Сормовская ул,178,-180/1, лит. Е ');
INSERT INTO stores VALUES (78120, 45.0273033, 39.1066619, 'Краснодарский край, Краснодар г, им Евдокии Бершанской ул,235');
INSERT INTO stores VALUES (78311, 45.04998913, 38.98233365, 'Краснодарский край, Краснодар г, Одесская ул,37,А ');
INSERT INTO stores VALUES (78325, 45.010535, 39.067848, 'Краснодарский край, Краснодар г, Мачуги ул,2');
INSERT INTO stores VALUES (78353, 45.04382363, 38.93054724, 'Краснодарский край, Краснодар г, Калинина ул,138');
