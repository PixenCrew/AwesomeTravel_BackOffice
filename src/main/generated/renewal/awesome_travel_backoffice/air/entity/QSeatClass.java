package renewal.awesome_travel_backoffice.air.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSeatClass is a Querydsl query type for SeatClass
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeatClass extends EntityPathBase<SeatClass> {

    private static final long serialVersionUID = -1766539196L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSeatClass seatClass = new QSeatClass("seatClass");

    public final QAir air;

    public final NumberPath<Integer> availableSeats = createNumber("availableSeats", Integer.class);

    public final EnumPath<renewal.awesome_travel_backoffice.air.utiles.SeatClassType> classType = createEnum("classType", renewal.awesome_travel_backoffice.air.utiles.SeatClassType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> maxSeats = createNumber("maxSeats", Integer.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public QSeatClass(String variable) {
        this(SeatClass.class, forVariable(variable), INITS);
    }

    public QSeatClass(Path<? extends SeatClass> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSeatClass(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSeatClass(PathMetadata metadata, PathInits inits) {
        this(SeatClass.class, metadata, inits);
    }

    public QSeatClass(Class<? extends SeatClass> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.air = inits.isInitialized("air") ? new QAir(forProperty("air"), inits.get("air")) : null;
    }

}

