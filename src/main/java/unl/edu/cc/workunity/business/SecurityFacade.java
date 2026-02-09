package unl.edu.cc.workunity.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.common.EntityRepository;
import unl.edu.cc.workunity.business.service.security.UserRepository;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.AlreadyEntityException;
import unl.edu.cc.workunity.exception.CredentialInvalidException;
import unl.edu.cc.workunity.exception.EncryptorException;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.util.EncryptorManager;

import java.io.Serializable;

@Stateless
public class SecurityFacade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserRepository userRepository;

    @Inject
    private EntityRepository entityRepository;

    public User createUser(User user) throws EncryptorException, AlreadyEntityException {
        String pwdEncrypted = EncryptorManager.encrypt(user.getPassword());
        user.setPassword(pwdEncrypted);
        try {
            userRepository.find(user.getName());
            throw new AlreadyEntityException("Usuario ya existe");
        } catch (EntityNotFoundException e) {
            return userRepository.save(user);
        }
    }

    public User registerUserWithEntity(String username, String password, String email,
            String nombre, String apellido, String telefono)
            throws AlreadyEntityException, EncryptorException {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        user = createUser(user);
        Entidad entidad = new Entidad(nombre, apellido, telefono);
        entidad.setUsuario(user);
        entidad = entityRepository.save(entidad);
        user.setEntidad(entidad);
        userRepository.save(user);
        return user;
    }

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
        }
        return userRepository.save(user);
    }

    public User authenticate(String name, String password) throws CredentialInvalidException {
        try {
            User userFound = userRepository.find(name);
            String pwdEncrypted = EncryptorManager.encrypt(password);
            if (pwdEncrypted.equals(userFound.getPassword())) {
                try {
                    Entidad entidad = entityRepository.findByUser(userFound.getId());
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
}