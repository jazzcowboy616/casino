package org.omega.casino.dtos;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.omega.casino.entities.Game;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GameSpecifications {
    public static Specification<Game> parse(String queryStr) {
        return (root, query, cb) -> {
            if (queryStr == null || queryStr.isBlank()) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();
            String[] conditions = queryStr.split(",");

            for (String condition : conditions) {
                condition = condition.trim();

                if (condition.contains(" between ")) {
                    // 解析 `between`
                    String[] parts = condition.split(" between ");
                    String field = parts[0].trim();
                    String[] values = parts[1].trim().split(" ");
                    if (values.length == 2) {
                        try {
                            predicates.add(parseBetween(root, cb, field, values[0], values[1]));
                        } catch (NumberFormatException ignored) {}
                    }
                } else if (condition.contains(">=")) {
                    // 解析 `>=`
                    String[] parts = condition.split(">=");
                    String field = parts[0].trim();
                    try {
                        predicates.add(parseGreaterThanOrEqualTo(root, cb, field, parts[1]));
                    } catch (NumberFormatException ignored) {}
                } else if (condition.contains("<=")) {
                    // 解析 `<=`
                    String[] parts = condition.split("<=");
                    String field = parts[0].trim();
                    try {
                        predicates.add(parseLessThanOrEqualTo(root, cb, field, parts[1]));
                    } catch (NumberFormatException ignored) {}
                } else if (condition.contains(">")) {
                    // 解析 `>`
                    String[] parts = condition.split(">");
                    String field = parts[0].trim();
                    try {
                        predicates.add(parseGreaterThan(root, cb, field, parts[1]));
                    } catch (NumberFormatException ignored) {}
                } else if (condition.contains("<")) {
                    // 解析 `<`
                    String[] parts = condition.split("<");
                    String field = parts[0].trim();
                    try {
                        predicates.add(parseLessThan(root, cb, field, parts[1]));
                    } catch (NumberFormatException ignored) {}
                } else if (condition.contains("=")) {
                    // 解析 `=`
                    String[] parts = condition.split("=");
                    String field = parts[0].trim();
                    String value = parts[1].trim();
                    if("name".equals(field) || "description".equals(field))
                        predicates.add(cb.like(root.get(field), "%" + value + "%"));
                    else
                        predicates.add(parseEqual(root, cb, field, value));
                }
            }
            return query.where(cb.and(predicates.toArray(new Predicate[predicates.size()]))).getRestriction();
        };
    }

    private static Predicate parseBetween(Root root, CriteriaBuilder cb, String field, String value1, String value2)
            throws NumberFormatException {
        Path path = root.get(field);
        value1 = value1.trim();
        value2 = value2.trim();
        switch (field){
            case "minBet", "maxBet":
                return cb.between(path, Long.parseLong(value1), Long.parseLong(value2));
            case "winRate", "winMultiplier":
                return cb.between(path, Double.parseDouble(value1), Double.parseDouble(value2));
            default:
                return cb.between(path, Long.parseLong(value1), Long.parseLong(value2));
        }
    }

    private static Predicate parseGreaterThanOrEqualTo(Root root, CriteriaBuilder cb, String field, String value)
            throws NumberFormatException {
        Path path = root.get(field);
        value = value.trim();
        switch (field){
            case "minBet", "maxBet":
                return cb.greaterThanOrEqualTo(path, Long.parseLong(value));
            case "winRate", "winMultiplier":
                return cb.greaterThanOrEqualTo(path, Double.parseDouble(value));
            default:
                return cb.greaterThanOrEqualTo(path, Long.parseLong(value));
        }
    }

    private static Predicate parseLessThanOrEqualTo(Root root, CriteriaBuilder cb, String field, String value)
            throws NumberFormatException {
        Path path = root.get(field);
        value = value.trim();
        switch (field){
            case "minBet", "maxBet":
                return cb.lessThanOrEqualTo(path, Long.parseLong(value));
            case "winRate", "winMultiplier":
                return cb.lessThanOrEqualTo(path, Double.parseDouble(value));
            default:
                return cb.lessThanOrEqualTo(path, Long.parseLong(value));
        }
    }

    private static Predicate parseGreaterThan(Root root, CriteriaBuilder cb, String field, String value)
            throws NumberFormatException {
        Path path = root.get(field);
        value = value.trim();
        switch (field){
            case "minBet", "maxBet":
                return cb.greaterThan(path, Long.parseLong(value));
            case "winRate", "winMultiplier":
                return cb.greaterThan(path, Double.parseDouble(value));
            default:
                return cb.greaterThan(path, Long.parseLong(value));
        }
    }

    private static Predicate parseLessThan(Root root, CriteriaBuilder cb, String field, String value)
            throws NumberFormatException {
        Path path = root.get(field);
        value = value.trim();
        switch (field){
            case "minBet", "maxBet":
                return cb.lessThan(path, Long.parseLong(value));
            case "winRate", "winMultiplier":
                return cb.lessThan(path, Double.parseDouble(value));
            default:
                return cb.lessThan(path, Long.parseLong(value));
        }
    }

    private static Predicate parseEqual(Root root, CriteriaBuilder cb, String field, String value)
            throws NumberFormatException {
        Path path = root.get(field);
        value = value.trim();
        switch (field){
            case "minBet", "maxBet":
                return cb.equal(path, Long.parseLong(value));
            case "winRate", "winMultiplier":
                return cb.equal(path, Double.parseDouble(value));
            default:
                return cb.equal(path, Long.parseLong(value));
        }
    }
}
