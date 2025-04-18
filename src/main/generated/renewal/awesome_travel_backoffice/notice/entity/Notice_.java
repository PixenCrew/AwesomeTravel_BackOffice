package renewal.awesome_travel_backoffice.notice.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;
import renewal.awesome_travel_backoffice.notice.utils.NoticeCategory;

@StaticMetamodel(Notice.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Notice_ extends renewal.awesome_travel_backoffice.config.AuditingFields_ {

	public static final String FIX = "fix";
	public static final String IMAGE_URL = "imageUrl";
	public static final String ID = "id";
	public static final String IS_VISIBLE = "isVisible";
	public static final String TITLE = "title";
	public static final String PRIORITY = "priority";
	public static final String CATEGORY = "category";
	public static final String END_AT = "endAt";
	public static final String CONTENT = "content";
	public static final String START_AT = "startAt";

	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#fix
	 **/
	public static volatile SingularAttribute<Notice, Boolean> fix;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#imageUrl
	 **/
	public static volatile SingularAttribute<Notice, String> imageUrl;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#id
	 **/
	public static volatile SingularAttribute<Notice, Long> id;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#isVisible
	 **/
	public static volatile SingularAttribute<Notice, Boolean> isVisible;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#title
	 **/
	public static volatile SingularAttribute<Notice, String> title;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#priority
	 **/
	public static volatile SingularAttribute<Notice, Integer> priority;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#category
	 **/
	public static volatile SingularAttribute<Notice, NoticeCategory> category;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#endAt
	 **/
	public static volatile SingularAttribute<Notice, LocalDateTime> endAt;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice
	 **/
	public static volatile EntityType<Notice> class_;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#content
	 **/
	public static volatile SingularAttribute<Notice, String> content;
	
	/**
	 * @see renewal.awesome_travel_backoffice.notice.entity.Notice#startAt
	 **/
	public static volatile SingularAttribute<Notice, LocalDateTime> startAt;

}

