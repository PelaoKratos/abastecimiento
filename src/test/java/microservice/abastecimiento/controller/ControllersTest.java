package microservice.abastecimiento.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import microservice.abastecimiento.model.OrdenCompra;
import microservice.abastecimiento.model.Proveedor;
import microservice.abastecimiento.model.RecepcionMercancia;
import microservice.abastecimiento.service.OrdenCompraService;
import microservice.abastecimiento.service.ProveedorService;
import microservice.abastecimiento.service.RecepcionMercanciaService;

@ExtendWith(MockitoExtension.class)
class ControllersTest {

	@Mock
	private ProveedorService proveedorService;

	@Mock
	private OrdenCompraService ordenService;

	@Mock
	private RecepcionMercanciaService recepcionService;

	@Test
	void proveedorControllerDelegaEnServicio() {
		Proveedor proveedor = new Proveedor();
		ProveedorController controller = new ProveedorController(proveedorService);
		when(proveedorService.listar()).thenReturn(List.of(proveedor));
		when(proveedorService.obtenerPorId(1L)).thenReturn(proveedor);
		when(proveedorService.buscarPorEstado("ACTIVO")).thenReturn(List.of(proveedor));
		when(proveedorService.crear(proveedor)).thenReturn(proveedor);
		when(proveedorService.actualizar(1L, proveedor)).thenReturn(proveedor);
		when(proveedorService.cambiarEstado(1L, "INACTIVO")).thenReturn(proveedor);

		assertThat(controller.listar()).containsExactly(proveedor);
		assertThat(controller.obtenerPorId(1L)).isEqualTo(proveedor);
		assertThat(controller.buscarPorEstado("ACTIVO")).containsExactly(proveedor);
		assertThat(controller.crear(proveedor)).isEqualTo(proveedor);
		assertThat(controller.actualizar(1L, proveedor)).isEqualTo(proveedor);
		assertThat(controller.cambiarEstado(1L, Map.of("estado", "INACTIVO"))).isEqualTo(proveedor);
		assertThat(controller.eliminar(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(proveedorService).eliminar(1L);
	}

	@Test
	void ordenControllerDelegaEnServicio() {
		OrdenCompra orden = new OrdenCompra();
		OrdenCompraController controller = new OrdenCompraController(ordenService);
		when(ordenService.listar()).thenReturn(List.of(orden));
		when(ordenService.obtenerPorId(1L)).thenReturn(orden);
		when(ordenService.buscarPorEstado("CREADA")).thenReturn(List.of(orden));
		when(ordenService.buscarPorProveedor(2L)).thenReturn(List.of(orden));
		when(ordenService.crear(2L, orden)).thenReturn(orden);
		when(ordenService.aprobar(1L)).thenReturn(orden);
		when(ordenService.cancelar(1L)).thenReturn(orden);

		assertThat(controller.listar()).containsExactly(orden);
		assertThat(controller.obtenerPorId(1L)).isEqualTo(orden);
		assertThat(controller.buscarPorEstado("CREADA")).containsExactly(orden);
		assertThat(controller.buscarPorProveedor(2L)).containsExactly(orden);
		assertThat(controller.crear(2L, orden)).isEqualTo(orden);
		assertThat(controller.aprobar(1L)).isEqualTo(orden);
		assertThat(controller.cancelar(1L)).isEqualTo(orden);
		assertThat(controller.eliminar(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(ordenService).eliminar(1L);
	}

	@Test
	void recepcionControllerDelegaEnServicio() {
		RecepcionMercancia recepcion = new RecepcionMercancia();
		RecepcionMercanciaController controller = new RecepcionMercanciaController(recepcionService);
		when(recepcionService.listar()).thenReturn(List.of(recepcion));
		when(recepcionService.obtenerPorId(1L)).thenReturn(recepcion);
		when(recepcionService.buscarPorOrden(2L)).thenReturn(List.of(recepcion));
		when(recepcionService.registrar(2L, recepcion)).thenReturn(recepcion);
		when(recepcionService.confirmar(1L)).thenReturn(recepcion);

		assertThat(controller.listar()).containsExactly(recepcion);
		assertThat(controller.obtenerPorId(1L)).isEqualTo(recepcion);
		assertThat(controller.buscarPorOrden(2L)).containsExactly(recepcion);
		assertThat(controller.registrar(2L, recepcion)).isEqualTo(recepcion);
		assertThat(controller.confirmar(1L)).isEqualTo(recepcion);
	}
}
