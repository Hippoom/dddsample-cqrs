alter table t_cargo_detail add ne_ha_voyage varchar2(5);

--//@UNDO
alter table t_cargo_detail drop column ne_ha_voyage; 

