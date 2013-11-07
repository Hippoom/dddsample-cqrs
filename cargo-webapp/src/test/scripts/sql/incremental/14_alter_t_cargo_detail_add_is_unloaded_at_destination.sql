alter table t_cargo_detail add is_unloaded_des varchar2(1) default '0';

--//@UNDO
alter table t_cargo_detail drop column is_unloaded_des; 

