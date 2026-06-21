package microservice.abastecimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import microservice.abastecimiento.exception.ResourceNotFoundException;
import microservice.abastecimiento.model.DetalleOrdenCompra;
import microservice.abastecimiento.model.OrdenCompra;
import microservice.abastecimiento.model.Proveedor;
import microservice.abastecimiento.repository.OrdenCompraRepository;
import microservice.abastecimiento.repository.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class OrdenCompraServiceTest {

	@Mock
	private OrdenCompraRepository ordenRepository;

	@Mock
	private ProveedorRepository proveedorRepository;

	private OrdenCompraService ordenService;

	@BeforeEach
	void setUp() {
		ordenService = new OrdenCompraService(ordenRepository, proveedorRepository);
	}

	@Test
	void listarObtenerYBusquedasDeleganEnRepositorio() {
		OrdenCompra orden = orden();
		when(ordenRepository.findAll()).thenReturn(List.of(orden));
		when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
		when(ordenRepository.findByEstado("CREADA")).thenReturn(List.of(orden));
		when(ordenRepository.findByProveedorIdProveedor(2L)).thenReturn(List.of(orden));

		assertThat(ordenService.listar()).containsExactly(orden);
		assertThat(ordenService.obtenerPorId(1L)).isEqualTo(orden);
		assertThat(ordenService.buscarPorEstado("creada")).containsExactly(orden);
		assertThat(ordenService.buscarPorProveedor(2L)).containsExactly(orden);
	}

	@Test
	void crearCalculaTotalesYAsociaProveedor() {
		OrdenCompra orden = orden();
		orden.setFechaOrden(null);
		orden.setEstado(null);
		when(proveedorRepository.findById(2L)).thenReturn(Optional.of(proveedor()));
		when(ordenRepository.save(orden)).thenReturn(orden);

		OrdenCompra resultado = ordenService.crear(2L, orden);

		assertThat(resultado.getFechaOrden()).isNotNull();
		assertThat(resultado.getEstado()).isEqualTo("CREADA");
		assertThat(resultado.getProveedor().getIdProveedor()).isEqualTo(2L);
		assertThat(resultado.getSubtotal()).isEqualByComparingTo("20000");
		assertThat(resultado.getTotal()).isEqualByComparingTo("20000");
		assertThat(resultado.getDetalles()).allSatisfy(detalle -> assertThat(detalle.getOrdenCompra()).isEqualTo(resultado));
	}

	@Test
	void crearNormalizaEstadoInformado() {
		OrdenCompra orden = orden();
		orden.setEstado(" aprobada ");
		when(proveedorRepository.findById(2L)).thenReturn(Optional.of(proveedor()));
		when(ordenRepository.save(orden)).thenReturn(orden);

		assertThat(ordenService.crear(2L, orden).getEstado()).isEqualTo("APROBADA");
	}

	@Test
	void crearFallaSiProveedorNoExisteOSinDetalles() {
		OrdenCompra orden = orden();
		when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> ordenService.crear(99L, orden))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No existe el proveedor con id 99");

		orden.setDetalles(new ArrayList<>());
		when(proveedorRepository.findById(2L)).thenReturn(Optional.of(proveedor()));
		assertThatThrownBy(() -> ordenService.crear(2L, orden))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("La orden debe tener al menos un detalle");
	}

	@Test
	void obtenerPorIdLanzaErrorCuandoNoExiste() {
		when(ordenRepository.findById(8L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> ordenService.obtenerPorId(8L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No existe la orden de compra con id 8");
	}

	@Test
	void aprobarYCambiarACanceladaFuncionan() {
		OrdenCompra orden = orden();
		when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
		when(ordenRepository.save(orden)).thenReturn(orden);

		assertThat(ordenService.aprobar(1L).getEstado()).isEqualTo("APROBADA");
		assertThat(ordenService.cancelar(1L).getEstado()).isEqualTo("CANCELADA");
	}

	@Test
	void aprobarRechazaOrdenCanceladaYCancelarRechazaRecepcionada() {
		OrdenCompra orden = orden();
		orden.setEstado("CANCELADA");
		when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
		assertThatThrownBy(() -> ordenService.aprobar(1L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No se puede aprobar una orden cancelada");

		orden.setEstado("RECEPCIONADA");
		assertThatThrownBy(() -> ordenService.cancelar(1L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No se puede cancelar una orden recepcionada");
	}

	@Test
	void eliminarBorraOrden() {
		OrdenCompra orden = orden();
		when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

		ordenService.eliminar(1L);

		verify(ordenRepository).delete(orden);
	}

	@Test
	void estadoInvalidoLanzaError() {
		assertThatThrownBy(() -> ordenService.buscarPorEstado(" "))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El estado no puede estar vacio");
		assertThatThrownBy(() -> ordenService.buscarPorEstado(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El estado no puede estar vacio");
		assertThatThrownBy(() -> ordenService.buscarPorEstado("PERDIDA"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Estado de orden no valido: PERDIDA");
	}

	private OrdenCompra orden() {
		OrdenCompra orden = new OrdenCompra();
		orden.setIdOrdenCompra(1L);
		orden.setIdSucursal(10L);
		orden.setIdUsuario(20L);
		orden.setFechaOrden(LocalDateTime.of(2026, 6, 21, 12, 0));
		orden.setEstado("CREADA");
		orden.setDetalles(new ArrayList<>(List.of(detalle())));
		return orden;
	}

	private DetalleOrdenCompra detalle() {
		DetalleOrdenCompra detalle = new DetalleOrdenCompra();
		detalle.setIdProducto(100L);
		detalle.setCantidad(2);
		detalle.setPrecioUnitario(new BigDecimal("10000"));
		return detalle;
	}

	private Proveedor proveedor() {
		Proveedor proveedor = new Proveedor();
		proveedor.setIdProveedor(2L);
		proveedor.setEstado("ACTIVO");
		return proveedor;
	}
}
