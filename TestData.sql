-- remove any existing acmepass password
delete from acmepass where user_id = 6;
delete from acmepass where user_id = 7;
delete from acmepass where user_id = 8;
delete from acmepass where user_id = 9;

insert into acmepass
  (site, login, password, created_date, last_modified_date, user_id)
values
  ('tester.com', 'user2', 'password', now(), now(), 9);