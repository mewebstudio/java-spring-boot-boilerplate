package com.mewebstudio.javaspringbootboilerplate.entity.specification;

import com.mewebstudio.javaspringbootboilerplate.entity.Role;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.UserCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class UserFilterSpecification implements Specification<User> {
    private final UserCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull final Root<User> root,
                                 @NonNull final CriteriaQuery<?> query,
                                 @NonNull final CriteriaBuilder builder) {
        if (criteria == null) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getRoles() != null && !criteria.getRoles().isEmpty()) {
            Join<User, Role> roleJoin = root.join("roles");
            predicates.add(
                builder.in(roleJoin.get("name")).value(criteria.getRoles())
            );
        }

        if (criteria.getIsAvatar() != null) {
            if (criteria.getIsAvatar()) {
                predicates.add(builder.isNotNull(root.get("avatar")));
            } else {
                predicates.add(builder.isNull(root.get("avatar")));
            }
        }

        if (criteria.getCreatedAtStart() != null) {
            predicates.add(
                builder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtStart())
            );
        }

        if (criteria.getCreatedAtEnd() != null) {
            predicates.add(
                builder.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtEnd())
            );
        }

        if (criteria.getIsBlocked() != null) {
            if (criteria.getIsBlocked()) {
                predicates.add(builder.isNotNull(root.get("blockedAt")));
            } else {
                predicates.add(builder.isNull(root.get("blockedAt")));
            }
        }

        if (criteria.getQ() != null) {
            String q = String.format("%%%s%%", criteria.getQ().toLowerCase());

            predicates.add(
                builder.or(
                    builder.like(builder.lower(root.get("id").as(String.class)), q),
                    builder.like(builder.lower(root.get("email")), q),
                    builder.like(builder.lower(root.get("name")), q),
                    builder.like(builder.lower(root.get("lastName")), q)
                )
            );
        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return query.distinct(true).getRestriction();
    }
}
