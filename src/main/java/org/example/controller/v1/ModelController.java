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
import org.example.dto.ModelDto;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ModelController {

    @Operation(summary = "Get models page", description = "Return models page", tags = {"model"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "204", description = "No models found", content = @Content)})
    @Parameter(name = "predicate",  description = "QueryDsl predicate", in = ParameterIn.QUERY,
        schema = @Schema(implementation = Predicate.class), example = "{\"id\" : 1, \"name\" : \"A6\"}")
    ResponseEntity<Page<ModelDto>> findAll(
            @Parameter(hidden = true) Predicate predicate, @ParameterObject Pageable pageable);

    @Operation(summary = "Get models page by manufacturer", description = "Return models page by manufacturer", tags = {"model"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "204", description = "No models found", content = @Content)})
    @Parameter(name = "predicate",  description = "QueryDsl predicate", in = ParameterIn.QUERY,
        schema = @Schema(implementation = Predicate.class), example = "{\"id\" : 1, \"name\" : \"A6\"}")
    ResponseEntity<Page<ModelDto>> findAllByManufacturer(
            String manufacturerName, @Parameter(hidden = true) Predicate predicate, @ParameterObject Pageable pageable);

    @Operation(summary = "Get model by id", description = "Returns a single model", tags = {"model"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ModelDto.class))),
        @ApiResponse(responseCode = "404", description = "Model not found", content = @Content)})
    @Parameter(name = "id",  description = "Model id", in = ParameterIn.PATH)
    ResponseEntity<ModelDto> findById(Long id);

    @Operation(summary = "Create model", description = "Create a single model", tags = {"model"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ModelDto.class)))})
    ResponseEntity<ModelDto> create(ModelDto modelDto);

    @Operation(summary = "Create model by manufacturer", description = "Create a single model by manufacturer", tags = {"model"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ModelDto.class)))})
    ResponseEntity<ModelDto> createByManufacturer(String manufacturerName, ModelDto modelDto);

    @Operation(summary = "Update model by id", description = "Update a single model", tags = {"model"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ModelDto.class))),
        @ApiResponse(responseCode = "404", description = "Model not found", content = @Content)})
    ResponseEntity<ModelDto> update(Long id, ModelDto modelDto);

    @Operation(summary = "Delete model by id", description = "Delete a single model", tags = {"model"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content),
        @ApiResponse(responseCode = "404", description = "Model not found", content = @Content)})
    ResponseEntity<?> delete(Long id);

}