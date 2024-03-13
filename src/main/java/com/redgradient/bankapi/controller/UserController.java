package com.redgradient.bankapi.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redgradient.bankapi.dto.UserDto;
import com.redgradient.bankapi.dto.UserUpdateDto;
import com.redgradient.bankapi.model.QUser;
import com.redgradient.bankapi.model.User;
import com.redgradient.bankapi.service.UserService;
import com.redgradient.bankapi.service.security.AccessControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.hibernate.sql.ast.tree.predicate.BooleanExpressionPredicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api/users")
@Tag(name = "users")
public class UserController {
    private final UserService userService;
    AccessControlService accessControlService;
    @PersistenceContext
    private final EntityManager entityManager;

    private static final int PAGE_SIZE = 10 ;
    private static final String DEFAULT_PAGE = "0";
    private static final String ID = "/{id}";
    private static final String AUTHENTICATED = "isAuthenticated()";
    private static final String ONLY_OWNER = """
        @userRepository.findById(#id).get().getUsername() == authentication.name
        """;

    public UserController(UserService userService, EntityManager entityManager, AccessControlService accessControlService) {
        this.userService = userService;
        this.accessControlService = accessControlService;
        this.entityManager = entityManager;
    }

    @Operation(summary = "Get Users", description = "Returns a list of users based on the provided filters")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @Parameter(description = "Filter users by first name (case-insensitive)") @RequestParam(required = false) String firstName,
            @Parameter(description = "Filter users by last name (case-insensitive)") @RequestParam(required = false) String lastName,
            @Parameter(description = "Filter users by middle name (case-insensitive)") @RequestParam(required = false) String middleName,
            @Parameter(description = "Filter users by date of birth (after specified date)") @RequestParam(required = false) Date dateOfBirth,
            @Parameter(description = "Filter users by phone number") @RequestParam(required = false) String phoneNumber,
            @Parameter(description = "Filter users by email address") @RequestParam(required = false) String email,
            @Parameter(description = "Page number") @RequestParam(defaultValue = DEFAULT_PAGE) int page) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
        var user = QUser.user;
        var query = queryFactory.selectFrom(user);

        if (firstName != null)   query.where(user.firstName.likeIgnoreCase(firstName + "%"));
        if (lastName != null)    query.where(user.lastName.likeIgnoreCase(lastName + "%"));
        if (middleName != null)  query.where(user.middleName.likeIgnoreCase(middleName + "%"));
        if (dateOfBirth != null) query.where(user.dateOfBirth.after(dateOfBirth));
        if (phoneNumber != null) query.where(user.phoneNumber.eq(phoneNumber));
        if (email != null)       query.where(user.email.eq(email));

        var users = query
                .offset((long) page * PAGE_SIZE)
                .limit(PAGE_SIZE).stream()
                .map(userService::toDto)
                .toList();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update User", description = "Update user details by ID")
    @ApiResponse(responseCode = "200", description = "User details updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized. User can be updated only by its owner", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @PutMapping(ID)
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userDto) {
        try {
            accessControlService.checkAccess(id);
            var updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(userService.toDto(updatedUser));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
