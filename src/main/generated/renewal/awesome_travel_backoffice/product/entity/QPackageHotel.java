package renewal.awesome_travel_backoffice.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPackageHotel is a Querydsl query type for PackageHotel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPackageHotel extends EntityPathBase<PackageHotel> {

    private static final long serialVersionUID = 1274559682L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPackageHotel packageHotel = new QPackageHotel("packageHotel");

    public final NumberPath<Integer> bookedRooms = createNumber("bookedRooms", Integer.class);

    public final DatePath<java.time.LocalDate> checkIn = createDate("checkIn", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> checkOut = createDate("checkOut", java.time.LocalDate.class);

    public final renewal.awesome_travel_backoffice.hotel.entity.QHotel hotel;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final NumberPath<Integer> reservedRooms = createNumber("reservedRooms", Integer.class);

    public final EnumPath<renewal.awesome_travel_backoffice.hotel.utils.RoomType> roomType = createEnum("roomType", renewal.awesome_travel_backoffice.hotel.utils.RoomType.class);

    public QPackageHotel(String variable) {
        this(PackageHotel.class, forVariable(variable), INITS);
    }

    public QPackageHotel(Path<? extends PackageHotel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPackageHotel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPackageHotel(PathMetadata metadata, PathInits inits) {
        this(PackageHotel.class, metadata, inits);
    }

    public QPackageHotel(Class<? extends PackageHotel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hotel = inits.isInitialized("hotel") ? new renewal.awesome_travel_backoffice.hotel.entity.QHotel(forProperty("hotel")) : null;
    }

}

