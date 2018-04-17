delete from egeis_leavetype where tenantid='default';

INSERT INTO egeis_leavetype(id, name, description, halfdayallowed, payeligible, accumulative, encashable, active, createdby, createddate, lastmodifiedby, lastmodifieddate,tenantid)  VALUES (nextval('seq_egeis_leavetype'), 'Casual Leave', 'Casual Leave', false, true, false,false, true, 1, now(), 1, now(),'default');
INSERT INTO egeis_leavetype(id, name, description, halfdayallowed, payeligible, accumulative, encashable, active, createdby, createddate, lastmodifiedby, lastmodifieddate,tenantid)  VALUES (nextval('seq_egeis_leavetype'), 'Medical Leave', 'Medical Leave', false, true, false, false, true,  1, now(), 1, now(), 'default');
INSERT INTO egeis_leavetype(id, name, description, halfdayallowed, payeligible, accumulative, encashable, active, createdby, createddate, lastmodifiedby, lastmodifieddate,tenantid)  VALUES (nextval('seq_egeis_leavetype'), 'Half Pay Leave', 'Half Pay Leave', true, true, true ,false, true,  1, now(), 1, now(), 'default');
INSERT INTO egeis_leavetype(id, name, description, halfdayallowed, payeligible, accumulative, encashable, active, createdby, createddate, lastmodifiedby, lastmodifieddate,tenantid)  VALUES (nextval('seq_egeis_leavetype'), 'Earned Leave', 'Earned Leave',false, true, true, false, true, 1, now(), 1, now(), 'default');
INSERT INTO egeis_leavetype(id, name, description, halfdayallowed, payeligible, accumulative, encashable, active, createdby, createddate, lastmodifiedby, lastmodifieddate,tenantid)  VALUES (nextval('seq_egeis_leavetype'), 'Extraordinary Leave', 'Extraordinary Leave', false, false, false, false, true,  1, now(), 1, now(),'default');
INSERT INTO egeis_leavetype(id, name, description, halfdayallowed, payeligible, accumulative, encashable, active, createdby, createddate, lastmodifiedby, lastmodifieddate,tenantid)  VALUES (nextval('seq_egeis_leavetype'), 'Maternity Leave', 'Maternity Leave', false, true, false,false, true, 1, now(), 1, now(), 'default');
INSERT INTO egeis_leavetype(id, name, description, halfdayallowed, payeligible, accumulative, encashable, active, createdby, createddate, lastmodifiedby, lastmodifieddate,tenantid)  VALUES (nextval('seq_egeis_leavetype'), 'Paternity Leave', 'Paternity Leave', false, true, false,false, true, 1, now(), 1, now(),  'default');