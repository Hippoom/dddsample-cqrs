alter table t_cargo_detail add eta date;

--//@UNDO
alter table t_cargo_detail drop column eta; 

