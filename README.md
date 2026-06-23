# Microservicio Abastecimiento

Este microservicio se encarga de la parte de compras internas: proveedores, ordenes de compra y recepcion de mercaderia. La idea es separar todo lo que ocurre antes de que los productos entren al inventario, para que el flujo quede mas ordenado y no se mezcle con ventas o despacho.

## Que gestiona

- Proveedores registrados para comprar productos.
- Ordenes de compra por proveedor y sucursal.
- Detalles de productos solicitados.
- Recepciones de mercaderia cuando llega una compra.
- Validacion de cantidades recibidas.

## Configuracion local

```properties
spring.application.name=abastecimiento
server.port=8085
spring.datasource.url=jdbc:mysql://localhost:3307/abastecimiento_bd?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
```

La base de datos usada es:

```sql
CREATE DATABASE abastecimiento_bd;
```

## Endpoints principales

Proveedores:

- `GET /api/proveedores`
- `GET /api/proveedores/{id}`
- `GET /api/proveedores/estado/{estado}`
- `POST /api/proveedores`
- `PUT /api/proveedores/{id}`
- `DELETE /api/proveedores/{id}`

Ordenes de compra:

- `GET /api/ordenes-compra`
- `GET /api/ordenes-compra/{id}`
- `GET /api/ordenes-compra/estado/{estado}`
- `GET /api/ordenes-compra/proveedor/{idProveedor}`
- `POST /api/ordenes-compra/proveedor/{idProveedor}`
- `DELETE /api/ordenes-compra/{id}`

Recepcion de mercaderia:

- `GET /api/recepciones`
- `GET /api/recepciones/{id}`
- `GET /api/recepciones/orden/{idOrdenCompra}`
- `POST /api/recepciones/orden/{idOrdenCompra}`

## Como se relaciona con el sistema

Abastecimiento trabaja como el punto de entrada de productos hacia el negocio. En una integracion completa, lo normal es que despues de confirmar una recepcion se pueda reflejar el aumento de stock en Inventario.

## Ejecutar

```powershell
mvn spring-boot:run
```

## Probar

```powershell
mvn test
```
