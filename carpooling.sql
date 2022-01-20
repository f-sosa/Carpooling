-- DATOS MAESTROS
DROP TABLE IF EXISTS Calificaciones;
DROP TABLE IF EXISTS PasajerosPorViaje;
DROP TABLE IF EXISTS Solicitudes;
DROP TABLE IF EXISTS Viajes;
DROP TABLE IF EXISTS Notificaciones;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Ciudades;
DROP TABLE IF EXISTS Provincias;

CREATE TABLE Roles (
  Id varchar(3) NOT NULL,
  Nombre varchar(10) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id)
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE Provincias (
  Id int NOT NULL auto_increment,
  Nombre varchar(30) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id)
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE Ciudades (
  Id int NOT NULL auto_increment,
  ProvinciaId int NOT NULL,
  Nombre varchar(30) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id),
  
  FOREIGN KEY (ProvinciaId)
	REFERENCES Provincias(Id)
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Roles` (Id,Nombre,EstadoRegistro) VALUES("CON",'Conductor',1);
INSERT INTO `Roles` (Id,Nombre,EstadoRegistro) VALUES("PAS",'Pasajero',1);

INSERT INTO `Provincias` (Nombre,EstadoRegistro) VALUES('CABA',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Belgrano',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Retiro',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Palermo',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Saavedra',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Caballito',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Colegiales',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Flores',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Monserrat',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Puerto Madero',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(1,'Saavedra',1);

INSERT INTO `Provincias` (Nombre,EstadoRegistro) VALUES('Buenos Aires',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Don Torcuato',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Tigre',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'San Martin',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'San Isidro',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Quilmes',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Lanus',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Mar del plata',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Bahia Blanca',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Junin',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(2,'Tandil',1);

INSERT INTO `Provincias` (Nombre,EstadoRegistro) VALUES('Entre Rios',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'Parana',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'Villa Elisa',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'Diamante',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'Concepcion del Uruguay',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'Concordia',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'Colon',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'San Jose',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(3,'Gualeguaychu',1);

INSERT INTO `Provincias` (Nombre,EstadoRegistro) VALUES('Cordoba',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'Cordoba Capital',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'Villa Carlos Paz',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'Cosquin',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'Gral. Belgrano',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'Cumbrecita',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'La Falda',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'Traslasierra',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(4,'Mina Clavero',1);

INSERT INTO `Provincias` (Nombre,EstadoRegistro) VALUES('Santa Fe',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(5,'Rosario',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(5,'Santa Fe',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(5,'Rafaela',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(5,'Venado Tuerto',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(5,'Esperanza',1);
INSERT INTO `Ciudades` (ProvinciaId,Nombre,EstadoRegistro) VALUES(5,'Cañanda de Gomez',1);

/**/

-- TABLAS TRANSACCIONALES
DROP TABLE IF EXISTS Calificaciones;
DROP TABLE IF EXISTS PasajerosPorViaje;
DROP TABLE IF EXISTS Solicitudes;
DROP TABLE IF EXISTS Viajes;
DROP TABLE IF EXISTS Notificaciones;
DROP TABLE IF EXISTS Usuarios;

CREATE TABLE Usuarios (
  Id int NOT NULL auto_increment,
  Email varchar(30) NOT NULL,
  Rol varchar(3) NOT NULL,
  Pass varchar(15) NOT NULL,
  Nombre varchar(20) NOT NULL,
  Apellido varchar(20) NOT NULL,
  Nacimiento datetime NOT NULL,
  Telefono varchar(20) NOT NULL,
  Dni varchar(8) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id),
  
  UNIQUE KEY(Email,Rol),
  
  FOREIGN KEY (Rol)
	REFERENCES Roles(Id)
    
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE Notificaciones (
  Id int NOT NULL auto_increment,
  UsuarioId int NOT NULL,
  Mensaje varchar(100) NOT NULL,
  EstadoNotificacion varchar(3) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id),
  
  FOREIGN KEY (UsuarioId)
	REFERENCES Usuarios(Id)
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE Viajes (
  Id int NOT NULL auto_increment,
  ConductorId int NOT NULL,
  ProvinciaOrigenId int NOT NULL,
  CiudadOrigenId int NOT NULL,
  ProvinciaDestinoId int NOT NULL,
  CiudadDestinoId int NOT NULL,
  FechaHoraInicio datetime NOT NULL,
  FechaHoraFinalizacion datetime,
  CantidadPasajeros int NOT NULL,
  EstadoViaje varchar(20) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id),
  
  FOREIGN KEY (ConductorId)
	REFERENCES Usuarios(Id),
    
  FOREIGN KEY (ProvinciaOrigenId,CiudadOrigenId)
	REFERENCES Ciudades(ProvinciaId, Id),
    
  FOREIGN KEY (ProvinciaDestinoId,CiudadDestinoId)
	REFERENCES Ciudades(ProvinciaId, Id)
    
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE Solicitudes (
  Id int NOT NULL auto_increment,
  PasajeroId int NOT NULL,
  ProvinciaOrigenId int NOT NULL,
  CiudadOrigenId int NOT NULL,
  ProvinciaDestinoId int NOT NULL,
  CiudadDestinoId int NOT NULL,
  FechaHoraInicio datetime NOT NULL,
  CantidadAcompaniantes int NOT NULL,
  EstadoSolicitud varchar(20) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id),
  
  FOREIGN KEY (PasajeroId)
	REFERENCES Usuarios(Id),
    
  FOREIGN KEY (ProvinciaOrigenId,CiudadOrigenId)
	REFERENCES Ciudades(ProvinciaId, Id),
    
  FOREIGN KEY (ProvinciaDestinoId,CiudadDestinoId)
	REFERENCES Ciudades(ProvinciaId, Id)
    
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE PasajerosPorViaje (
  ViajeId int NOT NULL,
  UsuarioId int NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  EstadoPasajero varchar(20) NOT NULL,
  cantAcompañantes int DEFAULT 0,
  
  PRIMARY KEY (ViajeId, UsuarioId),
  
  FOREIGN KEY (UsuarioId)
	REFERENCES Usuarios(Id),
    
  FOREIGN KEY (ViajeId)
	REFERENCES Viajes(Id)
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE Calificaciones (
  Id int NOT NULL AUTO_INCREMENT,
  UsuarioId int NOT NULL,
  CalificadorId int NOT NULL,
  ViajeId int NOT NULL,
  Calificacion float(3) NOT NULL,
  EstadoRegistro boolean DEFAULT true,
  
  PRIMARY KEY (Id),
  
  FOREIGN KEY (CalificadorId)
	REFERENCES Usuarios(Id),

  FOREIGN KEY (CalificadorId)
	REFERENCES Usuarios(Id),

  FOREIGN KEY (ViajeId)
	REFERENCES Viajes(Id)
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- USUARIOS
DELETE FROM Usuarios WHERE Id > 0;

INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("tobi@mail.com","CON","password","Tobias","Olea",'1997-05-12 00:00','+54 9 11 6920 3645','11111111',true);
INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("joni@mail.com","CON","password","Joni","Costa",'1997-05-12 00:00','+54 9 11 6920 3645','22222222',true);
INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("mati@mail.com","CON","password","Matias","Alvarado",'1997-05-12 00:00','+54 9 11 6920 3645','33333333',true);
INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("fran@mail.com","CON","password","Fran","Sosa",'1997-05-12 00:00','+54 9 11 6920 3645','44444444',true);
INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("tobi@mail.com","PAS","password","Tobias","Olea",'1997-05-12 00:00','+54 9 11 6920 3645','11111111',true);
INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("joni@mail.com","PAS","password","Joni","Costa",'1997-05-12 00:00','+54 9 11 6920 3645','22222222',true);
INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("mati@mail.com","PAS","password","Matias","Alvarado",'1997-05-12 00:00','+54 9 11 6920 3645','33333333',true);
INSERT INTO `Usuarios` (`Email`,`Rol`,`Pass`,`Nombre`,`Apellido`,`Nacimiento`,`Telefono`,`Dni`,`EstadoRegistro`)
	VALUES ("fran@mail.com","PAS","password","Fran","Sosa",'1997-05-12 00:00','+54 9 11 6920 3645','44444444',true);

--------------------------------------------------------------------------------------------------
DELETE FROM Calificaciones WHERE Id > 0;
DELETE FROM PasajerosPorViaje WHERE UsuarioId > 0;
DELETE FROM Viajes WHERE Id > 0;
DELETE FROM Solicitudes WHERE Id > 0;
-- VIAJE 1000 Tobias Olea
INSERT INTO Viajes VALUES (1000,1,1,2,2,20,"2021-10-01 14:30",null,4,"Finalizado",1);
  -- Pasajeros del viaje 1000
  INSERT INTO PasajerosPorViaje VALUES (1000,6,1,'Aceptado',1);
  INSERT INTO PasajerosPorViaje VALUES (1000,7,1,'Aceptado',0);
  INSERT INTO PasajerosPorViaje VALUES (1000,8,1,'Aceptado',0);
    -- Calificaciones del viaje 1000
    INSERT INTO Calificaciones (UsuarioId,CalificadorId,ViajeId,Calificacion,EstadoRegistro) VALUES(6,1,1000,4.5,1);
    INSERT INTO Calificaciones (UsuarioId,CalificadorId,ViajeId,Calificacion,EstadoRegistro) VALUES(7,1,1000,4,1);
    INSERT INTO Calificaciones (UsuarioId,CalificadorId,ViajeId,Calificacion,EstadoRegistro) VALUES(1,6,1000,5,1);
    INSERT INTO Calificaciones (UsuarioId,CalificadorId,ViajeId,Calificacion,EstadoRegistro) VALUES(1,7,1000,5,1);
    INSERT INTO Calificaciones (UsuarioId,CalificadorId,ViajeId,Calificacion,EstadoRegistro) VALUES(1,8,1000,5,1);

-- VIAJE 1001 Tobias Olea
INSERT INTO Viajes VALUES (1001,1,1,7,3,24,"2021-10-01 14:30",null,4,"Finalizado",1);
  -- Pasajeros del viaje 1001
  INSERT INTO PasajerosPorViaje VALUES (1001,6,1,'Aceptado',3);
    -- Calificaciones del viaje 1001
    INSERT INTO Calificaciones (UsuarioId,CalificadorId,ViajeId,Calificacion,EstadoRegistro) VALUES(6,1,1001,5,1);
    INSERT INTO Calificaciones (UsuarioId,CalificadorId,ViajeId,Calificacion,EstadoRegistro) VALUES(1,6,1001,4,1);

-- VIAJE 1002 Tobias Olea
INSERT INTO Viajes VALUES (1002,1,1,7,3,24,"2021-10-15 14:30",null,4,"Cancelado",1);
  -- Pasajeros del viaje 1002
  INSERT INTO PasajerosPorViaje VALUES (1002,6,1,'Aceptado',0);
  INSERT INTO PasajerosPorViaje VALUES (1002,7,1,'Aceptado',0);

-- VIAJE 1003 Tobias Olea
INSERT INTO Viajes VALUES (1003,1,2,11,3,21,"2021-11-25 18:30",null,4,"En Espera",1);
  -- Pasajeros del viaje 1003
  INSERT INTO PasajerosPorViaje VALUES (1003,6,1,'Aceptado',0);
  INSERT INTO PasajerosPorViaje VALUES (1003,7,1,'Pendiente',0);
  INSERT INTO PasajerosPorViaje VALUES (1003,8,1,'Pendiente',1);

-- VIAJE 1004 Tobias Olea
INSERT INTO Viajes VALUES (1004,1,3,21,2,11,"2021-11-27 07:30",null,4,"En Espera",1);
  -- Pasajeros del viaje 1004
  INSERT INTO PasajerosPorViaje VALUES (1004,6,1,'Aceptado',0);
  INSERT INTO PasajerosPorViaje VALUES (1004,7,1,'Pendiente',0);
  INSERT INTO PasajerosPorViaje VALUES (1004,8,1,'Pendiente',1);

-- VIAJE 1005 Joni Costa
INSERT INTO Viajes VALUES (1005,2,2,11,2,17,"2021-12-05 07:30",null,4,"En Espera",1);
  -- Pasajeros del viaje 1005

-- VIAJE 1006 Joni Costa
INSERT INTO Viajes VALUES (1006,2,2,17,2,11,"2021-12-01 10:00",null,4,"En Espera",1);

-- VIAJE 1007 Mati Alvarado
INSERT INTO Viajes VALUES (1007,3,5,40,4,31,"2022-12-02 10:00",null,4,"En Espera",1);

-- VIAJE 1008 Mati Alvarado
INSERT INTO Viajes VALUES (1008,4,4,33,3,23,"2022-12-03 11:00",null,4,"En Espera",1);

-- VIAJE 1009 Mati Alvarado
INSERT INTO Viajes VALUES (1009,4,2,16,5,38,"2021-12-04 11:00",null,4,"En Espera",1);

-- VIAJE 1010 Fran Sosa
INSERT INTO Viajes VALUES (1010,3,4,31,5,40,"2022-12-05 10:00",null,4,"En Espera",1);

-- VIAJE 1011 Mati Alvarado
INSERT INTO Viajes VALUES (1011,4,3,23,4,33,"2022-12-06 11:00",null,4,"En Espera",1);

-- VIAJE 1012 Mati Alvarado
INSERT INTO Viajes VALUES (1012,4,5,38,2,16,"2021-12-07 11:00",null,4,"En Espera",1);

-- VIAJE 1013 Fran Sosa
INSERT INTO Viajes VALUES (1013,3,1,1,2,12,"2022-12-08 10:00",null,4,"En Espera",1);

-- VIAJE 1014 Mati Alvarado
INSERT INTO Viajes VALUES (1014,4,4,34,3,28,"2022-12-09 11:00",null,4,"En Espera",1);

-- VIAJE 1015 Mati Alvarado
INSERT INTO Viajes VALUES (1015,4,2,20,2,18,"2021-12-10 11:00",null,4,"En Espera",1);

-- VIAJE 1000 Tobias Olea
INSERT INTO Viajes VALUES (1016,1,1,2,2,20,"2021-11-12 23:00",null,4,"En Espera",1);
  -- Pasajeros del viaje 1000
  INSERT INTO PasajerosPorViaje VALUES (1016,6,1,'Aceptado',1);
  INSERT INTO PasajerosPorViaje VALUES (1016,7,1,'Aceptado',0);
  INSERT INTO PasajerosPorViaje VALUES (1016,8,1,'Aceptado',0);

-- VIAJE 1000 Tobias Olea
INSERT INTO Viajes VALUES (1018,1,1,2,2,20,"2021-11-13 07:00",null,4,"En Espera",1);
  -- Pasajeros del viaje 1000
  INSERT INTO PasajerosPorViaje VALUES (1018,6,1,'Aceptado',1);
  INSERT INTO PasajerosPorViaje VALUES (1018,7,1,'Aceptado',0);
  INSERT INTO PasajerosPorViaje VALUES (1018,8,1,'Aceptado',0);

-- VIAJE 1000 Tobias Olea
INSERT INTO Viajes VALUES (1017,1,1,2,2,20,"2021-11-11 23:00",null,4,"En Espera",1);
  -- Pasajeros del viaje 1000
  INSERT INTO PasajerosPorViaje VALUES (1017,6,1,'Aceptado',1);
  INSERT INTO PasajerosPorViaje VALUES (1017,7,1,'Aceptado',0);
  INSERT INTO PasajerosPorViaje VALUES (1017,8,1,'Aceptado',0);

-- SOLICITUDES Tobi Olea
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1000,5,2,13,4,31,'2022-05-12 13:00',0,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1001,5,5,38,2,17,'2022-01-01 05:00',1,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1002,5,2,11,4,31,'2022-02-11 06:00',2,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1003,5,4,31,2,11,'2022-02-14 12:00',3,"Pendiente",1);

-- SOLICITUDES Joni Costa
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1004,6,4,31,3,25,'2021-12-01 13:00',0,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1005,6,1,3,2,13,'2021-12-03 05:00',1,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1006,6,2,17,1,9,'2021-12-08 06:00',2,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1007,6,4,36,5,39,'2021-12-11 12:00',3,"Pendiente",1);

-- SOLICITUDES Mati Alvarado
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1008,7,1,2,3,21,'2021-12-01 13:00',0,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1009,7,3,21,1,2,'2021-12-03 05:00',1,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1010,7,3,28,2,12,'2021-12-08 06:00',2,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1011,7,3,21,2,20,'2021-12-11 12:00',3,"Pendiente",1);

-- SOLICITUDES Fran Sosa
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1012,8,5,37,5,40,'2021-12-01 13:00',0,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1013,8,4,31,5,40,'2021-12-01 13:00',1,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1014,8,5,40,5,37,'2021-12-01 13:00',2,"Pendiente",1);
INSERT INTO Solicitudes(Id,PasajeroId,ProvinciaOrigenId,CiudadOrigenId,ProvinciaDestinoId,CiudadDestinoId,FechaHoraInicio,CantidadAcompaniantes,EstadoSolicitud,EstadoRegistro)
VALUES (1015,8,5,37,5,39,'2021-12-01 13:00',3,"Pendiente",1);

SELECT * FROM Notificaciones;
SELECT * FROM PasajerosPorViaje;


