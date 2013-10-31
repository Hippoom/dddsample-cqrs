create table t_cargo_detail (
        tracking_id number not null,
        origin_unlocode varchar2(5) not null,
        destination_unlocode varchar2(5) not null,
        arrival_deadline date not null
    );

alter table t_cargo_detail
  add constraint pk_cargo_detail primary key (tracking_id);

--//@UNDO
drop table t_cargo_detail;  

