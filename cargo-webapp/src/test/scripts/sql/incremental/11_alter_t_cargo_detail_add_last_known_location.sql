alter table t_cargo_detail add last_known_location varchar2(5);

--//@UNDO
alter table t_cargo_detail drop column last_known_location; 

