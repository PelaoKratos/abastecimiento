package microservice.abastecimiento.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Proveedor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idProveedor;

	@NotBlank(message = "El rut es obligatorio")
	@Column(nullable = false)
	private String rut;

	@NotBlank(message = "El nombre es obligatorio")
	@Column(nullable = false)
	private String nombre;

	@NotBlank(message = "El correo es obligatorio")
	@Column(nullable = false)
	private String correo;

	private String telefono;
	private String direccion;

	@Column(nullable = false)
	private String estado;

	@JsonManagedReference
	@OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
	private List<OrdenCompra> ordenesCompra = new ArrayList<>();
}
