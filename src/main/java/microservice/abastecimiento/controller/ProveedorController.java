package microservice.abastecimiento.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import microservice.abastecimiento.model.Proveedor;
import microservice.abastecimiento.service.ProveedorService;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

	private final ProveedorService proveedorService;

	public ProveedorController(ProveedorService proveedorService) {
		this.proveedorService = proveedorService;
	}

	@GetMapping
	public List<Proveedor> listar() {
		return proveedorService.listar();
	}

	@GetMapping("/{id}")
	public Proveedor obtenerPorId(@PathVariable Long id) {
		return proveedorService.obtenerPorId(id);
	}

	@GetMapping("/estado/{estado}")
	public List<Proveedor> buscarPorEstado(@PathVariable String estado) {
		return proveedorService.buscarPorEstado(estado);
	}

	@PostMapping
	public Proveedor crear(@Valid @RequestBody Proveedor proveedor) {
		return proveedorService.crear(proveedor);
	}

	@PutMapping("/{id}")
	public Proveedor actualizar(@PathVariable Long id, @Valid @RequestBody Proveedor proveedor) {
		return proveedorService.actualizar(id, proveedor);
	}

	@PatchMapping("/{id}/estado")
	public Proveedor cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
		return proveedorService.cambiarEstado(id, request.get("estado"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		proveedorService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
