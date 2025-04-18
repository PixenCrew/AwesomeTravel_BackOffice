package renewal.awesome_travel_backoffice.notice.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * renewal.awesome_travel_backoffice.notice.dto.response.QNoticeResponseDto is a Querydsl Projection type for NoticeResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QNoticeResponseDto extends ConstructorExpression<NoticeResponseDto> {

    private static final long serialVersionUID = 1624740742L;

    public QNoticeResponseDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<String> content, com.querydsl.core.types.Expression<Boolean> fix, com.querydsl.core.types.Expression<Integer> priority, com.querydsl.core.types.Expression<String> imageUrl, com.querydsl.core.types.Expression<renewal.awesome_travel_backoffice.notice.utils.NoticeCategory> category, com.querydsl.core.types.Expression<java.time.LocalDateTime> startAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> endAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> modifiedAt) {
        super(NoticeResponseDto.class, new Class<?>[]{long.class, String.class, String.class, boolean.class, int.class, String.class, renewal.awesome_travel_backoffice.notice.utils.NoticeCategory.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class}, id, title, content, fix, priority, imageUrl, category, startAt, endAt, createdAt, modifiedAt);
    }

}

