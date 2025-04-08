package renewal.awesome_travel_backoffice.air.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import renewal.awesome_travel_backoffice.air.dto.request.AirSearchRequestDto;
import renewal.awesome_travel_backoffice.air.dto.response.AirResponseDto;
import renewal.awesome_travel_backoffice.air.dto.response.SeatClassResponseDto;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.Airline;
import renewal.awesome_travel_backoffice.air.entity.QAir;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AirRepositoryCustomImpl implements AirRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AirResponseDto> searchAdminAirList(AirSearchRequestDto req) {
        QAir air = QAir.air;

        BooleanBuilder builder = new BooleanBuilder();

        if (req.getAirline() != null && !req.getAirline().isBlank()) {
            builder.and(air.airline.nameKor.containsIgnoreCase(req.getAirline()));
        }
        if (req.getDepart() != null) {
            builder.and(air.depart.eq(req.getDepart()));
        }
        if (req.getArrive() != null) {
            builder.and(air.arrive.eq(req.getArrive()));
        }
        if (req.getDepartDateFrom() != null && req.getDepartDateTo() != null) {
            builder.and(air.depart_time.between(req.getDepartDateFrom(), req.getDepartDateTo()));
        }
        if (req.getStatus() != null) {
            builder.and(air.status.eq(req.getStatus()));
        }

        OrderSpecifier<?> order = resolveSort(req, air);

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());

        List<Air> content = queryFactory.selectFrom(air)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(air.count())
                .from(air)
                .where(builder)
                .fetchOne();

        List<AirResponseDto> result = content.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, total);
    }

    private OrderSpecifier<?> resolveSort(AirSearchRequestDto req, QAir air) {
        String field = req.getSortField();
        String order = req.getSortOrder();

        boolean desc = "desc".equalsIgnoreCase(order);

        return switch (field != null ? field : "createdAt") {
            case "departTime" -> desc ? air.depart_time.desc() : air.depart_time.asc();
            case "airline" -> desc ? air.airline.nameKor.desc() : air.airline.nameKor.asc();
            case "stopovers" -> desc ? air.stopovers.desc() : air.stopovers.asc();
            case "createdAt" -> desc ? air.createdAt.desc() : air.createdAt.asc();
            case "updatedAt" -> desc ? air.modifiedAt.desc() : air.modifiedAt.asc();
            case "status" -> desc ? air.status.desc() : air.status.asc();
            default -> air.id.desc();
        };
    }

    private AirResponseDto toDto(Air air) {
        List<SeatClassResponseDto> seatDtos = air.getSeatClasses().stream()
                .map(seat -> SeatClassResponseDto.builder()
                        .id(seat.getId())
                        .classType(seat.getClassType())
                        .price(seat.getPrice())
                        .maxSeats(seat.getMaxSeats())
                        .availableSeats(seat.getAvailableSeats())
                        .build())
                .collect(Collectors.toList());

        Airline airline = air.getAirline();

        return AirResponseDto.builder()
                .id(air.getId())
                .code(air.getCode())
                .airlineCode(airline.getCode())
                .airlineNameKor(airline.getNameKor())
                .airlineNameEng(airline.getNameEng())
                .depart(air.getDepart())
                .departTime(air.getDepart_time())
                .arrive(air.getArrive())
                .arriveTime(air.getArrive_time())
                .stopovers(air.getStopovers())
                .flightType(air.getFlightType())
                .status(air.getStatus())
                .seatClasses(seatDtos)
                .build();
    }
}
