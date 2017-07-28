ALTER TABLE egpt_address ALTER latitude type numeric;
ALTER TABLE egpt_address ALTER longitude type numeric;
ALTER TABLE egpt_propertydetails ALTER sitelength type numeric;
ALTER TABLE egpt_propertydetails ALTER sitebreadth type numeric;
ALTER TABLE egpt_propertydetails ALTER sitalarea type numeric;
ALTER TABLE egpt_propertydetails ALTER totalbuiltuparea type numeric;
ALTER TABLE egpt_propertydetails ALTER undividedshare type numeric;
ALTER TABLE egpt_unit ALTER length type numeric;
ALTER TABLE egpt_unit ALTER width type numeric;
ALTER TABLE egpt_unit ALTER builtuparea type numeric;
ALTER TABLE egpt_unit ALTER assessablearea type numeric;
ALTER TABLE egpt_unit ALTER bpabuiltuparea type numeric;
ALTER TABLE egpt_unit ALTER rentCollected type numeric;
ALTER TABLE egpt_unit ALTER manualarv type numeric;
ALTER TABLE egpt_unit ALTER arv type numeric;
ALTER TABLE egpt_vacantland ALTER marketvalue type numeric;
ALTER TABLE egpt_vacantland ALTER capitalvalue type numeric;
ALTER TABLE egpt_vacantland ALTER resdplotarea type numeric;
ALTER TABLE egpt_vacantland ALTER nonresdplotarea type numeric;
ALTER TABLE egpt_mstr_floortype ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_structureclass ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_walltype ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_rooftype ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_woodtype ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_propertytype ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_department ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_occuapancy ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_usage ADD UNIQUE (tenantid, code);


ALTER TABLE egpt_property ALTER COLUMN tenantid TYPE character varying(128);
ALTER TABLE egpt_property ALTER COLUMN upicnumber TYPE character varying(128);
ALTER TABLE egpt_property ALTER COLUMN oldUpicNumber TYPE character varying(128);
ALTER TABLE egpt_property ALTER COLUMN vltUpicNumber TYPE character varying(128);
ALTER TABLE egpt_property ALTER COLUMN creationreason TYPE character varying(256);
ALTER TABLE egpt_property ALTER COLUMN gisRefNo TYPE character varying(32);
ALTER TABLE egpt_property ALTER COLUMN channel TYPE character varying(16);
ALTER TABLE egpt_propertydetails ALTER COLUMN source TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN regdDocNo TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN reason TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN status TYPE character varying(8);
ALTER TABLE egpt_propertydetails ALTER COLUMN exemptionReason TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN propertyType TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN category TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN usage TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN department TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN apartment TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN landOwner TYPE character varying(128);
ALTER TABLE egpt_propertydetails ALTER COLUMN floorType TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN woodType TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN roofType TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN wallType TYPE character varying(64);
ALTER TABLE egpt_propertydetails ALTER COLUMN applicationNo TYPE character varying(64);
ALTER TABLE egpt_floors ALTER COLUMN floorNo TYPE character varying(16);
ALTER TABLE egpt_unit ALTER COLUMN unitType TYPE character varying(16);
ALTER TABLE egpt_unit ALTER COLUMN bpaNo TYPE character varying(16);
ALTER TABLE egpt_unit ALTER COLUMN usage TYPE character varying(64);
ALTER TABLE egpt_unit ALTER COLUMN occupancy TYPE character varying(64); 
ALTER TABLE egpt_unit ALTER COLUMN occupierName TYPE character varying(128); 
ALTER TABLE egpt_unit ALTER COLUMN firmName TYPE character varying(128); 
ALTER TABLE egpt_unit ALTER COLUMN structure TYPE character varying(64); 
ALTER TABLE egpt_unit ALTER COLUMN age TYPE character varying(64);  
ALTER TABLE egpt_unit ALTER COLUMN exemptionReason TYPE character varying(64); 
ALTER TABLE egpt_unit ALTER COLUMN electricMeterNo TYPE character varying(64); 
ALTER TABLE egpt_unit ALTER COLUMN waterMeterNo TYPE character varying(64); 
ALTER TABLE egpt_vacantland ALTER COLUMN surveyNumber TYPE character varying(64);
ALTER TABLE egpt_vacantland ALTER COLUMN pattaNumber TYPE character varying(64);
ALTER TABLE egpt_vacantland ALTER COLUMN layoutApprovedAuth TYPE character varying(64);
ALTER TABLE egpt_vacantland ALTER COLUMN layoutPermissionNo TYPE character varying(64);
ALTER TABLE egpt_propertylocation ALTER COLUMN northBoundedBy TYPE character varying(256);
ALTER TABLE egpt_propertylocation ALTER COLUMN eastBoundedBy TYPE character varying(256);
ALTER TABLE egpt_propertylocation ALTER COLUMN westBoundedBy TYPE character varying(256);
ALTER TABLE egpt_propertylocation ALTER COLUMN southBoundedBy TYPE character varying(256);
ALTER TABLE egpt_property_owner ALTER COLUMN ownerType TYPE character varying(32);
ALTER TABLE egpt_mstr_department ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_department ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_floortype ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_floortype ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_occuapancy ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_occuapancy ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_propertytype ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_propertytype ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_rooftype ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_rooftype ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_structureclass ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_structureclass ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_usage ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_usage ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_walltype ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_walltype ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mstr_woodtype ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mstr_woodtype ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_depreciation ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_depreciation ALTER COLUMN code TYPE character varying(64);
ALTER TABLE egpt_mutation_master ALTER COLUMN tenantId TYPE character varying(128);
ALTER TABLE egpt_mutation_master ALTER COLUMN code TYPE character varying(64);

