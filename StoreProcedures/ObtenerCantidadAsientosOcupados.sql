CREATE PROCEDURE ObtenerCantidadAsientosOcupados(numero_viaje INT)
BEGIN

	SELECT CantidadPasajeros FROM Viajes WHERE Id = numero_viaje INTO @CantidadActual;

	SELECT COUNT(*)
	FROM Viajes vj
		INNER JOIN PasajerosPorViaje ppv ON vj.Id = ppv.ViajeId
	WHERE ViajeId = numero_viaje AND ppv.EstadoPasajero = 'Aceptado' INTO @Pasajeros;

	SELECT SUM(cantAcompañantes)
	FROM Viajes vj
		INNER JOIN PasajerosPorViaje ppv ON vj.Id = ppv.ViajeId
	WHERE ViajeId = numero_viaje AND ppv.EstadoPasajero = 'Aceptado' INTO @Acompaniantes;

	SELECT FLOOR(@Pasajeros + @Acompaniantes) as AsientosOcupados, @CantidadActual as CantidadActual;

END