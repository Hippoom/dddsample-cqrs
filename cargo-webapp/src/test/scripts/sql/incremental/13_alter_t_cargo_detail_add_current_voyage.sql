alter table t_cargo_detail add current_voyage varchar2(5);

--//@UNDO
alter table t_cargo_detail drop column current_voyage; 

