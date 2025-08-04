package renewal.awesome_travel_backoffice.hotel.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.entity.HotelReservation;
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

    // city == value
    public static Specification<Hotel> cityEquals(String city) {
        return (root, query, builder) -> {
            if (city == null)
                return null;
            return builder.equal(root.get("city"), city);
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
            Long requiredPersons) {
        return (root, query, builder) -> {
            // 중복 결과 방지를 위해 distinct 설정
            query.distinct(true);

            // 1) 이 호텔에 대해 BOOKED 상태로 겹치는 기간의 roomCount 합을 구하는 서브쿼리
            Subquery<Long> sq = query.subquery(Long.class);
            Root<HotelReservation> r = sq.from(HotelReservation.class);

            sq.select(builder.coalesce(builder.sum(r.get("roomCount")), 0L))
                    .where(
                            // HotelReservation.hotel 필드를 바깥 root(Hotel)와 연관시킴
                            builder.equal(r.get("hotelId"), root.get("id")),
                            builder.equal(r.get("status"), HotelReservation.Status.BOOKED),
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

    public static Specification<Hotel> availableOnDateAndRooms(LocalDate date, Long requiredRooms) {
        return (root, query, builder) -> {
            // 서브쿼리: 해당 날짜에 BOOKED 상태인 예약 합계(roomCount)
            Subquery<Long> sumSub = query.subquery(Long.class);
            Root<HotelReservation> res = sumSub.from(HotelReservation.class);

            // SUM(roomCount) 결과가 null 이면 0L 로 대체하기 위해 COALESCE 사용
            Expression<Long> sumRoomCount = builder.coalesce(builder.sum(res.get("roomCount")), 0L);
            sumSub.select(sumRoomCount);

            // subquery의 WHERE 절
            sumSub.where(
                    builder.equal(res.get("hotelId"), root.get("id")),
                    builder.equal(res.get("status"), HotelReservation.Status.BOOKED), // 예약 상태가 BOOKED
                    builder.lessThanOrEqualTo(res.get("startDate"), date), // startDate <= date
                    builder.greaterThanOrEqualTo(res.get("endDate"), date) // endDate >= date
            );

            // 호텔의 총 객실 수 - 예약된 객실 수 >= requiredRooms
            Expression<Long> availableRooms = builder.diff(root.get("maxRoomCount"), sumSub);
            return builder.greaterThanOrEqualTo(availableRooms, requiredRooms);
        };
    }
}
