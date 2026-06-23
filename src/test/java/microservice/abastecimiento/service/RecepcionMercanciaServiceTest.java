package microservice.abastecimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import microservice.abastecimiento.exception.ResourceNotFoundException;
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
	void registrarAsociaOrdenYMarcaRecepcion() {
		RecepcionMercancia recepcion = recepcion();
		recepcion.setFechaRecepcion(null);
		when(ordenRepository.findById(2L)).thenReturn(Optional.of(orden("APROBADA")));
		when(recepcionRepository.save(recepcion)).thenReturn(recepcion);

		RecepcionMercancia resultado = recepcionService.registrar(2L, recepcion);

		assertThat(resultado.getFechaRecepcion()).isNotNull();
		assertThat(resultado.getEstado()).isEqualTo("REGISTRADA");
		assertThat(resultado.getOrdenCompra()).isNotNull();
		assertThat(resultado.getIdOrdenCompra()).isEqualTo(2L);
	}

	@Test
	void registrarFallaSiOrdenNoExisteONoEstaAprobada() {
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
	}

	@Test
	void obtenerPorIdLanzaErrorCuandoNoExiste() {
		when(recepcionRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> recepcionService.obtenerPorId(9L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No existe la recepcion con id 9");
	}

	@Test
	void confirmarMarcaConfirmadaYRecepcionaOrden() {
		RecepcionMercancia recepcion = recepcion();
		when(recepcionRepository.findById(1L)).thenReturn(Optional.of(recepcion));
		when(recepcionRepository.save(recepcion)).thenReturn(recepcion);

		assertThat(recepcionService.confirmar(1L).getEstado()).isEqualTo("CONFIRMADA");
		assertThat(recepcion.getOrdenCompra().getEstado()).isEqualTo("RECEPCIONADA");
	}

	private RecepcionMercancia recepcion() {
		RecepcionMercancia recepcion = new RecepcionMercancia();
		recepcion.setIdRecepcion(1L);
		recepcion.setIdUsuario(30L);
		recepcion.setFechaRecepcion(LocalDateTime.of(2026, 6, 21, 12, 0));
		recepcion.setOrdenCompra(orden("APROBADA"));
		return recepcion;
	}

	private OrdenCompra orden(String estado) {
		OrdenCompra orden = new OrdenCompra();
		orden.setIdOrdenCompra(2L);
		orden.setEstado(estado);
		return orden;
	}
}
