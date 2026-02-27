package co.edu.eci.blueprints.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blueprints")
@Tag(name = "Blueprints", description = "CRUD de planos arquitectonicos - requiere JWT valido")
@SecurityRequirement(name = "bearer-jwt")
public class BlueprintController {

    @Operation(
            summary = "Listar blueprints",
            description = "Devuelve la lista completa de blueprints. Requiere el scope blueprints.read en el JWT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de blueprints",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BlueprintSummary.class)),
                            examples = @ExampleObject(value = "[{\"id\": \"b1\", \"name\": \"Casa de campo\"}, {\"id\": \"b2\", \"name\": \"Edificio urbano\"}]")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El token no incluye el scope blueprints.read")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public List<Map<String, String>> list() {
        return List.of(
                Map.of("id", "b1", "name", "Casa de campo"),
                Map.of("id", "b2", "name", "Edificio urbano")
        );
    }

    @Operation(
            summary = "Crear blueprint",
            description = "Crea un nuevo blueprint con el nombre proporcionado. Requiere el scope blueprints.write en el JWT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Blueprint creado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BlueprintSummary.class),
                            examples = @ExampleObject(value = "{\"id\": \"new\", \"name\": \"Torre residencial\"}")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El token no incluye el scope blueprints.write")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public Map<String, String> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo blueprint",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BlueprintRequest.class),
                            examples = @ExampleObject(value = "{\"name\": \"Torre residencial\"}")
                    )
            )
            @RequestBody Map<String, String> in) {
        return Map.of("id", "new", "name", in.getOrDefault("name", "nuevo"));
    }

    @Schema(name = "BlueprintSummary", description = "Representacion resumida de un blueprint")
    record BlueprintSummary(
            @Schema(description = "Identificador unico", example = "b1") String id,
            @Schema(description = "Nombre del plano", example = "Casa de campo") String name
    ) {}

    @Schema(name = "BlueprintRequest", description = "Cuerpo para crear un blueprint")
    record BlueprintRequest(
            @Schema(description = "Nombre del plano", example = "Torre residencial", requiredMode = Schema.RequiredMode.REQUIRED)
            String name
    ) {}
}