package renewal.awesome_travel_backoffice.air.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Airline.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Airline_ {

	public static final String NAME_KOR = "nameKor";
	public static final String NAME_ENG = "nameEng";
	public static final String INFANT_SEATS_REQUIRED = "infantSeatsRequired";
	public static final String CODE = "code";

	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Airline#nameKor
	 **/
	public static volatile SingularAttribute<Airline, String> nameKor;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Airline#nameEng
	 **/
	public static volatile SingularAttribute<Airline, String> nameEng;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Airline#infantSeatsRequired
	 **/
	public static volatile SingularAttribute<Airline, Boolean> infantSeatsRequired;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Airline#code
	 **/
	public static volatile SingularAttribute<Airline, String> code;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Airline
	 **/
	public static volatile EntityType<Airline> class_;

}

