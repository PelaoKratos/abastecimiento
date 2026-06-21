package microservice.abastecimiento.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import microservice.abastecimiento.model.RecepcionMercancia;
import microservice.abastecimiento.service.RecepcionMercanciaService;

@RestController
@RequestMapping("/api/recepciones")
public class RecepcionMercanciaController {

	private final RecepcionMercanciaService recepcionService;

	public RecepcionMercanciaController(RecepcionMercanciaService recepcionService) {
		this.recepcionService = recepcionService;
	}

	@GetMapping
	public List<RecepcionMercancia> listar() {
		return recepcionService.listar();
	}

	@GetMapping("/{id}")
	public RecepcionMercancia obtenerPorId(@PathVariable Long id) {
		return recepcionService.obtenerPorId(id);
	}

	@GetMapping("/orden/{idOrdenCompra}")
	public List<RecepcionMercancia> buscarPorOrden(@PathVariable Long idOrdenCompra) {
		return recepcionService.buscarPorOrden(idOrdenCompra);
	}

	@PostMapping("/orden/{idOrdenCompra}")
	public RecepcionMercancia registrar(@PathVariable Long idOrdenCompra,
			@Valid @RequestBody RecepcionMercancia recepcion) {
		return recepcionService.registrar(idOrdenCompra, recepcion);
	}

	@PatchMapping("/{id}/confirmacion")
	public RecepcionMercancia confirmar(@PathVariable Long id) {
		return recepcionService.confirmar(id);
	}
}
