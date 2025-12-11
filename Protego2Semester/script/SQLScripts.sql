-- Drop tables if they already exists
IF OBJECT_ID('dbo.Report', 'U') IS NOT NULL DROP TABLE dbo.Report;
IF OBJECT_ID('dbo.Diploma', 'U') IS NOT NULL DROP TABLE dbo.Diploma;
IF OBJECT_ID('dbo.Invoice', 'U') IS NOT NULL DROP TABLE dbo.Invoice;
IF OBJECT_ID('dbo.EmployeeShift', 'U') IS NOT NULL DROP TABLE dbo.EmployeeShift;
IF OBJECT_ID('dbo.Shift', 'U') IS NOT NULL DROP TABLE dbo.Shift;
IF OBJECT_ID('dbo.Certified', 'U') IS NOT NULL DROP TABLE dbo.Certified;
IF OBJECT_ID('dbo.Contract', 'U') IS NOT NULL DROP TABLE dbo.Contract;
IF OBJECT_ID('dbo.Customer', 'U') IS NOT NULL DROP TABLE dbo.Customer;
IF OBJECT_ID('dbo.Manager', 'U') IS NOT NULL DROP TABLE dbo.Manager;
IF OBJECT_ID('dbo.Employee', 'U') IS NOT NULL DROP TABLE dbo.Employee;
IF OBJECT_ID('dbo.Person', 'U') IS NOT NULL DROP TABLE dbo.Person;
IF OBJECT_ID('dbo.AddressCityPostal', 'U') IS NOT NULL DROP TABLE dbo.AddressCityPostal;

-- AddressCityPostal
CREATE TABLE dbo.AddressCityPostal (
    addressId INT IDENTITY(1,1) PRIMARY KEY,
    [address] NVARCHAR(255) NOT NULL,
    city NVARCHAR(100) NOT NULL,
    postalNr INT NOT NULL
);

-- Person
CREATE TABLE dbo.Person (
    personId INT IDENTITY(1,1) PRIMARY KEY,
    firstName NVARCHAR(100) NOT NULL,
    lastName NVARCHAR(100) NOT NULL,
    phone NVARCHAR(50) NULL,
    email NVARCHAR(255) NULL,
    personType NVARCHAR(50) NULL,
    addressId INT NULL,
    CONSTRAINT FK_Person_Address FOREIGN KEY (addressId) REFERENCES dbo.AddressCityPostal(addressId)
);

-- Employee (PK = employeeId) references Person(personId)
CREATE TABLE dbo.Employee (
    employeeId INT PRIMARY KEY, -- IKKE identity: skal matche Person.personId
    CONSTRAINT FK_Employee_Person FOREIGN KEY (employeeId) REFERENCES dbo.Person(personId)
);

-- Manager (PK = managerId) references Person(personId)
CREATE TABLE dbo.Manager (
    managerId INT PRIMARY KEY, -- ikke identity; matcher Person.personId
    CONSTRAINT FK_Manager_Person FOREIGN KEY (managerId) REFERENCES dbo.Person(personId)
);

-- Customer (PK = customerId) references Person(personId)
CREATE TABLE dbo.Customer (
    customerId INT PRIMARY KEY, -- ikke identity; matcher Person.personId
    firm NVARCHAR(255) NULL,
    CONSTRAINT FK_Customer_Person FOREIGN KEY (customerId) REFERENCES dbo.Person(personId)
);

-- Contract
CREATE TABLE dbo.Contract (
    contractId INT IDENTITY(1,1) PRIMARY KEY,
    startDate DATE NULL,
    endDate DATE NULL,
    guardAmount INT NULL,         -- antal vagter som kontrakten kræver per shift (max)
    estimatedPrice DECIMAL(10,2) NULL,
    active BIT DEFAULT 0,
    confirmed BIT DEFAULT 0,
    customerId INT NULL,          -- reference til kunde
    CONSTRAINT FK_Contract_Customer FOREIGN KEY (customerId) REFERENCES dbo.Customer(customerId)
);

-- Certified
CREATE TABLE dbo.Certified (
    certifiedId INT IDENTITY(1,1) PRIMARY KEY,
    [type] NVARCHAR(100) NULL,
    [name] NVARCHAR(200) NULL,
    contractId INT NULL,
    CONSTRAINT FK_Certified_Contract FOREIGN KEY (contractId) REFERENCES dbo.Contract(contractId)
);

-- Shift
CREATE TABLE dbo.Shift (
    shiftId INT IDENTITY(1,1) PRIMARY KEY,
    startTime INT NOT NULL,     -- eks. 800
    endTime INT NOT NULL,       -- eks. 1600
    guardAmount INT NOT NULL,   
    availability BIT DEFAULT 1,
    shiftLocation NVARCHAR(255) NULL,
    [type] NVARCHAR(100) NULL,
    contractId INT NULL,
    managerId INT NULL,
    certifiedId INT NULL,
    CONSTRAINT FK_Shift_Contract FOREIGN KEY (contractId) REFERENCES dbo.Contract(contractId),
    CONSTRAINT FK_Shift_Manager FOREIGN KEY (managerId) REFERENCES dbo.Manager(managerId),
    CONSTRAINT FK_Shift_Certified FOREIGN KEY (certifiedId) REFERENCES dbo.Certified(certifiedId)
);

