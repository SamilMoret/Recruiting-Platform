package br.com.one.jobportal.controller;

import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// src/main/java/br/com/one/jobportal/controller/UserProfileController.java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Perfil de Usuário", description = "Gerenciamento de perfis de usuário")
public class UserProfileController {

    private final UserService userService;

    @Operation(summary = "Obter perfil do usuário atual",
            description = "Retorna os dados do usuário autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado")
            })
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @Operation(summary = "Atualizar perfil do usuário atual",
            description = "Atualiza os dados do usuário autenticado",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    name = "Atualização de perfil",
                                    summary = "Exemplo de atualização",
                                    value = """
                               {
                                   "name": "Novo Nome",
                                   "email": "novoemail@exemplo.com",
                                   "phone": "(11) 91234-5678",
                                   "companyName": "Nova Empresa",
                                   "companyDescription": "Descrição da empresa"
                               }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado")
            })
    @PutMapping("/me")
    @Transactional
    public ResponseEntity<User> updateCurrentUser(@Valid @RequestBody User userDetails,
                                                  @AuthenticationPrincipal User currentUser) {
        // Update the current user's profile with the new details
        userDetails.setId(currentUser.getId());
        return ResponseEntity.ok(userService.updateUserProfile(userDetails));
    }


    @Operation(summary = "Obter perfil de um usuário por ID",
               description = "Retorna os dados de um usuário específico (apenas para administradores)",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado"),
                   @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
               })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Atualizar perfil de um usuário",
               description = "Atualiza os dados de um usuário específico (apenas para administradores)",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado"),
                   @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
               })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
}
