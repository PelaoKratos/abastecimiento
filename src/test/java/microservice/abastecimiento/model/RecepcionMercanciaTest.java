package microservice.abastecimiento.model;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class RecepcionMercanciaTest {

	@Test
	void registrarRecepcionAsignaFechaCuandoNoExisteYEstadoRegistrada() {
		RecepcionMercancia recepcion = new RecepcionMercancia();

		recepcion.registrarRecepcion();

		assertThat(recepcion.getFechaRecepcion()).isNotNull();
		assertThat(recepcion.getEstado()).isEqualTo("REGISTRADA");
	}

	@Test
	void registrarRecepcionMantieneFechaExistente() {
		LocalDateTime fecha = LocalDateTime.of(2026, 6, 28, 9, 30);
		RecepcionMercancia recepcion = new RecepcionMercancia();
		recepcion.setFechaRecepcion(fecha);

		recepcion.registrarRecepcion();

		assertThat(recepcion.getFechaRecepcion()).isEqualTo(fecha);
		assertThat(recepcion.getEstado()).isEqualTo("REGISTRADA");
	}

	@Test
	void validarYConfirmarActualizanEstado() {
		RecepcionMercancia recepcion = new RecepcionMercancia();

		recepcion.validarMercancia();
		assertThat(recepcion.getEstado()).isEqualTo("VALIDADA");

		recepcion.confirmarRecepcion();
		assertThat(recepcion.getEstado()).isEqualTo("CONFIRMADA");
	}

	@Test
	void setOrdenCompraSincronizaIdOrdenCompra() {
		OrdenCompra orden = new OrdenCompra();
		orden.setIdOrdenCompra(15L);
		RecepcionMercancia recepcion = new RecepcionMercancia();

		recepcion.setOrdenCompra(orden);

		assertThat(recepcion.getOrdenCompra()).isSameAs(orden);
		assertThat(recepcion.getIdOrdenCompra()).isEqualTo(15L);

		recepcion.setOrdenCompra(null);

		assertThat(recepcion.getOrdenCompra()).isNull();
		assertThat(recepcion.getIdOrdenCompra()).isNull();
	}
}
