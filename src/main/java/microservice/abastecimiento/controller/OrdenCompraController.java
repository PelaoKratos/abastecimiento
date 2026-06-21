package microservice.abastecimiento.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import microservice.abastecimiento.model.OrdenCompra;
import microservice.abastecimiento.service.OrdenCompraService;

@RestController
@RequestMapping("/api/ordenes-compra")
public class OrdenCompraController {

	private final OrdenCompraService ordenService;

	public OrdenCompraController(OrdenCompraService ordenService) {
		this.ordenService = ordenService;
	}

	@GetMapping
	public List<OrdenCompra> listar() {
		return ordenService.listar();
	}

	@GetMapping("/{id}")
	public OrdenCompra obtenerPorId(@PathVariable Long id) {
		return ordenService.obtenerPorId(id);
	}

	@GetMapping("/estado/{estado}")
	public List<OrdenCompra> buscarPorEstado(@PathVariable String estado) {
		return ordenService.buscarPorEstado(estado);
	}

	@GetMapping("/proveedor/{idProveedor}")
	public List<OrdenCompra> buscarPorProveedor(@PathVariable Long idProveedor) {
		return ordenService.buscarPorProveedor(idProveedor);
	}

	@PostMapping("/proveedor/{idProveedor}")
	public OrdenCompra crear(@PathVariable Long idProveedor, @Valid @RequestBody OrdenCompra orden) {
		return ordenService.crear(idProveedor, orden);
	}

	@PatchMapping("/{id}/aprobacion")
	public OrdenCompra aprobar(@PathVariable Long id) {
		return ordenService.aprobar(id);
	}

	@PatchMapping("/{id}/cancelacion")
	public OrdenCompra cancelar(@PathVariable Long id) {
		return ordenService.cancelar(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		ordenService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