-- EmployeeShift (tilknytning employee <-> shift)
CREATE TABLE dbo.EmployeeShift (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    employeeId INT NOT NULL,
    shiftId INT NOT NULL,
    CONSTRAINT FK_EmployeeShift_Employee FOREIGN KEY (employeeId) REFERENCES dbo.Employee(employeeId),
    CONSTRAINT FK_EmployeeShift_Shift FOREIGN KEY (shiftId) REFERENCES dbo.Shift(shiftId)
);

-- Invoice
CREATE TABLE dbo.Invoice (
    invoiceId INT IDENTITY(1,1) PRIMARY KEY,
    [date] DATE NULL,
    price DECIMAL(10,2) NULL,
    shiftId INT NULL,
    CONSTRAINT FK_Invoice_Shift FOREIGN KEY (shiftId) REFERENCES dbo.Shift(shiftId)
);

-- Diploma (tilknyttet employee)
CREATE TABLE dbo.Diploma (
    diplomaId INT IDENTITY(1,1) PRIMARY KEY,
    [date] DATE NULL,
    validPeriode NVARCHAR(100) NULL,
    certifiedType NVARCHAR(100) NULL,
    employeeId INT NULL,
    CONSTRAINT FK_Diploma_Employee FOREIGN KEY (employeeId) REFERENCES dbo.Employee(employeeId)
);

-- Report
CREATE TABLE dbo.Report (
    reportId INT IDENTITY(1,1) PRIMARY KEY,
    [date] DATE NULL,
    incident NVARCHAR(4000) NULL,
    note NVARCHAR(4000) NULL,
    employeeId INT NULL,
    managerId INT NULL,
    customerId INT NULL,
    CONSTRAINT FK_Report_Employee FOREIGN KEY (employeeId) REFERENCES dbo.Employee(employeeId),
    CONSTRAINT FK_Report_Manager FOREIGN KEY (managerId) REFERENCES dbo.Manager(managerId),
    CONSTRAINT FK_Report_Customer FOREIGN KEY (customerId) REFERENCES dbo.Customer(customerId)
);


--TESTDATA INSERT FOR NY DATABASESTRUKTUR
-- Indeholder: 1 manager, 1 customer, 2 employees
---------------------------------------------------------

------------------------------
-- Addresses
------------------------------
INSERT INTO AddressCityPostal ([address], city, postalNr)
VALUES ('Manager Street 1', 'Aarhus', 8000),       -- addressId = 1
       ('Customer Road 5', 'Odense', 5000),        -- addressId = 2
       ('Employee Lane 10', 'Aalborg', 9000),      -- addressId = 3
       ('Employee Lane 12', 'Aalborg', 9000);      -- addressId = 4


------------------------------
-- Persons
------------------------------
-- Manager person
INSERT INTO Person (firstName, lastName, phone, email, personType, addressId)
VALUES ('Morten', 'Manager', '12345678', 'manager@example.com', 'manager', 1);  -- personId = 1

-- Customer person
INSERT INTO Person (firstName, lastName, phone, email, personType, addressId)
VALUES ('Carla', 'Customer', '87654321', 'customer@example.com', 'customer', 2); -- personId = 2

-- Employee 1
INSERT INTO Person (firstName, lastName, phone, email, personType, addressId)
VALUES ('Erik', 'Employee', '22223333', 'emp1@example.com', 'employee', 3);     -- personId = 3

-- Employee 2
INSERT INTO Person (firstName, lastName, phone, email, personType, addressId)
VALUES ('Eva', 'Employee', '44445555', 'emp2@example.com', 'employee', 4);      -- personId = 4


------------------------------
-- Manager / Customer / Employee tables

------------------------------
INSERT INTO Manager (managerId) VALUES (1);
INSERT INTO Customer (customerId, firm) VALUES (2, 'Example Firm A/S');
INSERT INTO Employee (employeeId) VALUES (3);
INSERT INTO Employee (employeeId) VALUES (4);


------------------------------
-- Contract for the customer
------------------------------
INSERT INTO Contract (startDate, endDate, guardAmount, estimatedPrice, active, confirmed, customerId)
VALUES ('2025-01-01', '2025-12-31', 3, 15000.00, 1, 0, 2);  
-- contractId = 1


------------------------------
-- Shifts tied to contract
------------------------------
INSERT INTO Shift (startTime, endTime, guardAmount, availability, shiftLocation, type, contractId, managerId)
VALUES 
(800, 1600, 2, 1, 'Aarhus', 'Brandvagt',     1, 1),   -- shiftId = 1
(1200, 2000, 1, 1, 'Odense', 'Vagt',          1, 1),   -- shiftId = 2
(1000, 1400, 1, 1, 'Aalborg', 'Brandvagt',    1, 1);   -- shiftId = 3


------------------------------
-- Book a few shifts
------------------------------
INSERT INTO EmployeeShift (employeeId, shiftId)
VALUES 
(3, 1),  -- employee 1 booked shift 1
(4, 1),  -- employee 2 booked shift 1 (shift now 2/2 full)
(3, 2);  -- employee 1 booked shift 2


