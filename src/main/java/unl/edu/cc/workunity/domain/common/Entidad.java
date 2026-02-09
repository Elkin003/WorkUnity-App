package unl.edu.cc.workunity.domain.common;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import unl.edu.cc.workunity.domain.common.enums.Rol;
import unl.edu.cc.workunity.domain.security.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad representa el perfil de un usuario en el sistema.
 *
 * @author Cristian Guaman
 */
@Entity
@Table(name = "entidad")
public class Entidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull
    @NotEmpty
    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(length = 15)
    private String numeroTelefono;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaCreacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private User usuario;

    @OneToMany(mappedBy = "creador", cascade = CascadeType.ALL)
    private List<Proyecto> proyectos;

    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL)
    private List<Integrante> integrantes;

    public Entidad() {
    }

    public Entidad(String nombre, String apellido, String numeroTelefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroTelefono = numeroTelefono;
        this.fechaCreacion = LocalDate.now();
    }

    public String getFullName() {
        return nombre + " " + apellido;
    }

    public List<Proyecto> listarProyectos() {
        if (proyectos == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(proyectos);
    }

    public List<Integrante> listarIntegrantes() {
        if (integrantes == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(integrantes);
    }

    public void agregarProyecto(Proyecto proyecto) {
        getProyectos();
        if (!this.proyectos.contains(proyecto)) {
            proyectos.add(proyecto);
        }
    }

    public void crearProyecto(String nombre, String descripcion, LocalDate fechaLimite) {
        Proyecto proyecto = new Proyecto(nombre, descripcion, fechaLimite, this);
        this.agregarProyecto(proyecto);
        Integrante integrante = new Integrante(Rol.LIDER, this, proyecto);
        this.getIntegrantes().add(integrante);
        proyecto.getMiembros().add(integrante);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public List<Proyecto> getProyectos() {
        if (proyectos == null) {
            proyectos = new ArrayList<>();
        }
        return proyectos;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
    }

    public List<Integrante> getIntegrantes() {
        if (integrantes == null) {
            integrantes = new ArrayList<>();
        }
        return integrantes;
    }

    public void setIntegrantes(List<Integrante> integrantes) {
        this.integrantes = integrantes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Entidad entidad = (Entidad) o;
        return Objects.equals(nombre, entidad.nombre) &&
                Objects.equals(apellido, entidad.apellido) &&
                Objects.equals(numeroTelefono, entidad.numeroTelefono);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellido, numeroTelefono);
    }

    @Override
    public String toString() {
        return "Entidad{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
