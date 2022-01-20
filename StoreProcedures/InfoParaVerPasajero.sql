CREATE DEFINER=`sql10448827`@`%` PROCEDURE `InfoParaVerPasajero`(
	IN pasajero_id INT,
    IN viaje_id INT,
    IN conductor_id INT
)
BEGIN
	
    -- DATOS DE USUARIO
    SELECT 	u.Nombre, u.Apellido, u.Telefono, u.Dni, r.Id IdRol, r.Nombre NombreRol
	FROM Usuarios u
	INNER JOIN Roles r ON u.Rol = r.Id
	WHERE u.Id = pasajero_id
    LIMIT 1
    INTO 	@NombreUsuario, @ApellidoUsuario, @TelefonoUsuario, @DniUsuario, @IdRol, @NombreRol;
    
    -- CALIFICACION
    SELECT 	AVG(cal.Calificacion) as promedio
	FROM 	Calificaciones cal
    WHERE 	UsuarioId = pasajero_id
	LIMIT 1
	INTO @Promedio;
    
    -- CANTIDAD DE CALIFICACIONES
    SELECT COUNT(Calificacion)
    FROM Calificaciones 
    WHERE 	UsuarioId = pasajero_id
    LIMIT 1
	INTO @CantidadCalificaciones;
    
    -- VIAJE
    SELECT  vj.FechaHoraInicio, vj.Id, pr1.Nombre ProvinciaOrigen, ci1.Nombre CiudadOrigen, pr2.Nombre ProvinciaDestino,  ci2.Nombre CiudadDestino, vj.EstadoViaje
	FROM 
	  Viajes vj 
	  LEFT JOIN Provincias pr1 ON pr1.Id = vj.ProvinciaOrigenId 
	  LEFT JOIN Provincias pr2 ON pr2.Id = vj.ProvinciaDestinoId 
	  LEFT JOIN Ciudades ci1 ON ci1.Id = vj.CiudadOrigenId 
	  LEFT JOIN Ciudades ci2 ON ci2.Id = vj.CiudadDestinoId 
	WHERE
	  vj.Id = viaje_id
      LIMIT 1
	INTO @FHInicio, @IdViaje, @PO, @CO, @PD, @CD, @EstadoViaje;
    
    -- VERIFICAR CALIFICACION
    SELECT Id
    FROM Calificaciones
    WHERE 	UsuarioId = pasajero_id AND  CalificadorId = conductor_id AND ViajeId = viaje_id
    LIMIT 1
	INTO @IdCalificacion;
    
    SELECT 	@NombreUsuario Nombre,
			@ApellidoUsuario Apellido,
            @TelefonoUsuario Telefono,
            @DniUsuario Dni,
            @IdRol IdRol,
            @NombreRol NombreRol,
            @Promedio Promedio,
            @CantidadCalificaciones Cantidad,
            @FHInicio FechaHoraInicio,
            @IdViaje Id,
            @PO ProvinciaOrigen,
            @CO CiudadOrigen,
            @PD ProvinciaDestino,
            @CD CiudadDestino,
            @IdCalificacion IdCalificacion,
            @EstadoViaje EstadoViaje;
            
END