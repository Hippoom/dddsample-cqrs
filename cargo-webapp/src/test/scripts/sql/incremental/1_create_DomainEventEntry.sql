create table DomainEventEntry (
    aggregateIdentifier varchar2(255) not null,
    sequenceNumber number(19,0) not null,
    type varchar2(255) not null,
    eventIdentifier varchar2(255) not null,
    metaData blob,
    payload blob not null,
    payloadRevision varchar2(255),
    payloadType varchar2(255) not null,
    timeStamp varchar2(255) not null
);

alter table DomainEventEntry
  add constraint PK_DomainEventEntry primary key (aggregateIdentifier, sequenceNumber, type);
--//@UNDO
drop table DomainEventEntry;  


