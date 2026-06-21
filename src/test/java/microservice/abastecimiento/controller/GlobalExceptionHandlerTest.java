package microservice.abastecimiento.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import microservice.abastecimiento.exception.ResourceNotFoundException;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	void manejarNoEncontradoRetornaNotFound() {
		ResponseEntity<Map<String, Object>> respuesta = handler
				.manejarNoEncontrado(new ResourceNotFoundException("No existe"));

		assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(respuesta.getBody()).containsEntry("status", 404);
		assertThat(respuesta.getBody()).containsEntry("message", "No existe");
	}

	@Test
	void manejarSolicitudInvalidaRetornaBadRequest() {
		ResponseEntity<Map<String, Object>> respuesta = handler
				.manejarSolicitudInvalida(new IllegalArgumentException("Dato invalido"));

		assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(respuesta.getBody()).containsEntry("status", 400);
		assertThat(respuesta.getBody()).containsEntry("message", "Dato invalido");
	}

	@Test
	void manejarValidacionRetornaPrimerErrorOMensajeGenerico() {
		MethodArgumentNotValidException exception = crearValidacionException(
				List.of(new FieldError("proveedor", "rut", "El rut es obligatorio")));
		ResponseEntity<Map<String, Object>> respuesta = handler.manejarValidacion(exception);
		assertThat(respuesta.getBody()).containsEntry("message", "El rut es obligatorio");

		MethodArgumentNotValidException sinErrores = crearValidacionException(List.of());
		assertThat(handler.manejarValidacion(sinErrores).getBody()).containsEntry("message", "Solicitud invalida");
	}

	private MethodArgumentNotValidException crearValidacionException(List<FieldError> errores) {
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors()).thenReturn(errores);
		return new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);
	}
}
