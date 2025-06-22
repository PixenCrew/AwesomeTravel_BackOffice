package renewal.awesome_travel_backoffice.hotel.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.entity.Reservation;
import renewal.awesome_travel_backoffice.hotel.utils.HotelType;

public class HotelSpecification {

    // name LIKE %name%
    public static Specification<Hotel> nameContains(String name) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(name))
                return null;
            return builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    // city LIKE %city%
    public static Specification<Hotel> cityContains(String city) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(city))
                return null;
            return builder.like(builder.lower(root.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    // address LIKE %address%
    public static Specification<Hotel> addressContains(String address) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(address))
                return null;
            return builder.like(builder.lower(root.get("address")), "%" + address.toLowerCase() + "%");
        };
    }

    // email LIKE %email%
    public static Specification<Hotel> emailContains(String email) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(email))
                return null;
            return builder.like(builder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    // price BETWEEN from AND to
    public static Specification<Hotel> priceBetween(Long from, Long to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("price"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("price"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(root.get("price"), to);
            } else {
                return null;
            }
        };
    }

    // hotelType == value
    public static Specification<Hotel> hotelTypeEquals(HotelType hotelType) {
        return (root, query, builder) -> {
            if (hotelType == null)
                return null;
            return builder.equal(root.get("hotelType"), hotelType);
        };
    }

    // isActive == value
    public static Specification<Hotel> isActiveEquals(Boolean isActive) {
        return (root, query, builder) -> {
            if (isActive == null)
                return null;
            return builder.equal(root.get("isActive"), isActive);
        };
    }

    // // availableSeats >= value
    // public static Specification<Hotel> availableSeatsMore(Long more) {
    // return (root, query, builder) -> {
    // if (more == null || query == null)
    // return null;
    // Join<Hotel, SeatClass> join = root.join("seatClasses", JoinType.LEFT);
    // query.distinct(true);
    // return builder.greaterThanOrEqualTo(join.get("availableSeats"), more);
    // };
    // }

    // TODO : 남은 객실 수 계산 로직 필요
    public static Specification<Hotel> availableBetweenAndCapacity(
            LocalDate startDate,
            LocalDate endDate,
            Integer requiredPersons) {
        return (root, query, builder) -> {
            // 중복 결과 방지를 위해 distinct 설정
            query.distinct(true);

            // 1) 이 호텔에 대해 BOOKED 상태로 겹치는 기간의 roomCount 합을 구하는 서브쿼리
            Subquery<Long> sq = query.subquery(Long.class);
            Root<Reservation> r = sq.from(Reservation.class);

            sq.select(builder.coalesce(builder.sum(r.get("roomCount")), 0L))
                    .where(
                            // Reservation.hotel 필드를 바깥 root(Hotel)와 연관시킴
                            builder.equal(r.get("hotelId"), root.get("id")),
                            builder.equal(r.get("status"), Reservation.Status.BOOKED),
                            builder.and(
                                    builder.lessThan(r.get("startDate"), endDate),
                                    builder.greaterThan(r.get("endDate"), startDate)));

            // 2) availableRooms = maxRoomCount – 이미 예약된 방 수
            Expression<Long> availableRooms = builder.diff(
                    root.get("maxRoomCount").as(Long.class),
                    sq);

            // 3) 사용 가능 방 수가 requiredPersons 이상인지 비교
            return builder.greaterThanOrEqualTo(availableRooms, requiredPersons.longValue());
        };
    }

}
