package renewal.awesome_travel_backoffice.hotel.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHotel is a Querydsl query type for Hotel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHotel extends EntityPathBase<Hotel> {

    private static final long serialVersionUID = 1545157083L;

    public static final QHotel hotel = new QHotel("hotel");

    public final StringPath address = createString("address");

    public final ListPath<String, StringPath> amenities = this.<String, StringPath>createList("amenities", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final StringPath email = createString("email");

    public final EnumPath<renewal.awesome_travel_backoffice.hotel.utiles.HotelType> hotelType = createEnum("hotelType", renewal.awesome_travel_backoffice.hotel.utiles.HotelType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<HotelImage, QHotelImage> images = this.<HotelImage, QHotelImage>createList("images", HotelImage.class, QHotelImage.class, PathInits.DIRECT2);

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath name = createString("name");

    public final StringPath number = createString("number");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath website = createString("website");

    public QHotel(String variable) {
        super(Hotel.class, forVariable(variable));
    }

    public QHotel(Path<? extends Hotel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHotel(PathMetadata metadata) {
        super(Hotel.class, metadata);
    }

}

