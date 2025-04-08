package renewal.awesome_travel_backoffice.config;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.MappedSuperclassType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@StaticMetamodel(AuditingFields.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class AuditingFields_ {

	public static final String CREATED_AT = "createdAt";
	public static final String MODIFIED_AT = "modifiedAt";
	public static final String CRATED_BY = "cratedBy";
	public static final String MODIFIED_BY = "modifiedBy";

	
	/**
	 * @see renewal.awesome_travel_backoffice.config.AuditingFields#createdAt
	 **/
	public static volatile SingularAttribute<AuditingFields, LocalDateTime> createdAt;
	
	/**
	 * @see renewal.awesome_travel_backoffice.config.AuditingFields#modifiedAt
	 **/
	public static volatile SingularAttribute<AuditingFields, LocalDateTime> modifiedAt;
	
	/**
	 * @see renewal.awesome_travel_backoffice.config.AuditingFields#cratedBy
	 **/
	public static volatile SingularAttribute<AuditingFields, String> cratedBy;
	
	/**
	 * @see renewal.awesome_travel_backoffice.config.AuditingFields#modifiedBy
	 **/
	public static volatile SingularAttribute<AuditingFields, String> modifiedBy;
	
	/**
	 * @see renewal.awesome_travel_backoffice.config.AuditingFields
	 **/
	public static volatile MappedSuperclassType<AuditingFields> class_;

}

