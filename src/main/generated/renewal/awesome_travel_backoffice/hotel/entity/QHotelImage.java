package renewal.awesome_travel_backoffice.hotel.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHotelImage is a Querydsl query type for HotelImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHotelImage extends EntityPathBase<HotelImage> {

    private static final long serialVersionUID = 162492480L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHotelImage hotelImage = new QHotelImage("hotelImage");

    public final QHotel hotel;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath url = createString("url");

    public QHotelImage(String variable) {
        this(HotelImage.class, forVariable(variable), INITS);
    }

    public QHotelImage(Path<? extends HotelImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHotelImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHotelImage(PathMetadata metadata, PathInits inits) {
        this(HotelImage.class, metadata, inits);
    }

    public QHotelImage(Class<? extends HotelImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hotel = inits.isInitialized("hotel") ? new QHotel(forProperty("hotel")) : null;
    }

}

