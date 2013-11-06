-- Create sequence 
create sequence seq_cargo_tracking_id
minvalue 1
maxvalue 99999999
start with 1
increment by 1
cache 20;

--//@UNDO
drop sequence seq_cargo_tracking_id;
