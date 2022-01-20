CREATE DEFINER=`sql10448827`@`%` PROCEDURE `InfoParaVerBusqueda`(
    IN solicitud_id INT
)
BEGIN

	-- DATOS DE LA SOLICITUD
    SELECT  sl.FechaHoraInicio, sl.Id, pr1.Nombre ProvinciaOrigen, ci1.Nombre CiudadOrigen, pr2.Nombre ProvinciaDestino,  ci2.Nombre CiudadDestino, sl.PasajeroId, sl.CantidadAcompaniantes, sl.EstadoSolicitud
	FROM 
	  Solicitudes sl 
	  LEFT JOIN Provincias pr1 ON pr1.Id = sl.ProvinciaOrigenId 
	  LEFT JOIN Provincias pr2 ON pr2.Id = sl.ProvinciaDestinoId 
	  LEFT JOIN Ciudades ci1 ON ci1.Id = sl.CiudadOrigenId 
	  LEFT JOIN Ciudades ci2 ON ci2.Id = sl.CiudadDestinoId 
	WHERE
	  sl.Id = solicitud_id
	INTO @FHInicio, @IdViaje, @PO, @CO, @PD, @CD, @PasajeroId, @CantidadAcompaniantes, @EstadoSolicitud;
    
    -- DATOS DE USUARIO
    SELECT 	u.Nombre, u.Apellido, u.Telefono, u.Dni, r.Id IdRol, r.Nombre NombreRol
	FROM Usuarios u
	INNER JOIN Roles r ON u.Rol = r.Id
	WHERE u.Id = @PasajeroId
    LIMIT 1
    INTO 	@NombreUsuario, @ApellidoUsuario, @TelefonoUsuario, @DniUsuario, @IdRol, @NombreRol;
    
    -- CALIFICACION
    SELECT 	AVG(cal.Calificacion) as promedio
	FROM 	Calificaciones cal
    WHERE 	cal.UsuarioId = @PasajeroId
	INTO @Promedio;
    
    -- CANTIDAD DE CALIFICACIONES
    SELECT COUNT(Calificacion)
    FROM Calificaciones 
    WHERE 	UsuarioId = @PasajeroId
	INTO @CantidadCalificaciones;    
    
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
            @CantidadAcompaniantes CantidadAcompaniantes,
            @EstadoSolicitud EstadoSolicitud;
            
END