package renewal.awesome_travel_backoffice.air.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAirline is a Querydsl query type for Airline
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAirline extends EntityPathBase<Airline> {

    private static final long serialVersionUID = -1227911601L;

    public static final QAirline airline = new QAirline("airline");

    public final StringPath code = createString("code");

    public final BooleanPath infantSeatsRequired = createBoolean("infantSeatsRequired");

    public final StringPath nameEng = createString("nameEng");

    public final StringPath nameKor = createString("nameKor");

    public QAirline(String variable) {
        super(Airline.class, forVariable(variable));
    }

    public QAirline(Path<? extends Airline> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAirline(PathMetadata metadata) {
        super(Airline.class, metadata);
    }

}