ALTER TABLE egpt_address DROP COLUMN IF EXISTS addressid;

ALTER TABLE egpt_documenttype DROP CONSTRAINT fk_egpt_documenttype_document;
ALTER TABLE egpt_documenttype DROP CONSTRAINT egpt_documenttype_pk;
ALTER TABLE egpt_documenttype ADD CONSTRAINT pk_egpt_documenttype PRIMARY KEY (id);
ALTER TABLE egpt_document DROP CONSTRAINT egpt_document_pk;
ALTER TABLE egpt_document ADD CONSTRAINT pk_egpt_document PRIMARY KEY (id);
ALTER TABLE  egpt_documenttype ADD CONSTRAINT fk_egpt_documenttype_document FOREIGN KEY(document) REFERENCES egpt_document(id);
ALTER TABLE egpt_vacantland DROP CONSTRAINT egpt_vacantland_pk;
ALTER TABLE egpt_vacantland ADD CONSTRAINT pk_egpt_vacantland PRIMARY KEY (id);
ALTER TABLE egpt_property_owner DROP CONSTRAINT egpt_property_owner_pk;
ALTER TABLE egpt_property_owner ADD CONSTRAINT pk_egpt_property_owner PRIMARY KEY (id);
ALTER TABLE egpt_propertylocation DROP CONSTRAINT egpt_propertylocation_pk;
ALTER TABLE egpt_propertylocation ADD CONSTRAINT pk_egpt_propertylocation PRIMARY KEY (id);
ALTER TABLE egpt_mstr_department DROP CONSTRAINT egpt_mstr_department_pkey;
ALTER TABLE egpt_mstr_department ADD CONSTRAINT pk_egpt_mstr_department PRIMARY KEY (id);
ALTER TABLE egpt_mstr_floortype DROP CONSTRAINT egpt_mstr_floor_pkey;
ALTER TABLE egpt_mstr_floortype ADD CONSTRAINT pk_egpt_mstr_floortype PRIMARY KEY (id);
ALTER TABLE egpt_mstr_occuapancy DROP CONSTRAINT egpt_mstr_occuapancy_pkey;
ALTER TABLE egpt_mstr_occuapancy ADD CONSTRAINT pk_egpt_mstr_occuapancy PRIMARY KEY (id);
ALTER TABLE egpt_mstr_propertytype DROP CONSTRAINT egpt_mstr_property_pkey;
ALTER TABLE egpt_mstr_propertytype ADD CONSTRAINT pk_egpt_mstr_propertytype PRIMARY KEY (id);
ALTER TABLE egpt_mstr_rooftype DROP CONSTRAINT egpt_mstr_roof_pkey;
ALTER TABLE egpt_mstr_rooftype ADD CONSTRAINT pk_egpt_mstr_rooftype PRIMARY KEY (id);
ALTER TABLE egpt_mstr_structureclass DROP CONSTRAINT egpt_mstr_structure_pkey;
ALTER TABLE egpt_mstr_structureclass ADD CONSTRAINT pk_egpt_mstr_structureclass PRIMARY KEY (id);
ALTER TABLE egpt_mstr_usage DROP CONSTRAINT egpt_mstr_usage_pkey;
ALTER TABLE egpt_mstr_usage ADD CONSTRAINT pk_egpt_mstr_usage PRIMARY KEY (id);
ALTER TABLE egpt_mstr_walltype DROP CONSTRAINT egpt_mstr_wall_pkey;
ALTER TABLE egpt_mstr_walltype ADD CONSTRAINT pk_egpt_mstr_walltype PRIMARY KEY (id);
ALTER TABLE egpt_mstr_woodtype DROP CONSTRAINT egpt_mstr_wood_pkey;
ALTER TABLE egpt_mstr_woodtype ADD CONSTRAINT pk_egpt_mstr_woodtype PRIMARY KEY (id);
ALTER TABLE egpt_mutation_master RENAME TO egpt_mstr_mutation;
ALTER TABLE egpt_mstr_mutation DROP CONSTRAINT pk_egpt_mutation;
ALTER TABLE egpt_mstr_mutation ADD CONSTRAINT pk_egpt_mstr_mutation PRIMARY KEY (id);
ALTER TABLE egpt_depreciation RENAME to egpt_mstr_depreciation;
ALTER TABLE egpt_mstr_depreciation DROP CONSTRAINT pk_egpt_depreciation;
ALTER TABLE egpt_mstr_depreciation ADD CONSTRAINT pk_egpt_mstr_depreciation PRIMARY KEY (id);

