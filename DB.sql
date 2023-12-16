create database 	control;
use control;
select * from data_configs
CREATE TABLE IF NOT EXISTS data_configs (
    id INT PRIMARY KEY auto_increment,
    code VARCHAR(255),
    description VARCHAR(255),
    source_path VARCHAR(255),
    location VARCHAR(255),
    fileName VARCHAR(255),
    seperator VARCHAR(1),
    format VARCHAR(10),
    databaseNameStaging VARCHAR(255),
    databaseNameDatawarehouse VARCHAR(255),
    databaseNameMart VARCHAR(255),
    serverName VARCHAR(255),
    port VARCHAR(255),
    user VARCHAR(255),
    pass VARCHAR(255),
    tableNameStagingTemp  VARCHAR(255),
    columnsStagingTemp VARCHAR(255),
    TypeColumnsStagingTemp VARCHAR(255),
    created_at Timestamp default now(),
    updated_at Timestamp,
    create_by VARCHAR(255),
    update_by VARCHAR(255),
    flag bit
);

INSERT INTO data_configs (code,description,source_path,location,fileName,seperator,format,databaseNameStaging,databaseNameDatawarehouse,databaseNameMart,serverName,port,user, pass,tableNameStagingTemp,columnsStagingTemp,TypeColumnsStagingTemp,
    updated_at,create_by,update_by,flag) VALUES ('XS_TT','Config chứa dữ liệu của trang kqxs','kqxs.vn','D://data//','data',',','csv','Staging',
    'Datawarehouse','Mart','127.0.0.1',3306,'root','123456789','Lotteries_temp','(location,weekdays,date,prizes,num_result,region)','(VARCHAR(255), VARCHAR(255), VARCHAR(255), VARCHAR(255), VARCHAR(255),VARCHAR(255))',now(),'Kiên','Kiên',1);
select * from data_configs;
CREATE TABLE IF NOT EXISTS data_files (
    id INT PRIMARY KEY auto_increment,
    id_config INT,
    note VARCHAR(255),
    status VARCHAR(255),
    dateRun date,
    create_at Timestamp default now(),
    created_by_modul VARCHAR(255)
);

create database Staging;
use Staging;
select * from Lotteries_temp
create table Lotteries_temp(
	location TEXT,
    weekdays TEXT,
    date TEXT,
    prizes TEXT,
    num_result  TEXT,
    region TEXT
);
create table Lotteries_staging(
	id BIGINT auto_increment primary key,
    id_config BIGINT,
    location Text,
    weekdays Text,
    date date,
    prizes TEXT,
    num_result  TEXT,
    region TEXT,
    created_at TIMESTAMP default now(),
    updated_at TIMESTAMP
);

INSERT INTO data_files ( id_config, note, status,dateRun,create_at, created_by_modul)
VALUES
    (1, 'Bắt đầu lấy dữ liệu', 'CE', '2023-12-15','2023-12-15 17:31:00', 'EXTRACT')
 
select * from data_files;

SELECT * FROM config c join data_files d on c.id = d.id_config WHERE d.dateRun = '2023-11-23' ORDER BY create_at DESC LIMIT 1;
SELECT c.*, d.* FROM config c JOIN data_files d ON c.id = d.id_config WHERE c.flag = 1 and (d.id_config, d.create_at) IN 
(SELECT id_config, MAX(create_at) FROM data_files where id_config=id GROUP BY id_config);
truncate table data_files
-- get config
 DELIMITER //
CREATE PROCEDURE loadConfig(in id int, in date_Run date)
BEGIN
   SELECT  c.*, d.* FROM data_configs c JOIN data_files d ON c.id = d.id_config WHERE c.flag = 1 and (d.id_config, d.id) IN 
		(SELECT id_config,max(d.id) FROM data_files  d where id_config= id and dateRun = date_Run  GROUP BY id_config);
END //
DELIMITER ;
CALL loadConfig(1,'2023-12-15');
select * from data_files
DELIMITER //
CREATE PROCEDURE update_null()
BEGIN
    UPDATE Lotteries_temp
    SET
        location = COALESCE(location, 'default'),
        weekdays = COALESCE(weekdays, 'default'),
        date = COALESCE(date, 'default'),
        num_result = COALESCE(num_result, 'default'),
        prizes = COALESCE(prizes, 'default'),
        region = COALESCE(region, 'default')
    WHERE
        location IS NULL OR weekdays IS NULL OR date IS NULL OR num_result IS NULL OR prizes IS NULL OR region IS NULL;
