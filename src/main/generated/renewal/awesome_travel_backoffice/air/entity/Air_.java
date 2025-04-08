package renewal.awesome_travel_backoffice.air.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;

@StaticMetamodel(Air.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Air_ extends renewal.awesome_travel_backoffice.config.AuditingFields_ {

	public static final String STOPOVERS = "stopovers";
	public static final String CODE = "code";
	public static final String DEPART_TIME = "depart_time";
	public static final String ARRIVE = "arrive";
	public static final String SEAT_CLASSES = "seatClasses";
	public static final String ID = "id";
	public static final String FLIGHT_TYPE = "flightType";
	public static final String AIRLINE = "airline";
	public static final String DEPART = "depart";
	public static final String ARRIVE_TIME = "arrive_time";
	public static final String STATUS = "status";

	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#stopovers
	 **/
	public static volatile SingularAttribute<Air, Integer> stopovers;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#code
	 **/
	public static volatile SingularAttribute<Air, String> code;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#depart_time
	 **/
	public static volatile SingularAttribute<Air, String> depart_time;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#arrive
	 **/
	public static volatile SingularAttribute<Air, String> arrive;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#seatClasses
	 **/
	public static volatile ListAttribute<Air, SeatClass> seatClasses;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#id
	 **/
	public static volatile SingularAttribute<Air, Long> id;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#flightType
	 **/
	public static volatile SingularAttribute<Air, FlightType> flightType;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#airline
	 **/
	public static volatile SingularAttribute<Air, Airline> airline;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#depart
	 **/
	public static volatile SingularAttribute<Air, String> depart;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air
	 **/
	public static volatile EntityType<Air> class_;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#arrive_time
	 **/
	public static volatile SingularAttribute<Air, String> arrive_time;
	
	/**
	 * @see renewal.awesome_travel_backoffice.air.entity.Air#status
	 **/
	public static volatile SingularAttribute<Air, AirStatus> status;

}

