CREATE CACHED TABLE epsg_alias ( 
alias_code                                         INTEGER NOT NULL, 
object_table_name                                  VARCHAR(80) NOT NULL, 
object_code                                        INTEGER NOT NULL, 
naming_system_code                                 INTEGER NOT NULL, 
alias                                              VARCHAR(80) NOT NULL, 
remarks                                            VARCHAR(254), 
CONSTRAINT pk_alias PRIMARY KEY ( alias_code ) );

CREATE CACHED TABLE epsg_area ( 
area_code                                          INTEGER NOT NULL, 
area_name                                          VARCHAR(80) NOT NULL, 
area_of_use                                        VARCHAR NOT NULL, 
area_south_bound_lat                               DOUBLE PRECISION, 
area_north_bound_lat                               DOUBLE PRECISION, 
area_west_bound_lon                                DOUBLE PRECISION, 
area_east_bound_lon                                DOUBLE PRECISION, 
area_polygon_file_ref                              VARCHAR(254), 
iso_a2_code                                        VARCHAR(2), 
iso_a3_code                                        VARCHAR(3), 
iso_n_code                                         INTEGER, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_area PRIMARY KEY ( area_code ) );

CREATE CACHED TABLE epsg_change ( 
change_id                                          DOUBLE PRECISION NOT NULL, 
report_date                                        DATE NOT NULL, 
date_closed                                        DATE, 
reporter                                           VARCHAR(254) NOT NULL, 
request                                            VARCHAR(254) NOT NULL, 
tables_affected                                    VARCHAR(254), 
codes_affected                                     VARCHAR(254), 
change_comment                                     VARCHAR(254), 
action                                             VARCHAR,
UNIQUE (change_id) );

CREATE CACHED TABLE epsg_coordinateaxis ( 
coord_axis_code                                    INTEGER, 
coord_sys_code                                     INTEGER NOT NULL, 
coord_axis_name_code                               INTEGER NOT NULL, 
coord_axis_orientation                             VARCHAR(24) NOT NULL, 
coord_axis_abbreviation                            VARCHAR(24) NOT NULL, 
uom_code                                           INTEGER NOT NULL, 
coord_axis_order                                   SMALLINT NOT NULL,
UNIQUE (coord_axis_code),
CONSTRAINT pk_coordinateaxis PRIMARY KEY ( coord_sys_code, coord_axis_name_code ) );

CREATE CACHED TABLE epsg_coordinateaxisname ( 
coord_axis_name_code                               INTEGER NOT NULL, 
coord_axis_name                                    VARCHAR(80) NOT NULL, 
description                                        VARCHAR(254), 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_coordinateaxisname PRIMARY KEY ( coord_axis_name_code ) );

CREATE CACHED TABLE epsg_coordinatereferencesystem ( 
coord_ref_sys_code                                 INTEGER NOT NULL, 
coord_ref_sys_name                                 VARCHAR(80) NOT NULL, 
area_of_use_code                                   INTEGER NOT NULL, 
coord_ref_sys_kind                                 VARCHAR(24) NOT NULL, 
coord_sys_code                                     INTEGER, 
datum_code                                         INTEGER, 
source_geogcrs_code                                INTEGER, 
projection_conv_code                               INTEGER, 
cmpd_horizcrs_code                                 INTEGER, 
cmpd_vertcrs_code                                  INTEGER, 
crs_scope                                          VARCHAR(254) NOT NULL, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
show_crs                                           SMALLINT NOT NULL, 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_coordinatereferencesystem PRIMARY KEY ( coord_ref_sys_code ) );

CREATE CACHED TABLE epsg_coordinatesystem ( 
coord_sys_code                                     INTEGER NOT NULL, 
coord_sys_name                                     VARCHAR(254) NOT NULL, 
coord_sys_type                                     VARCHAR(24) NOT NULL, 
dimension                                          SMALLINT NOT NULL, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(50) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_coordinatesystem PRIMARY KEY ( coord_sys_code ) );

CREATE CACHED TABLE epsg_coordoperation ( 
coord_op_code                                      INTEGER NOT NULL, 
coord_op_name                                      VARCHAR(80) NOT NULL, 
coord_op_type                                      VARCHAR(24) NOT NULL, 
source_crs_code                                    INTEGER, 
target_crs_code                                    INTEGER, 
coord_tfm_version                                  VARCHAR(24), 
coord_op_variant                                   SMALLINT, 
area_of_use_code                                   INTEGER NOT NULL, 
coord_op_scope                                     VARCHAR(254) NOT NULL, 
coord_op_accuracy                                  FLOAT, 
coord_op_method_code                               INTEGER, 
uom_code_source_coord_diff                         INTEGER, 
uom_code_target_coord_diff                         INTEGER, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
show_operation                                     SMALLINT NOT NULL, 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_coordinate_operation PRIMARY KEY ( coord_op_code ) );

CREATE CACHED TABLE epsg_coordoperationmethod ( 
coord_op_method_code                               INTEGER NOT NULL, 
coord_op_method_name                               VARCHAR(50) NOT NULL, 
reverse_op                                         SMALLINT NOT NULL, 
formula                                            VARCHAR, 
example                                            VARCHAR, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_coordinate_operationmethod PRIMARY KEY ( coord_op_method_code ) );

CREATE CACHED TABLE epsg_coordoperationparam ( 
parameter_code                                     INTEGER NOT NULL, 
parameter_name                                     VARCHAR(80) NOT NULL, 
description                                        VARCHAR, 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_coordinate_operationparamet PRIMARY KEY ( parameter_code ) );

CREATE CACHED TABLE epsg_coordoperationparamusage ( 
coord_op_method_code                               INTEGER NOT NULL, 
parameter_code                                     INTEGER NOT NULL, 
sort_order                                         SMALLINT NOT NULL, 
param_sign_reversal                                VARCHAR(3), 
CONSTRAINT pk_coordinate_operationparame2 PRIMARY KEY ( parameter_code, coord_op_method_code ) );

CREATE CACHED TABLE epsg_coordoperationparamvalue ( 
coord_op_code                                      INTEGER NOT NULL, 
coord_op_method_code                               INTEGER NOT NULL, 
parameter_code                                     INTEGER NOT NULL, 
parameter_value                                    DOUBLE PRECISION, 
param_value_file_ref                               VARCHAR(254), 
uom_code                                           INTEGER, 
CONSTRAINT pk_coordinate_operationparame3 PRIMARY KEY ( coord_op_code, parameter_code, coord_op_method_code ) );

CREATE CACHED TABLE epsg_coordoperationpath ( 
concat_operation_code                              INTEGER NOT NULL, 
single_operation_code                              INTEGER NOT NULL, 
op_path_step                                       SMALLINT NOT NULL, 
CONSTRAINT pk_coordinate_operationpath PRIMARY KEY ( concat_operation_code, single_operation_code ) );

CREATE CACHED TABLE epsg_datum ( 
datum_code                                         INTEGER NOT NULL, 
datum_name                                         VARCHAR(80) NOT NULL, 
datum_type                                         VARCHAR(24) NOT NULL, 
origin_description                                 VARCHAR(254), 
realization_epoch                                  VARCHAR(4), 
ellipsoid_code                                     INTEGER, 
prime_meridian_code                                INTEGER, 
area_of_use_code                                   INTEGER NOT NULL, 
datum_scope                                        VARCHAR(254) NOT NULL, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_datum PRIMARY KEY ( datum_code ) );

CREATE CACHED TABLE epsg_deprecation ( 
deprecation_id                                     INTEGER, 
deprecation_date                                   DATE, 
change_id                                          DOUBLE PRECISION NOT NULL, 
object_table_name                                  VARCHAR(80), 
object_code                                        INTEGER, 
replaced_by                                        INTEGER, 
deprecation_reason                                 VARCHAR(254), 
CONSTRAINT pk_deprecation PRIMARY KEY ( deprecation_id ) );

CREATE CACHED TABLE epsg_ellipsoid ( 
ellipsoid_code                                     INTEGER NOT NULL, 
ellipsoid_name                                     VARCHAR(80) NOT NULL, 
semi_major_axis                                    DOUBLE PRECISION NOT NULL, 
uom_code                                           INTEGER NOT NULL, 
inv_flattening                                     DOUBLE PRECISION, 
semi_minor_axis                                    DOUBLE PRECISION, 
ellipsoid_shape                                    SMALLINT NOT NULL, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_ellipsoid PRIMARY KEY ( ellipsoid_code ) );

CREATE CACHED TABLE epsg_namingsystem ( 
naming_system_code                                 INTEGER NOT NULL, 
naming_system_name                                 VARCHAR(80) NOT NULL, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_namingsystem PRIMARY KEY ( naming_system_code ) );

CREATE CACHED TABLE epsg_primemeridian ( 
prime_meridian_code                                INTEGER NOT NULL, 
prime_meridian_name                                VARCHAR(80) NOT NULL, 
greenwich_longitude                                DOUBLE PRECISION NOT NULL, 
uom_code                                           INTEGER NOT NULL, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_primemeridian PRIMARY KEY ( prime_meridian_code ) );

CREATE CACHED TABLE epsg_supersession ( 
supersession_id                                    INTEGER, 
object_table_name                                  VARCHAR(80) NOT NULL, 
object_code                                        INTEGER NOT NULL, 
superseded_by                                      INTEGER, 
supersession_type                                  VARCHAR(50), 
supersession_year                                  SMALLINT, 
remarks                                            VARCHAR(254), 
CONSTRAINT pk_supersession PRIMARY KEY ( supersession_id ) );

CREATE CACHED TABLE epsg_unitofmeasure ( 
uom_code                                           INTEGER NOT NULL, 
unit_of_meas_name                                  VARCHAR(80) NOT NULL, 
unit_of_meas_type                                  VARCHAR(50), 
target_uom_code                                    INTEGER NOT NULL, 
factor_b                                           DOUBLE PRECISION, 
factor_c                                           DOUBLE PRECISION, 
remarks                                            VARCHAR(254), 
information_source                                 VARCHAR(254), 
data_source                                        VARCHAR(40) NOT NULL, 
revision_date                                      DATE NOT NULL, 
change_id                                          VARCHAR(255), 
deprecated                                         SMALLINT NOT NULL, 
CONSTRAINT pk_unitofmeasure PRIMARY KEY ( uom_code ) );

CREATE CACHED TABLE epsg_versionhistory ( 
version_history_code                               INTEGER NOT NULL, 
version_date                                       DATE, 
version_number                                     VARCHAR(10) NOT NULL, 
version_remarks                                    VARCHAR(254) NOT NULL, 
superceded_by                                      VARCHAR(10), 
supercedes                                         VARCHAR(10),
UNIQUE(version_number), 
CONSTRAINT pk_versionhistory PRIMARY KEY ( version_history_code ) );

INSERT INTO epsg_alias VALUES ( 4, 
'Datum', 
6258, 
7300, 
'ETRF89', 
'' ); 

INSERT INTO epsg_alias VALUES ( 5, 
'Ellipsoid', 
7013, 
7300, 
'Modified Clarke 1880 (South Africa)', 
'The Clarke 1880 (Arc) figure is one of several modifications to the original definition.  The name Clarke Modified is usually taken to be the RGS modification.  But in southern Africa it is usually taken to be the Arc or Cape modification.' ); 

INSERT INTO epsg_alias VALUES ( 6, 
'Coordinate_Operation', 
8570, 
7300, 
'ED50 to EUREF89 (2)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 7, 
'Unit of Measure', 
9001, 
7300, 
'meter', 
'' ); 

INSERT INTO epsg_alias VALUES ( 8, 
'Unit of Measure', 
9036, 
7300, 
'kilometer', 
'Spelling used in US' ); 

INSERT INTO epsg_alias VALUES ( 9, 
'Coordinate Reference System', 
21100, 
7300, 
'Genuk / NEIEZ', 
'' ); 

INSERT INTO epsg_alias VALUES ( 10, 
'Coordinate_Operation', 
1036, 
7301, 
'OSTN97', 
'' ); 

INSERT INTO epsg_alias VALUES ( 11, 
'Coordinate_Operation', 
1123, 
7301, 
'Genuk to WGS 84 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 12, 
'Coordinate_Operation', 
1149, 
7301, 
'ETRF89 to WGS 84 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 13, 
'Coordinate_Operation', 
1273, 
7301, 
'HD72 to ETRF89 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 14, 
'Coordinate_Operation', 
1309, 
7301, 
'DHDN to ETRF89 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 15, 
'Coordinate_Operation', 
1310, 
7301, 
'Pulkovo 1942 to ETRF89 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 16, 
'Coordinate_Operation', 
1311, 
7301, 
'ED50 to WGS 84 (Common Offshore)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 17, 
'Coordinate_Operation', 
1331, 
7301, 
'EST92 to ETRF89 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 19, 
'Coordinate_Operation', 
1513, 
7301, 
'Final Datum 1958 to WGS 84 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 20, 
'Coordinate_Operation', 
1571, 
7301, 
'Amersfoort to ETRF89 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 21, 
'Coordinate_Operation', 
1584, 
7301, 
'Levant to WGS 72BE (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 22, 
'Coordinate_Operation', 
1585, 
7301, 
'Levant to WGS 84 (2)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 23, 
'Coordinate_Operation', 
1586, 
7301, 
'Levant to WGS 84 (3)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 24, 
'Coordinate_Operation', 
1587, 
7301, 
'Levant to WGS 84 (4)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 26, 
'Coordinate_Operation', 
1589, 
7301, 
'ED50 to ETRF89 (3)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 28, 
'Coordinate_Operation', 
1611, 
7301, 
'IRENET95 to ETRF89 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 29, 
'Coordinate Reference System', 
2140, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 30, 
'Coordinate Reference System', 
2141, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 31, 
'Coordinate Reference System', 
2142, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 32, 
'Coordinate Reference System', 
2143, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 33, 
'Coordinate Reference System', 
2144, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 34, 
'Coordinate Reference System', 
2145, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 8', 
'' ); 

INSERT INTO epsg_alias VALUES ( 35, 
'Coordinate Reference System', 
2146, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 9', 
'' ); 

INSERT INTO epsg_alias VALUES ( 36, 
'Coordinate Reference System', 
2147, 
7301, 
'NAD83(CSRS98) / SCoPQ zone 10', 
'' ); 

INSERT INTO epsg_alias VALUES ( 37, 
'Coordinate Reference System', 
2159, 
7301, 
'Sierra Leone 1924 / Peninsular Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 38, 
'Coordinate Reference System', 
2291, 
7301, 
'NAD83 / PEI Stereo', 
'' ); 

INSERT INTO epsg_alias VALUES ( 39, 
'Coordinate Reference System', 
3200, 
7301, 
'Final Datum 1958 / Iraq zone', 
'' ); 

INSERT INTO epsg_alias VALUES ( 40, 
'Coordinate Reference System', 
4132, 
7301, 
'Final Datum 1958 (Iran)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 41, 
'Coordinate Reference System', 
4140, 
7301, 
'NAD83(CSRS)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 42, 
'Coordinate Reference System', 
4172, 
7301, 
'National Geodetic System [Argentina]', 
'see http://www.igm.gov.ar/posgar.html' ); 

INSERT INTO epsg_alias VALUES ( 43, 
'Coordinate Reference System', 
4211, 
7301, 
'Genuk', 
'' ); 

INSERT INTO epsg_alias VALUES ( 44, 
'Coordinate Reference System', 
4218, 
7301, 
'Bogota', 
'' ); 

INSERT INTO epsg_alias VALUES ( 45, 
'Coordinate Reference System', 
4227, 
7301, 
'Levant', 
'' ); 

INSERT INTO epsg_alias VALUES ( 46, 
'Coordinate Reference System', 
4258, 
7301, 
'ETRF89', 
'' ); 

INSERT INTO epsg_alias VALUES ( 47, 
'Coordinate Reference System', 
4272, 
7301, 
'GD49', 
'' ); 

INSERT INTO epsg_alias VALUES ( 48, 
'Coordinate Reference System', 
4813, 
7301, 
'Genuk (Jakarta)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 49, 
'Datum', 
5104, 
7301, 
'Huang Hai 1956', 
'' ); 

INSERT INTO epsg_alias VALUES ( 50, 
'Datum', 
6120, 
7301, 
'Old Greek', 
'Adjective "Old" applied since introduction of GGRS87 (code 6121)' ); 

INSERT INTO epsg_alias VALUES ( 51, 
'Datum', 
6125, 
7301, 
'Samboja P2 exc T9', 
'' ); 

INSERT INTO epsg_alias VALUES ( 52, 
'Datum', 
6160, 
7301, 
'Quini-Huao', 
'' ); 

INSERT INTO epsg_alias VALUES ( 53, 
'Datum', 
6174, 
7301, 
'Sierra Leone Peninsular 1924', 
'' ); 

INSERT INTO epsg_alias VALUES ( 54, 
'Datum', 
6211, 
7301, 
'Genuk', 
'' ); 

INSERT INTO epsg_alias VALUES ( 55, 
'Datum', 
6218, 
7301, 
'Bogota', 
'' ); 

INSERT INTO epsg_alias VALUES ( 56, 
'Datum', 
6222, 
7301, 
'South Africa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 57, 
'Datum', 
6227, 
7301, 
'Levant', 
'' ); 

INSERT INTO epsg_alias VALUES ( 58, 
'Datum', 
6258, 
7301, 
'European Terrestrial Reference Frame 1989', 
'' ); 

INSERT INTO epsg_alias VALUES ( 59, 
'Datum', 
6269, 
7301, 
'NAD83 (1986)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 60, 
'Datum', 
6272, 
7301, 
'GD49', 
'' ); 

INSERT INTO epsg_alias VALUES ( 61, 
'Datum', 
6308, 
7301, 
'Rikets koordinatsystem 1938', 
'' ); 

INSERT INTO epsg_alias VALUES ( 62, 
'Ellipsoid', 
7012, 
7301, 
'Clarke Modified 1880', 
'The Clarke 1880 (RGS) figure is one of several modifications to the original definition.  The name Clarke Modified is usually taken to be the RGS modification.' ); 

INSERT INTO epsg_alias VALUES ( 63, 
'Ellipsoid', 
7013, 
7301, 
'Clarke 1880 (Cape)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 64, 
'Ellipsoid', 
7019, 
7301, 
'International 1979', 
'Adopted by IUGG 1979 Canberra as the Geodetic Reference Spheroid of 1980 (GRS 1980).' ); 