END //
DELIMITER ;
use staging
select * from lotteries_staging
truncate table lotteries_staging
DELIMITER //
CREATE PROCEDURE moveTempToStaging()
BEGIN
    INSERT INTO lotteries_staging (location, weekdays, date, prizes, num_result, region)
	SELECT location, weekdays, STR_TO_DATE(date, '%d-%m-%Y'), prizes, num_result, region
	FROM lotteries_temp;
END //
DELIMITER ;
DELIMITER //
CREATE PROCEDURE insertDataFile(IN id int ,IN  dateRun date, in status VARCHAR(255),in note VARCHAR(255),in created_by_modul VARCHAR(255))
BEGIN
    INSERT INTO data_files (id_config,note,status,dateRun,created_by_modul) values (id, note,status,dateRun,created_by_modul );
END //
DELIMITER ;

DELIMITER //
use control
select * from data_files
CALL insertDataFile("1",'20231207',"BL","Begin Load","Load");
CREATE PROCEDURE loadDataFromFile(
    IN location VARCHAR(255),
    IN seperator VARCHAR(255),
    IN tableName VARCHAR(255)
)
BEGIN
    SET @loadDataQuery = CONCAT('LOAD DATA INFILE ''', location, ''' INTO TABLE ', tableName,
        ' FIELDS TERMINATED BY ''', seperator, ''' ENCLOSED BY ''"'' LINES TERMINATED BY ''\r\n''');
    PREPARE stmt FROM @loadDataQuery;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END //
use staging
select * from Lotteries_sta
LOAD DATA INFILE 'D://data//data.csv'
INTO TABLE Lotteries_temp
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(location, weekdays, date, prizes, @num_result, region)
SET num_result = REPLACE(@num_result, ';', '');
DELIMITER ;
CALL loadDataFromFile('D://data//data.csv','/n','Lotteries_temp');
DELIMITER //
CREATE PROCEDURE LoadLotteryData()
BEGIN
    DECLARE file_path VARCHAR(255);
    
    -- Set the file path to your CSV file
    SET file_path = 'D://data//data.csv';

    -- Use the LOAD DATA INFILE statement to load data into the table
    SET @sql = CONCAT("LOAD DATA INFILE '", file_path, "' ",
                      "INTO TABLE Lotteries_temp ",
                      "FIELDS TERMINATED BY ',' ",
                      "LINES TERMINATED BY '\\n' ",
                      "IGNORE 1 LINES;");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    
    -- Additional logic or actions after loading data, if needed
    -- ...
END //
DELIMITER ;
CALL LoadLotteryData();

create database Datawarehouse;
use Datawarehouse;
CREATE TABLE date_dims (
    id INT PRIMARY KEY auto_increment,
    id_date VARCHAR(10),
    date DATE,
    date_expired DATETIME
);
-- Insert data into the date_dims table
INSERT INTO date_dims (id, id_date, date, date_expired) VALUES 
(12, 'D02102023', '2023-10-02', '9999-12-30 23:59:00'),
(13, 'D03102023', '2023-10-03', '9999-12-31 23:59:59'),
(14, 'D04102023', '2023-10-04', '9999-12-31 23:59:59'),
(15, 'D05102023', '2023-10-05', '9999-12-31 23:59:59'),
(16, 'D06102023', '2023-10-06', '9999-12-31 23:59:59'),
(17, 'D07102023', '2023-10-07', '9999-12-31 23:59:59'),
(18, 'D08102023', '2023-10-08', '9999-12-31 23:59:59'),
(19, 'D09102023', '2023-10-09', '9999-12-31 23:59:59'),
(20, 'D10102023', '2023-10-10', '9999-12-31 23:59:59'),
(21, 'D11102023', '2023-10-11', '9999-12-31 23:59:59'),
(22, 'D12102023', '2023-10-12', '9999-12-31 23:59:59'),
(23, 'D13102023', '2023-10-13', '9999-12-31 23:59:59'),
(24, 'D14102023', '2023-10-14', '9999-12-31 23:59:59'),
(25, 'D15102023', '2023-10-15', '9999-12-31 23:59:59'),
(26, 'D16102023', '2023-10-16', '9999-12-31 23:59:59'),
(27, 'D17102023', '2023-10-17', '9999-12-31 23:59:59'),
(28, 'D18102023', '2023-10-18', '9999-12-31 23:59:59'),
(29, 'D19102023', '2023-10-19', '9999-12-31 23:59:59'),
(30, 'D20102023', '2023-10-20', '9999-12-31 23:59:59');


