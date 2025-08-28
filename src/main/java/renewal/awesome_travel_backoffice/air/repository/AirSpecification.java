package renewal.awesome_travel_backoffice.air.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.Air.AirStatus;
import renewal.awesome_travel_backoffice.air.entity.Air.FlightType;
import renewal.awesome_travel_backoffice.air.entity.Airline;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;

public class AirSpecification {

    // code LIKE %code%
    public static Specification<SeatClass> codeContains(String code) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (!StringUtils.hasText(code))
                return null;
            return builder.like(
                    builder.lower(seatJoin.get("code")),
                    "%" + code.toLowerCase() + "%");
        };
    }

    // airline IN (list)
    public static Specification<SeatClass> airlinesIn(List<String> codes) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (codes == null || codes.isEmpty()) {
                return null;
            }
            Join<Air, Airline> join = seatJoin.join("airline", JoinType.INNER);
            return join.get("code").in(codes);
        };
    }

    // InfantSeatsRequired == value
    public static Specification<SeatClass> getInfantSeatsRequired(Boolean check) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (check == null)
                return null;
            Join<Air, Airline> join = seatJoin.join("airline", JoinType.INNER);
            return builder.equal(join.get("infantSeatsRequired"), check);
        };
    }

    // departDate BETWEEN from AND to
    public static Specification<SeatClass> departDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (from != null && to != null) {
                return builder.between(seatJoin.get("departDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(seatJoin.get("departDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(seatJoin.get("departDate"), to);
            } else {
                return null;
            }
        };
    }

    // arriveDate BETWEEN from AND to
    public static Specification<SeatClass> arriveDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (from != null && to != null) {
                return builder.between(seatJoin.get("arriveDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(seatJoin.get("arriveDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(seatJoin.get("arriveDate"), to);
            } else {
                return null;
            }
        };
    }

    // departAirport == value
    public static Specification<SeatClass> departEquals(String depart) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (!StringUtils.hasText(depart))
                return null;
            return builder.equal(seatJoin.get("departAirport"), depart);
        };
    }

    // arriveAirport == value
    public static Specification<SeatClass> arriveEquals(String arrive) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (!StringUtils.hasText(arrive))
                return null;
            return builder.equal(seatJoin.get("arriveAirport"), arrive);
        };
    }

    // stopovers == value
    public static Specification<SeatClass> stopoversEquals(Long stopovers) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (stopovers == null)
                return null;
            return builder.equal(seatJoin.get("stopovers"), stopovers);
        };
    }

    // stopovers BETWEEN from AND to
    public static Specification<SeatClass> stopoversBetween(Long minStopovers, Long maxStopovers) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (minStopovers != null && maxStopovers != null) {
                return builder.between(seatJoin.get("stopovers"), minStopovers, maxStopovers);
            } else if (minStopovers != null) {
                return builder.greaterThanOrEqualTo(seatJoin.get("stopovers"), minStopovers);
            } else if (maxStopovers != null) {
                return builder.lessThanOrEqualTo(seatJoin.get("stopovers"), maxStopovers);
            } else {
                return null;
            }
        };
    }

    // flightType == value
    public static Specification<SeatClass> flightTypeEquals(FlightType type) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (type == null)
                return null;
            return builder.equal(seatJoin.get("flightType"), type);
        };
    }

    // status == value
    public static Specification<SeatClass> statusEquals(AirStatus status) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (status == null)
                return null;
            return builder.equal(seatJoin.get("status"), status);
        };
    }

    // seatClasses.price BETWEEN min AND max (JOIN & DISTINCT)
    public static Specification<SeatClass> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, builder) -> {
            if (min == null && max == null || query == null)
                return null;
            // Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (min != null && max != null) {
                query.distinct(true);
                return builder.between(root.get("price"), min, max);
            } else if (min != null) {
                query.distinct(true);
                return builder.greaterThanOrEqualTo(root.get("price"), min);
            } else {
                query.distinct(true);
                return builder.lessThanOrEqualTo(root.get("price"), max);
            }
        };
    }

    // availableSeats >= value
    public static Specification<SeatClass> availableSeatsMore(Long more) {
        return (root, query, builder) -> {
            if (more == null || query == null)
                return null;
            // Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            // query.distinct(true);
            return builder.greaterThanOrEqualTo(root.get("availableSeats"), more);
        };
    }

    // SeatClassType == value
    public static Specification<SeatClass> seatClassEquals(SeatClass.SeatClassType type) {
        return (root, query, builder) -> {
            if (query != null) {
                query.distinct(true);
            }
            // SeatClass 조인
            // Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            return builder.equal(root.get("classType"), type);
        };
    }
}
