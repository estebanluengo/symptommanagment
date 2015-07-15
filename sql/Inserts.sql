use symptom;

insert into doctor(name, lastname, login) values ('Gregory', 'House', 'doctor1');
insert into doctor(name, lastname, login) values ('Emily', 'Ronald', 'doctor2');
insert into patient(name, lastname, login, birthdate, id_doctor) values ('Chuck', 'Norris', 'patient1', '1973-06-24', 1);
insert into patient(name, lastname, login, birthdate, id_doctor) values ('Samuel', 'Jackson', 'patient2', '1985-07-21', 1);
insert into patient(name, lastname, login, birthdate, id_doctor) values ('Brad', 'Pitt', 'patient3', '1970-02-09', 2);
insert into patient(name, lastname, login, birthdate, id_doctor) values ('Perry', 'Mason', 'patient4', '1989-01-12', 2);
commit;


