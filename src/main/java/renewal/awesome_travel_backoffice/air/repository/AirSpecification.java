package renewal.awesome_travel_backoffice.air.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import renewal.common.entity.Air;
import renewal.common.entity.Air.AirStatus;
import renewal.common.entity.Air.FlightType;
import renewal.common.entity.Airline;
import renewal.common.entity.AirportCode;
import renewal.common.entity.SeatClass;

public class AirSpecification {

    // code LIKE %code%
    public static Specification<SeatClass> codeContains(String code) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (!StringUtils.hasText(code))
                return null;
            return builder.like(
                    builder.lower(seatJoin.get("flightNumber")),
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

    // departDateTime BETWEEN from AND to
    public static Specification<SeatClass> departDateTimeBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (from != null && to != null) {
                return builder.between(seatJoin.get("departDateTime"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(seatJoin.get("departDateTime"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(seatJoin.get("departDateTime"), to);
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
    public static Specification<SeatClass> departEquals(AirportCode depart) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (depart==null)
                return null;
            return builder.equal(seatJoin.get("departAirport"), depart);
        };
    }

    // arriveAirport == value
    public static Specification<SeatClass> arriveEquals(AirportCode arrive) {
        return (root, query, builder) -> {
            Join<SeatClass, Air> seatJoin = root.join("air", JoinType.LEFT);
            if (arrive==null)
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

    // seatClasses.price BETWEEN min AND max
    public static Specification<SeatClass> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, builder) -> {
            if (min == null && max == null)
                return null;
            if (min != null && max != null) {
                return builder.between(root.get("priceAdult"), min, max);
            } else if (min != null) {
                return builder.greaterThanOrEqualTo(root.get("priceAdult"), min);
            } else {
                return builder.lessThanOrEqualTo(root.get("priceAdult"), max);
            }
        };
    }

    // availableSeats >= value
    public static Specification<SeatClass> availableSeatsMore(Long more) {
        return (root, query, builder) -> {
            if (more == null)
                return null;
            return builder.greaterThanOrEqualTo(root.get("availableSeats"), more);
        };
    }

    // SeatClassType == value
    public static Specification<SeatClass> seatClassEquals(SeatClass.SeatClassType type) {
        return (root, query, builder) -> {
            if (type == null) {
                return null;
            }
            return builder.equal(root.get("classType"), type);
        };
    }
}