CREATE TABLE prize_dims (
    id INT PRIMARY KEY auto_increment,
    id_prize VARCHAR(2),
    name_prize VARCHAR(50),
    date_expired DATETIME
);

-- Insert data into the prize_dims table
INSERT INTO prize_dims (id, id_prize, name_prize, date_expired) VALUES
(1, 'G0', 'Giải Đặc Biệt', '9999-12-31 23:59:59'),
(2, 'G1', 'Giải Nhất', '9999-12-31 23:59:59'),
(3, 'G2', 'Giải Nhì', '9999-12-31 23:59:59'),
(4, 'G3', 'Giải Ba', '9999-12-31 23:59:59'),
(5, 'G4', 'Giải Tư', '9999-12-31 23:59:59'),
(6, 'G5', 'Giải Năm', '9999-12-31 23:59:59'),
(7, 'G6', 'Giải Sáu', '9999-12-31 23:59:59'),
(8, 'G7', 'Giải Bảy', '9999-12-31 23:59:59'),
(9, 'G8', 'Giải Tám', '9999-12-31 23:59:59'),
(10, 'G0', 'Giải ĐB', '9999-12-31 23:59:59'),
(11, 'G1', 'Giải 1', '9999-12-31 23:59:59'),
(12, 'G2', 'Giải 2', '9999-12-31 23:59:59'),
(13, 'G3', 'Giải 3', '9999-12-31 23:59:59'),
(14, 'G4', 'Giải 4', '9999-12-31 23:59:59'),
(15, 'G5', 'Giải 5', '9999-12-31 23:59:59'),
(16, 'G6', 'Giải 6', '9999-12-31 23:59:59'),
(17, 'G7', 'Giải 7', '9999-12-31 23:59:59'),
(18, 'G8', 'Giải 8', '9999-12-31 23:59:59');

-- Create the weekday_dims table
CREATE TABLE weekday_dims (
    id INT PRIMARY KEY auto_increment,
    id_weekday VARCHAR(2),
    weekday VARCHAR(20),
    date_expired DATETIME
);

-- Create the location_dims table
CREATE TABLE location_dims (
    id INT PRIMARY KEY auto_increment,
    id_location VARCHAR(3),
    location VARCHAR(50),
    date_expired DATETIME
);

-- Create the regions_dims table
CREATE TABLE regions_dims (
    id INT PRIMARY KEY auto_increment,
    id_region VARCHAR(2),
    region VARCHAR(50),
    date_expired DATETIME
);

-- Create the lottery_result_facts table
CREATE TABLE lottery_result_facts (
    id INT PRIMARY KEY auto_increment,
    id_sg_date INT,
    id_sg_prize INT,
    id_sg_weekday INT,
    id_sg_location INT,
    id_sg_region INT,
    result varchar(255)
);

INSERT INTO weekday_dims (id, id_weekday, weekday, date_expired) VALUES
(1, 'CN', 'Chủ Nhật', '9999-12-31 23:59:59'),
(2, 'T2', 'Thứ Hai', '9999-12-31 23:59:59'),
(3, 'T3', 'Thứ Ba', '9999-12-31 23:59:59'),
(4, 'T4', 'Thứ Tư', '9999-12-31 23:59:59'),
(5, 'T5', 'Thứ Năm', '9999-12-31 23:59:59'),
(6, 'T6', 'Thứ Sáu', '9999-12-31 23:59:59'),
(7, 'T7', 'Thứ Bảy', '9999-12-31 23:59:59');