INSERT INTO epsg_alias VALUES ( 65, 
'Ellipsoid', 
7022, 
7301, 
'Hayford 1909', 
'Described as a=6378388 m. and b=6356909 m. from which 1/f derived to be 296.95926...   The figure was adopted as the International ellipsoid in 1924 but with 1/f taken as 297 exactly from which b is derved as 6356911.946 m.' ); 

INSERT INTO epsg_alias VALUES ( 66, 
'Ellipsoid', 
7029, 
7301, 
'McCaw 1924', 
'' ); 

INSERT INTO epsg_alias VALUES ( 67, 
'Ellipsoid', 
7030, 
7301, 
'WGS84', 
'' ); 

INSERT INTO epsg_alias VALUES ( 68, 
'Ellipsoid', 
7036, 
7301, 
'International 1967', 
'More usually known as GRS 1967 to avoid confusion with the International 1924 figure.' ); 

INSERT INTO epsg_alias VALUES ( 69, 
'Ellipsoid', 
7043, 
7301, 
'NWL 10D', 
'Used by Transit Broadcast Ephemeris before 1989. Also referred to as WGS72 spheroid.' ); 

INSERT INTO epsg_alias VALUES ( 70, 
'Coordinate_Operation', 
8568, 
7301, 
'Levant to WGS 84 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 71, 
'Coordinate_Operation', 
8570, 
7301, 
'ED50 to ETRF89 (2)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 72, 
'Coordinate_Operation Parameter', 
8602, 
7301, 
'Longitude rotation', 
'' ); 

INSERT INTO epsg_alias VALUES ( 73, 
'Prime Meridian', 
8913, 
7301, 
'Kristiania', 
'' ); 

INSERT INTO epsg_alias VALUES ( 74, 
'Unit of Measure', 
9001, 
7301, 
'International metre', 
'' ); 

INSERT INTO epsg_alias VALUES ( 75, 
'Unit of Measure', 
9002, 
7301, 
'international foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 76, 
'Unit of Measure', 
9003, 
7301, 
'American foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 77, 
'Unit of Measure', 
9005, 
7301, 
'South African geodetic foot', 
'Not to be confused with the Cape foot.' ); 

INSERT INTO epsg_alias VALUES ( 78, 
'Unit of Measure', 
9030, 
7301, 
'International nautical mile', 
'' ); 

INSERT INTO epsg_alias VALUES ( 79, 
'Unit of Measure', 
9039, 
7301, 
'link (Clarke''s ratio)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 80, 
'Unit of Measure', 
9040, 
7301, 
'yard', 
'' ); 

INSERT INTO epsg_alias VALUES ( 81, 
'Unit of Measure', 
9041, 
7301, 
'foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 82, 
'Unit of Measure', 
9042, 
7301, 
'chain', 
'' ); 

INSERT INTO epsg_alias VALUES ( 83, 
'Unit of Measure', 
9043, 
7301, 
'link', 
'' ); 

INSERT INTO epsg_alias VALUES ( 84, 
'Unit of Measure', 
9050, 
7301, 
'yard', 
'' ); 

INSERT INTO epsg_alias VALUES ( 85, 
'Unit of Measure', 
9051, 
7301, 
'foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 86, 
'Unit of Measure', 
9052, 
7301, 
'chain', 
'' ); 

INSERT INTO epsg_alias VALUES ( 87, 
'Unit of Measure', 
9053, 
7301, 
'link', 
'' ); 

INSERT INTO epsg_alias VALUES ( 88, 
'Unit of Measure', 
9060, 
7301, 
'yard', 
'' ); 

INSERT INTO epsg_alias VALUES ( 89, 
'Unit of Measure', 
9061, 
7301, 
'foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 90, 
'Unit of Measure', 
9062, 
7301, 
'chain', 
'' ); 

INSERT INTO epsg_alias VALUES ( 91, 
'Unit of Measure', 
9063, 
7301, 
'link', 
'' ); 

INSERT INTO epsg_alias VALUES ( 92, 
'Unit of Measure', 
9070, 
7301, 
'foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 93, 
'Unit of Measure', 
9080, 
7301, 
'Indian geodetic foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 94, 
'Unit of Measure', 
9081, 
7301, 
'Indian geodetic foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 95, 
'Unit of Measure', 
9084, 
7301, 
'yard', 
'= 3 Indian feet.' ); 

INSERT INTO epsg_alias VALUES ( 96, 
'Unit of Measure', 
9085, 
7301, 
'yard', 
'= 3 Indian feet.' ); 

INSERT INTO epsg_alias VALUES ( 97, 
'Unit of Measure', 
9094, 
7301, 
'foot', 
'' ); 

INSERT INTO epsg_alias VALUES ( 98, 
'Unit of Measure', 
9114, 
7301, 
'mil', 
'Alias also applies to other variations of a mil, especially mil_6300 and mil_6000.' ); 

INSERT INTO epsg_alias VALUES ( 100, 
'Coordinate_Operation', 
17001, 
7301, 
'Ghana TM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 101, 
'Coordinate_Operation', 
17901, 
7301, 
'Mount Eden Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 102, 
'Coordinate_Operation', 
17902, 
7301, 
'Bay of Plenty Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 103, 
'Coordinate_Operation', 
17903, 
7301, 
'Poverty Bay Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 104, 
'Coordinate_Operation', 
17904, 
7301, 
'Hawkes Bay Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 105, 
'Coordinate_Operation', 
17905, 
7301, 
'Taranaki Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 106, 
'Coordinate_Operation', 
17906, 
7301, 
'Tuhirangi Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 107, 
'Coordinate_Operation', 
17907, 
7301, 
'Wanganui Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 108, 
'Coordinate_Operation', 
17908, 
7301, 
'Wairarapa Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 109, 
'Coordinate_Operation', 
17909, 
7301, 
'Wellington Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 110, 
'Coordinate_Operation', 
17910, 
7301, 
'Collingwood Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 111, 
'Coordinate_Operation', 
17911, 
7301, 
'Nelson Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 112, 
'Coordinate_Operation', 
17912, 
7301, 
'Karamea Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 113, 
'Coordinate_Operation', 
17913, 
7301, 
'Buller Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 114, 
'Coordinate_Operation', 
17914, 
7301, 
'Grey Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 115, 
'Coordinate_Operation', 
17915, 
7301, 
'Amuri Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 116, 
'Coordinate_Operation', 
17916, 
7301, 
'Marlborough Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 117, 
'Coordinate_Operation', 
17917, 
7301, 
'Hokitika Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 118, 
'Coordinate_Operation', 
17918, 
7301, 
'Okarito Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 119, 
'Coordinate_Operation', 
17919, 
7301, 
'Jacksons Bay Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 120, 
'Coordinate_Operation', 
17920, 
7301, 
'Mount Pleasant Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 121, 
'Coordinate_Operation', 
17921, 
7301, 
'Gawler Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 122, 
'Coordinate_Operation', 
17922, 
7301, 
'Timaru Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 123, 
'Coordinate_Operation', 
17923, 
7301, 
'Lindis Peak Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 124, 
'Coordinate_Operation', 
17924, 
7301, 
'Mount Nicholas Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 125, 
'Coordinate_Operation', 
17925, 
7301, 
'Mount York Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 126, 
'Coordinate_Operation', 
17926, 
7301, 
'Observation Point Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 127, 
'Coordinate_Operation', 
17927, 
7301, 
'North Taieri Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 128, 
'Coordinate_Operation', 
17928, 
7301, 
'Bluff Circuit 1949', 
'' ); 

INSERT INTO epsg_alias VALUES ( 129, 
'Coordinate_Operation', 
19906, 
7301, 
'IOEPC Lambert', 
'Sometimes seen defined with 2 standard parallels.' ); 

INSERT INTO epsg_alias VALUES ( 130, 
'Coordinate_Operation', 
19959, 
7301, 
'Gold Coast Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 131, 
'Coordinate_Operation', 
19963, 
7301, 
'Sierra Leone Peninsula Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 132, 
'Coordinate Reference System', 
21100, 
7301, 
'Genuk (Jakarta) / NEIEZ', 
'' ); 

INSERT INTO epsg_alias VALUES ( 133, 
'Coordinate Reference System', 
21148, 
7301, 
'Genuk / UTM zone 48S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 134, 
'Coordinate Reference System', 
21150, 
7301, 
'Genuk / UTM zone 50S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 135, 
'Coordinate Reference System', 
22700, 
7301, 
'Levant / Levant Zone', 
'' ); 

INSERT INTO epsg_alias VALUES ( 136, 
'Coordinate Reference System', 
22770, 
7301, 
'Levant / Syria Lambert', 
'' ); 

INSERT INTO epsg_alias VALUES ( 137, 
'Coordinate Reference System', 
22780, 
7317, 
'Levant / Levant Stereo', 
'' ); 

INSERT INTO epsg_alias VALUES ( 138, 
'Coordinate Reference System', 
25828, 
7301, 
'ETRF89 / UTM zone 28N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 139, 
'Coordinate Reference System', 
25829, 
7301, 
'ETRF89 / UTM zone 29N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 140, 
'Coordinate Reference System', 
25830, 
7301, 
'ETRF89 / UTM zone 30N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 141, 
'Coordinate Reference System', 
25831, 
7301, 
'ETRF89 / UTM zone 31N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 142, 
'Coordinate Reference System', 
25832, 
7301, 
'ETRF89 / UTM zone 32N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 143, 
'Coordinate Reference System', 
25833, 
7301, 
'ETRF89 / UTM zone 33N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 144, 
'Coordinate Reference System', 
25834, 
7301, 
'ETRF89 / UTM zone 34N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 145, 
'Coordinate Reference System', 
25835, 
7301, 
'ETRF89 / UTM zone 35N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 146, 
'Coordinate Reference System', 
25836, 
7301, 
'ETRF89 / UTM zone 36N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 147, 
'Coordinate Reference System', 
25837, 
7301, 
'ETRF89 / UTM zone 37N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 148, 
'Coordinate Reference System', 
25838, 
7301, 
'ETRF89 / UTM zone 38N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 149, 
'Coordinate Reference System', 
25884, 
7301, 
'ETRF89 / TM Baltic93', 
'' ); 

INSERT INTO epsg_alias VALUES ( 150, 
'Coordinate Reference System', 
27258, 
7301, 
'GD49 / UTM zone 58', 
'' ); 

INSERT INTO epsg_alias VALUES ( 151, 
'Coordinate Reference System', 
27259, 
7301, 
'GD49 / UTM zone 59', 
'' ); 

INSERT INTO epsg_alias VALUES ( 152, 
'Coordinate Reference System', 
27260, 
7301, 
'GD49 / UTM zone 60', 
'' ); 

INSERT INTO epsg_alias VALUES ( 153, 
'Coordinate Reference System', 
27291, 
7317, 
'GD49 / North Island Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 154, 
'Coordinate Reference System', 
27292, 
7317, 
'GD49 / South Island Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 156, 
'Coordinate Reference System', 
30791, 
7301, 
'Nord Sahara 1959 / Lambert Nord Voirol Unifie 1960', 
'Voirol Unifie 1960 is NOT a geodetic datum nor GeogCRS.  It is two Lambert projected coordinate reference systems based on Nord Sahara 1959 Datum. See also code 30792.' ); 

INSERT INTO epsg_alias VALUES ( 157, 
'Coordinate Reference System', 
30792, 
7301, 
'Nord Sahara 1959 / Lambert Sud Voirol Unifie 1960', 
'Voirol Unifie 1960 is NOT a geodetic datum nor GeogCRS.  It is two Lambert projected coordinate reference systems based on Nord Sahara 1959 Datum. See also code 30791.' ); 

INSERT INTO epsg_alias VALUES ( 158, 
'Coordinate Reference System', 
31170, 
7317, 
'Zanderij / Surinam Old', 
'Old country name spelling.' ); 

INSERT INTO epsg_alias VALUES ( 159, 
'Coordinate Reference System', 
31171, 
7301, 
'Zanderij / Surinam TM', 
'Old spelling for country name.' ); 

INSERT INTO epsg_alias VALUES ( 160, 
'Coordinate Reference System', 
31300, 
7317, 
'Belge Lambert 72', 
'' ); 

INSERT INTO epsg_alias VALUES ( 161, 
'Coordinate_Operation', 
1026, 
7302, 
'Madrid to ED50 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 162, 
'Coordinate_Operation', 
1027, 
7302, 
'Madrid to ED50 (2)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 163, 
'Coordinate_Operation', 
1028, 
7302, 
'Madrid to ED50 (3)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 164, 
'Coordinate_Operation', 
1029, 
7302, 
'RD New to ED50/UTM31 (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 165, 
'Coordinate_Operation', 
1030, 
7302, 
'ED50/UTM31 to RD New (1)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 166, 
'Coordinate_Operation', 
1031, 
7302, 
'RD New to ED50/UTM31 (2)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 167, 
'Coordinate_Operation', 
1032, 
7302, 
'ED50/UTM31 to RD New (2)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 170, 
'Coordinate Reference System', 
2000, 
7302, 
'Anguilla 1957 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 171, 
'Coordinate Reference System', 
2001, 
7302, 
'Antigua 1943 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 172, 
'Coordinate Reference System', 
2002, 
7302, 
'Dominica 1945 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 173, 
'Coordinate Reference System', 
2003, 
7302, 
'Grenada 1953 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 174, 
'Coordinate Reference System', 
2004, 
7302, 
'Montserrat 58 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 175, 
'Coordinate Reference System', 
2005, 
7302, 
'St Kitts 1955 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 176, 
'Coordinate Reference System', 
2006, 
7302, 
'St Lucia 1955 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 177, 
'Coordinate Reference System', 
2007, 
7302, 
'St Vincent 45 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 178, 
'Coordinate Reference System', 
2008, 
7302, 
'CGQ77 / SCoPQ zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 179, 
'Coordinate Reference System', 
2009, 
7302, 
'CGQ77 / SCoPQ zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 180, 
'Coordinate Reference System', 
2010, 
7302, 
'CGQ77 / SCoPQ zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 181, 
'Coordinate Reference System', 
2011, 
7302, 
'CGQ77 / SCoPQ zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 182, 
'Coordinate Reference System', 
2012, 
7302, 
'CGQ77 / SCoPQ zone 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 183, 
'Coordinate Reference System', 
2013, 
7302, 
'CGQ77 / SCoPQ zone 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 184, 
'Coordinate Reference System', 
2014, 
7302, 
'CGQ77 / SCoPQ zone 8', 
'' ); 

INSERT INTO epsg_alias VALUES ( 185, 
'Coordinate Reference System', 
2015, 
7302, 
'CGQ77 / SCoPQ zone 9', 
'' ); 

INSERT INTO epsg_alias VALUES ( 186, 
'Coordinate Reference System', 
2016, 
7302, 
'CGQ77 / SCoPQ zone 10', 
'' ); 

INSERT INTO epsg_alias VALUES ( 187, 
'Coordinate Reference System', 
2036, 
7302, 
'NAD83(CSRS) / NB Stereo', 
'' ); 

INSERT INTO epsg_alias VALUES ( 188, 
'Coordinate Reference System', 
2037, 
7302, 
'NAD83(CSRS) / UTM 19N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 189, 
'Coordinate Reference System', 
2038, 
7302, 
'NAD83(CSRS) / UTM 20N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 190, 
'Coordinate Reference System', 
2039, 
7302, 
'Israeli TM Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 191, 
'Coordinate Reference System', 
2040, 
7302, 
'Locodjo 65 / UTM 30N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 192, 
'Coordinate Reference System', 
2041, 
7302, 
'Abidjan 87 / UTM 30N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 193, 
'Coordinate Reference System', 
2042, 
7302, 
'Locodjo 65 / UTM 29N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 194, 
'Coordinate Reference System', 
2043, 
7302, 
'Abidjan 87 / UTM 29N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 195, 
'Coordinate Reference System', 
2044, 
7302, 
'Hanoi 72 / Gauss zone 18', 
'' ); 

INSERT INTO epsg_alias VALUES ( 196, 
'Coordinate Reference System', 
2045, 
7302, 
'Hanoi 72 / Gauss zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 197, 
'Coordinate Reference System', 
2046, 
7301, 
'New S African CS zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 198, 
'Coordinate Reference System', 
2047, 
7301, 
'New S African CS zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 199, 
'Coordinate Reference System', 
2048, 
7301, 
'New S African CS zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 200, 
'Coordinate Reference System', 
2049, 
7301, 
'New S African CS zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 201, 
'Coordinate Reference System', 
2050, 
7301, 
'New S African CS zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 202, 
'Coordinate Reference System', 
2051, 
7301, 
'New S African CS zone 25', 
'' ); 

INSERT INTO epsg_alias VALUES ( 203, 
'Coordinate Reference System', 
2052, 
7301, 
'New S African CS zone 27', 
'' ); 

INSERT INTO epsg_alias VALUES ( 204, 
'Coordinate Reference System', 
2053, 
7301, 
'New S African CS zone 29', 
'' ); 

INSERT INTO epsg_alias VALUES ( 205, 
'Coordinate Reference System', 
2054, 
7301, 
'New S African CS zone 31', 
'' ); 

INSERT INTO epsg_alias VALUES ( 206, 
'Coordinate Reference System', 
2055, 
7301, 
'New S African CS zone 33', 
'' ); 

INSERT INTO epsg_alias VALUES ( 207, 
'Coordinate Reference System', 
2056, 
7302, 
'LV95', 
'' ); 

INSERT INTO epsg_alias VALUES ( 208, 
'Coordinate Reference System', 
2066, 
7302, 
'Mount Dillon / Tobago', 
'' ); 

INSERT INTO epsg_alias VALUES ( 209, 
'Coordinate Reference System', 
2067, 
7302, 
'Naparima 1955 / UTM 20N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 210, 
'Coordinate Reference System', 
2081, 
7301, 
'Chos Malal 1914 / Argentina zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 212, 
'Coordinate Reference System', 
2083, 
7301, 
'Hito XVIII 1963 / Argentina zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 213, 
'Coordinate Reference System', 
2084, 
7302, 
'Hito XVIII / UTM 19S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 214, 
'Coordinate Reference System', 
2089, 
7302, 
'Yemen NGN96 / UTM 38N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 215, 
'Coordinate Reference System', 
2090, 
7302, 
'Yemen NGN96 / UTM 39N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 216, 
'Coordinate Reference System', 
2091, 
7302, 
'S Yemen / Gauss zone 8', 
'' ); 

