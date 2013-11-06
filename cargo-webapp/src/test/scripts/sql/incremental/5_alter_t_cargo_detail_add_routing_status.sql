alter table t_cargo_detail
  add routing_status varchar2(100);

--//@UNDO
alter table t_cargo_detail drop column routing_status; 

