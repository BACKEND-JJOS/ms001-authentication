-- podman machine start
-- podman start mariadb-ms001 ||| o el otro
-- podman run --name mariadb-ms001 -e MARIADB_ROOT_PASSWORD=root -e MARIADB_DATABASE=ms001_authentication_db -p 3306:3306 -d mariadb:10.11
-- podman exec -it mariadb-ms001 sh
-- mariadb -uroot -proot
-- USE ms001_authentication_db;



CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    documento_identidad VARCHAR(50) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    id_rol INT NULL,
    salario_base DECIMAL(15,2) NOT NULL,
    password VARCHAR(250) NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- Inserts iniciales en tabla rol
INSERT INTO rol (nombre, descripcion) VALUES ('ADMIN', 'Administrador del sistema');
INSERT INTO rol (nombre, descripcion) VALUES ('ASESOR', 'Usuario asesor del sistema');
INSERT INTO rol (nombre, descripcion) VALUES ('CLIENTE', 'Usuario cliente del sistema');