INSERT INTO epsg_alias VALUES ( 217, 
'Coordinate Reference System', 
2092, 
7302, 
'S Yemen / Gauss zone 9', 
'' ); 

INSERT INTO epsg_alias VALUES ( 218, 
'Coordinate Reference System', 
2096, 
7302, 
'Korean 1985 / East Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 219, 
'Coordinate Reference System', 
2097, 
7302, 
'Korean 1985 / Cen. Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 220, 
'Coordinate Reference System', 
2098, 
7302, 
'Korean 1985 / West Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 221, 
'Coordinate Reference System', 
2099, 
7301, 
'Qatar Plane CS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 222, 
'Coordinate Reference System', 
2136, 
7301, 
'Accra / Gold Coast Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 223, 
'Coordinate Reference System', 
2137, 
7301, 
'Accra / Ghana TM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 224, 
'Coordinate Reference System', 
2157, 
7302, 
'IRENET95 / ITM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 225, 
'Coordinate Reference System', 
2200, 
7302, 
'ATS77 / NB Stereographic', 
'' ); 

INSERT INTO epsg_alias VALUES ( 226, 
'Coordinate Reference System', 
2290, 
7302, 
'ATS77 / PEI Stereo', 
'' ); 

INSERT INTO epsg_alias VALUES ( 227, 
'Coordinate Reference System', 
2291, 
7302, 
'NAD83(CSRS) / PEI Stereo', 
'' ); 

INSERT INTO epsg_alias VALUES ( 228, 
'Coordinate Reference System', 
2294, 
7302, 
'ATS77 / MTM NS zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 229, 
'Coordinate Reference System', 
2295, 
7302, 
'ATS77 / MTM NS zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 230, 
'Coordinate Reference System', 
2393, 
7302, 
'KKJ / Finland zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 231, 
'Coordinate Reference System', 
2600, 
7302, 
'LKS94', 
'This alias is also used for geographical and geocentric CRSs.' ); 

INSERT INTO epsg_alias VALUES ( 232, 
'Coordinate Reference System', 
3561, 
7302, 
'Old Hawaiian / SP zone 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 233, 
'Coordinate Reference System', 
3562, 
7302, 
'Old Hawaiian / SP zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 234, 
'Coordinate Reference System', 
3563, 
7302, 
'Old Hawaiian / SP zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 235, 
'Coordinate Reference System', 
3564, 
7302, 
'Old Hawaiian / SP zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 236, 
'Coordinate Reference System', 
3565, 
7302, 
'Old Hawaiian / SP zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 237, 
'Coordinate Reference System', 
3991, 
7302, 
'Puerto Rico SPCS 27', 
'' ); 

INSERT INTO epsg_alias VALUES ( 238, 
'Coordinate Reference System', 
4134, 
7301, 
'PDO Survey Datum 1993', 
'' ); 

INSERT INTO epsg_alias VALUES ( 239, 
'Coordinate Reference System', 
4215, 
7302, 
'BD 50', 
'' ); 

INSERT INTO epsg_alias VALUES ( 240, 
'Coordinate Reference System', 
4268, 
7302, 
'NAD Michigan', 
'' ); 

INSERT INTO epsg_alias VALUES ( 241, 
'Coordinate Reference System', 
4313, 
7302, 
'BD 72', 
'' ); 

INSERT INTO epsg_alias VALUES ( 242, 
'Coordinate Reference System', 
4609, 
7302, 
'CGQ77', 
'' ); 

INSERT INTO epsg_alias VALUES ( 243, 
'Coordinate Reference System', 
4809, 
7302, 
'BD 50 (Brussels)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 244, 
'Datum', 
5100, 
7302, 
'msl', 
'' ); 

INSERT INTO epsg_alias VALUES ( 245, 
'Datum', 
5101, 
7302, 
'ODN', 
'' ); 

INSERT INTO epsg_alias VALUES ( 246, 
'Datum', 
5102, 
7302, 
'NGVD29', 
'' ); 

INSERT INTO epsg_alias VALUES ( 247, 
'Datum', 
5103, 
7302, 
'NAVD88', 
'' ); 

INSERT INTO epsg_alias VALUES ( 248, 
'Datum', 
5104, 
7302, 
'Yellow Sea', 
'' ); 

INSERT INTO epsg_alias VALUES ( 249, 
'Datum', 
5105, 
7302, 
'Baltic', 
'' ); 

INSERT INTO epsg_alias VALUES ( 250, 
'Datum', 
5106, 
7302, 
'Caspian', 
'' ); 

INSERT INTO epsg_alias VALUES ( 251, 
'Datum', 
5107, 
7302, 
'NGF', 
'' ); 

INSERT INTO epsg_alias VALUES ( 252, 
'Datum', 
5109, 
7302, 
'NAP', 
'' ); 

INSERT INTO epsg_alias VALUES ( 253, 
'Datum', 
5111, 
7302, 
'AHD', 
'' ); 

INSERT INTO epsg_alias VALUES ( 254, 
'Datum', 
5112, 
7302, 
'AHD (Tasmania)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 255, 
'Datum', 
5114, 
7301, 
'CVD28', 
'' ); 

INSERT INTO epsg_alias VALUES ( 256, 
'Datum', 
5115, 
7302, 
'Piraeus86', 
'' ); 

INSERT INTO epsg_alias VALUES ( 257, 
'Datum', 
5116, 
7302, 
'N60', 
'' ); 

INSERT INTO epsg_alias VALUES ( 258, 
'Datum', 
5117, 
7302, 
'RH70', 
'' ); 

INSERT INTO epsg_alias VALUES ( 259, 
'Datum', 
5118, 
7302, 
'NGF - Lallemand', 
'' ); 

INSERT INTO epsg_alias VALUES ( 260, 
'Datum', 
5119, 
7302, 
'NGF - IGN69', 
'' ); 

INSERT INTO epsg_alias VALUES ( 261, 
'Datum', 
5120, 
7302, 
'NGF - IGN78', 
'' ); 

INSERT INTO epsg_alias VALUES ( 262, 
'Datum', 
5122, 
7302, 
'JSLD', 
'' ); 

INSERT INTO epsg_alias VALUES ( 263, 
'Datum', 
5123, 
7302, 
'PHD93', 
'' ); 

INSERT INTO epsg_alias VALUES ( 264, 
'Datum', 
5127, 
7302, 
'LN02', 
'' ); 

INSERT INTO epsg_alias VALUES ( 265, 
'Datum', 
5128, 
7302, 
'LHN95', 
'' ); 

INSERT INTO epsg_alias VALUES ( 266, 
'Datum', 
5129, 
7302, 
'EVRF2000', 
'' ); 

INSERT INTO epsg_alias VALUES ( 267, 
'Coordinate Reference System', 
5701, 
7301, 
'Newlyn height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 268, 
'Coordinate Reference System', 
5702, 
7301, 
'National Geodetic Vertical Datum of 1929 height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 269, 
'Coordinate Reference System', 
5703, 
7301, 
'North American Vertical Datum of 1988 height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 270, 
'Coordinate Reference System', 
5709, 
7301, 
'Normaal Amsterdams Peil height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 271, 
'Coordinate Reference System', 
5711, 
7301, 
'Australian Height Datum height', 
'Australian Height Datum height' ); 

INSERT INTO epsg_alias VALUES ( 272, 
'Coordinate Reference System', 
5712, 
7301, 
'Australian Height Datum (Tasmania) height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 273, 
'Coordinate Reference System', 
5713, 
7301, 
'Canadian Geodetic Vertical Datum of 1928 height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 274, 
'Coordinate Reference System', 
5714, 
7301, 
'mean sea level height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 275, 
'Coordinate Reference System', 
5715, 
7301, 
'mean sea level depth', 
'' ); 

INSERT INTO epsg_alias VALUES ( 276, 
'Coordinate Reference System', 
5723, 
7301, 
'Japan Levelling Datum height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 277, 
'Coordinate Reference System', 
5724, 
7301, 
'PDO Height Datum 1993 height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 278, 
'Coordinate Reference System', 
5728, 
7301, 
'Landesnivellement 1902 height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 279, 
'Coordinate Reference System', 
5729, 
7301, 
'Landeshohennetz 1995 height', 
'' ); 

INSERT INTO epsg_alias VALUES ( 281, 
'Datum', 
6121, 
7302, 
'GGRS87', 
'' ); 

INSERT INTO epsg_alias VALUES ( 282, 
'Datum', 
6122, 
7302, 
'ATS77', 
'' ); 

INSERT INTO epsg_alias VALUES ( 283, 
'Datum', 
6123, 
7302, 
'KKJ', 
'' ); 

INSERT INTO epsg_alias VALUES ( 284, 
'Datum', 
6124, 
7302, 
'RT90', 
'' ); 

INSERT INTO epsg_alias VALUES ( 285, 
'Datum', 
6126, 
7302, 
'LKS94 (ETRS89)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 286, 
'Datum', 
6130, 
7302, 
'Moznet', 
'' ); 

INSERT INTO epsg_alias VALUES ( 287, 
'Datum', 
6132, 
7302, 
'FD58', 
'' ); 

INSERT INTO epsg_alias VALUES ( 288, 
'Datum', 
6133, 
7302, 
'EST92', 
'' ); 

INSERT INTO epsg_alias VALUES ( 289, 
'Datum', 
6134, 
7302, 
'PSD93', 
'' ); 

INSERT INTO epsg_alias VALUES ( 290, 
'Datum', 
6140, 
7302, 
'NAD83(CSRS)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 291, 
'Datum', 
6151, 
7302, 
'CHTRF95', 
'' ); 

INSERT INTO epsg_alias VALUES ( 292, 
'Datum', 
6152, 
7302, 
'NAD83(HARN)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 293, 
'Datum', 
6154, 
7302, 
'ED50(ED77)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 294, 
'Datum', 
6156, 
7302, 
'S-JTSK', 
'' ); 

INSERT INTO epsg_alias VALUES ( 295, 
'Datum', 
6159, 
7302, 
'ELD79', 
'' ); 

INSERT INTO epsg_alias VALUES ( 296, 
'Datum', 
6163, 
7302, 
'YNGN96', 
'' ); 

INSERT INTO epsg_alias VALUES ( 297, 
'Datum', 
6170, 
7302, 
'SIRGAS 1995', 
'' ); 

INSERT INTO epsg_alias VALUES ( 298, 
'Datum', 
6171, 
7302, 
'RGF93', 
'' ); 

INSERT INTO epsg_alias VALUES ( 299, 
'Datum', 
6172, 
7302, 
'POSGAR', 
'' ); 

INSERT INTO epsg_alias VALUES ( 300, 
'Datum', 
6202, 
7302, 
'AGD66', 
'' ); 

INSERT INTO epsg_alias VALUES ( 301, 
'Datum', 
6203, 
7302, 
'AGD84', 
'' ); 

INSERT INTO epsg_alias VALUES ( 302, 
'Datum', 
6204, 
7302, 
'Ain el Abd', 
'' ); 

INSERT INTO epsg_alias VALUES ( 303, 
'Datum', 
6215, 
7302, 
'Belge 1950', 
'' ); 

INSERT INTO epsg_alias VALUES ( 304, 
'Datum', 
6230, 
7302, 
'ED50', 
'' ); 

INSERT INTO epsg_alias VALUES ( 305, 
'Datum', 
6231, 
7302, 
'ED87', 
'' ); 

INSERT INTO epsg_alias VALUES ( 306, 
'Datum', 
6237, 
7302, 
'HD72', 
'' ); 

INSERT INTO epsg_alias VALUES ( 307, 
'Datum', 
6238, 
7302, 
'ID74', 
'' ); 

INSERT INTO epsg_alias VALUES ( 308, 
'Datum', 
6242, 
7302, 
'JAD69', 
'' ); 

INSERT INTO epsg_alias VALUES ( 309, 
'Datum', 
6246, 
7302, 
'KOC', 
'' ); 

INSERT INTO epsg_alias VALUES ( 310, 
'Datum', 
6248, 
7302, 
'PSAD56', 
'' ); 

INSERT INTO epsg_alias VALUES ( 311, 
'Datum', 
6258, 
7302, 
'ETRS89', 
'' ); 

INSERT INTO epsg_alias VALUES ( 312, 
'Datum', 
6267, 
7302, 
'NAD27', 
'' ); 

INSERT INTO epsg_alias VALUES ( 313, 
'Datum', 
6269, 
7302, 
'NAD83', 
'' ); 

INSERT INTO epsg_alias VALUES ( 314, 
'Datum', 
6272, 
7302, 
'NZGD49', 
'' ); 

INSERT INTO epsg_alias VALUES ( 315, 
'Datum', 
6275, 
7302, 
'NTF', 
'' ); 

INSERT INTO epsg_alias VALUES ( 316, 
'Datum', 
6278, 
7302, 
'OSGB70', 
'' ); 

INSERT INTO epsg_alias VALUES ( 317, 
'Datum', 
6279, 
7302, 
'OS(SN)80', 
'' ); 

INSERT INTO epsg_alias VALUES ( 318, 
'Datum', 
6280, 
7302, 
'Padang', 
'' ); 

INSERT INTO epsg_alias VALUES ( 319, 
'Datum', 
6283, 
7302, 
'GDA94', 
'' ); 

INSERT INTO epsg_alias VALUES ( 320, 
'Datum', 
6291, 
7302, 
'SAD69', 
'' ); 

INSERT INTO epsg_alias VALUES ( 321, 
'Datum', 
6297, 
7302, 
'Tananarive', 
'' ); 

INSERT INTO epsg_alias VALUES ( 322, 
'Datum', 
6303, 
7302, 
'TC(1948)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 324, 
'Datum', 
6308, 
7302, 
'RT38', 
'' ); 

INSERT INTO epsg_alias VALUES ( 325, 
'Datum', 
6312, 
7302, 
'MGI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 326, 
'Datum', 
6313, 
7302, 
'Belge 1972', 
'' ); 

INSERT INTO epsg_alias VALUES ( 327, 
'Datum', 
6314, 
7302, 
'DHDN', 
'' ); 

INSERT INTO epsg_alias VALUES ( 328, 
'Datum', 
6318, 
7302, 
'NGN', 
'' ); 

INSERT INTO epsg_alias VALUES ( 329, 
'Datum', 
6319, 
7302, 
'KUDAMS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 330, 
'Datum', 
6322, 
7302, 
'WGS 72', 
'' ); 

INSERT INTO epsg_alias VALUES ( 331, 
'Datum', 
6324, 
7302, 
'WGS 72BE', 
'' ); 

INSERT INTO epsg_alias VALUES ( 332, 
'Datum', 
6326, 
7302, 
'WGS 84', 
'' ); 

INSERT INTO epsg_alias VALUES ( 333, 
'Datum', 
6608, 
7302, 
'NAD27(76)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 334, 
'Datum', 
6609, 
7302, 
'CGQ77', 
'' ); 

INSERT INTO epsg_alias VALUES ( 335, 
'Datum', 
6901, 
7302, 
'ATF (Paris)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 336, 
'Datum', 
6902, 
7302, 
'NDG (Paris)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 337, 
'Ellipsoid', 
7003, 
7302, 
'ANS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 338, 
'Coordinate Reference System', 
7401, 
7302, 
'NTF / France II + Lalle', 
'' ); 

INSERT INTO epsg_alias VALUES ( 339, 
'Coordinate Reference System', 
7402, 
7302, 
'NTF / France II + IGN69', 
'' ); 

INSERT INTO epsg_alias VALUES ( 340, 
'Coordinate Reference System', 
7403, 
7302, 
'NTF / France III + IGN69', 
'' ); 

INSERT INTO epsg_alias VALUES ( 341, 
'Coordinate Reference System', 
7405, 
7302, 
'GB Nat Grid + ODN ht', 
'' ); 

INSERT INTO epsg_alias VALUES ( 342, 
'Coordinate Reference System', 
7407, 
7302, 
'NAD27 / TX_N + NGVD29 ht', 
'' ); 

INSERT INTO epsg_alias VALUES ( 343, 
'Coordinate_Operation Parameter', 
8663, 
7302, 
'k', 
'' ); 

INSERT INTO epsg_alias VALUES ( 344, 
'Unit of Measure', 
9001, 
7302, 
'm', 
'' ); 

INSERT INTO epsg_alias VALUES ( 345, 
'Unit of Measure', 
9002, 
7302, 
'ft', 
'' ); 

INSERT INTO epsg_alias VALUES ( 346, 
'Unit of Measure', 
9003, 
7302, 
'ftUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 347, 
'Unit of Measure', 
9005, 
7302, 
'ftCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 348, 
'Unit of Measure', 
9014, 
7302, 
'f', 
'' ); 

INSERT INTO epsg_alias VALUES ( 349, 
'Unit of Measure', 
9030, 
7302, 
'NM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 350, 
'Unit of Measure', 
9031, 
7302, 
'GLM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 351, 
'Unit of Measure', 
9033, 
7302, 
'chUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 352, 
'Unit of Measure', 
9034, 
7302, 
'lkUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 353, 
'Unit of Measure', 
9035, 
7302, 
'miUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 354, 
'Unit of Measure', 
9036, 
7302, 
'km', 
'' ); 

INSERT INTO epsg_alias VALUES ( 355, 
'Unit of Measure', 
9037, 
7302, 
'ydCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 356, 
'Unit of Measure', 
9038, 
7302, 
'chCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 357, 
'Unit of Measure', 
9039, 
7302, 
'lkCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 358, 
'Unit of Measure', 
9040, 
7302, 
'ydSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 359, 
'Unit of Measure', 
9041, 
7302, 
'ftSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 360, 
'Unit of Measure', 
9042, 
7302, 
'chSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 361, 
'Unit of Measure', 
9043, 
7302, 
'lkSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 362, 
'Unit of Measure', 
9050, 
7302, 
'ydBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 363, 
'Unit of Measure', 
9051, 
7302, 
'ftBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 364, 
'Unit of Measure', 
9052, 
7302, 
'chBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 365, 
'Unit of Measure', 
9053, 
7302, 
'lkBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 366, 
'Unit of Measure', 
9060, 
7302, 
'ydBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 367, 
'Unit of Measure', 
9061, 
7302, 
'ftBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 368, 
'Unit of Measure', 
9062, 
7302, 
'chBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 369, 
'Unit of Measure', 
9063, 
7302, 
'lkBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 370, 
'Unit of Measure', 
9070, 
7302, 
'ftBr(65)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 371, 
'Unit of Measure', 
9080, 
7302, 
'ftInd', 
'' ); 

