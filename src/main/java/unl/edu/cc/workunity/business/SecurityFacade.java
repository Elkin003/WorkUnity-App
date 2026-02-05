package unl.edu.cc.workunity.business;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.security.EntityRepository;
import unl.edu.cc.workunity.business.service.security.UserRepository;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.AlreadyEntityException;
import unl.edu.cc.workunity.exception.CredentialInvalidException;
import unl.edu.cc.workunity.exception.EncryptorException;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.util.EncryptorManager;

import java.io.Serializable;

/**
 * Facade de seguridad para autenticación y gestión de usuarios
 */
@Stateless
public class SecurityFacade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserRepository userRepository;

    @Inject
    private EntityRepository entityRepository;

    /**
     * Crea un usuario (credenciales) solamente
     */
    public User createUser(User user) throws EncryptorException, AlreadyEntityException {
        String pwdEncrypted = EncryptorManager.encrypt(user.getPassword());
        user.setPassword(pwdEncrypted);

        try {
            userRepository.find(user.getName());
            // Si encuentra el usuario, significa que ya existe
            throw new AlreadyEntityException("Usuario ya existe");
        } catch (EntityNotFoundException e) {
            // No existe, podemos crear
            return userRepository.save(user);
        }
    }

    /**
     * Registra un usuario completo: crea User (credenciales) y Entidad (perfil)
     * y los vincula entre sí
     */
    public User registerUserWithEntity(String username, String password, String email,
                                       String nombre, String apellido, String telefono)
            throws AlreadyEntityException, EncryptorException {

        // Crear usuario (credenciales)
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);

        // Guardar usuario con contraseña encriptada
        user = createUser(user);

        // Crear entidad (perfil)
        Entidad entidad = new Entidad(nombre, apellido, telefono);
        entidad.setUsuario(user);

        // Guardar entidad
        entidad = entityRepository.save(entidad);

        // Vincular entidad con usuario
        user.setEntidad(entidad);
        userRepository.save(user);

        return user;
    }

    /**
     * Autentica un usuario verificando credenciales
     */
    public User authenticate(String name, String password)
            throws CredentialInvalidException {
        try {
            User userFound = userRepository.find(name);
            String pwdEncrypted = EncryptorManager.encrypt(password);

            if (pwdEncrypted.equals(userFound.getPassword())) {
                // IMPORTANTE: Cargar la Entidad asociada
                try {
                    Entidad entidad = entityRepository.findByUser(userFound);
                    userFound.setEntidad(entidad);
                    System.out.println("Entidad cargada: " + entidad.getFullName());
                } catch (EntityNotFoundException e) {
                    System.out.println("Usuario sin Entidad: " + userFound.getName());
                }
                return userFound;
            }
            throw new CredentialInvalidException();

        } catch (EntityNotFoundException e) {
            throw new CredentialInvalidException();
        } catch (EncryptorException e) {
            throw new CredentialInvalidException("Credenciales incorrectas", e);
        }
    }

    /**
     * Actualiza un usuario existente
     */
    public User updateUser(User user) throws AlreadyEntityException, EncryptorException {
        if (user.getId() == null) {
            return createUser(user);
        }

        String pwdEncrypted = EncryptorManager.encrypt(user.getPassword());
        user.setPassword(pwdEncrypted);

        try {
            User userFound = userRepository.find(user.getName());
            if (!userFound.getId().equals(user.getId())) {
                throw new AlreadyEntityException("Ya existe otro usuario con ese nombre");
            }
        } catch (EntityNotFoundException ignored) {
            // No problem, nombre disponible
        }

        return userRepository.save(user);
    }

    /**
     * Inicializa datos de prueba al arrancar la aplicación
     */
    @PostConstruct
    public void initDemoData() {
        try {
            registerUserWithEntity(
                    "admin", // username
                    "12345678", // password
                    "admin@workunity.com", // email
                    "Elkin", // nombre
                    "Jimenez", // apellido
                    "0987654321" // teléfono
            );
            System.out.println("Usuario de prueba creado: admin / 12345678");
        } catch (Exception e) {
            System.out.println("Usuario de prueba ya existe o error: " + e.getMessage());
        }
    }
}
