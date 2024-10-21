package pe.edu.cibertec.patitas_frontend_wc_a.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.patitas_frontend_wc_a.client.AutenticacionClient;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutResponseDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/logout")
@CrossOrigin("http://localhost:5173/")
public class LoginControllerFeign {

    @Autowired
    AutenticacionClient autenticacionClient;

    @PostMapping("/cerrar-sesion-feign")
    public Mono<ResponseEntity<LogoutResponseDTO>> cerrarSesion(@RequestBody LogoutRequestDTO logoutRequestDTO) {

        System.out.println("Cerrar sesión con Feign");

        // Realizar la solicitud al servicio de autenticación
        return Mono.fromCallable(() -> autenticacionClient.logout(logoutRequestDTO))
            .flatMap(responseEntity -> { // Realizar el mapeo de la respuesta
                // Verificar si la respuesta es exitosa
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    // Obtener el cuerpo de la respuesta
                    LogoutResponseDTO logoutResponseDTO = responseEntity.getBody();
                    // Verificar si la respuesta es exitosa
                    if (logoutResponseDTO != null && logoutResponseDTO.codigo().equals("00")) {
                        return Mono.just(ResponseEntity.ok(logoutResponseDTO));
                    } else {
                        return Mono.just(ResponseEntity.status(401).body(new LogoutResponseDTO("02", "Error: No se pudo cerrar la sesión.")));
                    }
                } else {
                    return Mono.just(ResponseEntity.status(500).body(new LogoutResponseDTO("99", "Error: Ocurrió un problema en el cierre de sesión.")));
                }
        }).onErrorResume(e -> { // Manejo de errores
            // Imprimir el error
            System.out.println(e.getMessage());
            // Retornar la respuesta de error
            return Mono.just(ResponseEntity.status(500).body(new LogoutResponseDTO("99", "Error: Ocurrió un problema en el cierre de sesión.")));
        });

    }

}