INSERT INTO epsg_alias VALUES ( 372, 
'Unit of Measure', 
9081, 
7302, 
'ftInd(37)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 373, 
'Unit of Measure', 
9082, 
7302, 
'ftInd(62)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 374, 
'Unit of Measure', 
9083, 
7302, 
'ftInd(75)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 375, 
'Unit of Measure', 
9084, 
7302, 
'ydInd', 
'' ); 

INSERT INTO epsg_alias VALUES ( 376, 
'Unit of Measure', 
9085, 
7302, 
'ydInd(37)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 377, 
'Unit of Measure', 
9086, 
7302, 
'ydInd(62)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 378, 
'Unit of Measure', 
9087, 
7302, 
'ydInd(75)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 379, 
'Unit of Measure', 
9093, 
7302, 
'mi', 
'' ); 

INSERT INTO epsg_alias VALUES ( 380, 
'Unit of Measure', 
9094, 
7302, 
'ftGC', 
'' ); 

INSERT INTO epsg_alias VALUES ( 381, 
'Unit of Measure', 
9101, 
7302, 
'rad', 
'' ); 

INSERT INTO epsg_alias VALUES ( 382, 
'Unit of Measure', 
9102, 
7302, 
'deg', 
'' ); 

INSERT INTO epsg_alias VALUES ( 383, 
'Unit of Measure', 
9103, 
7302, 
'min', 
'' ); 

INSERT INTO epsg_alias VALUES ( 384, 
'Unit of Measure', 
9104, 
7302, 
'sec', 
'' ); 

INSERT INTO epsg_alias VALUES ( 385, 
'Unit of Measure', 
9105, 
7302, 
'gr', 
'' ); 

INSERT INTO epsg_alias VALUES ( 386, 
'Unit of Measure', 
9106, 
7302, 
'g', 
'' ); 

INSERT INTO epsg_alias VALUES ( 387, 
'Unit of Measure', 
9107, 
7302, 
'DMS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 388, 
'Unit of Measure', 
9108, 
7302, 
'DMSH', 
'' ); 

INSERT INTO epsg_alias VALUES ( 389, 
'Unit of Measure', 
9109, 
7302, 
'grad', 
'' ); 

INSERT INTO epsg_alias VALUES ( 390, 
'Unit of Measure', 
9110, 
7302, 
'DDD.MMSSsss', 
'' ); 

INSERT INTO epsg_alias VALUES ( 391, 
'Unit of Measure', 
9111, 
7302, 
'DDD.MMm', 
'' ); 

INSERT INTO epsg_alias VALUES ( 392, 
'Unit of Measure', 
9112, 
7302, 
'c', 
'' ); 

INSERT INTO epsg_alias VALUES ( 393, 
'Unit of Measure', 
9113, 
7302, 
'cc', 
'' ); 

INSERT INTO epsg_alias VALUES ( 394, 
'Unit of Measure', 
9114, 
7302, 
'mil', 
'' ); 

INSERT INTO epsg_alias VALUES ( 395, 
'Unit of Measure', 
9202, 
7302, 
'ppm', 
'' ); 

INSERT INTO epsg_alias VALUES ( 396, 
'Unit of Measure', 
9204, 
7302, 
'Bin330ftUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 397, 
'Unit of Measure', 
9205, 
7302, 
'Bin165ftUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 398, 
'Unit of Measure', 
9206, 
7302, 
'Bin82.5ftUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 399, 
'Unit of Measure', 
9207, 
7302, 
'Bin37.5m', 
'' ); 

INSERT INTO epsg_alias VALUES ( 400, 
'Unit of Measure', 
9208, 
7302, 
'Bin25m', 
'' ); 

INSERT INTO epsg_alias VALUES ( 401, 
'Unit of Measure', 
9209, 
7302, 
'Bin12.5m', 
'' ); 

INSERT INTO epsg_alias VALUES ( 402, 
'Unit of Measure', 
9210, 
7302, 
'Bin6.25m', 
'' ); 

INSERT INTO epsg_alias VALUES ( 403, 
'Unit of Measure', 
9211, 
7302, 
'Bin3.125m', 
'' ); 

INSERT INTO epsg_alias VALUES ( 404, 
'Coordinate_Operation Method', 
9633, 
7302, 
'OSTN', 
'' ); 

INSERT INTO epsg_alias VALUES ( 405, 
'Coordinate_Operation Method', 
9824, 
7302, 
'UTM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 406, 
'Coordinate_Operation', 
10101, 
7302, 
'Alabama East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 407, 
'Coordinate_Operation', 
10102, 
7302, 
'Alabama West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 408, 
'Coordinate_Operation', 
10131, 
7302, 
'Alabama East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 409, 
'Coordinate_Operation', 
10132, 
7302, 
'Alabama West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 410, 
'Coordinate_Operation', 
10201, 
7302, 
'Arizona East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 411, 
'Coordinate_Operation', 
10202, 
7302, 
'Arizona Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 412, 
'Coordinate_Operation', 
10203, 
7302, 
'Arizona West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 413, 
'Coordinate_Operation', 
10231, 
7302, 
'Arizona East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 414, 
'Coordinate_Operation', 
10232, 
7302, 
'Arizona Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 415, 
'Coordinate_Operation', 
10233, 
7302, 
'Arizona West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 416, 
'Coordinate_Operation', 
10301, 
7302, 
'Arkansas North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 417, 
'Coordinate_Operation', 
10302, 
7302, 
'Arkansas South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 418, 
'Coordinate_Operation Method', 
9809, 
7301, 
'Roussilhe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 419, 
'Coordinate_Operation', 
10331, 
7302, 
'Arkansas North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 420, 
'Coordinate_Operation', 
10332, 
7302, 
'Arkansas South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 421, 
'Coordinate_Operation', 
10401, 
7302, 
'California zone I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 422, 
'Coordinate_Operation', 
10402, 
7302, 
'California zone II', 
'' ); 

INSERT INTO epsg_alias VALUES ( 423, 
'Coordinate_Operation', 
10403, 
7302, 
'California zone III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 424, 
'Coordinate_Operation', 
10404, 
7302, 
'California zone IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 425, 
'Coordinate_Operation', 
10405, 
7302, 
'California zone V', 
'' ); 

INSERT INTO epsg_alias VALUES ( 426, 
'Coordinate_Operation', 
10406, 
7302, 
'California zone VI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 427, 
'Coordinate_Operation', 
10407, 
7302, 
'California zone VII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 428, 
'Coordinate_Operation', 
10431, 
7302, 
'California zone 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 429, 
'Coordinate_Operation', 
10432, 
7302, 
'California zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 430, 
'Coordinate_Operation', 
10433, 
7302, 
'California zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 431, 
'Coordinate_Operation', 
10434, 
7302, 
'California zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 432, 
'Coordinate_Operation', 
10435, 
7302, 
'California zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 433, 
'Coordinate_Operation', 
10436, 
7302, 
'California zone 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 434, 
'Coordinate_Operation', 
10501, 
7302, 
'Colorado North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 435, 
'Coordinate_Operation', 
10503, 
7302, 
'Colorado South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 436, 
'Coordinate_Operation', 
10531, 
7302, 
'Colorado North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 437, 
'Coordinate_Operation', 
10533, 
7302, 
'Colorado South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 438, 
'Coordinate_Operation', 
10600, 
7302, 
'Connecticut', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 439, 
'Coordinate_Operation', 
10630, 
7302, 
'Connecticut', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 440, 
'Coordinate_Operation', 
10700, 
7302, 
'Delaware', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 441, 
'Coordinate_Operation', 
10730, 
7302, 
'Delaware', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 442, 
'Coordinate_Operation', 
10901, 
7302, 
'Florida East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 443, 
'Coordinate_Operation', 
10902, 
7302, 
'Florida West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 444, 
'Coordinate_Operation', 
10903, 
7302, 
'Florida North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 445, 
'Coordinate_Operation', 
10931, 
7302, 
'Florida East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 446, 
'Coordinate_Operation', 
10932, 
7302, 
'Florida West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 447, 
'Coordinate_Operation', 
10933, 
7302, 
'Florida North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 448, 
'Coordinate_Operation', 
11001, 
7302, 
'Georgia East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 449, 
'Coordinate_Operation', 
11002, 
7302, 
'Georgia West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 450, 
'Coordinate_Operation', 
11031, 
7302, 
'Georgia East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 451, 
'Coordinate_Operation', 
11032, 
7302, 
'Georgia West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 452, 
'Coordinate_Operation', 
11101, 
7302, 
'Idaho East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 453, 
'Coordinate_Operation', 
11102, 
7302, 
'Idaho Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 454, 
'Coordinate_Operation', 
11103, 
7302, 
'Idaho West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 455, 
'Coordinate_Operation', 
11131, 
7302, 
'Idaho East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 456, 
'Coordinate_Operation', 
11132, 
7302, 
'Idaho Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 457, 
'Coordinate_Operation', 
11133, 
7302, 
'Idaho West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 458, 
'Coordinate_Operation', 
11201, 
7302, 
'Illinois East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 459, 
'Coordinate_Operation', 
11202, 
7302, 
'Illinois West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 460, 
'Coordinate_Operation', 
11231, 
7302, 
'Illinois East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 461, 
'Coordinate_Operation', 
11232, 
7302, 
'Illinois West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 462, 
'Coordinate_Operation', 
11301, 
7302, 
'Indiana East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 463, 
'Coordinate_Operation', 
11302, 
7302, 
'Indiana West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 464, 
'Coordinate_Operation', 
11331, 
7302, 
'Indiana East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 465, 
'Coordinate_Operation', 
11332, 
7302, 
'Indiana West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 466, 
'Coordinate_Operation', 
11401, 
7302, 
'Iowa North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 467, 
'Coordinate_Operation', 
11402, 
7302, 
'Iowa South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 468, 
'Coordinate_Operation', 
11431, 
7302, 
'Iowa North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 469, 
'Coordinate_Operation', 
11432, 
7302, 
'Iowa South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 470, 
'Coordinate_Operation', 
11501, 
7302, 
'Kansas North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 471, 
'Coordinate_Operation', 
11502, 
7302, 
'Kansas South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 472, 
'Coordinate_Operation', 
11531, 
7302, 
'Kansas North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 473, 
'Coordinate_Operation', 
11532, 
7302, 
'Kansas South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 474, 
'Coordinate_Operation', 
11601, 
7302, 
'Kentucky North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 475, 
'Coordinate_Operation', 
11602, 
7302, 
'Kentucky South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 476, 
'Coordinate_Operation', 
11631, 
7302, 
'Kentucky North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 477, 
'Coordinate_Operation', 
11632, 
7302, 
'Kentucky South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 478, 
'Coordinate_Operation', 
11701, 
7302, 
'Louisiana North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 479, 
'Coordinate_Operation', 
11702, 
7302, 
'Louisiana South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 480, 
'Coordinate_Operation', 
11731, 
7302, 
'Louisiana North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 481, 
'Coordinate_Operation', 
11732, 
7302, 
'Louisiana South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 482, 
'Coordinate_Operation', 
11801, 
7302, 
'Maine East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 483, 
'Coordinate_Operation', 
11802, 
7302, 
'Maine West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 484, 
'Coordinate_Operation', 
11831, 
7302, 
'Maine East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 485, 
'Coordinate_Operation', 
11832, 
7302, 
'Maine West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 486, 
'Coordinate_Operation', 
11900, 
7302, 
'Maryland', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 487, 
'Coordinate_Operation', 
11930, 
7302, 
'Maryland', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 488, 
'Coordinate_Operation', 
12001, 
7302, 
'Massachusetts Mainland', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 489, 
'Coordinate_Operation', 
12002, 
7302, 
'Massachusetts Island', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 490, 
'Coordinate_Operation', 
12031, 
7302, 
'Massachusetts Mainland', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 491, 
'Coordinate_Operation', 
12032, 
7302, 
'Massachusetts Island', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 492, 
'Coordinate_Operation', 
12101, 
7302, 
'Michigan East', 
'' ); 

INSERT INTO epsg_alias VALUES ( 493, 
'Coordinate_Operation', 
12102, 
7302, 
'Michigan Old Central', 
'' ); 

INSERT INTO epsg_alias VALUES ( 494, 
'Coordinate_Operation', 
12111, 
7302, 
'Michigan North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 495, 
'Coordinate_Operation', 
12112, 
7302, 
'Michigan Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 496, 
'Coordinate_Operation', 
12113, 
7302, 
'Michigan South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 497, 
'Coordinate_Operation', 
12141, 
7302, 
'Michigan North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 498, 
'Coordinate_Operation', 
12142, 
7302, 
'Michigan Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 499, 
'Coordinate_Operation', 
12143, 
7302, 
'Michigan South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 500, 
'Coordinate_Operation', 
12201, 
7302, 
'Minnesota North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 501, 
'Coordinate_Operation', 
12202, 
7302, 
'Minnesota Central', 
'' ); 

INSERT INTO epsg_alias VALUES ( 502, 
'Coordinate_Operation', 
12203, 
7302, 
'Minnesota South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 503, 
'Coordinate_Operation', 
12231, 
7302, 
'Minnesota North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 504, 
'Coordinate_Operation', 
12232, 
7302, 
'Minnesota Central (m)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 505, 
'Coordinate_Operation', 
12233, 
7302, 
'Minnesota South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 506, 
'Coordinate_Operation', 
12301, 
7302, 
'Mississippi East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 507, 
'Coordinate_Operation', 
12302, 
7302, 
'Mississippi West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 508, 
'Coordinate_Operation', 
12331, 
7302, 
'Mississippi East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 509, 
'Coordinate_Operation', 
12332, 
7302, 
'Mississippi West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 510, 
'Coordinate_Operation', 
12401, 
7302, 
'Missouri East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 511, 
'Coordinate_Operation', 
12402, 
7302, 
'Missouri Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 512, 
'Coordinate_Operation', 
12403, 
7302, 
'Missouri West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 513, 
'Coordinate_Operation', 
12431, 
7302, 
'Missouri East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 514, 
'Coordinate_Operation', 
12432, 
7302, 
'Missouri Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 515, 
'Coordinate_Operation', 
12433, 
7302, 
'Missouri West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 516, 
'Coordinate_Operation', 
12501, 
7302, 
'Montana North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 517, 
'Coordinate_Operation', 
12502, 
7302, 
'Montana Central', 
'' ); 

INSERT INTO epsg_alias VALUES ( 518, 
'Coordinate_Operation', 
12503, 
7302, 
'Montana South', 
'' ); 

INSERT INTO epsg_alias VALUES ( 519, 
'Coordinate_Operation', 
12530, 
7302, 
'Montana', 
'' ); 

INSERT INTO epsg_alias VALUES ( 520, 
'Coordinate_Operation', 
12601, 
7302, 
'Nebraska North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 521, 
'Coordinate_Operation', 
12602, 
7302, 
'Nebraska South', 
'' ); 

INSERT INTO epsg_alias VALUES ( 522, 
'Coordinate_Operation', 
12630, 
7302, 
'Nebraska (m)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 523, 
'Coordinate_Operation', 
12701, 
7302, 
'Nevada East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 524, 
'Coordinate_Operation', 
12702, 
7302, 
'Nevada Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 525, 
'Coordinate_Operation', 
12703, 
7302, 
'Nevada West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 526, 
'Coordinate_Operation', 
12731, 
7302, 
'Nevada East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 527, 
'Coordinate_Operation', 
12732, 
7302, 
'Nevada Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 528, 
'Coordinate_Operation', 
12733, 
7302, 
'Nevada West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 529, 
'Coordinate_Operation', 
12800, 
7302, 
'New Hampshire', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 530, 
'Coordinate_Operation', 
12830, 
7302, 
'New Hampshire', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 531, 
'Coordinate_Operation', 
12900, 
7302, 
'New Jersey', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 532, 
'Coordinate_Operation', 
12930, 
7302, 
'New Jersey', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 533, 
'Coordinate_Operation', 
13001, 
7302, 
'New Mexico East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 534, 
'Coordinate_Operation', 
13002, 
7302, 
'New Mexico Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 535, 
'Coordinate_Operation', 
13003, 
7302, 
'New Mexico West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 536, 
'Coordinate_Operation', 
13031, 
7302, 
'New Mexico East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 537, 
'Coordinate_Operation', 
13032, 
7302, 
'New Mexico Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 538, 
'Coordinate_Operation', 
13033, 
7302, 
'New Mexico West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 539, 
'Coordinate_Operation', 
13101, 
7302, 
'New York East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 540, 
'Coordinate_Operation', 
13102, 
7302, 
'New York Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 541, 
'Coordinate_Operation', 
13103, 
7302, 
'New York  West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 542, 
'Coordinate_Operation', 
13104, 
7302, 
'New York Long Island', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 543, 
'Coordinate_Operation', 
13131, 
7302, 
'New York East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 544, 
'Coordinate_Operation', 
13132, 
7302, 
'New York Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 545, 
'Coordinate_Operation', 
13133, 
7302, 
'New York  West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 546, 
'Coordinate_Operation', 
13134, 
7302, 
'New York Long Island', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 547, 
'Coordinate_Operation', 
13200, 
7302, 
'North Carolina', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 548, 
'Coordinate_Operation', 
13230, 
7302, 
'North Carolina', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 549, 
'Coordinate_Operation', 
13301, 
7302, 
'North Dakota North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 550, 
'Coordinate_Operation', 
13302, 
7302, 
'North Dakota South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 551, 
'Coordinate_Operation', 
13331, 
7302, 
'North Dakota North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 552, 
'Coordinate_Operation', 
13332, 
7302, 
'North Dakota South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 553, 
'Coordinate_Operation', 
13401, 
7302, 
'Ohio North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 554, 
'Coordinate_Operation', 
13402, 
7302, 
'Ohio South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 555, 
'Coordinate_Operation', 
13431, 
7302, 
'Ohio North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 556, 
'Coordinate_Operation', 
13432, 
7302, 
'Ohio South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 557, 
'Coordinate_Operation', 
13501, 
7302, 
'Oklahoma North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 558, 
'Coordinate_Operation', 
13502, 
7302, 
'Oklahoma South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 559, 
'Coordinate_Operation', 
13531, 
7302, 
'Oklahoma North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 560, 
'Coordinate_Operation', 
13532, 
7302, 
'Oklahoma South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 561, 
'Coordinate_Operation', 
13601, 
7302, 
'Oregon North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 562, 
'Coordinate_Operation', 
13602, 
7302, 
'Oregon South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 563, 
'Coordinate_Operation', 
13631, 
7302, 
'Oregon North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 564, 
'Coordinate_Operation', 
13632, 
7302, 
'Oregon South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 565, 
'Coordinate_Operation', 
13701, 
7302, 
'Pennsylvania North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 566, 
'Coordinate_Operation', 
13702, 
7302, 
'Pennsylvania South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 567, 
'Coordinate_Operation', 
13731, 
7302, 
'Pennsylvania North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 568, 
'Coordinate_Operation', 
13732, 
7302, 
'Pennsylvania South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 569, 
'Coordinate_Operation', 
13800, 
7302, 
'Rhode Island', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 570, 
'Coordinate_Operation', 
13830, 
7302, 
'Rhode Island', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 571, 
'Coordinate_Operation', 
13901, 
7302, 
'South Carolina North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 572, 
'Coordinate_Operation', 
13902, 
7302, 
'South Carolina South', 
'' ); 

