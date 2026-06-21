package microservice.abastecimiento.model;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RecepcionMercancia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRecepcion;

	@NotNull(message = "El id de usuario es obligatorio")
	private Long idUsuario;

	@Column(nullable = false)
	private LocalDateTime fechaRecepcion;

	private String observacion;

	@Column(nullable = false)
	private String estado;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orden_compra", nullable = false)
	private OrdenCompra ordenCompra;

	@Valid
	@JsonManagedReference
	@OneToMany(mappedBy = "recepcion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleRecepcionMercancia> detalles = new ArrayList<>();
}
