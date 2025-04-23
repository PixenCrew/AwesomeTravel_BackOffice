package renewal.awesome_travel_backoffice.notice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotice is a Querydsl query type for Notice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotice extends EntityPathBase<Notice> {

    private static final long serialVersionUID = -1944551195L;

    public static final QNotice notice = new QNotice("notice");

    public final renewal.awesome_travel_backoffice.config.QAuditingFields _super = new renewal.awesome_travel_backoffice.config.QAuditingFields(this);

    public final EnumPath<renewal.awesome_travel_backoffice.notice.utils.NoticeCategory> category = createEnum("category", renewal.awesome_travel_backoffice.notice.utils.NoticeCategory.class);

    public final StringPath content = createString("content");

    //inherited
    public final StringPath cratedBy = _super.cratedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> endAt = createDateTime("endAt", java.time.LocalDateTime.class);

    public final BooleanPath fix = createBoolean("fix");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final BooleanPath isVisible = createBoolean("isVisible");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final NumberPath<Integer> priority = createNumber("priority", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startAt = createDateTime("startAt", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QNotice(String variable) {
        super(Notice.class, forVariable(variable));
    }

    public QNotice(Path<? extends Notice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotice(PathMetadata metadata) {
        super(Notice.class, metadata);
    }

}

