package modulos.Front.providers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import modulos.Front.dtos.input.AuthResponseDTO;
import modulos.Front.dtos.input.LoginDtoInput;
import modulos.Front.services.WebApiCallerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

    private final WebApiCallerService webApiCallerService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {


        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try{
            LoginDtoInput dtoInput = LoginDtoInput.builder()
                    .nombreDeUsuario(username)
                    .contrasenia(password)
                    .build();
            ResponseEntity<?> rta = webApiCallerService.login(dtoInput, AuthResponseDTO.class);

            if (!rta.getStatusCode().is2xxSuccessful()){
                throw new BadCredentialsException("Usuario o contraseña inválidos");
            }

            AuthResponseDTO authResponse = (AuthResponseDTO) rta.getBody();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            request.getSession().setAttribute("accessToken", authResponse.getAccessToken());
            request.getSession().setAttribute("refreshToken", authResponse.getRefreshToken());
            request.getSession().setAttribute("username", username);

            List<GrantedAuthority> authorities = new ArrayList<>();

            authorities.add(new SimpleGrantedAuthority("ROLE_" + authResponse.getRol().name()));

            return new UsernamePasswordAuthenticationToken(username, password, authorities);

        } catch (RuntimeException e){
            throw new BadCredentialsException("Error en el sistema de autenticación: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
