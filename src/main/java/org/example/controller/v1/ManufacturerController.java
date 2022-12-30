package org.example.controller.v1;

import com.querydsl.core.types.Predicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.dto.ManufacturerDto;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ManufacturerController {

    @Operation(summary = "Get manufacturers page", description = "Return manufacturers page", tags = {"manufacturer"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "204", description = "No manufacturers found", content = @Content)})
    @Parameter(name = "predicate",  description = "QueryDsl predicate", in = ParameterIn.QUERY,
        schema = @Schema(implementation = Predicate.class), example = "{\"id\" : 1, \"name\" : \"Audi\"}")
    ResponseEntity<Page<ManufacturerDto>> findAll(
            @Parameter(hidden = true) Predicate predicate, @ParameterObject Pageable pageable);

    @Operation(summary = "Get manufacturer by id", description = "Returns a single manufacturer", tags = {"manufacturer"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ManufacturerDto.class))),
        @ApiResponse(responseCode = "404", description = "Manufacturer not found", content = @Content)})
    @Parameter(name = "id",  description = "Manufacturer id", in = ParameterIn.PATH)
    ResponseEntity<ManufacturerDto> findById(Long id);

    @Operation(summary = "Create manufacturer", description = "Create a single manufacturer", tags = {"manufacturer"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ManufacturerDto.class)))})
    ResponseEntity<ManufacturerDto> create(ManufacturerDto manufacturerDto);

    @Operation(summary = "Update manufacturer by id", description = "Update a single manufacturer", tags = {"manufacturer"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ManufacturerDto.class))),
        @ApiResponse(responseCode = "404", description = "Manufacturer not found", content = @Content)})
    ResponseEntity<ManufacturerDto> update(Long id, ManufacturerDto manufacturerDto);

    @Operation(summary = "Delete manufacturer by id", description = "Delete a single manufacturer", tags = {"manufacturer"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content),
        @ApiResponse(responseCode = "404", description = "Manufacturer not found", content = @Content)})
    ResponseEntity<?> delete(Long id);

}