INSERT INTO epsg_alias VALUES ( 573, 
'Coordinate_Operation', 
13930, 
7302, 
'South Carolina', 
'' ); 

INSERT INTO epsg_alias VALUES ( 574, 
'Coordinate_Operation', 
14001, 
7302, 
'South Dakota North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 575, 
'Coordinate_Operation', 
14002, 
7302, 
'South Dakota South', 
'' ); 

INSERT INTO epsg_alias VALUES ( 576, 
'Coordinate_Operation', 
14031, 
7302, 
'South Dakota North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 577, 
'Coordinate_Operation', 
14032, 
7302, 
'South Dakota South', 
'' ); 

INSERT INTO epsg_alias VALUES ( 578, 
'Coordinate_Operation', 
14100, 
7302, 
'Tennessee', 
'' ); 

INSERT INTO epsg_alias VALUES ( 579, 
'Coordinate_Operation', 
14130, 
7302, 
'Tennessee', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 580, 
'Coordinate_Operation', 
14201, 
7302, 
'Texas North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 581, 
'Coordinate_Operation', 
14202, 
7302, 
'Texas North Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 582, 
'Coordinate_Operation', 
14203, 
7302, 
'Texas Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 583, 
'Coordinate_Operation', 
14204, 
7302, 
'Texas South Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 584, 
'Coordinate_Operation', 
14205, 
7302, 
'Texas South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 585, 
'Coordinate_Operation', 
14231, 
7302, 
'Texas North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 586, 
'Coordinate_Operation', 
14232, 
7302, 
'Texas North Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 587, 
'Coordinate_Operation', 
14233, 
7302, 
'Texas Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 588, 
'Coordinate_Operation', 
14234, 
7302, 
'Texas South Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 589, 
'Coordinate_Operation', 
14235, 
7302, 
'Texas South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 590, 
'Coordinate_Operation', 
14301, 
7302, 
'Utah North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 591, 
'Coordinate_Operation', 
14302, 
7302, 
'Utah Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 592, 
'Coordinate_Operation', 
14303, 
7302, 
'Utah South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 593, 
'Coordinate_Operation', 
14331, 
7302, 
'Utah North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 594, 
'Coordinate_Operation', 
14332, 
7302, 
'Utah Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 595, 
'Coordinate_Operation', 
14333, 
7302, 
'Utah South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 596, 
'Coordinate_Operation', 
14400, 
7302, 
'Vermont', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 597, 
'Coordinate_Operation', 
14430, 
7302, 
'Vermont', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 598, 
'Coordinate_Operation', 
14501, 
7302, 
'Virginia North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 599, 
'Coordinate_Operation', 
14502, 
7302, 
'Virginia South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 600, 
'Coordinate_Operation', 
14531, 
7302, 
'Virginia North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 601, 
'Coordinate_Operation', 
14532, 
7302, 
'Virginia South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 602, 
'Coordinate_Operation', 
14601, 
7302, 
'Washington North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 603, 
'Coordinate_Operation', 
14602, 
7302, 
'Washington South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 604, 
'Coordinate_Operation', 
14631, 
7302, 
'Washington North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 605, 
'Coordinate_Operation', 
14632, 
7302, 
'Washington South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 606, 
'Coordinate_Operation', 
14701, 
7302, 
'West Virginia North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 607, 
'Coordinate_Operation', 
14702, 
7302, 
'West Virginia South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 608, 
'Coordinate_Operation', 
14731, 
7302, 
'West Virginia North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 609, 
'Coordinate_Operation', 
14732, 
7302, 
'West Virginia South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 610, 
'Coordinate_Operation', 
14801, 
7302, 
'Wisconsin North', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 611, 
'Coordinate_Operation', 
14802, 
7302, 
'Wisconsin Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 612, 
'Coordinate_Operation', 
14803, 
7302, 
'Wisconsin South', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 613, 
'Coordinate_Operation', 
14831, 
7302, 
'Wisconsin North', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 614, 
'Coordinate_Operation', 
14832, 
7302, 
'Wisconsin Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 615, 
'Coordinate_Operation', 
14833, 
7302, 
'Wisconsin South', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 616, 
'Coordinate_Operation', 
14901, 
7302, 
'Wyoming East', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 617, 
'Coordinate_Operation', 
14902, 
7302, 
'Wyoming East Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 618, 
'Coordinate_Operation', 
14903, 
7302, 
'Wyoming West Central', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 619, 
'Coordinate_Operation', 
14904, 
7302, 
'Wyoming West', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 620, 
'Coordinate_Operation', 
14931, 
7302, 
'Wyoming East', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 621, 
'Coordinate_Operation', 
14932, 
7302, 
'Wyoming East Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 622, 
'Coordinate_Operation', 
14933, 
7302, 
'Wyoming West Central', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 623, 
'Coordinate_Operation', 
14934, 
7302, 
'Wyoming West', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 624, 
'Coordinate_Operation', 
15001, 
7302, 
'Alaska zone 1', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 625, 
'Coordinate_Operation', 
15002, 
7302, 
'Alaska zone 2', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 626, 
'Coordinate_Operation', 
15003, 
7302, 
'Alaska zone 3', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 627, 
'Coordinate_Operation', 
15004, 
7302, 
'Alaska zone 4', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 628, 
'Coordinate_Operation', 
15005, 
7302, 
'Alaska zone 5', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 629, 
'Coordinate_Operation', 
15006, 
7302, 
'Alaska zone 6', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 630, 
'Coordinate_Operation', 
15007, 
7302, 
'Alaska zone 7', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 631, 
'Coordinate_Operation', 
15008, 
7302, 
'Alaska zone 8', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 632, 
'Coordinate_Operation', 
15009, 
7302, 
'Alaska zone 9', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 633, 
'Coordinate_Operation', 
15010, 
7302, 
'Alaska zone 10', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 634, 
'Coordinate_Operation', 
15031, 
7302, 
'Alaska zone 1', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 635, 
'Coordinate_Operation', 
15032, 
7302, 
'Alaska zone 2', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 636, 
'Coordinate_Operation', 
15033, 
7302, 
'Alaska zone 3', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 637, 
'Coordinate_Operation', 
15034, 
7302, 
'Alaska zone 4', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 638, 
'Coordinate_Operation', 
15035, 
7302, 
'Alaska zone 5', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 639, 
'Coordinate_Operation', 
15036, 
7302, 
'Alaska zone 6', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 640, 
'Coordinate_Operation', 
15037, 
7302, 
'Alaska zone 7', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 641, 
'Coordinate_Operation', 
15038, 
7302, 
'Alaska zone 8', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 642, 
'Coordinate_Operation', 
15039, 
7302, 
'Alaska zone 9', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 643, 
'Coordinate_Operation', 
15040, 
7302, 
'Alaska zone 10', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 644, 
'Coordinate_Operation', 
15101, 
7302, 
'Hawaii  zone 1', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 645, 
'Coordinate_Operation', 
15102, 
7302, 
'Hawaii  zone 2', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 646, 
'Coordinate_Operation', 
15103, 
7302, 
'Hawaii  zone 3', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 647, 
'Coordinate_Operation', 
15104, 
7302, 
'Hawaii  zone 4', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 648, 
'Coordinate_Operation', 
15105, 
7302, 
'Hawaii  zone 5', 
'This alias is ambiguous as also used for SPCS83 projection.' ); 

INSERT INTO epsg_alias VALUES ( 649, 
'Coordinate_Operation', 
15131, 
7302, 
'Hawaii zone 1', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 650, 
'Coordinate_Operation', 
15132, 
7302, 
'Hawaii zone 2', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 651, 
'Coordinate_Operation', 
15133, 
7302, 
'Hawaii zone 3', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 652, 
'Coordinate_Operation', 
15134, 
7302, 
'Hawaii zone 4', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 653, 
'Coordinate_Operation', 
15135, 
7302, 
'Hawaii zone 5', 
'This alias is ambiguous as also used for SPCS27 projection.' ); 

INSERT INTO epsg_alias VALUES ( 654, 
'Coordinate_Operation', 
15201, 
7302, 
'Puerto Rico', 
'' ); 

INSERT INTO epsg_alias VALUES ( 655, 
'Coordinate_Operation', 
15202, 
7302, 
'St. Croix', 
'' ); 

INSERT INTO epsg_alias VALUES ( 656, 
'Coordinate_Operation', 
15230, 
7302, 
'Puerto Rico & Virgin Is.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 657, 
'Coordinate_Operation', 
15914, 
7302, 
'BLM 14N (ftUS)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 658, 
'Coordinate_Operation', 
15915, 
7302, 
'BLM 15N (ftUS)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 659, 
'Coordinate_Operation', 
15916, 
7302, 
'BLM 16N (ftUS)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 660, 
'Coordinate_Operation', 
15917, 
7302, 
'BLM 17N (ftUS)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 661, 
'Coordinate_Operation', 
16061, 
7302, 
'UPS North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 662, 
'Coordinate_Operation', 
16161, 
7302, 
'UPS South', 
'' ); 

INSERT INTO epsg_alias VALUES ( 663, 
'Coordinate_Operation', 
16261, 
7302, 
'3-degree Gauss zone 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 664, 
'Coordinate_Operation', 
16262, 
7302, 
'3-degree Gauss zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 665, 
'Coordinate_Operation', 
16263, 
7302, 
'3-degree Gauss zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 666, 
'Coordinate_Operation', 
16264, 
7302, 
'3-degree Gauss zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 667, 
'Coordinate_Operation', 
16265, 
7302, 
'3-degree Gauss zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 668, 
'Coordinate_Operation', 
16266, 
7302, 
'3-degree Gauss zone 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 669, 
'Coordinate_Operation', 
16267, 
7302, 
'3-degree Gauss zone 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 670, 
'Coordinate_Operation', 
16268, 
7302, 
'3-degree Gauss zone 8', 
'' ); 

INSERT INTO epsg_alias VALUES ( 671, 
'Coordinate_Operation', 
16361, 
7302, 
'3-deg Gauss-Kruger 3E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 672, 
'Coordinate_Operation', 
16362, 
7302, 
'3-deg Gauss-Kruger 6E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 673, 
'Coordinate_Operation', 
16363, 
7302, 
'3-deg Gauss-Kruger 9E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 674, 
'Coordinate_Operation', 
16364, 
7302, 
'3-deg Gauss-Kruger 12E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 675, 
'Coordinate_Operation', 
16365, 
7302, 
'3-deg Gauss-Kruger 15E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 676, 
'Coordinate_Operation', 
16366, 
7302, 
'3-deg Gauss-Kruger 18E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 677, 
'Coordinate_Operation', 
16367, 
7302, 
'3-deg Gauss-Kruger 21E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 678, 
'Coordinate_Operation', 
16368, 
7302, 
'3-deg Gauss-Kruger 24E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 679, 
'Coordinate_Operation', 
17348, 
7302, 
'MGA zone 48', 
'' ); 

INSERT INTO epsg_alias VALUES ( 680, 
'Coordinate_Operation', 
17349, 
7302, 
'MGA zone 49', 
'' ); 

INSERT INTO epsg_alias VALUES ( 681, 
'Coordinate_Operation', 
17350, 
7302, 
'MGA zone 50', 
'' ); 

INSERT INTO epsg_alias VALUES ( 682, 
'Coordinate_Operation', 
17351, 
7302, 
'MGA zone 51', 
'' ); 

INSERT INTO epsg_alias VALUES ( 683, 
'Coordinate_Operation', 
17352, 
7302, 
'MGA zone 52', 
'' ); 

INSERT INTO epsg_alias VALUES ( 684, 
'Coordinate_Operation', 
17353, 
7302, 
'MGA zone 53', 
'' ); 

INSERT INTO epsg_alias VALUES ( 685, 
'Coordinate_Operation', 
17354, 
7302, 
'MGA zone 54', 
'' ); 

INSERT INTO epsg_alias VALUES ( 686, 
'Coordinate_Operation', 
17355, 
7302, 
'MGA zone 55', 
'' ); 

INSERT INTO epsg_alias VALUES ( 687, 
'Coordinate_Operation', 
17356, 
7302, 
'MGA zone 56', 
'' ); 

INSERT INTO epsg_alias VALUES ( 688, 
'Coordinate_Operation', 
17357, 
7302, 
'MGA zone 57', 
'' ); 

INSERT INTO epsg_alias VALUES ( 689, 
'Coordinate_Operation', 
17358, 
7302, 
'MGA zone 58', 
'' ); 

INSERT INTO epsg_alias VALUES ( 690, 
'Coordinate_Operation', 
17448, 
7302, 
'AMG zone 48', 
'' ); 

INSERT INTO epsg_alias VALUES ( 691, 
'Coordinate_Operation', 
17449, 
7302, 
'AMG zone 49', 
'' ); 

INSERT INTO epsg_alias VALUES ( 692, 
'Coordinate_Operation', 
17450, 
7302, 
'AMG zone 50', 
'' ); 

INSERT INTO epsg_alias VALUES ( 693, 
'Coordinate_Operation', 
17451, 
7302, 
'AMG zone 51', 
'' ); 

INSERT INTO epsg_alias VALUES ( 694, 
'Coordinate_Operation', 
17452, 
7302, 
'AMG zone 52', 
'' ); 

INSERT INTO epsg_alias VALUES ( 695, 
'Coordinate_Operation', 
17453, 
7302, 
'AMG zone 53', 
'' ); 

INSERT INTO epsg_alias VALUES ( 696, 
'Coordinate_Operation', 
17454, 
7302, 
'AMG zone 54', 
'' ); 

INSERT INTO epsg_alias VALUES ( 697, 
'Coordinate_Operation', 
17455, 
7302, 
'AMG zone 55', 
'' ); 

INSERT INTO epsg_alias VALUES ( 698, 
'Coordinate_Operation', 
17456, 
7302, 
'AMG zone 56', 
'' ); 

INSERT INTO epsg_alias VALUES ( 699, 
'Coordinate_Operation', 
17457, 
7302, 
'AMG zone 57', 
'' ); 

INSERT INTO epsg_alias VALUES ( 700, 
'Coordinate_Operation', 
17458, 
7302, 
'AMG zone 58', 
'' ); 

INSERT INTO epsg_alias VALUES ( 701, 
'Coordinate_Operation', 
17515, 
7302, 
'S. African Grid zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 702, 
'Coordinate_Operation', 
17517, 
7302, 
'S. African Grid zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 703, 
'Coordinate_Operation', 
17519, 
7302, 
'S. African Grid zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 704, 
'Coordinate_Operation', 
17521, 
7302, 
'S. African Grid zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 705, 
'Coordinate_Operation', 
17523, 
7302, 
'S. African Grid zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 706, 
'Coordinate_Operation', 
17525, 
7302, 
'S. African Grid zone 25', 
'' ); 

INSERT INTO epsg_alias VALUES ( 707, 
'Coordinate_Operation', 
17527, 
7302, 
'S. African Grid zone 27', 
'' ); 

INSERT INTO epsg_alias VALUES ( 708, 
'Coordinate_Operation', 
17529, 
7302, 
'S. African Grid zone 29', 
'' ); 

INSERT INTO epsg_alias VALUES ( 709, 
'Coordinate_Operation', 
17531, 
7302, 
'S. African Grid zone 31', 
'' ); 

INSERT INTO epsg_alias VALUES ( 710, 
'Coordinate_Operation', 
17533, 
7302, 
'S. African Grid zone 33', 
'' ); 

INSERT INTO epsg_alias VALUES ( 711, 
'Coordinate_Operation', 
17611, 
7302, 
'SW African Grid zone 11', 
'' ); 

INSERT INTO epsg_alias VALUES ( 712, 
'Coordinate_Operation', 
17613, 
7302, 
'SW African Grid zone 13', 
'' ); 

INSERT INTO epsg_alias VALUES ( 713, 
'Coordinate_Operation', 
17615, 
7302, 
'SW African Grid zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 714, 
'Coordinate_Operation', 
17617, 
7302, 
'SW African Grid zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 715, 
'Coordinate_Operation', 
17619, 
7302, 
'SW African Grid zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 716, 
'Coordinate_Operation', 
17621, 
7302, 
'SW African Grid zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 717, 
'Coordinate_Operation', 
17623, 
7302, 
'SW African Grid zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 718, 
'Coordinate_Operation', 
17625, 
7302, 
'SW African Grid zone 25', 
'' ); 

INSERT INTO epsg_alias VALUES ( 719, 
'Coordinate_Operation', 
17702, 
7302, 
'MTM zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 720, 
'Coordinate_Operation', 
17801, 
7302, 
'Japan zone I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 721, 
'Coordinate_Operation', 
17802, 
7302, 
'Japan zone II', 
'' ); 

