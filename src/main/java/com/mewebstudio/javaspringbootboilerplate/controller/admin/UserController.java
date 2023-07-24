package com.mewebstudio.javaspringbootboilerplate.controller.admin;

import com.mewebstudio.javaspringbootboilerplate.controller.AbstractBaseController;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.CreateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.response.ErrorResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UserResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UsersPaginationResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.PaginationCriteria;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.UserCriteria;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Tag(name = "101. Admin - Users", description = "Admin - Users API")
public class UserController extends AbstractBaseController {
    private static final String[] SORT_COLUMNS = new String[]{"id", "email", "name", "lastName", "blockedAt",
        "createdAt", "updatedAt"};

    private final UserService userService;

    private final MessageSourceService messageSourceService;

    @GetMapping
    @Operation(
        summary = "Users list endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success operation",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UsersPaginationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Full authentication is required to access this resource",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<UsersPaginationResponse> list(
        @Parameter(name = "roles", description = "Roles", example = "admin,user")
        @RequestParam(required = false) final List<String> roles,
        @Parameter(name = "createdAtStart", description = "Created date start", example = "2022-10-25T22:54:58")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdAtStart,
        @Parameter(name = "createdAtEnd", description = "Created date end", example = "2022-10-25T22:54:58")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdAtEnd,
        @Parameter(name = "isAvatar", description = "Is avatar?", example = "true")
        @RequestParam(required = false) final Boolean isAvatar,
        @Parameter(name = "isBlocked", description = "Is blocked?", example = "true")
        @RequestParam(required = false) final Boolean isBlocked,
        @Parameter(name = "q", description = "Search keyword", example = "lorem")
        @RequestParam(required = false) final String q,
        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(defaultValue = "1", required = false) final Integer page,
        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}",
            required = false) final Integer size,
        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt",
            schema = @Schema(type = "String", allowableValues = {"id", "email", "name", "lastName", "blockedAt",
                "createdAt", "updatedAt"}))
        @RequestParam(defaultValue = "createdAt", required = false) final String sortBy,
        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
            allowableValues = {"asc", "desc"}, defaultValue = "asc"))
        @RequestParam(defaultValue = "asc", required = false) @Pattern(regexp = "asc|desc") final String sort
    ) {
        sortColumnCheck(messageSourceService, SORT_COLUMNS, sortBy);

        Page<User> users = userService.findAll(
            UserCriteria.builder()
                .roles(roles != null ? roles.stream().map(Constants.RoleEnum::get)
                    .collect(Collectors.toList()) : null)
                .createdAtStart(createdAtStart)
                .createdAtEnd(createdAtEnd)
                .isAvatar(isAvatar)
                .isBlocked(isBlocked)
                .q(q)
                .build(),
            PaginationCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sort(sort)
                .columns(SORT_COLUMNS)
                .build()
        );

        return ResponseEntity.ok(new UsersPaginationResponse(users, users.stream()
            .map(UserResponse::convert)
            .toList()));
    }

    @PostMapping
    @Operation(
        summary = "Create user endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Success operation",
                content = @Content(schema = @Schema(hidden = true)),
                headers = @Header(
                    name = "Location",
                    description = "Location of created user",
                    schema = @Schema(type = "string")
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Full authentication is required to access this resource",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Validation Failed",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<URI> create(
        @Parameter(description = "Request body to user create", required = true)
        @RequestBody @Valid final CreateUserRequest request
    ) throws BindException {
        User user = userService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Show user endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success operation",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Full authentication is required to access this resource",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<UserResponse> show(
        @Parameter(name = "id", description = "User ID", required = true)
        @PathVariable("id") final String id
    ) {
        return ResponseEntity.ok(UserResponse.convert(userService.findById(id)));
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update user endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success operation",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Full authentication is required to access this resource",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Validation Failed",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<UserResponse> update(
        @Parameter(name = "id", description = "User ID", required = true)
        @PathVariable("id") final String id,
        @Parameter(description = "Request body to user update", required = true)
        @RequestBody @Valid final UpdateUserRequest request
    ) throws BindException {
        return ResponseEntity.ok(UserResponse.convert(userService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete user endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Success operation",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Full authentication is required to access this resource",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<Void> delete(
        @Parameter(name = "id", description = "User ID", required = true)
        @PathVariable("id") final String id
    ) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
