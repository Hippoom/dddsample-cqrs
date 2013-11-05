create table t_cargo_leg_detail (
        tracking_id number(8) not null,
        idx number not null,
        voyage_number varchar2(100) not null,
        from_location varchar2(5) not null,
        to_location varchar2(5) not null,
        load_time date not null,
        unload_time date not null
    );

alter table t_cargo_leg_detail
  add constraint pk_cargo_leg_detail primary key (tracking_id, idx);

--//@UNDO
drop table t_cargo_leg_detail;  

