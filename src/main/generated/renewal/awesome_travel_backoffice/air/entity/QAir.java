package renewal.awesome_travel_backoffice.air.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAir is a Querydsl query type for Air
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAir extends EntityPathBase<Air> {

    private static final long serialVersionUID = 1852326875L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAir air = new QAir("air");

    public final renewal.awesome_travel_backoffice.config.QAuditingFields _super = new renewal.awesome_travel_backoffice.config.QAuditingFields(this);

    public final QAirline airline;

    public final StringPath arrive = createString("arrive");

    public final StringPath arrive_time = createString("arrive_time");

    public final StringPath code = createString("code");

    //inherited
    public final StringPath cratedBy = _super.cratedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath depart = createString("depart");

    public final StringPath depart_time = createString("depart_time");

    public final EnumPath<renewal.awesome_travel_backoffice.air.utiles.FlightType> flightType = createEnum("flightType", renewal.awesome_travel_backoffice.air.utiles.FlightType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final ListPath<SeatClass, QSeatClass> seatClasses = this.<SeatClass, QSeatClass>createList("seatClasses", SeatClass.class, QSeatClass.class, PathInits.DIRECT2);

    public final EnumPath<renewal.awesome_travel_backoffice.air.utiles.AirStatus> status = createEnum("status", renewal.awesome_travel_backoffice.air.utiles.AirStatus.class);

    public final NumberPath<Integer> stopovers = createNumber("stopovers", Integer.class);

    public QAir(String variable) {
        this(Air.class, forVariable(variable), INITS);
    }

    public QAir(Path<? extends Air> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAir(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAir(PathMetadata metadata, PathInits inits) {
        this(Air.class, metadata, inits);
    }

    public QAir(Class<? extends Air> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.airline = inits.isInitialized("airline") ? new QAirline(forProperty("airline")) : null;
    }

}

