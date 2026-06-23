INSERT IGNORE INTO proveedor (id_proveedor, rut, nombre, correo, telefono, direccion, estado) VALUES
(1001, '76000001-1', 'Aromas del Sur SpA', 'ventas@aromasdelsur.cl', '221234501', 'Av. Industrial 1200, Santiago', 'ACTIVO'),
(1002, '76000002-2', 'Fragancias Premium Ltda', 'contacto@fraganciaspremium.cl', '221234502', 'Camino La Bodega 455, Quilicura', 'ACTIVO'),
(1003, '76000003-3', 'Insumos Cosmeticos Norte', 'abastecimiento@insumosnorte.cl', '221234503', 'Ruta 5 Norte 980, La Serena', 'ACTIVO');

INSERT IGNORE INTO orden_compra (id_orden_compra, id_sucursal, id_usuario, fecha_orden, fecha_estimada, subtotal, total, estado, id_proveedor) VALUES
(1001, 1001, 1001, '2026-06-10 09:15:00', '2026-06-17', 245000.00, 291550.00, 'EMITIDA', 1001),
(1002, 1002, 1002, '2026-06-11 10:30:00', '2026-06-18', 390000.00, 464100.00, 'RECIBIDA_PARCIAL', 1002),
(1003, 1003, 1003, '2026-06-12 11:45:00', '2026-06-20', 180000.00, 214200.00, 'PENDIENTE', 1003);

INSERT IGNORE INTO detalle_orden_compra (id_detalle_orden, id_producto, cantidad, precio_unitario, subtotal, id_orden_compra) VALUES
(1001, 2001, 20, 8500.00, 170000.00, 1001),
(1002, 2002, 10, 7500.00, 75000.00, 1001),
(1003, 2003, 30, 9000.00, 270000.00, 1002),
(1004, 2004, 12, 10000.00, 120000.00, 1002),
(1005, 2005, 15, 12000.00, 180000.00, 1003);

INSERT IGNORE INTO recepcion_mercancia (id_recepcion, id_usuario, fecha_recepcion, observacion, estado, id_orden_compra) VALUES
(1001, 1001, '2026-06-17 15:20:00', 'Recepcion completa sin diferencias.', 'COMPLETA', 1001),
(1002, 1002, '2026-06-18 16:10:00', 'Faltan unidades del producto 2004.', 'PARCIAL', 1002);

INSERT IGNORE INTO detalle_recepcion_mercancia (id_detalle_recepcion, id_producto, cantidad_esperada, cantidad_recibida, estado, observacion, id_recepcion) VALUES
(1001, 2001, 20, 20, 'RECIBIDO', 'Sin observaciones.', 1001),
(1002, 2002, 10, 10, 'RECIBIDO', 'Sin observaciones.', 1001),
(1003, 2003, 30, 30, 'RECIBIDO', 'Sin observaciones.', 1002),
(1004, 2004, 12, 8, 'PENDIENTE', 'Proveedor enviara 4 unidades restantes.', 1002);
