package pe.edu.cibertec.patitas_frontend_wc_a.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutResponseDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
@CrossOrigin("http://localhost:5173/")
public class LoginControllerAsync {

    @Autowired
    WebClient webClientAutenticacion;

    @PostMapping("/autenticar-async")
    public Mono<LoginResponseDTO> autenticar(@RequestBody LoginRequestDTO loginRequestDTO) {

        // Validar campos de entrada
        if (loginRequestDTO.tipoDocumento() == null || loginRequestDTO.tipoDocumento().trim().isEmpty() ||
            loginRequestDTO.numeroDocumento() == null || loginRequestDTO.numeroDocumento().trim().isEmpty() ||
            loginRequestDTO.password() == null || loginRequestDTO.password().trim().isEmpty()) {

            return  Mono.just(new LoginResponseDTO("01", "Error: Debe completar correctamente sus credenciales", "", ""));

        }

        try {

            // Realizamos la solicitud al servicio
            return webClientAutenticacion.post()
                    .uri("http://localhost:8081/autenticacion/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .flatMap(response -> {
                        if(response.codigo().equals("00")) {
                            return Mono.just(new LoginResponseDTO("00", "", response.nombreUsuario(), ""));
                        } else {
                            return Mono.just(new LoginResponseDTO("02", "Error: Credenciales incorrectas", "", ""));
                        }
                    });

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return Mono.just(new LoginResponseDTO("99", "Error: Ocurrió un problema en al autenticación.", "", ""));

        }

    }

    @PostMapping("/cerrar-sesion-async")
    public Mono<LogoutResponseDTO> cerrarSesion(@RequestBody LogoutRequestDTO logoutRequestDTO) {

        // Validar campos de entrada
        if (logoutRequestDTO.tipoDocumento() == null || logoutRequestDTO.tipoDocumento().trim().isEmpty() ||
            logoutRequestDTO.numeroDocumento() == null || logoutRequestDTO.numeroDocumento().trim().isEmpty()) {

            return  Mono.just(new LogoutResponseDTO("01", "Error: Debe completar correctamente sus credenciales"));

        }

        try {

            // Realizamos la solicitud al servicio
            return webClientAutenticacion.post()
                    .uri("http://localhost:8081/autenticacion/logout")
                    .body(Mono.just(logoutRequestDTO), LogoutRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LogoutResponseDTO.class)
                    .flatMap(response -> {
                        if(response.codigo().equals("00")) {
                            return Mono.just(new LogoutResponseDTO("00", ""));
                        } else {
                            return Mono.just(new LogoutResponseDTO("02", "Error: No se puedo cerrar sesión"));
                        }
                    });

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return Mono.just(new LogoutResponseDTO("99", "Error: Ocurrió un problema en al cerrar sesión."));

        }

    }


}
