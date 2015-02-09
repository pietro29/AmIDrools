--
-- File generated with SQLiteStudio v3.0.2 on dom feb 8 18:05:18 2015
--
-- Text encoding used: windows-1252
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: attributes
CREATE TABLE attributes (id_attribute INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_model INTEGER NOT NULL, des_attribute VARCHAR (45) NOT NULL, type_attribute VARCHAR (45) NOT NULL, FOREIGN KEY (id_model) REFERENCES models (id_model) ON DELETE RESTRICT)

-- Table: rulesiffactsdetails
CREATE TABLE rulesiffactsdetails (id_ruleiffactdetail INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_ruleiffact INTEGER NOT NULL, id_attribute INTEGER NOT NULL, operation VARCHAR (10) NOT NULL, value VARCHAR (100) NOT NULL, FOREIGN KEY (id_ruleiffact) REFERENCES models ON DELETE RESTRICT, FOREIGN KEY (id_attribute) REFERENCES attributes (id_attribute) ON DELETE RESTRICT)

-- Table: rulesif
CREATE TABLE rulesif (id_ruleif INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_rule INTEGER NOT NULL, FOREIGN KEY (id_rule) REFERENCES rules (id_rule) ON DELETE RESTRICT)

-- Table: models
CREATE TABLE models (id_model INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, des_model VARCHAR (45) NOT NULL, id_user INTEGER DEFAULT (1), if_model BOOLEAN DEFAULT (1), then_model BOOLEAN DEFAULT (1))

-- Table: rules
CREATE TABLE rules (id_rule INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name VARCHAR (45) NOT NULL, date_creation DATETIME DEFAULT CURRENT_TIMESTAMP, id_user INTEGER DEFAULT NULL, no_loop BOOLEAN DEFAULT ('1'), saliance INTEGER DEFAULT '100', public BOOLEAN DEFAULT '0')

-- Table: rulesthenfactsdetails
CREATE TABLE rulesthenfactsdetails (id_rulethenfactdetail INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_rulethenfact INTEGER NOT NULL, id_attribute INTEGER NOT NULL, operation VARCHAR (10) NOT NULL, value VARCHAR (100) NOT NULL DEFAULT '', FOREIGN KEY (id_rulethenfact) REFERENCES models ON DELETE RESTRICT, FOREIGN KEY (id_attribute) REFERENCES attributes (id_attribute) ON DELETE RESTRICT)

-- Table: attributesinstances
CREATE TABLE attributesinstances (id_attributeinstance INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_modelinstance INTEGER NOT NULL, id_attribute INTEGER NOT NULL, value_attribute VARCHAR (45) NOT NULL, FOREIGN KEY (id_modelinstance) REFERENCES models ON DELETE RESTRICT, FOREIGN KEY (id_attribute) REFERENCES models ON DELETE RESTRICT)

-- Table: rulesthen
CREATE TABLE rulesthen (id_rulethen INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_rule INTEGER NOT NULL, FOREIGN KEY (id_rule) REFERENCES rules (id_rule) ON DELETE RESTRICT)

-- Table: rulesthenfacts
CREATE TABLE rulesthenfacts (id_rulethenfact INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_model INTEGER NOT NULL, id_rulethen INTEGER NOT NULL, var_name VARCHAR (45) NOT NULL DEFAULT 'f', FOREIGN KEY (id_model) REFERENCES models (id_model) ON DELETE RESTRICT, FOREIGN KEY (id_rulethen) REFERENCES rulesthen (id_rulethen) ON DELETE RESTRICT)

-- Table: modelsinstances
CREATE TABLE modelsinstances (id_modelinstance INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, id_model INTEGER NOT NULL, des_modelinstance VARCHAR (45) NOT NULL, ip_model VARCHAR (45) DEFAULT NULL, FOREIGN KEY (id_model) REFERENCES models (id_model) ON DELETE RESTRICT)

-- Table: rulesiffacts
CREATE TABLE rulesiffacts (id_ruleiffact INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_ruleif INTEGER NOT NULL, id_model INTEGER NOT NULL, var_name VARCHAR (45) NOT NULL DEFAULT 'f', FOREIGN KEY (id_ruleif) REFERENCES rulesif (id_ruleif) ON DELETE RESTRICT, FOREIGN KEY (id_model) REFERENCES models (id_model) ON DELETE RESTRICT)

COMMIT TRANSACTION;
