package renewal.awesome_travel_backoffice.air.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;

@StaticMetamodel(SeatClass.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class SeatClass_ {

	public static final String PRICE = "price";
	public static final String AVAILABLE_SEATS = "availableSeats";
	public static final String MAX_SEATS = "maxSeats";
	public static final String ID = "id";
	public static final String AIR = "air";
	public static final String CLASS_TYPE = "classType";

	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.SeatClass#price
	 **/
	public static volatile SingularAttribute<SeatClass, Integer> price;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.SeatClass#availableSeats
	 **/
	public static volatile SingularAttribute<SeatClass, Integer> availableSeats;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.SeatClass#maxSeats
	 **/
	public static volatile SingularAttribute<SeatClass, Integer> maxSeats;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.SeatClass#id
	 **/
	public static volatile SingularAttribute<SeatClass, Long> id;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.SeatClass#air
	 **/
	public static volatile SingularAttribute<SeatClass, Air> air;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.SeatClass
	 **/
	public static volatile EntityType<SeatClass> class_;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.SeatClass#classType
	 **/
	public static volatile SingularAttribute<SeatClass, SeatClassType> classType;

}