INSERT INTO location_dims (id, id_location, location, date_expired) VALUES 
(1, 'L1', 'Miền Bắc', '9999-12-31 23:59:59'),
(2, 'L2', 'Hồ Chí Minh', '9999-12-31 23:59:59'),
(3, 'L3', 'Đồng Tháp', '9999-12-31 23:59:59'),
(4, 'L4', 'Cà Mau', '9999-12-31 23:59:59'),
(5, 'L5', 'Bến Tre', '9999-12-31 23:59:59'),
(6, 'L6', 'Vũng Tàu', '9999-12-31 23:59:59'),
(7, 'L7', 'Bạc Liêu', '9999-12-31 23:59:59'),
(8, 'L8', 'Đồng Nai', '9999-12-31 23:59:59'),
(9, 'L9', 'Cần Thơ', '9999-12-31 23:59:59'),
(10, 'L10', 'Sóc Trăng', '9999-12-31 23:59:59'),
(11, 'L11', 'Tây Ninh', '9999-12-31 23:59:59'),
(12, 'L12', 'An Giang', '9999-12-31 23:59:59'),
(13, 'L13', 'Bình Thuận', '9999-12-31 23:59:59'),
(14, 'L14', 'Vĩnh Long', '9999-12-31 23:59:59'),
(15, 'L15', 'Bình Dương', '9999-12-31 23:59:59'),
(16, 'L16', 'Trà Vinh', '9999-12-31 23:59:59'),
(17, 'L17', 'Long An', '9999-12-31 23:59:59'),
(18, 'L18', 'Bình Phước', '9999-12-31 23:59:59'),
(19, 'L19', 'Hậu Giang', '9999-12-31 23:59:59'),
(20, 'L20', 'Tiền Giang', '9999-12-31 23:59:59'),
(21, 'L21', 'Kiên Giang', '9999-12-31 23:59:59'),
(22, 'L22', 'Đà Lạt', '9999-12-31 23:59:59'),
(23, 'L23', 'Phú Yên', '9999-12-31 23:59:59'),
(24, 'L24', 'Thừa Thiên Huế', '9999-12-31 23:59:59'),
(25, 'L25', 'Đắk Lắk', '9999-12-31 23:59:59'),
(26, 'L26', 'Quảng Nam', '9999-12-31 23:59:59'),
(27, 'L27', 'Đà Nẵng', '9999-12-31 23:59:59'),
(28, 'L28', 'Khánh Hòa', '9999-12-31 23:59:59'),
(29, 'L29', 'Quảng Bình', '9999-12-31 23:59:59'),
(30, 'L30', 'Bình Định', '9999-12-31 23:59:59'),
(31, 'L31', 'Quảng Trị', '9999-12-31 23:59:59'),
(32, 'L32', 'Gia Lai', '9999-12-31 23:59:59'),
(33, 'L33', 'Ninh Thuận', '9999-12-31 23:59:59'),
(34, 'L34', 'Quảng Ngãi', '9999-12-31 23:59:59'),
(35, 'L35', 'Đắk Nông', '9999-12-31 23:59:59'),
(36, 'L36', 'Kon Tum', '9999-12-31 23:59:59');

INSERT INTO regions_dims (id, id_region, region, date_expired) VALUES
(1, 'MB', 'Miền Bắc', '9999-12-31 23:59:59'),
(2, 'MN', 'Miền Nam', '9999-12-31 23:59:59'),
(3, 'MT', 'Miền Trung', '9999-12-31 23:59:59');

INSERT INTO lottery_result_facts (id, id_sg_date, id_sg_prize, id_sg_weekday, id_sg_location, id_sg_region, result) VALUES 
(1, 30, 10, 1, 21, 2, '402281'),
(2, 30, 11, 1, 21, 2, '39659'),
(3, 30, 12, 1, 21, 2, '74572'),
(4, 30, 13, 1, 21, 2, '98985  42455'),
(5, 30, 14, 1, 21, 2, '71808  51438  46011  91807'),
(6, 30, 14, 1, 21, 2, '3388'),
(7, 30, 14, 1, 21, 2, '3134  7533  6098'),
(8, 30, 14, 1, 21, 2, '396'),
(9, 30, 15, 1, 21, 2, '99');

select * from lottery_result_facts;

CREATE TABLE result_lottery_aggregates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255),
    weekdays VARCHAR(255),
    date DATE,
    prizes VARCHAR(255),
    num_result VARCHAR(255),
    region VARCHAR(255)
);
DELIMITER //
CREATE PROCEDURE InsertResultByDaySouthAggregates()
BEGIN
    INSERT INTO result_lottery_aggregates (location, weekdays, date, prizes, num_result, region)
    SELECT ld.location, w.weekday, p.name_prize, l.result ,num_result, r.region
    FROM lottery_result_facts l
    JOIN weekday_dims w ON l.id_sg_weekday = w.id
    JOIN prize_dims p ON l.id_sg_prize = p.id
    JOIN date_dims d ON l.id_sg_date = d.id
    JOIN location_dims ld ON l.id_sg_location = ld.id
    JOIN regions_dims r ON l.id_sg_region = r.id
    GROUP BY ld.location, w.weekday, p.name_prize, r.region, d.date;
END //
DELIMITER ;
Call InsertResultByDaySouthAggregates()