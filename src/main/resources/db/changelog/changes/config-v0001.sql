CREATE TABLE WMS_CONFIG 
( 
     ID serial primary key,
     APPLICATION character varying(200), 
     PROFILE     character varying(200), 
     MODULE       character varying(200),
     LABEL       character varying(25),
     KEY         character varying(200), 
     VALUE       character varying(25),
     DESCRIPTION       character varying(1000),
 	 HOST_NAME  character varying(50),
     CREATED_DTTM  timestamp not null default NOW(),
     UPDATED_DTTM  timestamp not null default NOW(),
     CREATED_BY character varying(25),
     UPDATED_BY character varying(25),
     VERSION INTEGER 
);
CREATE TABLE WMS_BUS_LOCN_CONFIG 
( 
     ID serial primary key,
     WMS_CONFIG_ID integer,
     BUS_NAME    character varying(25),
     LOCN_NBR integer not null,
     VALUE       character varying(25),
     COMMENTS       character varying(200),
 	 HOST_NAME  character varying(50),
     CREATED_DTTM  timestamp not null default NOW(),
     UPDATED_DTTM  timestamp not null default NOW(),
     CREATED_BY character varying(25),
     UPDATED_BY character varying(25),
     VERSION INTEGER 
);

INSERT INTO WMS_CONFIG (id,key, value, application, profile, module, description)
VALUES (1,'enable.inventory.management','N','appplication','dev','inventory','Enable Inventory Management');
INSERT INTO WMS_CONFIG (id,key, value, application, profile, module, description)
VALUES (2,'homestore','N','appplication','dev','fulfillment','Small Store (Pack/Print)');
INSERT INTO WMS_CONFIG (id,key, value, application, profile, module, description)
VALUES (3,'warehouse','N','appplication','dev','fulfillment','Warehouse (Inventory/Pick/Pack/Print)');
INSERT INTO WMS_CONFIG (id,key, value, application, profile, module, description)
VALUES (4,'waveless','Y','appplication','dev','fulfillment','Waveless (Order Streaming, Process Orders as they drop), needs inventory management module');