INSERT INTO epsg_alias VALUES ( 722, 
'Coordinate_Operation', 
17803, 
7302, 
'Japan zone III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 723, 
'Coordinate_Operation', 
17804, 
7302, 
'Japan zone IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 724, 
'Coordinate_Operation', 
17805, 
7302, 
'Japan zone V', 
'' ); 

INSERT INTO epsg_alias VALUES ( 725, 
'Coordinate_Operation', 
17806, 
7302, 
'Japan zone VI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 726, 
'Coordinate_Operation', 
17807, 
7302, 
'Japan zone VII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 727, 
'Coordinate_Operation', 
17808, 
7302, 
'Japan zone VIII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 728, 
'Coordinate_Operation', 
17809, 
7302, 
'Japan zone IX', 
'' ); 

INSERT INTO epsg_alias VALUES ( 729, 
'Coordinate_Operation', 
17810, 
7302, 
'Japan zone X', 
'' ); 

INSERT INTO epsg_alias VALUES ( 730, 
'Coordinate_Operation', 
17811, 
7302, 
'Japan zone XI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 731, 
'Coordinate_Operation', 
17812, 
7302, 
'Japan zone XII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 732, 
'Coordinate_Operation', 
17813, 
7302, 
'Japan zone XIII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 733, 
'Coordinate_Operation', 
17814, 
7302, 
'Japan zone XIV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 734, 
'Coordinate_Operation', 
17815, 
7302, 
'Japan zone XV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 735, 
'Coordinate_Operation', 
17816, 
7302, 
'Japan zone XVI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 736, 
'Coordinate_Operation', 
17817, 
7302, 
'Japan zone XVII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 737, 
'Coordinate_Operation', 
17818, 
7302, 
'Japan zone XVIII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 738, 
'Coordinate_Operation', 
17901, 
7302, 
'Mt Eden Circuit', 
'' ); 

INSERT INTO epsg_alias VALUES ( 739, 
'Coordinate_Operation', 
17920, 
7302, 
'Mt Pleasant Circuit', 
'' ); 

INSERT INTO epsg_alias VALUES ( 740, 
'Coordinate_Operation', 
17924, 
7302, 
'Mt Nicholas Circuit', 
'' ); 

INSERT INTO epsg_alias VALUES ( 741, 
'Coordinate_Operation', 
17925, 
7302, 
'Mt York Circuit', 
'' ); 

INSERT INTO epsg_alias VALUES ( 742, 
'Coordinate_Operation', 
17926, 
7302, 
'Observation Pt Circuit', 
'' ); 

INSERT INTO epsg_alias VALUES ( 743, 
'Coordinate_Operation', 
18031, 
7302, 
'Argentina 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 744, 
'Coordinate_Operation', 
18032, 
7302, 
'Argentina 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 745, 
'Coordinate_Operation', 
18033, 
7302, 
'Argentina 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 746, 
'Coordinate_Operation', 
18034, 
7302, 
'Argentina 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 747, 
'Coordinate_Operation', 
18035, 
7302, 
'Argentina 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 748, 
'Coordinate_Operation', 
18036, 
7302, 
'Argentina 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 749, 
'Coordinate_Operation', 
18037, 
7302, 
'Argentina 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 750, 
'Coordinate_Operation', 
18044, 
7302, 
'M28', 
'' ); 

INSERT INTO epsg_alias VALUES ( 751, 
'Coordinate_Operation', 
18045, 
7302, 
'M31', 
'' ); 

INSERT INTO epsg_alias VALUES ( 752, 
'Coordinate_Operation', 
18046, 
7302, 
'M34', 
'' ); 

INSERT INTO epsg_alias VALUES ( 753, 
'Coordinate_Operation', 
18051, 
7302, 
'Colombia 3W', 
'' ); 

INSERT INTO epsg_alias VALUES ( 754, 
'Coordinate_Operation', 
18052, 
7302, 
'Colombia Bogota', 
'' ); 

INSERT INTO epsg_alias VALUES ( 755, 
'Coordinate_Operation', 
18053, 
7302, 
'Colombia 3E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 756, 
'Coordinate_Operation', 
18054, 
7302, 
'Colombia 6E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 757, 
'Coordinate_Operation', 
18071, 
7302, 
'Blue Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 758, 
'Coordinate_Operation', 
18072, 
7302, 
'Red Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 759, 
'Coordinate_Operation', 
18073, 
7302, 
'Purple Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 760, 
'Coordinate_Operation', 
18074, 
7302, 
'Extended Purple Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 761, 
'Coordinate_Operation', 
18141, 
7302, 
'North Island Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 762, 
'Coordinate_Operation', 
18142, 
7302, 
'South Island Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 763, 
'Coordinate_Operation', 
18193, 
7302, 
'Finland zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 764, 
'Coordinate_Operation', 
18203, 
7302, 
'ICS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 765, 
'Coordinate_Operation', 
18204, 
7302, 
'ITM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 766, 
'Coordinate_Operation', 
18231, 
7302, 
'India zone I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 767, 
'Coordinate_Operation', 
18232, 
7302, 
'India zone IIa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 768, 
'Coordinate_Operation', 
18233, 
7302, 
'India zone IIIa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 769, 
'Coordinate_Operation', 
18234, 
7302, 
'India zone IVa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 770, 
'Coordinate_Operation', 
18235, 
7302, 
'India zone IIb', 
'' ); 

INSERT INTO epsg_alias VALUES ( 771, 
'Coordinate_Operation', 
18236, 
7302, 
'India zone I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 772, 
'Coordinate_Operation', 
18237, 
7302, 
'India zone IIa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 773, 
'Coordinate_Operation', 
18238, 
7302, 
'India zone IIb', 
'' ); 

INSERT INTO epsg_alias VALUES ( 774, 
'Coordinate_Operation', 
19900, 
7302, 
'Bahrain Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 775, 
'Coordinate_Operation', 
19905, 
7302, 
'NEIEZ', 
'' ); 

INSERT INTO epsg_alias VALUES ( 776, 
'Coordinate_Operation', 
19917, 
7302, 
'NZMG', 
'' ); 

INSERT INTO epsg_alias VALUES ( 777, 
'Coordinate_Operation', 
19922, 
7302, 
'LV03', 
'' ); 

INSERT INTO epsg_alias VALUES ( 778, 
'Coordinate_Operation', 
19923, 
7302, 
'LV03C', 
'' ); 

INSERT INTO epsg_alias VALUES ( 779, 
'Coordinate_Operation', 
19928, 
7302, 
'KTM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 781, 
'Coordinate_Operation', 
19931, 
7302, 
'EOV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 782, 
'Coordinate_Operation', 
19933, 
7302, 
'PEI Stereographic ATS77', 
'' ); 

INSERT INTO epsg_alias VALUES ( 783, 
'Coordinate_Operation', 
19935, 
7302, 
'R.S.O. Malaya', 
'' ); 

INSERT INTO epsg_alias VALUES ( 784, 
'Coordinate_Operation', 
19945, 
7302, 
'NB Stereographic ATS77', 
'' ); 

INSERT INTO epsg_alias VALUES ( 785, 
'Coordinate_Operation', 
19946, 
7302, 
'NB Stereographic NAD83', 
'' ); 

INSERT INTO epsg_alias VALUES ( 786, 
'Coordinate_Operation', 
19950, 
7302, 
'LV95', 
'' ); 

INSERT INTO epsg_alias VALUES ( 787, 
'Coordinate_Operation', 
19951, 
7302, 
'Nakhl e Taqi', 
'' ); 

INSERT INTO epsg_alias VALUES ( 788, 
'Coordinate_Operation', 
19956, 
7302, 
'RSO Borneo (chSe)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 789, 
'Coordinate_Operation', 
19957, 
7302, 
'RSO Borneo (ftSe)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 790, 
'Coordinate_Operation', 
19958, 
7302, 
'RSO Borneo (m)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 791, 
'Coordinate_Operation', 
19960, 
7302, 
'PEI Stereographic NAD83', 
'' ); 

INSERT INTO epsg_alias VALUES ( 792, 
'Coordinate_Operation', 
19962, 
7302, 
'ITM', 
'' ); 

INSERT INTO epsg_alias VALUES ( 793, 
'Coordinate Reference System', 
20004, 
7302, 
'S-95 zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 794, 
'Coordinate Reference System', 
20005, 
7302, 
'S-95 zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 795, 
'Coordinate Reference System', 
20006, 
7302, 
'S-95 zone 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 796, 
'Coordinate Reference System', 
20007, 
7302, 
'S-95 zone 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 797, 
'Coordinate Reference System', 
20008, 
7302, 
'S-95 zone 8', 
'' ); 

INSERT INTO epsg_alias VALUES ( 798, 
'Coordinate Reference System', 
20009, 
7302, 
'S-95 zone 9', 
'' ); 

INSERT INTO epsg_alias VALUES ( 799, 
'Coordinate Reference System', 
20010, 
7302, 
'S-95 zone 10', 
'' ); 

INSERT INTO epsg_alias VALUES ( 800, 
'Coordinate Reference System', 
20011, 
7302, 
'S-95 zone 11', 
'' ); 

INSERT INTO epsg_alias VALUES ( 801, 
'Coordinate Reference System', 
20012, 
7302, 
'S-95 zone 12', 
'' ); 

INSERT INTO epsg_alias VALUES ( 802, 
'Coordinate Reference System', 
20013, 
7302, 
'S-95 zone 13', 
'' ); 

INSERT INTO epsg_alias VALUES ( 803, 
'Coordinate Reference System', 
20014, 
7302, 
'S-95 zone 14', 
'' ); 

INSERT INTO epsg_alias VALUES ( 804, 
'Coordinate Reference System', 
20015, 
7302, 
'S-95 zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 805, 
'Coordinate Reference System', 
20016, 
7302, 
'S-95 zone 16', 
'' ); 

INSERT INTO epsg_alias VALUES ( 806, 
'Coordinate Reference System', 
20017, 
7302, 
'S-95 zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 807, 
'Coordinate Reference System', 
20018, 
7302, 
'S-95 zone 18', 
'' ); 

INSERT INTO epsg_alias VALUES ( 808, 
'Coordinate Reference System', 
20019, 
7302, 
'S-95 zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 809, 
'Coordinate Reference System', 
20020, 
7302, 
'S-95 zone 20', 
'' ); 

INSERT INTO epsg_alias VALUES ( 810, 
'Coordinate Reference System', 
20021, 
7302, 
'S-95 zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 811, 
'Coordinate Reference System', 
20022, 
7302, 
'S-95 zone 22', 
'' ); 

INSERT INTO epsg_alias VALUES ( 812, 
'Coordinate Reference System', 
20023, 
7302, 
'S-95 zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 813, 
'Coordinate Reference System', 
20024, 
7302, 
'S-95 zone 24', 
'' ); 

INSERT INTO epsg_alias VALUES ( 814, 
'Coordinate Reference System', 
20025, 
7302, 
'S-95 zone 25', 
'' ); 

INSERT INTO epsg_alias VALUES ( 815, 
'Coordinate Reference System', 
20026, 
7302, 
'S-95 zone 26', 
'' ); 

INSERT INTO epsg_alias VALUES ( 816, 
'Coordinate Reference System', 
20027, 
7302, 
'S-95 zone 27', 
'' ); 

INSERT INTO epsg_alias VALUES ( 817, 
'Coordinate Reference System', 
20028, 
7302, 
'S-95 zone 28', 
'' ); 

INSERT INTO epsg_alias VALUES ( 818, 
'Coordinate Reference System', 
20029, 
7302, 
'S-95 zone 29', 
'' ); 

INSERT INTO epsg_alias VALUES ( 819, 
'Coordinate Reference System', 
20030, 
7302, 
'S-95 zone 30', 
'' ); 

INSERT INTO epsg_alias VALUES ( 820, 
'Coordinate Reference System', 
20031, 
7302, 
'S-95 zone 31', 
'' ); 

INSERT INTO epsg_alias VALUES ( 821, 
'Coordinate Reference System', 
20032, 
7302, 
'S-95 zone 32', 
'' ); 

INSERT INTO epsg_alias VALUES ( 822, 
'Coordinate Reference System', 
20064, 
7302, 
'Pulkovo 1995 / Gauss 4N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 823, 
'Coordinate Reference System', 
20065, 
7302, 
'Pulkovo 1995 / Gauss 5N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 824, 
'Coordinate Reference System', 
20066, 
7302, 
'Pulkovo 1995 / Gauss 6N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 825, 
'Coordinate Reference System', 
20067, 
7302, 
'Pulkovo 1995 / Gauss 7N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 826, 
'Coordinate Reference System', 
20068, 
7302, 
'Pulkovo 1995 / Gauss 8N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 827, 
'Coordinate Reference System', 
20069, 
7302, 
'Pulkovo 1995 / Gauss 9N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 828, 
'Coordinate Reference System', 
20070, 
7302, 
'Pulkovo 1995 / Gauss 10N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 829, 
'Coordinate Reference System', 
20071, 
7302, 
'Pulkovo 1995 / Gauss 11N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 830, 
'Coordinate Reference System', 
20072, 
7302, 
'Pulkovo 1995 / Gauss 12N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 831, 
'Coordinate Reference System', 
20073, 
7302, 
'Pulkovo 1995 / Gauss 13N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 832, 
'Coordinate Reference System', 
20074, 
7302, 
'Pulkovo 1995 / Gauss 14N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 833, 
'Coordinate Reference System', 
20075, 
7302, 
'Pulkovo 1995 / Gauss 15N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 834, 
'Coordinate Reference System', 
20076, 
7302, 
'Pulkovo 1995 / Gauss 16N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 835, 
'Coordinate Reference System', 
20077, 
7302, 
'Pulkovo 1995 / Gauss 17N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 836, 
'Coordinate Reference System', 
20078, 
7302, 
'Pulkovo 1995 / Gauss 18N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 837, 
'Coordinate Reference System', 
20079, 
7302, 
'Pulkovo 1995 / Gauss 19N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 838, 
'Coordinate Reference System', 
20080, 
7302, 
'Pulkovo 1995 / Gauss 20N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 839, 
'Coordinate Reference System', 
20081, 
7302, 
'Pulkovo 1995 / Gauss 21N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 840, 
'Coordinate Reference System', 
20082, 
7302, 
'Pulkovo 1995 / Gauss 22N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 841, 
'Coordinate Reference System', 
20083, 
7302, 
'Pulkovo 1995 / Gauss 23N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 842, 
'Coordinate Reference System', 
20084, 
7302, 
'Pulkovo 1995 / Gauss 24N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 843, 
'Coordinate Reference System', 
20085, 
7302, 
'Pulkovo 1995 / Gauss 25N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 844, 
'Coordinate Reference System', 
20086, 
7302, 
'Pulkovo 1995 / Gauss 26N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 845, 
'Coordinate Reference System', 
20087, 
7302, 
'Pulkovo 1995 / Gauss 27N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 846, 
'Coordinate Reference System', 
20088, 
7302, 
'Pulkovo 1995 / Gauss 28N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 847, 
'Coordinate Reference System', 
20089, 
7302, 
'Pulkovo 1995 / Gauss 29N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 848, 
'Coordinate Reference System', 
20090, 
7302, 
'Pulkovo 1995 / Gauss 30N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 849, 
'Coordinate Reference System', 
20091, 
7302, 
'Pulkovo 1995 / Gauss 31N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 850, 
'Coordinate Reference System', 
20092, 
7302, 
'Pulkovo 1995 / Gauss 32N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 851, 
'Coordinate Reference System', 
20437, 
7302, 
'Ain el Abd / UTM 37N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 852, 
'Coordinate Reference System', 
20438, 
7302, 
'Ain el Abd / UTM 38N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 853, 
'Coordinate Reference System', 
20439, 
7302, 
'Ain el Abd / UTM 39N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 854, 
'Coordinate Reference System', 
20790, 
7302, 
'Lisbon / Portuguese Nat', 
'' ); 

INSERT INTO epsg_alias VALUES ( 855, 
'Coordinate Reference System', 
21100, 
7302, 
'Batavia / NEIEZ', 
'' ); 

INSERT INTO epsg_alias VALUES ( 856, 
'Coordinate Reference System', 
21291, 
7302, 
'Barbados 1938 / BWI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 857, 
'Coordinate Reference System', 
21292, 
7302, 
'Barbados NationaI Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 858, 
'Coordinate Reference System', 
21413, 
7302, 
'Beijing / GK zone 13', 
'' ); 

INSERT INTO epsg_alias VALUES ( 859, 
'Coordinate Reference System', 
21414, 
7302, 
'Beijing / GK zone 14', 
'' ); 

INSERT INTO epsg_alias VALUES ( 860, 
'Coordinate Reference System', 
21415, 
7302, 
'Beijing / GK zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 861, 
'Coordinate Reference System', 
21416, 
7302, 
'Beijing / GK zone 16', 
'' ); 

INSERT INTO epsg_alias VALUES ( 862, 
'Coordinate Reference System', 
21417, 
7302, 
'Beijing / GK zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 863, 
'Coordinate Reference System', 
21418, 
7302, 
'Beijing / GK zone 18', 
'' ); 

INSERT INTO epsg_alias VALUES ( 864, 
'Coordinate Reference System', 
21419, 
7302, 
'Beijing / GK zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 865, 
'Coordinate Reference System', 
21420, 
7302, 
'Beijing / GK zone 20', 
'' ); 

INSERT INTO epsg_alias VALUES ( 866, 
'Coordinate Reference System', 
21421, 
7302, 
'Beijing / GK zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 867, 
'Coordinate Reference System', 
21422, 
7302, 
'Beijing / GK zone 22', 
'' ); 

