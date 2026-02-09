INSERT INTO user_ (id, name, password, email) VALUES (1, 'elkin', 'uVQoLxtZvlhBuamIlWRLGQ==', 'elkin@workunity.com');
INSERT INTO user_ (id, name, password, email) VALUES (2, 'leonel', 'uVQoLxtZvlhBuamIlWRLGQ==', 'leonel@workunity.com');
INSERT INTO user_ (id, name, password, email) VALUES (3, 'cristian', 'uVQoLxtZvlhBuamIlWRLGQ==', 'cristian@workunity.com');
INSERT INTO user_ (id, name, password, email) VALUES (4, 'jose', 'uVQoLxtZvlhBuamIlWRLGQ==', 'jose@workunity.com');

INSERT INTO entidad (id, nombre, apellido, numerotelefono, fechacreacion, usuario_id) VALUES (1, 'Elkin', 'Jiménez', '0987654321', CURRENT_DATE, 1);
INSERT INTO entidad (id, nombre, apellido, numerotelefono, fechacreacion, usuario_id) VALUES (2, 'Leonel', 'Lima', '0976543210', CURRENT_DATE, 2);
INSERT INTO entidad (id, nombre, apellido, numerotelefono, fechacreacion, usuario_id) VALUES (3, 'Cristian', 'Guamán', '0965432109', CURRENT_DATE, 3);
INSERT INTO entidad (id, nombre, apellido, numerotelefono, fechacreacion, usuario_id) VALUES (4, 'Jose', 'Salazar', '0954321098', CURRENT_DATE, 4);

INSERT INTO proyecto (id, nombre, descripcion, fechacreacion, fechalimite, creador_id) VALUES (1, 'WorkUnity', 'Una aplicación web para la gestión de proyectos académicos en equipo', CURRENT_DATE, CURRENT_DATE + 90, 1);

INSERT INTO integrante (id, rol, fechaunion, entidad_id, proyecto_id) VALUES (1, 'LIDER', CURRENT_DATE, 1, 1);
INSERT INTO integrante (id, rol, fechaunion, entidad_id, proyecto_id) VALUES (2, 'MIEMBRO', CURRENT_DATE, 2, 1);
INSERT INTO integrante (id, rol, fechaunion, entidad_id, proyecto_id) VALUES (3, 'MIEMBRO', CURRENT_DATE, 3, 1);
INSERT INTO integrante (id, rol, fechaunion, entidad_id, proyecto_id) VALUES (4, 'MIEMBRO', CURRENT_DATE, 4, 1);

INSERT INTO tarea (id, titulo, descripcion, entregada, estado, fechaasignacion, fechalimite, proyecto_id) VALUES (1, 'Configurar Docker', 'Configurar Docker Compose y Dockerfile', false, 'EN_CURSO', CURRENT_DATE, CURRENT_DATE + 30, 1);
INSERT INTO tarea (id, titulo, descripcion, entregada, estado, fechaasignacion, fechalimite, integrante_asignado_id, proyecto_id) VALUES (2, 'Diseñar Interfaz de Usuario', 'Crear mockups y prototipos de las pantallas principales de la aplicación', false, 'EN_CURSO', CURRENT_DATE, CURRENT_DATE + 14, 2, 1);
INSERT INTO tarea (id, titulo, descripcion, entregada, estado, fechaasignacion, fechalimite, integrante_asignado_id, proyecto_id) VALUES (3, 'Implementar Autenticación', 'Desarrollar sistema de login, gestión de sesiones y autorización de usuarios', true, 'COMPLETADA', CURRENT_DATE - 10, CURRENT_DATE - 2, 3, 1);
INSERT INTO tarea (id, titulo, descripcion, entregada, estado, fechaasignacion, fechalimite, integrante_asignado_id, proyecto_id) VALUES (4, 'Implementar Lógica de Negocio', 'Desarrollar facades, services y controllers para gestión de proyectos, tareas, integrantes y comentarios', false, 'EN_CURSO', CURRENT_DATE, CURRENT_DATE + 10, 4, 1);

INSERT INTO comentario (id, texto, fechacreacion, autor_id, tarea_id) VALUES (1, 'Voy a usar Figma para los mockups. ¿Qué paleta de colores prefieren para la interfaz?', CURRENT_DATE, 2, 2);
INSERT INTO comentario (id, texto, fechacreacion, autor_id, tarea_id) VALUES (2, 'He terminado los prototipos de login y gestión de proyectos. Por favor revisen el diseño.', CURRENT_DATE + 1, 2, 2);
INSERT INTO comentario (id, texto, fechacreacion, autor_id, tarea_id) VALUES (3, 'Excelente trabajo Leonel! El diseño se ve muy profesional y moderno.', CURRENT_DATE + 2, 3, 2);
INSERT INTO comentario (id, texto, fechacreacion, autor_id, tarea_id) VALUES (4, 'He implementado el login. Ya está funcionando.', CURRENT_DATE - 2, 3, 3);
INSERT INTO comentario (id, texto, fechacreacion, autor_id, tarea_id) VALUES (5, 'Perfecto Cristian! El sistema de autenticación funciona de maravilla. Tarea completada.', CURRENT_DATE - 1, 1, 3);
INSERT INTO comentario (id, texto, fechacreacion, autor_id, tarea_id) VALUES (6, 'Estoy trabajando en los facades para proyectos y tareas. ¿Alguna preferencia en la estructura?', CURRENT_DATE, 4, 4);
INSERT INTO comentario (id, texto, fechacreacion, autor_id, tarea_id) VALUES (7, 'Jose, te recomiendo seguir el patrón que usamos en SecurityFacade. Mantiene todo consistente.', CURRENT_DATE + 1, 1, 4);

SELECT setval('user__id_seq', (SELECT MAX(id) FROM user_));
SELECT setval('entidad_id_seq', (SELECT MAX(id) FROM entidad));
SELECT setval('proyecto_id_seq', (SELECT MAX(id) FROM proyecto));
SELECT setval('integrante_id_seq', (SELECT MAX(id) FROM integrante));
SELECT setval('tarea_id_seq', (SELECT MAX(id) FROM tarea));
SELECT setval('comentario_id_seq', (SELECT MAX(id) FROM comentario));

COMMIT;