package microservice.abastecimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import microservice.abastecimiento.exception.ResourceNotFoundException;
import microservice.abastecimiento.model.Proveedor;
import microservice.abastecimiento.repository.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

	@Mock
	private ProveedorRepository proveedorRepository;

	private ProveedorService proveedorService;

	@BeforeEach
	void setUp() {
		proveedorService = new ProveedorService(proveedorRepository);
	}

	@Test
	void listarObtenerYBuscarDeleganEnRepositorio() {
		Proveedor proveedor = proveedor();
		when(proveedorRepository.findAll()).thenReturn(List.of(proveedor));
		when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
		when(proveedorRepository.findByEstado("ACTIVO")).thenReturn(List.of(proveedor));

		assertThat(proveedorService.listar()).containsExactly(proveedor);
		assertThat(proveedorService.obtenerPorId(1L)).isEqualTo(proveedor);
		assertThat(proveedorService.buscarPorEstado(" activo ")).containsExactly(proveedor);
	}

	@Test
	void obtenerPorIdLanzaErrorCuandoNoExiste() {
		when(proveedorRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> proveedorService.obtenerPorId(9L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No existe el proveedor con id 9");
	}

	@Test
	void crearAsignaEstadoActivoCuandoNoVieneEstado() {
		Proveedor proveedor = proveedor();
		proveedor.setEstado(null);
		when(proveedorRepository.save(proveedor)).thenReturn(proveedor);

		assertThat(proveedorService.crear(proveedor).getEstado()).isEqualTo("ACTIVO");
	}

	@Test
	void crearNormalizaEstadoInformado() {
		Proveedor proveedor = proveedor();
		proveedor.setEstado(" inactivo ");
		when(proveedorRepository.save(proveedor)).thenReturn(proveedor);

		assertThat(proveedorService.crear(proveedor).getEstado()).isEqualTo("INACTIVO");
	}

	@Test
	void actualizarYCambiarEstadoModificanProveedor() {
		Proveedor existente = proveedor();
		Proveedor datos = proveedor();
		datos.setNombre("Proveedor Nuevo");
		datos.setEstado("INACTIVO");
		when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(proveedorRepository.save(existente)).thenReturn(existente);

		assertThat(proveedorService.actualizar(1L, datos).getNombre()).isEqualTo("Proveedor Nuevo");
		assertThat(proveedorService.cambiarEstado(1L, "ACTIVO").getEstado()).isEqualTo("ACTIVO");
	}

	@Test
	void eliminarBorraProveedor() {
		Proveedor proveedor = proveedor();
		when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

		proveedorService.eliminar(1L);

		verify(proveedorRepository).delete(proveedor);
	}

	@Test
	void estadoInvalidoLanzaError() {
		assertThatThrownBy(() -> proveedorService.buscarPorEstado(" "))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El estado no puede estar vacio");
		assertThatThrownBy(() -> proveedorService.buscarPorEstado(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El estado no puede estar vacio");
		assertThatThrownBy(() -> proveedorService.buscarPorEstado("BLOQUEADO"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Estado de proveedor no valido: BLOQUEADO");
	}

	private Proveedor proveedor() {
		Proveedor proveedor = new Proveedor();
		proveedor.setIdProveedor(1L);
		proveedor.setRut("11.111.111-1");
		proveedor.setNombre("Proveedor Demo");
		proveedor.setCorreo("proveedor@demo.cl");
		proveedor.setTelefono("999999999");
		proveedor.setDireccion("Calle 123");
		proveedor.setEstado("ACTIVO");
		return proveedor;
	}
}
