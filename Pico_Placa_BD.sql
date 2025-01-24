CREATE DATABASE PicoPlaca;
use PicoPlaca;

CREATE TABLE DiasPicoPlaca (
    IdDia INT PRIMARY KEY IDENTITY(1,1),
    Dia NVARCHAR(15) NOT NULL,
    PlacasRestringidas NVARCHAR(10) NOT NULL
);

INSERT INTO DiasPicoPlaca (Dia, PlacasRestringidas)
VALUES 
    ('Lunes', '1'),
	('Lunes', '2'),
    ('Martes', '3'),
	('Martes', '4'),
    ('Miércoles', '5'),
	('Miércoles', '6'),
    ('Jueves', '7'),
	('Jueves', '8'),
    ('Viernes', '9'),
	('Viernes', '0');

CREATE TABLE HorasPicoPlaca (
    IdHora INT PRIMARY KEY IDENTITY(1,1),
    Inicio TIME NOT NULL,
    Fin TIME NOT NULL,
    Turno NVARCHAR(10) NOT NULL
);

INSERT INTO HorasPicoPlaca (Inicio, Fin, Turno)
VALUES 
    ('06:00', '09:30', 'Mañana'),
    ('16:00', '20:00', 'Tarde');

CREATE TABLE Consultas (
    IdConsulta INT PRIMARY KEY IDENTITY(1,1),
    Placa NVARCHAR(10) NOT NULL,
    Hora NVARCHAR(5) NOT NULL,
    Dia NVARCHAR(15) NOT NULL,
    TienePicoPlaca NVARCHAR(3) NOT NULL
);

SELECT * FROM DiasPicoPlaca;
SELECT * FROM HorasPicoPlaca;
SELECT * FROM Consultas;