INSERT INTO epsg_alias VALUES ( 868, 
'Coordinate Reference System', 
21423, 
7302, 
'Beijing / GK zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 869, 
'Coordinate Reference System', 
21473, 
7302, 
'Beijing / Gauss 13N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 870, 
'Coordinate Reference System', 
21474, 
7302, 
'Beijing / Gauss 14N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 871, 
'Coordinate Reference System', 
21475, 
7302, 
'Beijing / Gauss 15N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 872, 
'Coordinate Reference System', 
21476, 
7302, 
'Beijing / Gauss 16N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 873, 
'Coordinate Reference System', 
21477, 
7302, 
'Beijing / Gauss 17N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 874, 
'Coordinate Reference System', 
21478, 
7302, 
'Beijing / Gauss 18N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 875, 
'Coordinate Reference System', 
21479, 
7302, 
'Beijing / Gauss 19N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 876, 
'Coordinate Reference System', 
21480, 
7302, 
'Beijing / Gauss 20N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 877, 
'Coordinate Reference System', 
21481, 
7302, 
'Beijing / Gauss 21N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 878, 
'Coordinate Reference System', 
21482, 
7302, 
'Beijing / Gauss 22N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 879, 
'Coordinate Reference System', 
21483, 
7302, 
'Beijing / Gauss 23N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 880, 
'Coordinate Reference System', 
21500, 
7302, 
'Belge Lambert 50', 
'' ); 

INSERT INTO epsg_alias VALUES ( 881, 
'Coordinate Reference System', 
21780, 
7302, 
'LV03C', 
'' ); 

INSERT INTO epsg_alias VALUES ( 882, 
'Coordinate Reference System', 
21781, 
7302, 
'LV03', 
'' ); 

INSERT INTO epsg_alias VALUES ( 883, 
'Coordinate Reference System', 
21891, 
7302, 
'Bogota / Colombia 3W', 
'' ); 

INSERT INTO epsg_alias VALUES ( 884, 
'Coordinate Reference System', 
21892, 
7302, 
'Bogota / Colombia Bogota', 
'' ); 

INSERT INTO epsg_alias VALUES ( 885, 
'Coordinate Reference System', 
21893, 
7302, 
'Bogota / Colombia 3E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 886, 
'Coordinate Reference System', 
21894, 
7302, 
'Bogota / Colombia 6E', 
'' ); 

INSERT INTO epsg_alias VALUES ( 887, 
'Coordinate Reference System', 
22191, 
7302, 
'C Inchauspe /Argentina 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 888, 
'Coordinate Reference System', 
22192, 
7302, 
'C Inchauspe /Argentina 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 889, 
'Coordinate Reference System', 
22193, 
7302, 
'C Inchauspe /Argentina 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 890, 
'Coordinate Reference System', 
22194, 
7302, 
'C Inchauspe /Argentina 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 891, 
'Coordinate Reference System', 
22195, 
7302, 
'C Inchauspe /Argentina 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 892, 
'Coordinate Reference System', 
22196, 
7302, 
'C Inchauspe /Argentina 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 893, 
'Coordinate Reference System', 
22197, 
7302, 
'C Inchauspe /Argentina 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 894, 
'Coordinate Reference System', 
22275, 
7302, 
'South African CS zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 895, 
'Coordinate Reference System', 
22277, 
7302, 
'South African CS zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 896, 
'Coordinate Reference System', 
22279, 
7302, 
'South African CS zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 897, 
'Coordinate Reference System', 
22281, 
7302, 
'South African CS zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 898, 
'Coordinate Reference System', 
22283, 
7302, 
'South African CS zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 899, 
'Coordinate Reference System', 
22285, 
7302, 
'South African CS zone 25', 
'' ); 

INSERT INTO epsg_alias VALUES ( 900, 
'Coordinate Reference System', 
22287, 
7302, 
'South African CS zone 27', 
'' ); 

INSERT INTO epsg_alias VALUES ( 901, 
'Coordinate Reference System', 
22289, 
7302, 
'South African CS zone 29', 
'' ); 

INSERT INTO epsg_alias VALUES ( 902, 
'Coordinate Reference System', 
22291, 
7302, 
'South African CS zone 31', 
'' ); 

INSERT INTO epsg_alias VALUES ( 903, 
'Coordinate Reference System', 
22293, 
7302, 
'South African CS zone 33', 
'' ); 

INSERT INTO epsg_alias VALUES ( 904, 
'Coordinate Reference System', 
22300, 
7302, 
'Tunisia Mining Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 905, 
'Coordinate Reference System', 
22523, 
7302, 
'Corrego Alegre / UTM 23S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 906, 
'Coordinate Reference System', 
22524, 
7302, 
'Corrego Alegre / UTM 24S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 907, 
'Coordinate Reference System', 
22994, 
7302, 
'Egypt 1907 / Ext. Purple', 
'' ); 

INSERT INTO epsg_alias VALUES ( 908, 
'Coordinate Reference System', 
23946, 
7302, 
'Indian 1954 / UTM 46N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 909, 
'Coordinate Reference System', 
23947, 
7302, 
'Indian 1954 / UTM 47N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 910, 
'Coordinate Reference System', 
23948, 
7302, 
'Indian 1954 / UTM 48N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 911, 
'Coordinate Reference System', 
24047, 
7302, 
'Indian 1975 / UTM 47N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 912, 
'Coordinate Reference System', 
24048, 
7302, 
'Indian 1975 / UTM 48N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 913, 
'Coordinate Reference System', 
24100, 
7302, 
'Jamaica 1875 / Old Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 914, 
'Coordinate Reference System', 
24200, 
7302, 
'JAD69 / Jamaica Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 915, 
'Coordinate Reference System', 
24305, 
7302, 
'Kalianpur 37 / UTM 45N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 916, 
'Coordinate Reference System', 
24306, 
7302, 
'Kalianpur 37 / UTM 46N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 917, 
'Coordinate Reference System', 
24311, 
7302, 
'Kalianpur 62 / UTM 41N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 918, 
'Coordinate Reference System', 
24312, 
7302, 
'Kalianpur 62 / UTM 42N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 919, 
'Coordinate Reference System', 
24313, 
7302, 
'Kalianpur 62 / UTM 43N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 920, 
'Coordinate Reference System', 
24342, 
7302, 
'Kalianpur 75 / UTM 42N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 921, 
'Coordinate Reference System', 
24343, 
7302, 
'Kalianpur 75 / UTM 43N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 922, 
'Coordinate Reference System', 
24344, 
7302, 
'Kalianpur 75 / UTM 44N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 923, 
'Coordinate Reference System', 
24345, 
7302, 
'Kalianpur 75 / UTM 45N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 924, 
'Coordinate Reference System', 
24346, 
7302, 
'Kalianpur 75 / UTM 46N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 925, 
'Coordinate Reference System', 
24347, 
7302, 
'Kalianpur 75 / UTM 47N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 926, 
'Coordinate Reference System', 
24370, 
7302, 
'Kalianpur / India 0', 
'' ); 

INSERT INTO epsg_alias VALUES ( 927, 
'Coordinate Reference System', 
24371, 
7302, 
'Kalianpur / India I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 928, 
'Coordinate Reference System', 
24372, 
7302, 
'Kalianpur / India IIa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 929, 
'Coordinate Reference System', 
24373, 
7302, 
'Kalianpur / India III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 930, 
'Coordinate Reference System', 
24374, 
7302, 
'Kalianpur / India IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 931, 
'Coordinate Reference System', 
24375, 
7302, 
'Kalianpur 37 / India IIb', 
'' ); 

INSERT INTO epsg_alias VALUES ( 932, 
'Coordinate Reference System', 
24376, 
7302, 
'Kalianpur 62 / India I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 933, 
'Coordinate Reference System', 
24377, 
7302, 
'Kalianpur 62 / India IIa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 934, 
'Coordinate Reference System', 
24378, 
7302, 
'Kalianpur 75 / India I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 935, 
'Coordinate Reference System', 
24379, 
7302, 
'Kalianpur 75 / India IIa', 
'' ); 

INSERT INTO epsg_alias VALUES ( 936, 
'Coordinate Reference System', 
24380, 
7302, 
'Kalianpur 75 / India IIb', 
'' ); 

INSERT INTO epsg_alias VALUES ( 937, 
'Coordinate Reference System', 
24381, 
7302, 
'Kalianpur 75 / India III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 938, 
'Coordinate Reference System', 
24382, 
7302, 
'Kalianpur / India IIb', 
'' ); 

INSERT INTO epsg_alias VALUES ( 939, 
'Coordinate Reference System', 
24383, 
7302, 
'Kalianpur 75 / India IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 940, 
'Coordinate Reference System', 
24892, 
7302, 
'PSAD56 / Peru central', 
'' ); 

INSERT INTO epsg_alias VALUES ( 941, 
'Coordinate Reference System', 
25000, 
7302, 
'Leigon / Ghana Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 942, 
'Coordinate Reference System', 
25391, 
7302, 
'Luzon / Philippines I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 943, 
'Coordinate Reference System', 
25392, 
7302, 
'Luzon / Philippines II', 
'' ); 

INSERT INTO epsg_alias VALUES ( 944, 
'Coordinate Reference System', 
25393, 
7302, 
'Luzon / Philippines III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 945, 
'Coordinate Reference System', 
25394, 
7302, 
'Luzon / Philippines IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 946, 
'Coordinate Reference System', 
25395, 
7302, 
'Luzon / Philippines V', 
'' ); 

INSERT INTO epsg_alias VALUES ( 947, 
'Coordinate Reference System', 
25700, 
7302, 
'Makassar / NEIEZ', 
'' ); 

INSERT INTO epsg_alias VALUES ( 948, 
'Coordinate Reference System', 
25932, 
7302, 
'Malongo 1987 / UTM 32S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 949, 
'Coordinate Reference System', 
26391, 
7302, 
'Minna / Nigeria West', 
'' ); 

INSERT INTO epsg_alias VALUES ( 950, 
'Coordinate Reference System', 
26393, 
7302, 
'Minna / Nigeria East', 
'' ); 

INSERT INTO epsg_alias VALUES ( 951, 
'Coordinate Reference System', 
26591, 
7302, 
'Monte Mario / Italy 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 952, 
'Coordinate Reference System', 
26592, 
7302, 
'Monte Mario / Italy 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 953, 
'Coordinate Reference System', 
26632, 
7302, 
'M''poraloko / UTM 32N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 954, 
'Coordinate Reference System', 
26692, 
7302, 
'M''poraloko / UTM 32S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 955, 
'Coordinate Reference System', 
26741, 
7302, 
'NAD27 / California I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 956, 
'Coordinate Reference System', 
26742, 
7302, 
'NAD27 / California II', 
'' ); 

INSERT INTO epsg_alias VALUES ( 957, 
'Coordinate Reference System', 
26743, 
7302, 
'NAD27 / California III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 958, 
'Coordinate Reference System', 
26744, 
7302, 
'NAD27 / California IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 959, 
'Coordinate Reference System', 
26745, 
7302, 
'NAD27 / California V', 
'' ); 

INSERT INTO epsg_alias VALUES ( 960, 
'Coordinate Reference System', 
26746, 
7302, 
'NAD27 / California VI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 961, 
'Coordinate Reference System', 
26747, 
7302, 
'NAD27 / California VII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 962, 
'Coordinate Reference System', 
26786, 
7302, 
'NAD27 / Massachusetts', 
'' ); 

INSERT INTO epsg_alias VALUES ( 963, 
'Coordinate Reference System', 
26787, 
7302, 
'NAD27 / Massachusetts Is', 
'' ); 

INSERT INTO epsg_alias VALUES ( 964, 
'Coordinate Reference System', 
26792, 
7302, 
'NAD27 / Minnesota Cent.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 965, 
'Coordinate Reference System', 
26801, 
7302, 
'NAD27 / Michigan East', 
'' ); 

INSERT INTO epsg_alias VALUES ( 966, 
'Coordinate Reference System', 
26802, 
7302, 
'NAD27 / Michigan Old Cen', 
'' ); 

INSERT INTO epsg_alias VALUES ( 967, 
'Coordinate Reference System', 
26803, 
7302, 
'NAD27 / Michigan West', 
'' ); 

INSERT INTO epsg_alias VALUES ( 968, 
'Coordinate Reference System', 
26811, 
7302, 
'NAD27 / Michigan North', 
'' ); 

INSERT INTO epsg_alias VALUES ( 969, 
'Coordinate Reference System', 
26812, 
7302, 
'NAD27 / Michigan Central', 
'' ); 

INSERT INTO epsg_alias VALUES ( 970, 
'Coordinate Reference System', 
26813, 
7302, 
'NAD27 / Michigan South', 
'' ); 

INSERT INTO epsg_alias VALUES ( 971, 
'Coordinate Reference System', 
26941, 
7302, 
'NAD83 / California 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 972, 
'Coordinate Reference System', 
26942, 
7302, 
'NAD83 / California 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 973, 
'Coordinate Reference System', 
26943, 
7302, 
'NAD83 / California 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 974, 
'Coordinate Reference System', 
26944, 
7302, 
'NAD83 / California 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 975, 
'Coordinate Reference System', 
26945, 
7302, 
'NAD83 / California 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 976, 
'Coordinate Reference System', 
26946, 
7302, 
'NAD83 / California 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 977, 
'Coordinate Reference System', 
26986, 
7302, 
'NAD83 / Massachusetts', 
'' ); 

INSERT INTO epsg_alias VALUES ( 978, 
'Coordinate Reference System', 
26987, 
7302, 
'NAD83 / Massachusetts Is', 
'' ); 

INSERT INTO epsg_alias VALUES ( 979, 
'Coordinate Reference System', 
26992, 
7302, 
'NAD83 / Minnesota Cent.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 980, 
'Coordinate Reference System', 
27038, 
7302, 
'Nahrwan 1967 / UTM 38N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 981, 
'Coordinate Reference System', 
27039, 
7302, 
'Nahrwan 1967 / UTM 39N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 982, 
'Coordinate Reference System', 
27040, 
7302, 
'Nahrwan 1967 / UTM 40N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 983, 
'Coordinate Reference System', 
27120, 
7302, 
'Naparima 1972 / UTM 20N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 984, 
'Coordinate Reference System', 
27200, 
7302, 
'NZGD49 / NZ Map Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 985, 
'Coordinate Reference System', 
27391, 
7302, 
'NGO 1948 / I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 986, 
'Coordinate Reference System', 
27392, 
7302, 
'NGO 1948 / II', 
'' ); 

INSERT INTO epsg_alias VALUES ( 987, 
'Coordinate Reference System', 
27393, 
7302, 
'NGO 1948 / III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 988, 
'Coordinate Reference System', 
27394, 
7302, 
'NGO 1948 / IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 989, 
'Coordinate Reference System', 
27395, 
7302, 
'NGO 1948 / V', 
'' ); 

INSERT INTO epsg_alias VALUES ( 990, 
'Coordinate Reference System', 
27396, 
7302, 
'NGO 1948 / VI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 991, 
'Coordinate Reference System', 
27397, 
7302, 
'NGO 1948 / VII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 992, 
'Coordinate Reference System', 
27398, 
7302, 
'NGO 1948 / VIII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 993, 
'Coordinate Reference System', 
27500, 
7302, 
'ATF / Nord de Guerre', 
'' ); 

INSERT INTO epsg_alias VALUES ( 994, 
'Coordinate Reference System', 
27581, 
7302, 
'NTF / France I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 995, 
'Coordinate Reference System', 
27582, 
7302, 
'NTF / France II', 
'' ); 

INSERT INTO epsg_alias VALUES ( 996, 
'Coordinate Reference System', 
27583, 
7302, 
'NTF / France III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 997, 
'Coordinate Reference System', 
27584, 
7302, 
'NTF / France IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 998, 
'Coordinate Reference System', 
27591, 
7302, 
'NTF / Nord France', 
'' ); 

INSERT INTO epsg_alias VALUES ( 999, 
'Coordinate Reference System', 
27592, 
7302, 
'NTF / Centre France', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1000, 
'Coordinate Reference System', 
27593, 
7302, 
'NTF / Sud France', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1001, 
'Coordinate Reference System', 
27594, 
7302, 
'NTF / Corse', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1002, 
'Coordinate Reference System', 
27700, 
7302, 
'British National Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1003, 
'Coordinate Reference System', 
28191, 
7302, 
'Palestine Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1004, 
'Coordinate Reference System', 
28192, 
7302, 
'Palestine Belt', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1005, 
'Coordinate Reference System', 
28193, 
7302, 
'Israeli CS Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1006, 
'Coordinate Reference System', 
28232, 
7302, 
'Point Noire / UTM 32S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1009, 
'Coordinate Reference System', 
28404, 
7317, 
'S-42 zone 4', 
'This name is ambiguous as it is also used for other CRSs.' ); 

INSERT INTO epsg_alias VALUES ( 1010, 
'Coordinate Reference System', 
28405, 
7317, 
'S-42 zone 5', 
'This name is ambiguous as it is also used for other CRSs.' ); 

