CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    documento_identidad VARCHAR(50) UNIQUE NOT NULL,
    telefono VARCHAR(50),
    id_rol INT,
    salario_base DOUBLE,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- Inserts iniciales en tabla rol
INSERT INTO rol (nombre, descripcion) VALUES ('ADMIN', 'Administrador del sistema');
INSERT INTO rol (nombre, descripcion) VALUES ('USER', 'Usuario estándar');
