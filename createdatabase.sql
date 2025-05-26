CREATE TABLE LogPengguna
(
    IdL INT IDENTITY (1,1) PRIMARY KEY
)

CREATE TABLE Tower
(
    IdT INT IDENTITY (1,1) PRIMARY KEY,
	nama VARCHAR(50)
)

CREATE TABLE [Role]
(
    IdR INT IDENTITY (1,1) PRIMARY KEY,
	tipe VARCHAR(30)
)

CREATE TABLE [User] 
(
	NIK CHAR(16) PRIMARY KEY,
	nama VARCHAR(100),
	alamatDomisili VARCHAR(100),
	noTelp CHAR(12),
	OTP CHAR(6)
)

CREATE TABLE RoleUser
(
	NIK CHAR(16) FOREIGN KEY REFERENCES [User] (NIK),
	IdR INT FOREIGN KEY REFERENCES Role (IdR)
)

CREATE TABLE Sarusun
(
	IdS INT IDENTITY(1,1) PRIMARY KEY,
	Lantai VARCHAR(30),
	penggunaanAirH INT,
	penggunaanAirB INT,
	penggunaanAirT INT,
	nikPemilik CHAR(16) FOREIGN KEY REFERENCES [User] (NIK),
	IdT INT FOREIGN KEY REFERENCES Tower (IdT)
)

CREATE TABLE Perangkat
(
	noSerial CHAR(16) PRIMARY KEY,
	statusAir BIT,
	IdS INT FOREIGN KEY REFERENCES Sarusun (IdS)
)

CREATE TABLE Pengelola
(
	IdT INT FOREIGN KEY REFERENCES Tower (IdT),
	nikPengelola CHAR(16) FOREIGN KEY REFERENCES [User] (NIK)
)

CREATE TABLE logMonitorAir
(
	IdL INT FOREIGN KEY REFERENCES LogPengguna (IdL),
	NIK CHAR(16) FOREIGN KEY REFERENCES [User] (NIK),
	IdS INT FOREIGN KEY REFERENCES Sarusun (IdS),
	waktu DATETIME
)

CREATE TABLE aktivasiPerangkat
(
	IdL INT FOREIGN KEY REFERENCES LogPengguna (IdL),
	NIK CHAR(16) FOREIGN KEY REFERENCES [User] (NIK),
	IdS INT FOREIGN KEY REFERENCES Sarusun (IdS),
	waktu DATETIME
)

-- Insert into LogPengguna
INSERT INTO LogPengguna DEFAULT VALUES;  -- IdL = 1
INSERT INTO LogPengguna DEFAULT VALUES;  -- IdL = 2
INSERT INTO LogPengguna DEFAULT VALUES;  -- IdL = 3

-- Insert into Tower
INSERT INTO Tower (nama) VALUES ('Tower A'), ('Tower B'), ('Tower C');

-- Insert into Role
INSERT INTO [Role] (tipe) VALUES ('Pemilik'), ('Pengelola'), ('Admin');

-- Insert Users
INSERT INTO [User] (NIK, nama, alamatDomisili, noTelp, OTP) VALUES
('1234567890123456', 'Ali', 'Jl. Mawar No.1', '081234567890', '123456'),
('2234567890123456', 'Budi', 'Jl. Melati No.2', '081234567891', '654321'),
('3234567890123456', 'Citra', 'Jl. Kenanga No.3', '081234567892', '789012'),
('4234567890123456', 'Dedi', 'Jl. Kamboja No.4', '081234567893', '456789'),
('5234567890123456', 'Eka', 'Jl. Anggrek No.5', '081234567894', '321654');

-- Assign Roles
INSERT INTO RoleUser (NIK, IdR) VALUES
('1234567890123456', 1),  -- Ali as Pemilik
('2234567890123456', 2),  -- Budi as Pengelola
('3234567890123456', 1),  -- Citra as Pemilik
('4234567890123456', 3),  -- Dedi as Admin
('5234567890123456', 1);  -- Eka as Pemilik

-- Insert Sarusun (units)
INSERT INTO Sarusun (Lantai, penggunaanAirH, penggunaanAirB, penggunaanAirT, nikPemilik, IdT) VALUES
('1A', 10, 5, 2, '1234567890123456', 1),
('2B', 15, 7, 3, '3234567890123456', 1),
('3C', 20, 10, 5, '5234567890123456', 2),
('4D', 8, 4, 2, '1234567890123456', 3),
('5E', 18, 9, 4, '5234567890123456', 2);

-- Insert Perangkat (devices)
INSERT INTO Perangkat (noSerial, statusAir, IdS) VALUES
('DEV0000000000001', 1, 1),
('DEV0000000000002', 0, 2),
('DEV0000000000003', 1, 3),
('DEV0000000000004', 0, 4),
('DEV0000000000005', 1, 5);

-- Insert Pengelola
INSERT INTO Pengelola (IdT, nikPengelola) VALUES
(1, '2234567890123456'),
(2, '2234567890123456'); -- Same user managing multiple towers

-- Insert logMonitorAir
INSERT INTO logMonitorAir (IdL, NIK, IdS, waktu) VALUES
(1, '1234567890123456', 1, GETDATE()),
(2, '3234567890123456', 2, DATEADD(HOUR, -1, GETDATE()));

-- Insert aktivasiPerangkat
INSERT INTO aktivasiPerangkat (IdL, NIK, IdS, waktu) VALUES
(2, '5234567890123456', 3, GETDATE()),
(3, '1234567890123456', 1, DATEADD(HOUR, -2, GETDATE()));
