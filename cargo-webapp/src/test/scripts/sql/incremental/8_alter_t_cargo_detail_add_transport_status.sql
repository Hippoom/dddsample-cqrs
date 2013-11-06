alter table t_cargo_detail add transport_status varchar2(5);

--//@UNDO
alter table t_cargo_detail drop column transport_status; 

