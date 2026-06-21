package microservice.abastecimiento.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrdenCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idOrdenCompra;

	@NotNull(message = "El id de sucursal es obligatorio")
	private Long idSucursal;

	@NotNull(message = "El id de usuario es obligatorio")
	private Long idUsuario;

	@Column(nullable = false)
	private LocalDateTime fechaOrden;

	private LocalDate fechaEstimada;

	@PositiveOrZero(message = "El subtotal no puede ser negativo")
	private BigDecimal subtotal = BigDecimal.ZERO;

	@PositiveOrZero(message = "El total no puede ser negativo")
	private BigDecimal total = BigDecimal.ZERO;

	@Column(nullable = false)
	private String estado;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proveedor", nullable = false)
	private Proveedor proveedor;

	@Valid
	@JsonManagedReference
	@OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleOrdenCompra> detalles = new ArrayList<>();

	@JsonManagedReference
	@OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RecepcionMercancia> recepciones = new ArrayList<>();
}
