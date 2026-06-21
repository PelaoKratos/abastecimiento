package microservice.abastecimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
import microservice.abastecimiento.model.DetalleRecepcionMercancia;
import microservice.abastecimiento.model.OrdenCompra;
import microservice.abastecimiento.model.RecepcionMercancia;
import microservice.abastecimiento.repository.OrdenCompraRepository;
import microservice.abastecimiento.repository.RecepcionMercanciaRepository;

@ExtendWith(MockitoExtension.class)
class RecepcionMercanciaServiceTest {

	@Mock
	private RecepcionMercanciaRepository recepcionRepository;

	@Mock
	private OrdenCompraRepository ordenRepository;

	private RecepcionMercanciaService recepcionService;

	@BeforeEach
	void setUp() {
		recepcionService = new RecepcionMercanciaService(recepcionRepository, ordenRepository);
	}

	@Test
	void listarObtenerYBuscarPorOrdenDeleganEnRepositorio() {
		RecepcionMercancia recepcion = recepcion();
		when(recepcionRepository.findAll()).thenReturn(List.of(recepcion));
		when(recepcionRepository.findById(1L)).thenReturn(Optional.of(recepcion));
		when(recepcionRepository.findByOrdenCompraIdOrdenCompra(2L)).thenReturn(List.of(recepcion));

		assertThat(recepcionService.listar()).containsExactly(recepcion);
		assertThat(recepcionService.obtenerPorId(1L)).isEqualTo(recepcion);
		assertThat(recepcionService.buscarPorOrden(2L)).containsExactly(recepcion);
	}

	@Test
	void registrarAsociaOrdenYPreparaDetalles() {
		RecepcionMercancia recepcion = recepcion();
		recepcion.setFechaRecepcion(null);
		when(ordenRepository.findById(2L)).thenReturn(Optional.of(orden("APROBADA")));
		when(recepcionRepository.save(recepcion)).thenReturn(recepcion);

		RecepcionMercancia resultado = recepcionService.registrar(2L, recepcion);

		assertThat(resultado.getFechaRecepcion()).isNotNull();
		assertThat(resultado.getEstado()).isEqualTo("REGISTRADA");
		assertThat(resultado.getDetalles().get(0).getEstado()).isEqualTo("COMPLETO");
		assertThat(resultado.getDetalles().get(0).getRecepcion()).isEqualTo(resultado);
	}

	@Test
	void registrarDetalleParcial() {
		RecepcionMercancia recepcion = recepcion();
		recepcion.getDetalles().get(0).setCantidadRecibida(1);
		when(ordenRepository.findById(2L)).thenReturn(Optional.of(orden("APROBADA")));
		when(recepcionRepository.save(recepcion)).thenReturn(recepcion);

		assertThat(recepcionService.registrar(2L, recepcion).getDetalles().get(0).getEstado()).isEqualTo("PARCIAL");
	}

	@Test
	void registrarFallaSiOrdenNoExisteNoEstaAprobadaOSinDetallesOCantidadMayor() {
		RecepcionMercancia recepcionOrdenInexistente = recepcion();
		when(ordenRepository.findById(99L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> recepcionService.registrar(99L, recepcionOrdenInexistente))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No existe la orden de compra con id 99");

		when(ordenRepository.findById(2L)).thenReturn(Optional.of(orden("CREADA")));
		RecepcionMercancia recepcionOrdenNoAprobada = recepcion();
		assertThatThrownBy(() -> recepcionService.registrar(2L, recepcionOrdenNoAprobada))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Solo se puede recepcionar una orden aprobada");

		when(ordenRepository.findById(3L)).thenReturn(Optional.of(orden("APROBADA")));
		RecepcionMercancia recepcionSinDetalles = recepcion();
		recepcionSinDetalles.setDetalles(new ArrayList<>());
		assertThatThrownBy(() -> recepcionService.registrar(3L, recepcionSinDetalles))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("La recepcion debe tener al menos un detalle");

		RecepcionMercancia recepcionCantidadMayor = recepcion();
		recepcionCantidadMayor.getDetalles().get(0).setCantidadRecibida(5);
		when(ordenRepository.findById(4L)).thenReturn(Optional.of(orden("APROBADA")));
		assertThatThrownBy(() -> recepcionService.registrar(4L, recepcionCantidadMayor))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("La cantidad recibida no puede superar la cantidad esperada");
	}

	@Test
	void obtenerPorIdLanzaErrorCuandoNoExiste() {
		when(recepcionRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> recepcionService.obtenerPorId(9L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No existe la recepcion con id 9");
	}

	@Test
	void confirmarMarcaConfirmadaOConDiferenciasYRecepcionaOrden() {
		RecepcionMercancia recepcion = recepcion();
		when(recepcionRepository.findById(1L)).thenReturn(Optional.of(recepcion));
		when(recepcionRepository.save(recepcion)).thenReturn(recepcion);

		assertThat(recepcionService.confirmar(1L).getEstado()).isEqualTo("CONFIRMADA");
		assertThat(recepcion.getOrdenCompra().getEstado()).isEqualTo("RECEPCIONADA");

		recepcion.getDetalles().get(0).setCantidadRecibida(1);
		assertThat(recepcionService.confirmar(1L).getEstado()).isEqualTo("CON_DIFERENCIAS");
	}

	private RecepcionMercancia recepcion() {
		RecepcionMercancia recepcion = new RecepcionMercancia();
		recepcion.setIdRecepcion(1L);
		recepcion.setIdUsuario(30L);
		recepcion.setFechaRecepcion(LocalDateTime.of(2026, 6, 21, 12, 0));
		recepcion.setOrdenCompra(orden("APROBADA"));
		recepcion.setDetalles(new ArrayList<>(List.of(detalle())));
		return recepcion;
	}

	private DetalleRecepcionMercancia detalle() {
		DetalleRecepcionMercancia detalle = new DetalleRecepcionMercancia();
		detalle.setIdProducto(100L);
		detalle.setCantidadEsperada(2);
		detalle.setCantidadRecibida(2);
		return detalle;
	}

	private OrdenCompra orden(String estado) {
		OrdenCompra orden = new OrdenCompra();
		orden.setIdOrdenCompra(2L);
		orden.setEstado(estado);
		return orden;
	}
}
