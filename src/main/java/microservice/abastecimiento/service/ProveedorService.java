package microservice.abastecimiento.service;

import java.util.List;

import org.springframework.stereotype.Service;

import microservice.abastecimiento.exception.ResourceNotFoundException;
import microservice.abastecimiento.model.Proveedor;
import microservice.abastecimiento.repository.ProveedorRepository;

@Service
public class ProveedorService {

	private static final String ESTADO_ACTIVO = "ACTIVO";
	private static final String ESTADO_INACTIVO = "INACTIVO";

	private final ProveedorRepository proveedorRepository;

	public ProveedorService(ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}

	public List<Proveedor> listar() {
		return proveedorRepository.findAll();
	}

	public Proveedor obtenerPorId(Long id) {
		return buscarProveedor(id);
	}

	public List<Proveedor> buscarPorEstado(String estado) {
		return proveedorRepository.findByEstado(normalizarEstado(estado));
	}

	public Proveedor crear(Proveedor proveedor) {
		if (proveedor.getEstado() == null || proveedor.getEstado().isBlank()) {
			proveedor.setEstado(ESTADO_ACTIVO);
		} else {
			proveedor.setEstado(normalizarEstado(proveedor.getEstado()));
		}
		return proveedorRepository.save(proveedor);
	}

	public Proveedor actualizar(Long id, Proveedor datosProveedor) {
		Proveedor proveedor = buscarProveedor(id);
		proveedor.setRut(datosProveedor.getRut());
		proveedor.setNombre(datosProveedor.getNombre());
		proveedor.setCorreo(datosProveedor.getCorreo());
		proveedor.setTelefono(datosProveedor.getTelefono());
		proveedor.setDireccion(datosProveedor.getDireccion());
		proveedor.setEstado(normalizarEstado(datosProveedor.getEstado()));
		return proveedorRepository.save(proveedor);
	}

	public Proveedor cambiarEstado(Long id, String estado) {
		Proveedor proveedor = buscarProveedor(id);
		proveedor.setEstado(normalizarEstado(estado));
		return proveedorRepository.save(proveedor);
	}

	public void eliminar(Long id) {
		Proveedor proveedor = buscarProveedor(id);
		proveedorRepository.delete(proveedor);
	}

	private Proveedor buscarProveedor(Long id) {
		return proveedorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el proveedor con id " + id));
	}

	private String normalizarEstado(String estado) {
		if (estado == null || estado.isBlank()) {
			throw new IllegalArgumentException("El estado no puede estar vacio");
		}
		String estadoNormalizado = estado.trim().toUpperCase();
		if (!List.of(ESTADO_ACTIVO, ESTADO_INACTIVO).contains(estadoNormalizado)) {
			throw new IllegalArgumentException("Estado de proveedor no valido: " + estado);
		}
		return estadoNormalizado;
	}
}