INSERT INTO epsg_alias VALUES ( 1011, 
'Coordinate Reference System', 
28406, 
7317, 
'S-42 zone 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1012, 
'Coordinate Reference System', 
28407, 
7317, 
'S-42 zone 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1013, 
'Coordinate Reference System', 
28408, 
7317, 
'S-42 zone 8', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1014, 
'Coordinate Reference System', 
28409, 
7317, 
'S-42 zone 9', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1015, 
'Coordinate Reference System', 
28410, 
7317, 
'S-42 zone 10', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1016, 
'Coordinate Reference System', 
28411, 
7317, 
'S-42 zone 11', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1017, 
'Coordinate Reference System', 
28412, 
7317, 
'S-42 zone 12', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1018, 
'Coordinate Reference System', 
28413, 
7317, 
'S-42 zone 13', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1019, 
'Coordinate Reference System', 
28414, 
7317, 
'S-42 zone 14', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1020, 
'Coordinate Reference System', 
28415, 
7317, 
'S-42 zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1021, 
'Coordinate Reference System', 
28416, 
7317, 
'S-42 zone 16', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1022, 
'Coordinate Reference System', 
28417, 
7317, 
'S-42 zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1023, 
'Coordinate Reference System', 
28418, 
7317, 
'S-42 zone 18', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1024, 
'Coordinate Reference System', 
28419, 
7317, 
'S-42 zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1025, 
'Coordinate Reference System', 
28420, 
7317, 
'S-42 zone 20', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1026, 
'Coordinate Reference System', 
28421, 
7317, 
'S-42 zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1027, 
'Coordinate Reference System', 
28422, 
7317, 
'S-42 zone 22', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1028, 
'Coordinate Reference System', 
28423, 
7317, 
'S-42 zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1029, 
'Coordinate Reference System', 
28424, 
7317, 
'S-42 zone 24', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1030, 
'Coordinate Reference System', 
28425, 
7317, 
'S-42 zone 25', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1031, 
'Coordinate Reference System', 
28426, 
7317, 
'S-42 zone 26', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1032, 
'Coordinate Reference System', 
28427, 
7317, 
'S-42 zone 27', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1033, 
'Coordinate Reference System', 
28428, 
7317, 
'S-42 zone 28', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1034, 
'Coordinate Reference System', 
28429, 
7317, 
'S-42 zone 29', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1035, 
'Coordinate Reference System', 
28430, 
7317, 
'S-42 zone 30', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1036, 
'Coordinate Reference System', 
28431, 
7317, 
'S-42 zone 31', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1037, 
'Coordinate Reference System', 
28432, 
7317, 
'S-42 zone 32', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1038, 
'Coordinate Reference System', 
28462, 
7302, 
'Pulkovo / Gauss 2N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1039, 
'Coordinate Reference System', 
28463, 
7302, 
'Pulkovo / Gauss 3N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1040, 
'Coordinate Reference System', 
28464, 
7302, 
'Pulkovo / Gauss 4N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1041, 
'Coordinate Reference System', 
28465, 
7302, 
'Pulkovo / Gauss 5N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1042, 
'Coordinate Reference System', 
28466, 
7302, 
'Pulkovo / Gauss 6N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1043, 
'Coordinate Reference System', 
28467, 
7302, 
'Pulkovo / Gauss 7N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1044, 
'Coordinate Reference System', 
28468, 
7302, 
'Pulkovo / Gauss 8N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1045, 
'Coordinate Reference System', 
28469, 
7302, 
'Pulkovo / Gauss 9N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1046, 
'Coordinate Reference System', 
28470, 
7302, 
'Pulkovo / Gauss 10N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1047, 
'Coordinate Reference System', 
28471, 
7302, 
'Pulkovo / Gauss 11N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1048, 
'Coordinate Reference System', 
28472, 
7302, 
'Pulkovo / Gauss 12N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1049, 
'Coordinate Reference System', 
28473, 
7302, 
'Pulkovo / Gauss 13N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1050, 
'Coordinate Reference System', 
28474, 
7302, 
'Pulkovo / Gauss 14N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1051, 
'Coordinate Reference System', 
28475, 
7302, 
'Pulkovo / Gauss 15N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1052, 
'Coordinate Reference System', 
28476, 
7302, 
'Pulkovo / Gauss 16N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1053, 
'Coordinate Reference System', 
28477, 
7302, 
'Pulkovo / Gauss 17N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1054, 
'Coordinate Reference System', 
28478, 
7302, 
'Pulkovo / Gauss 18N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1055, 
'Coordinate Reference System', 
28479, 
7302, 
'Pulkovo / Gauss 19N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1056, 
'Coordinate Reference System', 
28480, 
7302, 
'Pulkovo / Gauss 20N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1057, 
'Coordinate Reference System', 
28481, 
7302, 
'Pulkovo / Gauss 21N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1058, 
'Coordinate Reference System', 
28482, 
7302, 
'Pulkovo / Gauss 22N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1059, 
'Coordinate Reference System', 
28483, 
7302, 
'Pulkovo / Gauss 23N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1060, 
'Coordinate Reference System', 
28484, 
7302, 
'Pulkovo / Gauss 24N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1061, 
'Coordinate Reference System', 
28485, 
7302, 
'Pulkovo / Gauss 25N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1062, 
'Coordinate Reference System', 
28486, 
7302, 
'Pulkovo / Gauss 26N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1063, 
'Coordinate Reference System', 
28487, 
7302, 
'Pulkovo / Gauss 27N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1064, 
'Coordinate Reference System', 
28488, 
7302, 
'Pulkovo / Gauss 28N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1065, 
'Coordinate Reference System', 
28489, 
7302, 
'Pulkovo / Gauss 29N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1066, 
'Coordinate Reference System', 
28490, 
7302, 
'Pulkovo / Gauss 30N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1067, 
'Coordinate Reference System', 
28491, 
7302, 
'Pulkovo / Gauss 31N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1068, 
'Coordinate Reference System', 
28492, 
7302, 
'Pulkovo / Gauss 32N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1069, 
'Coordinate Reference System', 
28600, 
7302, 
'Qatar National Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1070, 
'Coordinate Reference System', 
29220, 
7302, 
'Sapper Hill / UTM 20S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1071, 
'Coordinate Reference System', 
29221, 
7302, 
'Sapper Hill / UTM 21S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1072, 
'Coordinate Reference System', 
29333, 
7302, 
'Schwarzeck / UTM 33S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1073, 
'Coordinate Reference System', 
29371, 
7302, 
'SW African CS zone 11', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1074, 
'Coordinate Reference System', 
29373, 
7302, 
'SW African CS zone 13', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1075, 
'Coordinate Reference System', 
29375, 
7302, 
'SW African CS zone 15', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1076, 
'Coordinate Reference System', 
29377, 
7302, 
'SW African CS zone 17', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1077, 
'Coordinate Reference System', 
29379, 
7302, 
'SW African CS zone 19', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1078, 
'Coordinate Reference System', 
29381, 
7302, 
'SW African CS zone 21', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1079, 
'Coordinate Reference System', 
29383, 
7302, 
'SW African CS zone 23', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1080, 
'Coordinate Reference System', 
29385, 
7302, 
'SW African CS zone 25', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1081, 
'Coordinate Reference System', 
29700, 
7302, 
'Tananarive  / Laborde', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1082, 
'Coordinate Reference System', 
29738, 
7302, 
'Tananarive / UTM 38S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1083, 
'Coordinate Reference System', 
29739, 
7302, 
'Tananarive / UTM 39S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1084, 
'Coordinate Reference System', 
29849, 
7302, 
'Timbalai 1948 / UTM 49N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1085, 
'Coordinate Reference System', 
29850, 
7302, 
'Timbalai 1948 / UTM 50N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1086, 
'Coordinate Reference System', 
29871, 
7302, 
'Timbalai  / Borneo (ch)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1087, 
'Coordinate Reference System', 
29872, 
7302, 
'Timbalai  / Borneo (ft)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1088, 
'Coordinate Reference System', 
29873, 
7302, 
'Timbalai  / Borneo (m)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1089, 
'Coordinate Reference System', 
29900, 
7302, 
'TM65 / Irish Nat Grid', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1090, 
'Coordinate Reference System', 
30161, 
7302, 
'Tokyo / Japan zone I', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1091, 
'Coordinate Reference System', 
30162, 
7302, 
'Tokyo / Japan zone II', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1092, 
'Coordinate Reference System', 
30163, 
7302, 
'Tokyo / Japan zone III', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1093, 
'Coordinate Reference System', 
30164, 
7302, 
'Tokyo / Japan zone IV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1094, 
'Coordinate Reference System', 
30165, 
7302, 
'Tokyo / Japan zone V', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1095, 
'Coordinate Reference System', 
30166, 
7302, 
'Tokyo / Japan zone VI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1096, 
'Coordinate Reference System', 
30167, 
7302, 
'Tokyo / Japan zone VII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1097, 
'Coordinate Reference System', 
30168, 
7302, 
'Tokyo / Japan zone VIII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1098, 
'Coordinate Reference System', 
30169, 
7302, 
'Tokyo / Japan zone IX', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1099, 
'Coordinate Reference System', 
30170, 
7302, 
'Tokyo / Japan zone X', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1100, 
'Coordinate Reference System', 
30171, 
7302, 
'Tokyo / Japan zone XI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1101, 
'Coordinate Reference System', 
30172, 
7302, 
'Tokyo / Japan zone XII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1102, 
'Coordinate Reference System', 
30173, 
7302, 
'Tokyo / Japan zone XIII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1103, 
'Coordinate Reference System', 
30174, 
7302, 
'Tokyo / Japan zone XIV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1104, 
'Coordinate Reference System', 
30175, 
7302, 
'Tokyo / Japan zone XV', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1105, 
'Coordinate Reference System', 
30176, 
7302, 
'Tokyo / Japan zone XVI', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1106, 
'Coordinate Reference System', 
30177, 
7302, 
'Tokyo / Japan zone XVII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1107, 
'Coordinate Reference System', 
30178, 
7302, 
'Tokyo / Japan zone XVIII', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1108, 
'Coordinate Reference System', 
30200, 
7317, 
'Trinidad 1903 / Cassini', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1109, 
'Coordinate Reference System', 
30491, 
7302, 
'Voirol75 / N Algeria old', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1110, 
'Coordinate Reference System', 
30492, 
7302, 
'Voirol75 / S Algeria old', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1111, 
'Coordinate Reference System', 
30729, 
7302, 
'Nord Sahara / UTM 29N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1112, 
'Coordinate Reference System', 
30730, 
7302, 
'Nord Sahara / UTM 30N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1113, 
'Coordinate Reference System', 
30731, 
7302, 
'Nord Sahara / UTM 31N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1114, 
'Coordinate Reference System', 
30732, 
7302, 
'Nord Sahara / UTM 32N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1115, 
'Coordinate Reference System', 
30791, 
7302, 
'Nord Sahara / N Algerie', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1116, 
'Coordinate Reference System', 
30792, 
7302, 
'Nord Sahara / S Algerie', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1117, 
'Coordinate Reference System', 
31265, 
7302, 
'MGI / Gauss zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1118, 
'Coordinate Reference System', 
31266, 
7302, 
'MGI / Gauss zone 6', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1119, 
'Coordinate Reference System', 
31267, 
7302, 
'MGI / Gauss zone 7', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1120, 
'Coordinate Reference System', 
31268, 
7302, 
'MGI / Gauss zone 8', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1121, 
'Coordinate Reference System', 
31291, 
7302, 
'MGI / Austria West', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1122, 
'Coordinate Reference System', 
31292, 
7302, 
'MGI / Austria Central', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1123, 
'Coordinate Reference System', 
31293, 
7302, 
'MGI / Austria East', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1124, 
'Coordinate Reference System', 
31370, 
7301, 
'BD 72 / Lambert 72', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1125, 
'Coordinate Reference System', 
31461, 
7302, 
'DHDN / Gauss zone 1', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1126, 
'Coordinate Reference System', 
31462, 
7302, 
'DHDN / Gauss zone 2', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1127, 
'Coordinate Reference System', 
31463, 
7302, 
'DHDN / Gauss zone 3', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1128, 
'Coordinate Reference System', 
31464, 
7302, 
'DHDN / Gauss zone 4', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1129, 
'Coordinate Reference System', 
31465, 
7302, 
'DHDN / Gauss zone 5', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1130, 
'Coordinate Reference System', 
31600, 
7317, 
'Stereo 33', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1131, 
'Coordinate Reference System', 
31700, 
7302, 
'Stereo 70', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1132, 
'Coordinate Reference System', 
32013, 
7302, 
'NAD27 / New Mexico Cent.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1133, 
'Coordinate Reference System', 
32018, 
7302, 
'NAD27 / New York Long Is', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1134, 
'Coordinate Reference System', 
32020, 
7302, 
'NAD27 / North Dakota N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1135, 
'Coordinate Reference System', 
32021, 
7302, 
'NAD27 / North Dakota S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1136, 
'Coordinate Reference System', 
32028, 
7302, 
'NAD27 / Pennsylvania N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1137, 
'Coordinate Reference System', 
32029, 
7302, 
'NAD27 / Pennsylvania S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1138, 
'Coordinate Reference System', 
32031, 
7302, 
'NAD27 / South Carolina N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1139, 
'Coordinate Reference System', 
32033, 
7302, 
'NAD27 / South Carolina S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1140, 
'Coordinate Reference System', 
32034, 
7302, 
'NAD27 / South Dakota N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1141, 
'Coordinate Reference System', 
32035, 
7302, 
'NAD27 / South Dakota S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1142, 
'Coordinate Reference System', 
32038, 
7302, 
'NAD27 / Texas North Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1143, 
'Coordinate Reference System', 
32040, 
7302, 
'NAD27 / Texas South Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1144, 
'Coordinate Reference System', 
32050, 
7302, 
'NAD27 / West Virginia N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1145, 
'Coordinate Reference System', 
32051, 
7302, 
'NAD27 / West Virginia S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1146, 
'Coordinate Reference System', 
32053, 
7302, 
'NAD27 / Wisconsin Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1147, 
'Coordinate Reference System', 
32056, 
7302, 
'NAD27 / Wyoming E. Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1148, 
'Coordinate Reference System', 
32057, 
7302, 
'NAD27 / Wyoming W. Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1149, 
'Coordinate Reference System', 
32113, 
7302, 
'NAD83 / New Mexico Cent.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1150, 
'Coordinate Reference System', 
32118, 
7302, 
'NAD83 / New York Long Is', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1151, 
'Coordinate Reference System', 
32120, 
7302, 
'NAD83 / North Dakota N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1152, 
'Coordinate Reference System', 
32121, 
7302, 
'NAD83 / North Dakota S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1153, 
'Coordinate Reference System', 
32128, 
7302, 
'NAD83 / Pennsylvania N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1154, 
'Coordinate Reference System', 
32129, 
7302, 
'NAD83 / Pennsylvania S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1155, 
'Coordinate Reference System', 
32134, 
7302, 
'NAD83 / South Dakota N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1156, 
'Coordinate Reference System', 
32135, 
7302, 
'NAD83 / South Dakota S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1157, 
'Coordinate Reference System', 
32138, 
7302, 
'NAD83 / Texas North Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1158, 
'Coordinate Reference System', 
32140, 
7302, 
'NAD83 / Texas South Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1159, 
'Coordinate Reference System', 
32150, 
7302, 
'NAD83 / West Virginia N', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1160, 
'Coordinate Reference System', 
32151, 
7302, 
'NAD83 / West Virginia S', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1161, 
'Coordinate Reference System', 
32153, 
7302, 
'NAD83 / Wisconsin Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1162, 
'Coordinate Reference System', 
32156, 
7302, 
'NAD83 / Wyoming E. Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1163, 
'Coordinate Reference System', 
32157, 
7302, 
'NAD83 / Wyoming W. Cen.', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1164, 
'Unit of Measure', 
9001, 
7306, 
'm', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1165, 
'Unit of Measure', 
9002, 
7306, 
'ft', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1166, 
'Unit of Measure', 
9003, 
7306, 
'ftUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1167, 
'Unit of Measure', 
9005, 
7306, 
'ftCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1168, 
'Unit of Measure', 
9014, 
7306, 
'fathom', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1169, 
'Unit of Measure', 
9030, 
7306, 
'nautmi', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1170, 
'Unit of Measure', 
9031, 
7306, 
'mGer', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1171, 
'Unit of Measure', 
9033, 
7306, 
'chUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1172, 
'Unit of Measure', 
9034, 
7306, 
'lkUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1173, 
'Unit of Measure', 
9035, 
7306, 
'miUS', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1174, 
'Unit of Measure', 
9036, 
7306, 
'km', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1175, 
'Unit of Measure', 
9037, 
7306, 
'ydCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1176, 
'Unit of Measure', 
9038, 
7306, 
'chCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1177, 
'Unit of Measure', 
9039, 
7306, 
'lkCla', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1178, 
'Unit of Measure', 
9040, 
7306, 
'ydSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1179, 
'Unit of Measure', 
9041, 
7306, 
'ftSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1180, 
'Unit of Measure', 
9042, 
7306, 
'chSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1181, 
'Unit of Measure', 
9043, 
7306, 
'lkSe', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1182, 
'Unit of Measure', 
9050, 
7306, 
'ydBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1183, 
'Unit of Measure', 
9051, 
7306, 
'ftBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1184, 
'Unit of Measure', 
9052, 
7306, 
'chBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1185, 
'Unit of Measure', 
9053, 
7306, 
'lkBnA', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1186, 
'Unit of Measure', 
9060, 
7306, 
'ydBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1187, 
'Unit of Measure', 
9061, 
7306, 
'ftBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1188, 
'Unit of Measure', 
9062, 
7306, 
'chBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1189, 
'Unit of Measure', 
9063, 
7306, 
'lkBnB', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1190, 
'Unit of Measure', 
9070, 
7306, 
'ftBr(65)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1191, 
'Unit of Measure', 
9080, 
7306, 
'ftInd', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1192, 
'Unit of Measure', 
9081, 
7306, 
'ftInd(37)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1193, 
'Unit of Measure', 
9082, 
7306, 
'ftInd(62)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1194, 
'Unit of Measure', 
9083, 
7306, 
'ftInd(75)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1195, 
'Unit of Measure', 
9084, 
7306, 
'ydInd', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1196, 
'Unit of Measure', 
9085, 
7306, 
'ydInd(37)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1197, 
'Unit of Measure', 
9086, 
7306, 
'ydInd(62)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1198, 
'Unit of Measure', 
9087, 
7306, 
'ydInd(75)', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1199, 
'Unit of Measure', 
9093, 
7306, 
'mi', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1200, 
'Unit of Measure', 
9094, 
7306, 
'ftGC', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1201, 
'Unit of Measure', 
9101, 
7306, 
'rad', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1202, 
'Unit of Measure', 
9102, 
7306, 
'dega', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1203, 
'Unit of Measure', 
9103, 
7306, 
'mina', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1204, 
'Unit of Measure', 
9104, 
7306, 
'seca', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1205, 
'Unit of Measure', 
9105, 
7306, 
'gr', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1206, 
'Unit of Measure', 
9106, 
7306, 
'gon', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1207, 
'Unit of Measure', 
9107, 
7306, 
'dega', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1208, 
'Unit of Measure', 
9108, 
7306, 
'dega', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1209, 
'Unit of Measure', 
9109, 
7306, 
'urad', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1210, 
'Unit of Measure', 
9110, 
7306, 
'dega', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1211, 
'Unit of Measure', 
9111, 
7306, 
'dega', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1212, 
'Unit of Measure', 
9112, 
7306, 
'cgr', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1213, 
'Unit of Measure', 
9113, 
7306, 
'ccgr', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1214, 
'Unit of Measure', 
9114, 
7306, 
'mila', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1215, 
'Coordinate_Operation Method', 
9824, 
7302, 
'UTM grid system', 
'' ); 

INSERT INTO epsg_alias VALUES ( 1216, 
'Coordinate Reference System', 
4143, 
7301, 
'C