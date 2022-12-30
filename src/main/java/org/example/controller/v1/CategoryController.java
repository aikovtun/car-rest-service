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
import org.example.dto.CategoryDto;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface CategoryController {

    @Operation(summary = "Get categories page", description = "Return categories page", tags = {"category"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "204", description = "No categories found", content = @Content)})
    @Parameter(name = "predicate",  description = "QueryDsl predicate", in = ParameterIn.QUERY,
        schema = @Schema(implementation = Predicate.class), example = "{\"id\" : 1, \"name\" : \"SUV\"}")
    ResponseEntity<Page<CategoryDto>> findAll(
            @Parameter(hidden = true) Predicate predicate, @ParameterObject Pageable pageable);

    @Operation(summary = "Get category by id", description = "Returns a single category", tags = {"category"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)})
    @Parameter(name = "id",  description = "Category id", in = ParameterIn.PATH)
    ResponseEntity<CategoryDto> findById(@PathVariable(name = "id") Long id);

    @Operation(summary = "Create category", description = "Create a single category", tags = {"category"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = CategoryDto.class)))})
    ResponseEntity<CategoryDto> create(CategoryDto categoryDto);

    @Operation(summary = "Update category by id", description = "Update a single category", tags = {"category"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)})
    ResponseEntity<CategoryDto> update(Long id, CategoryDto categoryDto);

    @Operation(summary = "Delete category by id", description = "Delete a single category", tags = {"category"},
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)})
    ResponseEntity<?> delete(@PathVariable(name = "id") Long id);

}