ALTER TABLE egpt_mstr_mutation ADD UNIQUE (tenantid, code);
ALTER TABLE egpt_mstr_depreciation ADD UNIQUE (tenantid, code);

ALTER TABLE egpt_unit ADD COLUMN isAuthorised boolean DEFAULT true;
ALTER TABLE egpt_documenttype ADD COLUMN tenantId character varying(128);

ALTER TABLE egpt_address ALTER COLUMN property TYPE bigint;
ALTER TABLE egpt_propertydetails ALTER COLUMN property TYPE bigint;
ALTER TABLE egpt_floors ALTER COLUMN propertydetails TYPE bigint;
ALTER TABLE egpt_unit ALTER COLUMN floor TYPE bigint;
ALTER TABLE egpt_document ALTER COLUMN propertydetails TYPE bigint;
ALTER TABLE egpt_vacantland ALTER COLUMN property TYPE bigint;
ALTER TABLE egpt_property_owner ALTER COLUMN property TYPE bigint;
ALTER TABLE egpt_property_owner ALTER COLUMN owner TYPE bigint;
ALTER TABLE egpt_propertylocation ALTER COLUMN property TYPE bigint;

ALTER TABLE egpt_document ADD COLUMN documenttype character varying(128) DEFAULT '' NOT NULL;
DROP TABLE IF EXISTS egpt_documenttype;
